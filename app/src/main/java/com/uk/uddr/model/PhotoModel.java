package com.uk.uddr.model;

import android.net.Uri;

public class PhotoModel {

    String filepath;
    String imageId;

    public PhotoModel(String filepath, String imageId) {
        this.filepath = filepath;
        this.imageId = imageId;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
