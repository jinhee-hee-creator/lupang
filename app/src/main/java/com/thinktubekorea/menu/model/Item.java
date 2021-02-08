package com.thinktubekorea.menu.model;

import lombok.Data;

@Data
public class Item {
    String categoryCode;
    String itemName;
    String itemCode;
    String expression;
    String glassAmount;
    String bottleAmount;
    boolean isAnimation = false;
    boolean largeSize = false;

    public void setAnimation(boolean b) {
    }

    public boolean isLargeSize() {
        return largeSize;
    }

    public void setLargeSize(boolean largeSize) {
        this.largeSize = largeSize;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}
