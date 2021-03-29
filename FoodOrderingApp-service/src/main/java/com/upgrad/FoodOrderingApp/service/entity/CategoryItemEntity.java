package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "category_item")
@NamedQueries(
        {
                @NamedQuery(name = "getCategoryByItem", query = "select ci from CategoryItemEntity ci where ci.itemEntity = :item"),
                @NamedQuery(name = "getItemsByCategory", query = "select ci from CategoryItemEntity ci where ci.categoryEntity = :category")
        }
)
public class CategoryItemEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ItemEntity itemEntity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CategoryEntity categoryEntity;

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

    public CategoryEntity getCategory() {
        return categoryEntity;
    }

    public void setCategory(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }
}
