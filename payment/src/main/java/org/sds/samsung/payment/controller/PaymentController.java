package org.sds.samsung.payment.controller;

import org.sds.samsung.payment.entity.Payment;
import org.sds.samsung.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class PaymentController {

    @Autowired
    PaymentRepository paymentRepository;

    @GetMapping("/payment")
    public String test() {
        return "Response from Payment Service!";
    }

    @PostMapping("/pay/{name}")
    public String processPayment(@PathVariable String name) {
        // Process the payment here
        return "Payment processed for: " + name;
    }

    @GetMapping("/paymentResponse")
    public String processPayment() {
        // Process the payment here
        return "Hello from Payment Service!";
    }

    @GetMapping("/pay")
    public List<Payment> getAllPayment() {
        System.out.println("----------------All Payment fetched from payment db-----------");
        return paymentRepository.findAll();
    }
    @GetMapping("/payById/{id}")
    public Payment getAllPayment(@PathVariable String id) {
        Optional<Payment> byId = paymentRepository.findById(id);

        Payment payment = new Payment();
        if(byId.isPresent()){
            payment = byId.get();
        }

        return payment;
    }

}
