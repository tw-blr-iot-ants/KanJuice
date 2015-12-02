package com.example.kanjuice;

import com.google.gson.Gson;

public class Juice {
    public String name;
    public boolean available;
    public int imageId;
    public int localLangId;

    public String asJson() {
        return new Gson().toJson(this);
    }
}
