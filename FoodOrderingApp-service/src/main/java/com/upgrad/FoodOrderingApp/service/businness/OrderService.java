package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerService customerService;

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(final OrderEntity order) throws AuthorizationFailedException{
        CustomerAddressEntity customerAddressEntity = addressDao.getCustomerAddressMapping(order.getAddress());
        if(order.getCustomer().getId() != customerAddressEntity.getCustomer().getId()){
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
        OrderEntity orderPlaced = orderDao.saveOrder(order);

        return orderPlaced;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(final OrderItemEntity orderItem){
        return orderDao.saveOrderItem(orderItem);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderEntity> getOrdersByCustomers(final String customerId){
        CustomerEntity customer = customerService.getCustomerByUUID(customerId);
        return orderDao.getCustomerOrders(customer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderItemEntity> getOrderItems(final OrderEntity order){
        return orderDao.getOrderItems(order);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponId(final String couponId) throws CouponNotFoundException {
        CouponEntity coupon = orderDao.getCoupon(couponId);
        if(coupon == null){
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
        return coupon;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponName(final String couponName) throws CouponNotFoundException{
        if(couponName.equals("")){
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        CouponEntity coupon = orderDao.getCouponByName(couponName);
        if(coupon == null){
            throw new CouponNotFoundException("CPF-001", "No coupon by this name");
        }
        return coupon;
    }
}
