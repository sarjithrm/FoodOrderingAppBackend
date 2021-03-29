package com.upgrad.FoodOrderingApp.service.common;

public enum ItemType {

    VEG("VEG"), NON_VEG("NON_VEG");

    private String value;

    private ItemType(String value){
        this.value = value;
    }

    public static String getItemType(String type){
        if(type.equals("0")){
            return ItemType.VEG.value;
        }else {
            return ItemType.NON_VEG.value;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
