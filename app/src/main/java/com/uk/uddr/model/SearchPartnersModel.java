package com.uk.uddr.model;

public class SearchPartnersModel {

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_address() {
        return store_address;
    }

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }

    public String getDelivery_text() {
        return delivery_text;
    }

    public void setDelivery_text(String delivery_text) {
        this.delivery_text = delivery_text;
    }

    public String getStore_image() {
        return store_image;
    }

    public void setStore_image(String store_image) {
        this.store_image = store_image;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTag_line() {
        return tag_line;
    }

    public void setTag_line(String tag_line) {
        this.tag_line = tag_line;
    }

    public String getIs_commerce() {
        return is_commerce;
    }

    public void setIs_commerce(String is_commerce) {
        this.is_commerce = is_commerce;
    }

    public String getStore_rating() {
        return store_rating;
    }

    public void setStore_rating(String store_rating) {
        this.store_rating = store_rating;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public SearchPartnersModel(String store_id, String store_type_id,String store_type, String store_name, String store_address, String delivery_text, String store_image,String store_logo, String postcode, String latitude, String longitude, String tag_line, String is_commerce, String store_rating, String review_count) {
        this.store_id = store_id;
        this.store_type_id=store_type_id;
        this.store_type=store_type;
        this.store_name = store_name;
        this.store_address = store_address;
        this.delivery_text = delivery_text;
        this.store_image = store_image;
        this.store_logo=store_logo;
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tag_line = tag_line;
        this.is_commerce = is_commerce;
        this.store_rating = store_rating;
        this.review_count = review_count;
    }

    String store_id;

    public String getStore_type_id() {
        return store_type_id;
    }

    public void setStore_type_id(String store_type_id) {
        this.store_type_id = store_type_id;
    }

    public String getStore_type() {
        return store_type;
    }

    public void setStore_type(String store_type) {
        this.store_type = store_type;
    }

    String store_type_id;
    String store_type;
    String store_name;
    String store_address;
    String delivery_text;
    String store_image;

    public String getStore_logo() {
        return store_logo;
    }

    public void setStore_logo(String store_logo) {
        this.store_logo = store_logo;
    }

    String store_logo;
    String postcode;
    String latitude;
    String longitude;
    String tag_line;
    String is_commerce;
    String store_rating;
    String review_count ;

}
