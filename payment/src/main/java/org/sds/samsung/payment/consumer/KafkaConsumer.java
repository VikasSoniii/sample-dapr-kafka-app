package org.sds.samsung.payment.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sds.samsung.payment.dto.OrderDTO;
import org.sds.samsung.payment.entity.Payment;
import org.sds.samsung.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.payment}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderEvents(@Payload String order, Acknowledgment ack) throws JsonProcessingException {
        OrderDTO orderDTO = objectMapper.readValue(order, OrderDTO.class);

        Payment payment = new Payment();
        try{
            if(orderDTO != null){
                payment.setOrderId(orderDTO.getId());
                payment.setOrderAmount(orderDTO.getAmount());
            }
            paymentRepository.save(payment);
        }catch (Exception ex){
            throw new RuntimeException("Error while consuming the orderDTO event. please check event consuming logic.");
        }
        System.out.println("Received orderDTO event: " + orderDTO);
        ack.acknowledge();
    }
}
