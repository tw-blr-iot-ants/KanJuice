package com.example.kanjuice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

public class JuiceDecorator {

    private static HashMap<String, Integer> imageIdMap;
     private static HashMap<String, HashMap> regionTextMap;
    private static HashMap<String, Integer> kanTextMap;
    private static HashMap<String, Integer> tamTextMap;

   static {
        regionTextMap = new HashMap<>();
        regionTextMap.put("bangalore", kanTextMap);
        regionTextMap.put("chennai", tamTextMap);

        imageIdMap = new HashMap<>();
        imageIdMap.put("amla", R.drawable.amla);
        imageIdMap.put("black jeera masala soda", R.drawable.black_jeera_masala_soda);
        imageIdMap.put("cucumber", R.drawable.cucumber);
        imageIdMap.put("ginger lime", R.drawable.ginger_lime);
        imageIdMap.put("ginger mint lime", R.drawable.ginger_mint_lime);
        imageIdMap.put("grapes", R.drawable.grapes);
        imageIdMap.put("grape", R.drawable.grapes);
        imageIdMap.put("jal jeera", R.drawable.jal_jeera);
        imageIdMap.put("jal jeera soda", R.drawable.jal_jeera);
        imageIdMap.put("kokum", R.drawable.kokum);
        imageIdMap.put("lime", R.drawable.lime);
        imageIdMap.put("mint lime", R.drawable.mint_lime);
        imageIdMap.put("mixed fruit", R.drawable.mixed_fruit);
        imageIdMap.put("muskmelon", R.drawable.muskmelon);
        imageIdMap.put("musk melon", R.drawable.muskmelon);
        imageIdMap.put("orange", R.drawable.orange);
        imageIdMap.put("pineapple", R.drawable.pineapple);
        imageIdMap.put("pine apple", R.drawable.pineapple);
        imageIdMap.put("salt lime soda", R.drawable.salt_lime_soda);
        imageIdMap.put("soda", R.drawable.soda);
        imageIdMap.put("sweet and salt lime soda", R.drawable.sweet_and_salt_lime_soda);
        imageIdMap.put("sweet & salt lime soda", R.drawable.sweet_and_salt_lime_soda);
        imageIdMap.put("sweet lime soda", R.drawable.sweet_lime_soda);
        imageIdMap.put("watermelon", R.drawable.watermelon);
        imageIdMap.put("water melon", R.drawable.watermelon);
        imageIdMap.put("apple", R.drawable.apple);
        imageIdMap.put("custard apple", R.drawable.custard_apple);
        imageIdMap.put("custardapple", R.drawable.custard_apple);
        imageIdMap.put("butter fruit", R.drawable.butter_fruit);
        imageIdMap.put("butterfruit", R.drawable.butter_fruit);
        imageIdMap.put("banana", R.drawable.banana);
        imageIdMap.put("banana shake", R.drawable.banana);
        imageIdMap.put("mango", R.drawable.mango);
        imageIdMap.put("papaya", R.drawable.papaya);
        imageIdMap.put("sapota", R.drawable.sapota);
        imageIdMap.put("mosambi", R.drawable.mosambi);
        imageIdMap.put("register user", R.drawable.register_user);

        kanTextMap = new HashMap<>();
        kanTextMap.put("amla", R.string.amla_kan);
        kanTextMap.put("black jeera masala soda", R.string.black_jeera_masala_soda_kan);
        kanTextMap.put("cucumber", R.string.cucumber_kan);
        kanTextMap.put("ginger lime", R.string.ginger_lime_kan);
        kanTextMap.put("ginger mint lime", R.string.ginger_mint_lime_kan);
        kanTextMap.put("grapes", R.string.grapes_kan);
        kanTextMap.put("grape", R.string.grapes_kan);
        kanTextMap.put("jal jeera", R.string.jal_jeera_kan);
        kanTextMap.put("jal jeera soda", R.string.jal_jeera_kan);
        kanTextMap.put("kokum", R.string.kokum_kan);
        kanTextMap.put("lime", R.string.lime_kan);
        kanTextMap.put("mint lime", R.string.mint_lime_kan);
        kanTextMap.put("mixed fruit", R.string.mixed_fruit_kan);
        kanTextMap.put("mix fruit", R.string.mixed_fruit_kan);
        kanTextMap.put("muskmelon", R.string.muskmelon_kan);
        kanTextMap.put("musk melon", R.string.muskmelon_kan);
        kanTextMap.put("orange", R.string.orange_kan);
        kanTextMap.put("pineapple", R.string.pineapple_kan);
        kanTextMap.put("salt lime soda", R.string.salt_lime_soda_kan);
        kanTextMap.put("soda", R.string.soda_kan);
        kanTextMap.put("sweet and salt lime soda", R.string.sweet_and_salt_lime_soda_kan);
        kanTextMap.put("sweet & salt lime soda", R.string.sweet_and_salt_lime_soda_kan);
        kanTextMap.put("sweet lime soda", R.string.sweet_lime_soda_kan);
        kanTextMap.put("watermelon", R.string.watermelon_kan);
        kanTextMap.put("water melon", R.string.watermelon_kan);
        kanTextMap.put("banana", R.string.banana_kan);
        kanTextMap.put("banana shake", R.string.banana_kan);
        kanTextMap.put("butter fruit", R.string.butter_fruit_kan);
        kanTextMap.put("butterfruit", R.string.butter_fruit_kan);
        kanTextMap.put("sapota", R.string.sapota_kan);
        kanTextMap.put("apple", R.string.apple_kan);
        kanTextMap.put("mosambi", R.string.moosambi_kan);
        kanTextMap.put("grapes", R.string.grapes_kan);
        kanTextMap.put("grape", R.string.grapes_kan);
        kanTextMap.put("mango", R.string.mango_kan);
        kanTextMap.put("custard apple", R.string.custard_apple);
        kanTextMap.put("custardapple", R.string.custard_apple);
        kanTextMap.put("papaya", R.string.papaya);

        tamTextMap = new HashMap<>();
        tamTextMap.put("amla", R.string.amla_tam);
        tamTextMap.put("black jeera masala soda", R.string.black_jeera_masala_soda_tam);
        tamTextMap.put("cucumber", R.string.cucumber_tam);
        tamTextMap.put("ginger lime", R.string.ginger_lime_tam);
        tamTextMap.put("ginger mint lime", R.string.ginger_mint_lime_tam);
        tamTextMap.put("grapes", R.string.grapes_tam);
        tamTextMap.put("grape", R.string.grapes_tam);
        tamTextMap.put("jal jeera", R.string.jal_jeera_tam);
        tamTextMap.put("jal jeera soda", R.string.jal_jeera_tam);
        tamTextMap.put("kokum", R.string.kokum_tam);
        tamTextMap.put("lime", R.string.lime_tam);
        tamTextMap.put("mint lime", R.string.mint_lime_tam);
        tamTextMap.put("mixed fruit", R.string.mixed_fruit_tam);
        tamTextMap.put("mix fruit", R.string.mixed_fruit_tam);
        tamTextMap.put("muskmelon", R.string.muskmelon_tam);
        tamTextMap.put("musk melon", R.string.muskmelon_tam);
        tamTextMap.put("orange", R.string.orange_tam);
        tamTextMap.put("pineapple", R.string.pineapple_tam);
        tamTextMap.put("salt lime soda", R.string.salt_lime_soda_tam);
        tamTextMap.put("soda", R.string.soda_tam);
        tamTextMap.put("sweet and salt lime soda", R.string.sweet_and_salt_lime_soda_tam);
        tamTextMap.put("sweet & salt lime soda", R.string.sweet_and_salt_lime_soda_tam);
        tamTextMap.put("sweet lime soda", R.string.sweet_lime_soda_tam);
        tamTextMap.put("watermelon", R.string.watermelon_tam);
        tamTextMap.put("water melon", R.string.watermelon_tam);
        tamTextMap.put("banana", R.string.banana_tam);
        tamTextMap.put("banana shake", R.string.banana_tam);
        tamTextMap.put("butter fruit", R.string.butter_fruit_tam);
        tamTextMap.put("butterfruit", R.string.butter_fruit_tam);
        tamTextMap.put("sapota", R.string.sapota_tam);
        tamTextMap.put("apple", R.string.apple_tam);
        tamTextMap.put("mosambi", R.string.moosambi_tam);
        tamTextMap.put("grapes", R.string.grapes_tam);
        tamTextMap.put("grape", R.string.grapes_tam);
        tamTextMap.put("mango", R.string.mango_tam);
        tamTextMap.put("custard apple", R.string.custard_apple_tam);
        tamTextMap.put("custardapple", R.string.custard_apple_tam);
        tamTextMap.put("papaya", R.string.papaya_tam);

    }

    public static int matchImage(String name) {
        Integer id = imageIdMap.get(name.toLowerCase().trim());
        if (id == null) {
            id = R.drawable.mixed_fruit;
        }
        return id;
    }

    public static int matchLocalName(String name, String region) {
        Integer id = (Integer) (regionTextMap.get(region)).get(name.toLowerCase().trim());
        if (id == null) {
            id = R.string.unknown;
        }
        return id;
    }
}
