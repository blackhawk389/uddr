package com.uk.uddr.model;

public class FeatureStoreModel {


    String store_id;
    String tag_line;
    String des;

    public String getIs_bookmark() {
        return is_bookmark;
    }

    public void setIs_bookmark(String is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    String is_bookmark;

    public FeatureStoreModel(String store_id, String store_name, String store_address, String distance, String store_image, String store_logo, String postcode, String latitude, String longitude, String total_cat, String store_rating, String review_count, String tag_line, String des,String is_bookmark) {
        this.store_id = store_id;
        this.store_name = store_name;
        this.store_address = store_address;
        this.distance = distance;
        this.store_image = store_image;
        this.store_logo = store_logo;
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.total_cat = total_cat;
        this.store_rating = store_rating;
        this.review_count = review_count;
        this.tag_line = tag_line;
        this.des = des;
        this.is_bookmark=is_bookmark;
    }

    public String getTag_line() {
        return tag_line;
    }

    public void setTag_line(String tag_line) {
        this.tag_line = tag_line;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStore_image() {
        return store_image;
    }

    public void setStore_image(String store_image) {
        this.store_image = store_image;
    }

    public String getStore_logo() {
        return store_logo;
    }

    public void setStore_logo(String store_logo) {
        this.store_logo = store_logo;
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

    public String getTotal_cat() {
        return total_cat;
    }

    public void setTotal_cat(String total_cat) {
        this.total_cat = total_cat;
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

    String store_name;
    String store_address;
    String distance;
    String store_image;
    String store_logo;
    String postcode;
    String latitude;
    String longitude;
    String total_cat;
    String store_rating;
    String review_count;

}
