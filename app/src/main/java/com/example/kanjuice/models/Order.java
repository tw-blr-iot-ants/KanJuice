package com.example.kanjuice.models;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Order {
    public String employeeId;
    public String region;
    public List<Drink> drinks = new ArrayList<>();
    public String employeeName;
    public Boolean isSwipe;

    public static class Drink {
        public String name;
        public boolean isSugarless;
        public int quantity;
        public boolean isFruit;
        public String type;
    }

    public Drink newDrink(String name, boolean isSugarless, int quantity, boolean isFruit, String type) {
        Drink drink = new Drink();
        drink.name = name;
        drink.isSugarless = isSugarless;
        drink.quantity = quantity;
        drink.isFruit = isFruit;
        drink.type = type;
        return drink;
    }

    public void addDrink(String name, boolean isSugarless, int quantity, boolean isFruit, String type) {
        drinks.add(newDrink(name, isSugarless, quantity, isFruit, type));
    }

    public String asJson() {
        return new Gson().toJson(this);
    }

    public String toString() {
        return String.format("Order[ empid: %s, length: %s]", this.employeeId, this.drinks.size());
    }
}
