package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<PaymentEntity> getPayments(){
        List<PaymentEntity> payments = entityManager.createNamedQuery("getPayments", PaymentEntity.class)
                .getResultList();
        return payments;
    }


    public PaymentEntity getPayment(final String paymentId){
        try{
            PaymentEntity payment = entityManager.createNamedQuery("getPayment", PaymentEntity.class)
                    .setParameter("uuid", paymentId)
                    .getSingleResult();

            return payment;
        }catch (NoResultException nre){
            return null;
        }
    }

}
