package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public OrderEntity saveOrder(final OrderEntity order){
        entityManager.persist(order);
        return order;
    }

    public OrderItemEntity saveOrderItem(final OrderItemEntity orderItem){
        entityManager.persist(orderItem);
        return orderItem;
    }

    public List<OrderEntity> getCustomerOrders(final CustomerEntity customer){
        List<OrderEntity> orders = entityManager.createNamedQuery("getCustomerOrders", OrderEntity.class)
                .setParameter("customer", customer)
                .getResultList();
        return orders;
    }

    public List<OrderItemEntity> getOrderItems(final OrderEntity order){
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
