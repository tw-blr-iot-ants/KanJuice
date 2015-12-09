package com.example.kanjuice.models;

import com.google.gson.Gson;

public class User {
    public String empId;
    public String externalNumber;
    public String internalNumber;
    public String employeeName;

    @Override
    public String toString() {
        return String.format("user[%s:%s:%s:%s]", empId, externalNumber, internalNumber, employeeName);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
