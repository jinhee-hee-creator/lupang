package com.thinktubekorea.menu.model;

import lombok.Data;

@Data
public class Category {
    String categoryName;
    String categotyCode;

    public String getCategotyCode() {
        return categotyCode;
    }

    public void setCategotyCode(String categotyCode) {
        this.categotyCode = categotyCode;
    }
}
