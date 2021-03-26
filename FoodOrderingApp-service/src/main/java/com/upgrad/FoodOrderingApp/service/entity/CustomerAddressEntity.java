package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_address")
@NamedQueries(
        {
                @NamedQuery(name = "getCustomerAddressMap", query = "select ca from CustomerAddressEntity ca where ca.addressEntity = :address")
        }
)
public class CustomerAddressEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CustomerEntity customerEntity;

    @ManyToOne
    @JoinColumn(name = "address_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AddressEntity addressEntity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
