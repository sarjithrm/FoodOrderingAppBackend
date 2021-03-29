package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<PaymentEntity> getAllPaymentMethods(){
        return paymentDao.getPayments();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentEntity getPaymentByUUID(final String paymentId) throws PaymentMethodNotFoundException{
        PaymentEntity payment = paymentDao.getPayment(paymentId);
        if(payment == null){
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        return payment;
    }
}
