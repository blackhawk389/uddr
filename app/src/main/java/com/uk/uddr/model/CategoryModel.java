package com.uk.uddr.model;

public class CategoryModel {

    String  cat_id;

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_title() {
        return cat_title;
    }

    public void setCat_title(String cat_title) {
        this.cat_title = cat_title;
    }

    public String getCat_image() {
        return cat_image;
    }

    public void setCat_image(String cat_image) {
        this.cat_image = cat_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CategoryModel(String cat_id, String cat_title, String cat_image, String status) {
        this.cat_id = cat_id;
        this.cat_title = cat_title;
        this.cat_image = cat_image;
        this.status = status;
    }

    String cat_title;
    String cat_image;
    String status;


}
