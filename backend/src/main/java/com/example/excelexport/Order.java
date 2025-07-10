package com.example.excelexport;

import java.util.Date;

public class Order {
    private int id;
    private String item;
    private Date date;

    public Order(int id, String item, Date date) {
        this.id = id;
        this.item = item;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public Date getDate() {
        return date;
    }
}
