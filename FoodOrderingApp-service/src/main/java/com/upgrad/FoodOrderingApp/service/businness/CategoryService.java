package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CategoryEntity getCategoryById(final String categoryId) throws CategoryNotFoundException{
        if(categoryId.equals("")){
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }
        CategoryEntity category = categoryDao.getCategory(categoryId);
        if( category == null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        return category;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getAllCategoriesOrderedByName(){
        return categoryDao.getCategories();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryItemEntity> getCategoryItems(final CategoryEntity category){
        List<CategoryItemEntity> categoryItems = categoryDao.getCategoryItems(category);
        return categoryItems;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CategoryEntity> getCategoriesByRestaurant(final String restaurantId){
        RestaurantEntity restaurant = restaurantDao.getRestaurantsByUuid(restaurantId);
        List<RestaurantCategoryEntity> restaurantCategories = categoryDao.getCategoriesByRestaurant(restaurant);

        List<CategoryEntity> categories =  new ArrayList<>();
        for(RestaurantCategoryEntity category: restaurantCategories){
            categories.add(category.getCategory());
        }
        return categories;
    }
}
