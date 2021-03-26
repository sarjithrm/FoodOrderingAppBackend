package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
public class OrdersEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "bill")
    @NotNull
    private Integer bill;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private CouponEntity couponEntity;

    @Column(name = "discount")
    @NotNull
    private Integer discount;

    @Column(name = "date")
    @NotNull
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity addressEntity;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getBill() {
        return bill;
    }

    public void setBill(Integer bill) {
        this.bill = bill;
    }

    public CouponEntity getCoupon() {
        return couponEntity;
    }

    public void setCoupon(CouponEntity couponEntity) {
        this.couponEntity = couponEntity;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public CustomerEntity getCustomer() {
        return customerEntity;
    }

    public void setCustomer(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public AddressEntity getAddress() {
        return addressEntity;
    }

    public void setAddress(AddressEntity addressEntity) {
        this.addressEntity = addressEntity;
    }

    public RestaurantEntity getRestaurant() {
        return restaurantEntity;
    }

    public void setRestaurant(RestaurantEntity restaurantEntity) {
        this.restaurantEntity = restaurantEntity;
    }
}
