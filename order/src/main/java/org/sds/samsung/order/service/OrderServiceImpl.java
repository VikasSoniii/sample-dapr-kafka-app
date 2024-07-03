package org.sds.samsung.order.service;

import org.sds.samsung.order.dto.OrderDTO;
import org.sds.samsung.order.entity.Order;
import org.sds.samsung.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl {

    @Autowired
    OrderRepository orderRepository;


    public Order createOrderData(OrderDTO orderDTO){
        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setAmount(orderDTO.getAmount());

        return orderRepository.save(order);
    }
}
