package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "restaurant_item")
public class RestaurantItemEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ItemEntity itemEntity;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RestaurantEntity restaurantEntity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ItemEntity getItem() {
        return itemEntity;
    }

    public void setItem(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    public RestaurantEntity getRestaurant() {
        return restaurantEntity;
    }

    public void setRestaurant(RestaurantEntity restaurantEntity) {
        this.restaurantEntity = restaurantEntity;
    }
}
