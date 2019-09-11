package com.uk.uddr.model;

public class ProductListModel {

    String product_id, store_id, cat_id, product_name, product_price,product_code,product_discount,product_description,product_image,status,Count;

    public ProductListModel(String product_id, String store_id, String cat_id, String product_name, String product_price, String product_code, String product_discount, String product_description, String product_image, String status, String Count) {
        this.product_id = product_id;
        this.store_id = store_id;
        this.cat_id = cat_id;
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_code = product_code;
        this.product_discount = product_discount;
        this.product_description = product_description;
        this.product_image = product_image;
        this.status = status;
        this.Count = Count;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String Count) {
        this.Count = Count;
    }
}
