package com.example.kanjuice;

import java.util.HashMap;

public class JuiceDecorator {

    private final HashMap<String, Integer> imageIdMap;
    private final HashMap<String, Integer> kanTextMap;

    public JuiceDecorator() {
        imageIdMap = new HashMap<>();
        imageIdMap.put("amla", R.drawable.amla);
        imageIdMap.put("black jeera masala soda", R.drawable.black_jeera_masala_soda);
        imageIdMap.put("cucumber", R.drawable.cucumber);
        imageIdMap.put("ginger lime", R.drawable.ginger_lime);
        imageIdMap.put("ginger mint lime", R.drawable.ginger_mint_lime);
        imageIdMap.put("grapes", R.drawable.grapes);
        imageIdMap.put("jal jeera", R.drawable.jal_jeera);
        imageIdMap.put("kokum", R.drawable.kokum);
        imageIdMap.put("lime", R.drawable.lime);
        imageIdMap.put("mint lime", R.drawable.mint_lime);
        imageIdMap.put("mixed fruit", R.drawable.mixed_fruit);
        imageIdMap.put("muskmelon", R.drawable.muskmelon);
        imageIdMap.put("orange", R.drawable.orange);
        imageIdMap.put("pineapple", R.drawable.pineapple);
        imageIdMap.put("salt lime soda", R.drawable.salt_lime_soda);
        imageIdMap.put("soda", R.drawable.soda);
        imageIdMap.put("sweet and salt lime soda", R.drawable.sweet_and_salt_lime_soda);
        imageIdMap.put("sweet lime soda", R.drawable.sweet_lime_soda);
        imageIdMap.put("watermelon", R.drawable.watermelon);

        kanTextMap = new HashMap<>();
        kanTextMap.put("amla", R.string.amla_kan);
        kanTextMap.put("black jeera masala soda", R.string.black_jeera_masala_soda_kan);
        kanTextMap.put("cucumber", R.string.cucumber_kan);
        kanTextMap.put("ginger lime", R.string.ginger_lime_kan);
        kanTextMap.put("ginger mint lime", R.string.ginger_mint_lime_kan);
        kanTextMap.put("grapes", R.string.grapes_kan);
        kanTextMap.put("jal jeera", R.string.jal_jeera_kan);
        kanTextMap.put("kokum", R.string.kokum_kan);
        kanTextMap.put("lime", R.string.lime_kan);
        kanTextMap.put("mint lime", R.string.mint_lime_kan);
        kanTextMap.put("mixed fruit", R.string.mixed_fruit_kan);
        kanTextMap.put("muskmelon", R.string.muskmelon_kan);
        kanTextMap.put("orange", R.string.orange_kan);
        kanTextMap.put("pineapple", R.string.pineapple_kan);
        kanTextMap.put("salt lime soda", R.string.salt_lime_soda_kan);
        kanTextMap.put("soda", R.string.soda_kan);
        kanTextMap.put("sweet and salt lime soda", R.string.sweet_and_salt_lime_soda_kan);
        kanTextMap.put("sweet lime soda", R.string.sweet_lime_soda_kan);
        kanTextMap.put("watermelon", R.string.watermelon_kan);
        kanTextMap.put("banana", R.string.banana_kan);
        kanTextMap.put("butter fruit", R.string.butter_fruit_kan);
        kanTextMap.put("sapota", R.string.sapota_kan);
        kanTextMap.put("apple", R.string.apple_kan);
        kanTextMap.put("moosambi", R.string.moosambi_kan);
        kanTextMap.put("grapes", R.string.grapes_kan);
        kanTextMap.put("grape", R.string.grapes_kan);
        kanTextMap.put("mango", R.string.mango_kan);
    }

    public int matchImage(String name) {
        Integer id = imageIdMap.get(name.toLowerCase().trim());
        if (id == null) {
            id = R.drawable.mixed_fruit;
        }
        return id;
    }

    public int matchKannadaName(String name) {
        Integer id = kanTextMap.get(name.toLowerCase().trim());
        if (id == null) {
            id = R.string.unknown_kan;
        }
        return id;
    }
}
