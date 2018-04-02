package com.example.arne.translogistics_app.Model;

/**
 * Created by Arne on 11-03-2018.
 */



import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class Package implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private int qty;
    private String company;
    @ColumnInfo(name = "ship_from")
    private String shopFrom;
    @ColumnInfo(name = "ship_to")
    private String shipTo;
    private String currier;

    public Package(){}


    public Package(String description, int qty, String company, String shopFrom, String shipTo, String currier) {
        this.description = description;
        this.qty = qty;
        this.company = company;
        this.shopFrom = shopFrom;
        this.shipTo = shipTo;
        this.currier = currier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getShopFrom() {
        return shopFrom;
    }

    public void setShopFrom(String shopFrom) {
        this.shopFrom = shopFrom;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = shipTo;
    }

    public String getCurrier() {
        return currier;
    }

    public void setCurrier(String currier) {
        this.currier = currier;
    }
}
