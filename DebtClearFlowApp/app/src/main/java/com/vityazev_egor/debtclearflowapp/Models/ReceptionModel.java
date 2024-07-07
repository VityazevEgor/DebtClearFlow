package com.vityazev_egor.debtclearflowapp.Models;

public class ReceptionModel {
    public Integer id;
    public String name;
    public String time;

    public ReceptionModel(Integer id, String name, String time){
        this.id = id;
        this.name = name;
        this.time = time;
    }
}
