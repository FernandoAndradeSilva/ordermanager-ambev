package br.com.ordermanager.service;

import br.com.ordermanager.dto.OrderDTO;
import br.com.ordermanager.dto.ProductDTO;
import br.com.ordermanager.entties.Order;
import br.com.ordermanager.enums.OrderStatus;
import br.com.ordermanager.mapper.OrderMapper;
import br.com.ordermanager.repository.OrderRepository;
import br.com.ordermanager.service.exceptions.DuplicateOrderIdException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static br.com.ordermanager.configuration.RabbitMQConfig.ORDER_CREATED_QUEUE;

@RequiredArgsConstructor
@Service
public class OrderService {

    private static final Log logger = LogFactory.getLog(OrderService.class);
    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void managerOrderComplete(Long orderId) {

        logger.info("Starting order management for orderId: " + orderId);

        if (isDuplicateOrder(orderId)) {
            logger.warn("Duplicate order detected: " + orderId);
            throw new DuplicateOrderIdException("Pedido já existente: " + orderId);
        }

        // Enviar o pedido para a fila RabbitMQ para processamento assíncrono
        rabbitTemplate.convertAndSend(ORDER_CREATED_QUEUE, orderId);
        logger.info("Order sent to RabbitMQ for processing: " + orderId);
    }


    // Mecanismo para limitar a taxa de consumo de mensagens da fila RabbitMQ.
    // Isso ajuda a evitar que o banco de dados seja sobrecarregado por um grande volume
    // de operações simultâneas.
    @Transactional
    @RabbitListener(queues = ORDER_CREATED_QUEUE, concurrency = "5-10")
    public void processOrder(Long orderId) {
        logger.info("Processing order from RabbitMQ for orderId: " + orderId);

        try {

            // AS INTEGRAÇÕES ESTÃO COMENTADAS PORQUE NÃO FOI PEDIDO PARA IMPLEMENTAR A QUESTÃO DOS PRODUTOS INTERNOS
            // OrderDTO order = fetchOrderFromExternalA(orderId);
            // AQUI A INTEGRAÇÃO RETORNA O PEDIDO DO CLIENTE

            // CRIEI UM MOCK DTO PARA REPRESENTAR O RETORNO DA API EXTERNA A
            OrderDTO mockOrderDto = mockOrderDto();

            // AQUI O PEDIDO É CALCULADO
            OrderDTO managedOrder = calculateOrder(mockOrderDto);

            // AQUI O PEDIDO É SALVO NO BANCO DE DADOS
            Order order = saveOrder(managedOrder);

            // CONFORME SOLICITADO NO TESTE AQUI O PEDIDO É ENVIADO AO PRODUTO EXTERNO PARA PROCESSAMENTO
            //sendOrderToExternalB(managedOrder);

            logger.info("O PEDIDO SERÁ ENVIADO AO SERVIÇO EXTERNO B:" + order);

            logger.info("Order processing completed for orderId: " + orderId);
        } catch (Exception e) {
            logger.error("Error processing order from RabbitMQ for orderId: " + orderId, e);
            throw new RuntimeException("Erro ao processar o pedido");

        }
    }

    private OrderDTO mockOrderDto() {
        OrderDTO orderDTO = new OrderDTO();

        ProductDTO productDTO = new ProductDTO("Cabo USB", 10.0, 2);
        ProductDTO productDTO1 = new ProductDTO("Impressora Multifunctional", 175.50, 4);
        ProductDTO productDTO2 = new ProductDTO("Monitor", 500.0, 7);
        ProductDTO productDTO3 = new ProductDTO("Estabilizador", 300.0, 12);

        orderDTO.setProducts(Arrays.asList(productDTO, productDTO1, productDTO2, productDTO3));

        return orderDTO;
    }

    public OrderDTO calculateOrder(OrderDTO orderDTO) {
        logger.debug("Calculating order total amount for order: " + orderDTO);
        orderDTO.setTotalAmount(orderDTO.getProducts().stream()
                .map(item -> item.getPrice() * item.getQuantity())
                .reduce(Double::sum)
                .orElse(null));
        logger.debug("Order total amount calculated for order: " + orderDTO);
        return orderDTO;
    }

    public OrderDTO fetchOrderFromExternalA(Long orderId) {
        logger.info("Fetching order from external A for orderId: " + orderId);
        String url = "https://api.externalA.com/orders/" + orderId;
        return restTemplate.getForObject(url, OrderDTO.class);
    }

    public void sendOrderToExternalB(OrderDTO order) {
        logger.info("Sending order to external B for order: " + order);
        String url = "https://api.externalB.com/orders";
        restTemplate.postForEntity(url, order, Void.class);
    }

    @Transactional
    protected Order saveOrder(OrderDTO managedOrder) {
        logger.info("Saving order to database for order: " + managedOrder);
        Order order = OrderMapper.INSTANCE.toEntity(managedOrder);
        try {
            order.setStatus(OrderStatus.PENDING);
            logger.info("Order saved to database");

            Order savedOrder = orderRepository.save(order);

            savedOrder.getProducts().forEach(product -> {
                product.setOrder(savedOrder);
            });

            return savedOrder;
        } catch (Exception e) {
            logger.error("Error saving order to database for orderId: " + managedOrder, e);
            throw new RuntimeException("Erro ao salvar o pedido no banco de dados");
        }
    }

    private boolean isDuplicateOrder(Long orderId) {
        logger.debug("Checking for duplicate order for orderId: " + orderId);
        return orderRepository.existsById(orderId);
    }
}
