package com.example.kanjuice.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kanjuice.R;
import com.example.kanjuice.models.Juice;
import com.example.kanjuice.models.JuiceItem;

import java.util.ArrayList;
import java.util.List;

public class FruitAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private Context context;
    private ArrayList<JuiceItem> fruitItems;

    public FruitAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fruitItems = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return fruitItems.size();
    }

    @Override
    public JuiceItem getItem(int i) {
        return fruitItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View juiceItemView = view != null ? view : View.inflate(context, R.layout.fruit_item, null);
        ((TextView) juiceItemView.findViewById(R.id.juice_name)).setText(getItem(i).juiceName);
        ((ImageView) juiceItemView.findViewById(R.id.juice_image)).setImageResource(getItem(i).imageResId);
        return juiceItemView;
    }

    public void addAll(List<Juice> fruits) {

        fruitItems = new ArrayList<>();

        for(Juice juice : fruits) {
            if(juice.available) {
                fruitItems.add(new JuiceItem(juice.name, juice.imageId, juice.kanId, false));
            }
        }

        notifyDataSetChanged();
    }
}