package org.sds.samsung.order.controller;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.HttpExtension;
import org.sds.samsung.order.dto.OrderDTO;
import org.sds.samsung.order.entity.Order;
import org.sds.samsung.order.producer.KafkaProducer;
import org.sds.samsung.order.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {

    //USE REST Template - Service-to-Service Invocation
    @Autowired
    private RestTemplate restTemplate;

    //USE DAPR Client - Service-to-Service Invocation
    @Autowired
    DaprClient daprClient;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    OrderServiceImpl orderService;

    private static final String PUBSUB_NAME = "pubsub";
    private static final String TOPIC_NAME = "orders";

    @GetMapping("/order")
    public String testOrder() {
        return "Response from Order Service!";
    }

    //Service-to-Service Invocation
    @PostMapping("/invoke/{name}")
    public String invokePayment(@PathVariable String name) {
        String result = "Response -> ";
        try {
            // Correctly formatted invokeMethod call
            result = result + daprClient.invokeMethod(
                    "payment-service",
                    "pay/" + name,
                    null,
                    HttpExtension.POST,
                    String.class
            ).block();

            System.out.println("Order service received: " + result);
        } catch (Exception e) {
            System.out.println("Exception received in OrderController.invokePayment(): " + e.getMessage());
        }
        return result;
    }

    //Integrate Kafka with DAPR
    @PostMapping("/publish")
    public void publishMessage(@RequestBody OrderDTO orderDTO) {
        //save data into DB
        Order orderResponse = orderService.createOrderData(orderDTO);

        if (orderResponse != null && orderResponse.getId() != null) {
            orderDTO.setId(orderResponse.getId());

            //publish event to kafka
            try (DaprClient client = new DaprClientBuilder().build()) {
                System.out.println("------Publishing the message---");
                client.publishEvent(PUBSUB_NAME, TOPIC_NAME, orderDTO).block();
            } catch (Exception e) {
                throw new RuntimeException("Exception in publishMessage(): " + e.getMessage());
            }
        }
    }


    //Without DAPR
    //Integrate Kafka without DAPR - Message Queue
    @PostMapping("/order")
    public Order createOrder(@RequestBody OrderDTO orderDTO) {

        System.out.println("Request data: " + orderDTO.toString());

        //save data into DB
        Order orderResponse = orderService.createOrderData(orderDTO);

        if (orderResponse != null && orderResponse.getId() != null) {
            orderDTO.setId(orderResponse.getId());

            //publish event to kafka
            kafkaProducer.send(orderDTO);
        }
        return orderResponse;
    }

    //Without DAPR
    //Service-to-Service Invocation
    @PostMapping("/callPaymentRestTemplate")
    public String callPaymentService() {
        String paymentServiceUrl = "http://localhost:8001/paymentResponse";
        ResponseEntity<String> response = restTemplate.getForEntity(paymentServiceUrl, String.class);
        return response.getBody();
    }
}
