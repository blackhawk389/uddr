package com.uk.uddr.model;

public class CommentModel {
    String id,store_id,user_id,rating,review,status,created_at,updated_at,name,user_image;

    public CommentModel(String id, String store_id, String user_id, String rating, String review, String status, String created_at, String updated_at, String name, String user_image) {
        this.id = id;
        this.store_id = store_id;
        this.user_id = user_id;
        this.rating = rating;
        this.review = review;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.name = name;
        this.user_image = user_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }
}
