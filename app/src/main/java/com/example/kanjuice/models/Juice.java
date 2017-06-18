package com.example.kanjuice.models;

import com.google.gson.Gson;

public class Juice {
    public String name;
    public boolean isSugarless;
    public boolean available;
    public int imageId;
    public int kanId;
    public String isFruit;
    public String type;

    public String asJson() {
        return new Gson().toJson(this);
    }
}
