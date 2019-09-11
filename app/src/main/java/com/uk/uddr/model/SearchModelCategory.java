package com.uk.uddr.model;

public class SearchModelCategory {

    String id,title,type_image,description,tag_line,type_order,status;

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

    public String getType_image() {
        return type_image;
    }

    public void setType_image(String type_image) {
        this.type_image = type_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag_line() {
        return tag_line;
    }

    public void setTag_line(String tag_line) {
        this.tag_line = tag_line;
    }

    public String getType_order() {
        return type_order;
    }

    public void setType_order(String type_order) {
        this.type_order = type_order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SearchModelCategory(String id, String title, String type_image, String description, String tag_line, String type_order, String status) {
        this.id = id;
        this.title = title;
        this.type_image = type_image;
        this.description = description;
        this.tag_line = tag_line;
        this.type_order = type_order;
        this.status = status;
    }
}
