package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemEntity> getItemsByPopularity(final RestaurantEntity restaurant){
        List<RestaurantItemEntity> restaurantItems = restaurantDao.getRestaurantItems(restaurant);
        Map<ItemEntity, Integer> topItems = new LinkedHashMap<>();
        Map<ItemEntity, Integer> items = new LinkedHashMap<>();


        for (RestaurantItemEntity restaurantItem : restaurantItems) {
            Integer itemOrderedCount= itemDao.getItemOrderedCount(restaurantItem.getItem());
            items.put(restaurantItem.getItem(), itemOrderedCount);
        }

        topItems.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> items.put(x.getKey(), x.getValue()));

        List<ItemEntity> popularItems = new ArrayList<>();
        int itemsCount = 0;
        if(topItems.size() < 5){
            for(Map.Entry item: topItems.entrySet()){
                popularItems.add((ItemEntity) item.getKey());
            }
        }else{
            for(Map.Entry item: topItems.entrySet()){
                if(itemsCount == 5){
                    break;
                }else {
                    popularItems.add((ItemEntity) item.getKey());
                    itemsCount+=1;
                }
            }
        }

        return popularItems;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemEntity> getItemsByCategoryAndRestaurant(final String restaurantId, final String categoryId){
        CategoryEntity category = categoryDao.getCategory(categoryId);
        RestaurantEntity restaurant = restaurantDao.getRestaurantsByUuid(restaurantId);

        return itemDao.getItemsByCategoryAndRestaurant(restaurant, category);
    }


    public ItemEntity getItem(final String itemId) throws ItemNotFoundException{
        ItemEntity item = itemDao.getItem(itemId);
        if(item == null){
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return item;
    }

}
