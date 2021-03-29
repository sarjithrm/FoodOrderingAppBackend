package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public OrdersEntity saveOrder(final OrdersEntity order){
        entityManager.persist(order);
        return order;
    }

    public void saveOrderItems(final List<OrderItemEntity> orderItems){
        for(OrderItemEntity item: orderItems){
            entityManager.persist(item);
        }
    }

    public List<OrdersEntity> getCustomerOrders(final CustomerEntity customer){
        List<OrdersEntity> orders = entityManager.createNamedQuery("getCustomerOrders", OrdersEntity.class)
                .setParameter("customer", customer)
                .getResultList();
        return orders;
    }

    public List<OrderItemEntity> getOrderItems(final OrdersEntity order){
        List<OrderItemEntity> orderItems = entityManager.createNamedQuery("getOrdersItem", OrderItemEntity.class)
                .setParameter("order", order)
                .getResultList();
        return orderItems;
    }

    public CouponEntity getCoupon(final String couponId){
        try{
            CouponEntity coupon = entityManager.createNamedQuery("getCoupon", CouponEntity.class)
                    .setParameter("uuid", couponId)
                    .getSingleResult();
            return coupon;
        }catch (NoResultException nre){
            return null;
        }
    }

    public CouponEntity getCouponByName(final String couponName){
        try{
            CouponEntity coupon = entityManager.createNamedQuery("getCouponByName", CouponEntity.class)
                    .setParameter("name", couponName)
                    .getSingleResult();
            return coupon;
        }catch (NoResultException nre){
            return null;
        }
    }
}
