package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CustomerService customerService;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<RestaurantEntity> restaurantsByRating(){
        return restaurantDao.restaurantsByRating();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException {
        if(restaurantName.equals("")){
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }
        return restaurantDao.getRestaurantsByName(restaurantName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<RestaurantEntity> restaurantByCategory(final String categoryId) throws CategoryNotFoundException {
        CategoryEntity category = categoryService.getCategoryById(categoryId);
        List<RestaurantCategoryEntity> restaurantCategories = restaurantDao.getRestaurantsByCategory(category);
        List<RestaurantEntity> restaurants = new ArrayList<>();
        ListIterator<RestaurantCategoryEntity> listIterator = restaurantCategories.listIterator();
        while(listIterator.hasNext()){
            restaurants.add(listIterator.next().getRestaurant());
        }
        if(restaurants.size() > 1){
            Collections.sort(restaurants, new Comparator<RestaurantEntity>() {
                @Override
                public int compare(final RestaurantEntity object1, final RestaurantEntity object2) {
                    return object1.getRestaurantName().compareTo(object2.getRestaurantName());
                }
            });
        }
        return restaurants;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity restaurantByUUID(final String restaurantUuid) throws RestaurantNotFoundException {
        if(restaurantUuid.equals("")){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }
        RestaurantEntity restaurant = restaurantDao.getRestaurantsByUuid(restaurantUuid);
        if(restaurant == null){
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        return restaurant;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantRating(final RestaurantEntity restaurant, final Double customerRating) throws InvalidRatingException{

        if(customerRating == null || customerRating.isNaN() || (customerRating < 1 && customerRating > 5)){
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        double avgCustomerRating = (restaurant.getCustomerRating() + customerRating)/2;
        restaurant.setCustomerRating(avgCustomerRating);
        restaurant.setNumberOfCustomersRated(restaurant.getNumberOfCustomersRated() + 1);

        return restaurantDao.updateRestaurant(restaurant);
    }
}
