package com.huce.qlvdcn.model;

import java.io.Serializable;

public class Item implements Serializable {
    private String id, title, position, time, description, image, catagory, quantity;

    public Item() {
    }

    public Item(String id, String title, String position, String time, String description, String image, String catagory, String quantity) {
        this.id = id;
        this.title = title;
        this.position = position;
        this.time = time;
        this.description = description;
        this.image = image;
        this.catagory = catagory;
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
