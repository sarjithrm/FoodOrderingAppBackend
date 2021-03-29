package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Integer getItemOrderedCount(final ItemEntity item){
        Integer count = entityManager.createNamedQuery("getItemOrderedCount", OrderItemEntity.class)
                .setParameter("item", item)
                .getFirstResult();

        return count;
    }


    public ItemEntity getItem(final String itemId){
        try{
            ItemEntity item = entityManager.createNamedQuery("getItem", ItemEntity.class)
                    .setParameter("uuid", itemId)
                    .getSingleResult();
            return item;
        }catch (NoResultException nre){
            return null;
        }
    }


    public List<ItemEntity> getItemsByCategoryAndRestaurant(final RestaurantEntity restaurant, final CategoryEntity category){
        List<CategoryItemEntity> itemsCategory = entityManager.createNamedQuery("getItemsByCategory", CategoryItemEntity.class)
                .setParameter("category", category)
                .getResultList();

        List<ItemEntity> items = new ArrayList<>();

        for(CategoryItemEntity item: itemsCategory){
            try{
                RestaurantItemEntity restaurantItemEntity = entityManager.createNamedQuery("getItemOfRestaurant", RestaurantItemEntity.class)
                        .setParameter("restaurant", restaurant)
                        .setParameter("item", item.getItem())
                        .getSingleResult();
                items.add(restaurantItemEntity.getItem());
            }catch (NoResultException nre){
                continue;
            }
        }
        return items;
    }
}
