package com.example.kanjuice;

import java.util.ArrayList;
import java.util.List;

public class Order {
    public String employeeId;
    public List<Drink> drinks = new ArrayList<>();

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

    public void addDrink(String name, int quality) {
        drinks.add(newDrink(name, quality));
    }
}
