package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.common.ItemType;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(method = RequestMethod.GET, path = "/item/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getItems(@PathVariable final String restaurant_id) throws RestaurantNotFoundException{
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurant_id);
        List<ItemEntity> topItems = itemService.getItemsByPopularity(restaurant);
        ItemListResponse itemListResponse = new ItemListResponse();

        for(ItemEntity item: topItems){
            ItemList itemList = new ItemList().id(UUID.fromString(item.getUuid())).itemName(item.getItemName())
                    .price(item.getPrice()).itemType(ItemList.ItemTypeEnum.valueOf(ItemType.getItemType(item.getType().toString())));
            itemListResponse.add(itemList);
        }

        return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);
    }
}
