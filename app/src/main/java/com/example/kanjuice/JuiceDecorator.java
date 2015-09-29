package com.example.kanjuice;

import java.util.HashMap;

public class JuiceDecorator {

    private final HashMap<String, Integer> imageIdMap;

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
        imageIdMap.put("amla", R.drawable.amla);
        imageIdMap.put("sweet lime soda", R.drawable.sweet_lime_soda);
        imageIdMap.put("watermelon", R.drawable.watermelon);
    }

    public int matchImage(String name) {
        Integer id = imageIdMap.get(name.toLowerCase().trim());
        if (id == null) {
            id = R.drawable.mixed_fruit;
        }
        return id;
    }

    public int matchKannadaName(String name) {
         return R.string.watermelon_kan;
    }
}
