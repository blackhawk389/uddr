package com.uk.uddr.model;

public class AddressModel {
    String  address_id,user_id,name,address,state,city,postcode,phone,lable;

    public AddressModel(String address_id, String user_id, String name, String address, String state, String city, String postcode, String phone, String lable) {
        this.address_id = address_id;
        this.user_id = user_id;
        this.name = name;
        this.address = address;
        this.state = state;
        this.city = city;
        this.postcode = postcode;
        this.phone = phone;
        this.lable = lable;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }
}
