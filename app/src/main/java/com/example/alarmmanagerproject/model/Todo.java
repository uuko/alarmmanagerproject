package com.example.alarmmanagerproject.model;

public class Todo {
    String title, date, desc, key,clock;
    Integer priority;



    private int position = 0;
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
    public Todo(){}
    public Todo(String title, String date, String desc, String key, String clock, Integer priority) {
        this.title = title;
        this.date = date;
        this.desc = desc;
        this.key = key;
        this.clock = clock;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

}
