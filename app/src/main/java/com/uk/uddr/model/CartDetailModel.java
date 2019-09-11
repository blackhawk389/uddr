package com.uk.uddr.model;

public class CartDetailModel {
    String img;
    String title;
    String price;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    String quantity;

    public CartDetailModel(String img, String title, String price,String qty) {
        this.img = img;
        this.title = title;
        this.price = price;
        this.quantity=qty;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
