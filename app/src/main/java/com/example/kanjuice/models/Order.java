package com.example.kanjuice.models;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.mime.TypedString;

public class Order {
    public String employeeId;
    public List<Drink> drinks = new ArrayList<>();
    public String employeeName;
    public Boolean isSwipe;

    public static class Drink {
        public String name;
        public int quantity;
    }

    public Drink newDrink(String name , int quantity) {
        Drink drink = new Drink();
        drink.name = name;
        drink.quantity = quantity;
        return drink;
    }

    public void addDrink(String name, int quantity) {
        drinks.add(newDrink(name, quantity));
    }

    public String asJson() {
        return new Gson().toJson(this);
    }

    public String toString() {
        return String.format("Order[ empid: %s, length: %s]", this.employeeId, this.drinks.size());
    }
}
