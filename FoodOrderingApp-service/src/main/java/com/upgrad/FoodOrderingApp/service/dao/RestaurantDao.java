package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> restaurantsByRating(){
            List<RestaurantEntity> restaurants = entityManager.createNamedQuery("getRestaurants", RestaurantEntity.class)
                    .getResultList();
        return restaurants;
    }



    public List<RestaurantEntity> getRestaurantsByName(final String restaurantName){
        List<RestaurantEntity> restaurants = entityManager.createNamedQuery("getRestaurantsByName", RestaurantEntity.class)
                .setParameter("restaurantName", "%" + restaurantName + "%")
                .getResultList();

        return restaurants;
    }

    public List<RestaurantCategoryEntity> getRestaurantsByCategory(final CategoryEntity category){
        List<RestaurantCategoryEntity> categoryRestaurants = entityManager.createNamedQuery("getRestaurantsByCategory", RestaurantCategoryEntity.class)
                .setParameter("category", category)
                .getResultList();

        return categoryRestaurants;
    }

    public RestaurantEntity getRestaurantsByUuid(final String restaurantId){
        RestaurantEntity restaurant = entityManager.createNamedQuery("getRestaurantsByUuid", RestaurantEntity.class)
                .setParameter("uuid", restaurantId)
                .getSingleResult();

        return restaurant;
    }


    public List<RestaurantItemEntity> getRestaurantItems(final RestaurantEntity restaurant){
        List<RestaurantItemEntity> restaurantItems = entityManager.createNamedQuery("getItemsByRestaurant", RestaurantItemEntity.class)
                .setParameter("restaurant", restaurant)
                .getResultList();
        return restaurantItems;
    }

    public RestaurantEntity updateRestaurant(final RestaurantEntity restaurant){
        entityManager.merge(restaurant);
        return restaurant;
    }
}
