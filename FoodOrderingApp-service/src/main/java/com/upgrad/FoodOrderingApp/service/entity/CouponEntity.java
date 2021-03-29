package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "coupon")
@NamedQueries(
        {
                @NamedQuery(name = "getCoupon", query = "select c from CouponEntity c where c.uuid = :uuid"),
                @NamedQuery(name = "getCouponByName", query = "select c from CouponEntity c where c.couponName = :name")
        }
)
public class CouponEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "coupon_name")
    @NotNull
    @Size(max = 255)
    private String couponName;

    @Column(name = "percent")
    @NotNull
    private Integer percent;

    public CouponEntity(){}

    public CouponEntity(String uuid, String couponName, int percent) {
        this.uuid = uuid;
        this.couponName = couponName;
        this.percent = (Integer)percent;
    }

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

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }
}
