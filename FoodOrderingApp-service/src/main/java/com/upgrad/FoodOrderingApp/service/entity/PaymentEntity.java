package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "payment")
@NamedQueries(
        {
                @NamedQuery(name = "getPayments", query = "select p from PaymentEntity p"),
                @NamedQuery(name = "getPayment", query = "select p from PaymentEntity p where p.uuid = :uuid")
        }
)
public class PaymentEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "payment_name")
    @NotNull
    @Size(max = 255)
    private String paymentName;

    public PaymentEntity() {
    }

    public PaymentEntity(String uuid, String paymentName) {
        this.uuid = uuid;
        this.paymentName = paymentName;
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

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
