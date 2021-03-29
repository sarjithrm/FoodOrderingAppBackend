package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.common.ItemType;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategoriesOrderedByName(){
        List<CategoryEntity> categories = categoryService.getAllCategoriesOrderedByName();
        List<CategoryListResponse> categoriesList = new ArrayList<>();

        for(CategoryEntity category: categories){
            CategoryListResponse categoryList = new CategoryListResponse().id(UUID.fromString(category.getUuid()))
                    .categoryName(category.getCategoryName());
            categoriesList.add(categoryList);
        }

        CategoriesListResponse categoriesListResponse = new CategoriesListResponse().categories(categoriesList);
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable final String category_id) throws CategoryNotFoundException{
        CategoryEntity category = categoryService.getCategoryById(category_id);
        List<CategoryItemEntity> categoryItems = categoryService.getCategoryItems(category);
        List<ItemList> items = new ArrayList<>();

        for(CategoryItemEntity categoryItem: categoryItems){
            ItemList item = new ItemList().id(UUID.fromString(categoryItem.getItem().getUuid())).itemName(categoryItem.getItem().getItemName())
                    .price(categoryItem.getItem().getPrice()).itemType(ItemList.ItemTypeEnum.valueOf(ItemType.getItemType(categoryItem.getItem().getType())));
            items.add(item);
        }

        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse().id(UUID.fromString(category.getUuid()))
                .categoryName(category.getCategoryName()).itemList(items);
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }
}
