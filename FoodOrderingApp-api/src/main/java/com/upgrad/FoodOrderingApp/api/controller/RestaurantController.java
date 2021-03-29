package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.common.ItemType;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CustomerService customerService;

    public RestaurantDetailsResponseAddress getRestaurantDetails(final RestaurantEntity restaurant){
        RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurant.getAddress().getState().getUuid()))
                .stateName(restaurant.getAddress().getState().getStateName());
        RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress().id(UUID.fromString(restaurant.getAddress().getUuid()))
                .flatBuildingName(restaurant.getAddress().getFlatBuilNumber())
                .city(restaurant.getAddress().getCity()).locality(restaurant.getAddress().getLocality()).pincode(restaurant.getAddress().getPincode())
                .state(state);

        return  address;
    }

    public ResponseEntity<RestaurantListResponse> getRestaurantList(final List<RestaurantEntity> restaurants){
        List<RestaurantList> restaurantsList = new ArrayList<>();

        for(RestaurantEntity restaurant: restaurants){
            RestaurantDetailsResponseAddress address = getRestaurantDetails(restaurant);

            List<CategoryEntity> categories = categoryService.getCategoriesByRestaurant(restaurant.getUuid());
            List<String> categoriesName = new ArrayList<>();
            ListIterator<CategoryEntity> listIterator = categories.listIterator();
            while(listIterator.hasNext()){
                categoriesName.add(listIterator.next().getCategoryName());
            }
            Collections.sort(categoriesName, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            RestaurantList restaurantList = new RestaurantList().id(UUID.fromString(restaurant.getUuid())).restaurantName(restaurant.getRestaurantName())
                    .photoURL(restaurant.getPhotoUrl()).customerRating(BigDecimal.valueOf(restaurant.getCustomerRating()))
                    .averagePrice(restaurant.getAvgPrice()).numberCustomersRated(restaurant.getNumberOfCustomersRated())
                    .address(address).categories(categoriesName.toString());
            restaurantsList.add(restaurantList);
        }

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantsList);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurants(){

        List<RestaurantEntity> restaurants = restaurantService.restaurantsByRating();
        return getRestaurantList(restaurants);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable final String restaurant_name) throws RestaurantNotFoundException{

        List<RestaurantEntity> restaurants = restaurantService.restaurantsByName(restaurant_name);
        return getRestaurantList(restaurants);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategory(@PathVariable final String category_id) throws CategoryNotFoundException{
        List<RestaurantEntity> restaurants = restaurantService.restaurantByCategory(category_id);
        return getRestaurantList(restaurants);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurant(@PathVariable final String restaurant_id) throws RestaurantNotFoundException{
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurant_id);
        List<CategoryEntity> categories = categoryService.getCategoriesByRestaurant(restaurant.getUuid());

        List<CategoryList> categoriesList = new ArrayList<>();
        for(CategoryEntity category: categories){
            List<ItemEntity> items = itemService.getItemsByCategoryAndRestaurant(restaurant_id, category.getUuid());
            List<ItemList> itemsList = new ArrayList<>();
            for(ItemEntity itemEntity: items){
                ItemList item = new ItemList().id(UUID.fromString(itemEntity.getUuid())).itemName(itemEntity.getItemName())
                        .price(itemEntity.getPrice()).itemType(ItemList.ItemTypeEnum.valueOf(ItemType.getItemType(itemEntity.getType().toString())));
                itemsList.add(item);
            }
            Collections.sort(itemsList, new Comparator<ItemList>() {
                @Override
                public int compare(final ItemList object1, final ItemList object2) {
                    return object1.getItemName().compareTo(object2.getItemName());
                }
            });
            CategoryList categoryList = new CategoryList().id(UUID.fromString(category.getUuid()))
                    .categoryName(category.getCategoryName()).itemList(itemsList);
            categoriesList.add(categoryList);
        }
        Collections.sort(categoriesList, new Comparator<CategoryList>() {
            @Override
            public int compare(final CategoryList object1, final CategoryList object2) {
                return object1.getCategoryName().compareTo(object2.getCategoryName());
            }
        });

        RestaurantDetailsResponseAddress address = getRestaurantDetails(restaurant);

        RestaurantDetailsResponse response = new RestaurantDetailsResponse().id(UUID.fromString(restaurant.getUuid()))
                .restaurantName(restaurant.getRestaurantName())
                .photoURL(restaurant.getPhotoUrl()).customerRating(BigDecimal.valueOf(restaurant.getCustomerRating()))
                .averagePrice(restaurant.getAvgPrice()).numberCustomersRated(restaurant.getNumberOfCustomersRated())
                .address(address).categories(categoriesList);
        return new ResponseEntity<RestaurantDetailsResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurant(@PathVariable final String restaurant_id, @RequestParam final Double customerRating, @RequestHeader(name = "authorization") final String authorization) throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException{
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customer = customerService.getCustomer(accessToken);
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurant_id);

        RestaurantEntity updatedRestaurant = restaurantService.updateRestaurantRating(restaurant, customerRating);
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse().id(UUID.fromString(updatedRestaurant.getUuid()))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }
}
