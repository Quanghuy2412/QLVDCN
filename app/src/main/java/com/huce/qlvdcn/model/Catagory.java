package com.huce.qlvdcn.model;

import java.io.Serializable;

public class Catagory implements Serializable {
    private String name;
    private String pic;

    public Catagory() {
    }

    public Catagory(String name) {
        this.name = name;
    }

    public Catagory(String name, String pic) {
        this.name = name;
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
