package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.GenerationType;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CategoryEntity getCategory(final String categoryId){
        CategoryEntity category = entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class)
                .setParameter("uuid", categoryId)
                .getSingleResult();
        return category;
    }

    public List<CategoryEntity> getCategories(){
        List<CategoryEntity> categories = entityManager.createNamedQuery("getCategories", CategoryEntity.class)
                .getResultList();
        return categories;
    }

    public List<CategoryItemEntity> getCategoryItems(final CategoryEntity category){
        List<CategoryItemEntity> categoryItems = entityManager.createNamedQuery("getItemsByCategory", CategoryItemEntity.class)
                .setParameter("category", category)
                .getResultList();
        return categoryItems;
    }

    public List<RestaurantCategoryEntity> getCategoriesByRestaurant(final RestaurantEntity restaurant){
        List<RestaurantCategoryEntity> restaurantCategories = entityManager.createNamedQuery("getCategoriesByRestaurant", RestaurantCategoryEntity.class)
                .setParameter("restaurant", restaurant)
                .getResultList();
        return restaurantCategories;
    }
}
