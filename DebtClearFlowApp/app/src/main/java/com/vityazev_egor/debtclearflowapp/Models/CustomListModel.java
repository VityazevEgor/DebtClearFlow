package com.vityazev_egor.debtclearflowapp.Models;

public class CustomListModel {
    private Integer id;
    private String name;
    private String time;
    private String receptionRoom;

    public CustomListModel(Integer id, String name, String time, String receptionRoom){
        this.id = id;
        this.name = name;
        this.time = time;
        this.receptionRoom = receptionRoom;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceptionRoom() {
        return receptionRoom;
    }

    public void setReceptionRoom(String receptionRoom) {
        this.receptionRoom = receptionRoom;
    }
}
