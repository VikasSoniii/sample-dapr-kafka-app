package org.sds.samsung.order.producer;

import org.sds.samsung.order.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, OrderDTO> kafkaTemplate;

    String kafkaTopic = "orders";

    public void send(OrderDTO orderDTO) {
        kafkaTemplate.send(kafkaTopic, orderDTO);
    }
}
