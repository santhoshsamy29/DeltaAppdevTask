package com.example.application.unit;

public class Product {

    String prodName,color,storeName,img_url;
    int price,size,quantity;
    Long transactionId;

    public Product(String prodName, String color, String storeName, int price, int size, int quantity, Long transactionId,String img_url) {
        this.prodName = prodName;
        this.color = color;
        this.storeName = storeName;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
        this.transactionId = transactionId;
        this.img_url = img_url;
    }

    public Product() {

    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getProdName() {
        return prodName;
    }

    public String getColor() {
        return color;
    }

    public String getStoreName() {
        return storeName;
    }

    public int getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
