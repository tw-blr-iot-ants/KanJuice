package com.example.kanjuice;

import com.google.gson.Gson;

public class Juice {
    public String name;
    public boolean isSugarless;
    public boolean available;
    public int imageId;
    public int kanId;
    public int sugarlessImgId;

    public String asJson() {
        return new Gson().toJson(this);
    }
}
