package br.com.ordermanager.controller;

import br.com.ordermanager.entties.Order;
import br.com.ordermanager.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Log logger = LogFactory.getLog(OrderController.class);

    private final OrderService service;

    // Boas práticas usadas no controller:
    // Dto's devem ser usados para trafegar dados entre a aplicação e o cliente
    // O uso de DTOs é uma prática comum para evitar que entidades sejam expostas fora do serviço


    // Reecebe requisicao de consulta do pedido
    @PostMapping("/{orderId}")
    public ResponseEntity<?> receiveOrderFromExternalA(@PathVariable Long orderId) {
        logger.info("Received request to manage order with orderId: " + orderId);

        try {
            service.managerOrderComplete(orderId);
            logger.info("Order management completed successfully for orderId: " + orderId);
        } catch (Exception e) {
            logger.error("Error managing order with orderId: " + orderId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);


    }




}