package com.uk.uddr.model;

public class CartModel  {
    String id;
    String title;
    String text;
    String price;
    String image;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    String count;

    public CartModel(String id, String title, String text, String price, String image,String count) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.price = price;
        this.image = image;
        this.count=count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
