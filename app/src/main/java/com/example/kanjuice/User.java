package com.example.kanjuice;

public class User {
    public String empId;
    public String externalNumber;
    public String internalNumber;
    public String employeeName;

    @Override
    public String toString() {
        return String.format("user[%s:%s:%s:%s]", empId, externalNumber, internalNumber, employeeName);
    }
}
