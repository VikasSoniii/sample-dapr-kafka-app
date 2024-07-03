package org.sds.samsung.payment.service;

import org.sds.samsung.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl {

    @Autowired
    PaymentRepository paymentRepository;


}
