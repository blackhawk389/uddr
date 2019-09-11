package com.uk.uddr.model;

public class HomeModel {
    String id,title,type_image,tag_line,des;

    public HomeModel(String id, String title, String tag_line, String type_image, String des) {
        this.id = id;
        this.title = title;
        this.type_image = type_image;
        this.tag_line = tag_line;
        this.des = des;
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
}
