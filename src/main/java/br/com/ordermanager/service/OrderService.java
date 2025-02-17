package br.com.ordermanager.service;

import br.com.ordermanager.dto.OrderDTO;
import br.com.ordermanager.entties.Order;
import br.com.ordermanager.mapper.OrderMapper;
import br.com.ordermanager.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * A prática de usar @RequiredArgsConstructor é geralmente considerada melhor do que usar @Autowired em classes de serviço no Spring Boot.
 * Aqui estão algumas razões:
 * Imutabilidade: Com @RequiredArgsConstructor, você pode declarar os campos como final, garantindo que eles sejam inicializados apenas uma vez e nunca alterados, promovendo a imutabilidade.
 * Injeção de Dependência no Construtor: A injeção de dependência no construtor é preferida sobre a injeção de dependência em campos, pois facilita a criação de objetos de teste e torna as dependências explícitas.
 * Testabilidade: Facilita a criação de instâncias de classes para testes, pois você pode passar as dependências diretamente no construtor.
 */


/**
 * Método calculateOrder seguindo SRP (SOLID)
 * O SRP afirma que cada classe ou método deve ter uma, e apenas uma, razão para mudar.
 * Em outras palavras, um método deve fazer apenas uma coisa e fazê-la bem.
 * Isso torna o código mais fácil de entender, testar e manter.
 */

@RequiredArgsConstructor
@Service
public class OrderService {

    // Utilizando Observabilidade para monitorar o serviço
    private static final Log logger = LogFactory.getLog(OrderService.class);

    // Utilizado para simular as integrações com os produtos externos
    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;

    @Transactional
    public void managerOrderComplete(Long orderId) {

        logger.info("Starting order management for orderId: " + orderId);


        // Antes de fazer o processo verifica se o pedido é duplicado
        if (isDuplicateOrder(orderId)) {
            logger.warn("Duplicate order detected: " + orderId);
            throw new IllegalArgumentException("Pedido duplicado: " + orderId);
        }

        // Buscar o pedido do produto externo A
        OrderDTO order = fetchOrderFromExternalA(orderId);

        // Calcula o pedido
        OrderDTO managedOrder = calculateOrder(order);

        // Disponibilizar o pedido gerenciado para o produto externo B
        sendOrderToExternalB(managedOrder);

        // Salvar o pedido gerenciado no banco de dados
        saveOrder(managedOrder);

        logger.info("Order management completed for orderId: " + orderId);

    }


    // Recebe o pedido
    // Calcula
    // Repassa o pedido com valor calculado
    public OrderDTO calculateOrder(OrderDTO orderDTO) {
        logger.debug("Calculating order total amount for order: " + orderDTO);


        // Calcula o valor total do pedido
        // Usando map para converter em atributo
        // Usando reduce para somar os valores e reduzir a um valor só
        orderDTO.setTotalAmount(orderDTO.getProducts().stream()
                .map(item -> item.getPrice() * item.getQuantity())
                .reduce(Double::sum)
                .orElse(null));

        logger.debug("Order total amount calculated for order: " + orderDTO);

        return orderDTO;

    }

    public OrderDTO fetchOrderFromExternalA(Long orderId) {
        logger.info("Fetching order from external A for orderId: " + orderId);


        // URL do produto externo A
        String url = "https://api.externalA.com/orders/" + orderId;

        // Fazer a chamada REST para buscar o pedido
        return restTemplate.getForObject(url, OrderDTO.class);
    }

    public void sendOrderToExternalB(OrderDTO order) {
        logger.info("Sending order to external B for order: " + order);

        // URL do produto externo B
        String url = "https://api.externalB.com/orders";

        // Fazer a chamada REST para enviar o pedido
        restTemplate.postForEntity(url, order, Void.class);
    }

    private void saveOrder(OrderDTO managedOrder) {
        logger.info("Saving order to database for order: " + managedOrder);
        Order order = OrderMapper.INSTANCE.toEntity(managedOrder);

        try {
            orderRepository.save(order);
            logger.info("Order saved to database for order: " + order);
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
