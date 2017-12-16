package com.example.application.unit;

public class Store {
    String name,prodName,landmark,storeImgUrl,ownImgUrl;
    double lati,longi;
    int timer;

    public Store(String name, String prodName, String landmark, double lati, double longi, int timer,String storeImgUrl,String ownImgUrl) {
        this.name = name;
        this.prodName = prodName;
        this.landmark = landmark;
        this.lati = lati;
        this.longi = longi;
        this.timer = timer;
        this.storeImgUrl = storeImgUrl;
        this.ownImgUrl = ownImgUrl;
    }

    public Store() {
    }

    public String getStoreImgUrl() {
        return storeImgUrl;
    }

    public void setStoreImgUrl(String storeImgUrl) {
        this.storeImgUrl = storeImgUrl;
    }

    public String getOwnImgUrl() {
        return ownImgUrl;
    }

    public void setOwnImgUrl(String ownImgUrl) {
        this.ownImgUrl = ownImgUrl;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
