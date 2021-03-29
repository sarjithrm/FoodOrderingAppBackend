package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(method = RequestMethod.GET, path = "/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getPayments(){
        List<PaymentEntity> payments = paymentService.getAllPaymentMethods();
        List<PaymentResponse> paymentResponses = new ArrayList<>();

        for(PaymentEntity payment: payments){
            PaymentResponse paymentResponse = new PaymentResponse().id(UUID.fromString(payment.getUuid()))
                    .paymentName(payment.getPaymentName());
            paymentResponses.add(paymentResponse);
        }

        PaymentListResponse response = new PaymentListResponse().paymentMethods(paymentResponses);
        return new ResponseEntity<PaymentListResponse>(response, HttpStatus.OK);
    }
}
