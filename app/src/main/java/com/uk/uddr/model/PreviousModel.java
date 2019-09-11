package com.uk.uddr.model;

public class PreviousModel {

    public String getStore_phone() {
        return store_phone;
    }

    public void setStore_phone(String store_phone) {
        this.store_phone = store_phone;
    }

    String store_phone;

    public PreviousModel(String transactionID, String store_id, String store_phone,String total, String paymentStatus, String receipts, String order_date, String order_time,String title,String type_image) {
        TransactionID = transactionID;
        this.store_id = store_id;
        Total = total;
        PaymentStatus = paymentStatus;
        this.receipts = receipts;
        this.order_date = order_date;
        this.order_time = order_time;
        this.title=title;
        this.type_image=type_image;
        this.store_phone=store_phone;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getReceipts() {
        return receipts;
    }

    public void setReceipts(String receipts) {
        this.receipts = receipts;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    String TransactionID;
    String store_id;
    String Total;
    String PaymentStatus;
    String receipts;
    String order_date;
    String order_time;
    String type_image;

    public String getType_image() {
        return type_image;
    }

    public void setType_image(String type_image) {
        this.type_image = type_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

}
