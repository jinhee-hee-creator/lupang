package com.thinktubekorea.menu;

public class ItemSub {

    private String name;
    private String tel;
    private int image;

    public ItemSub(String name, String tel, int image) {
        this.name = name;
        this.tel = tel;
        this.image = image;
    }

    public ItemSub(String[] str_item, String[] str_price, Integer image) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}