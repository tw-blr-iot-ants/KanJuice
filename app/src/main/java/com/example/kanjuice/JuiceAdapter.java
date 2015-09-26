package com.example.kanjuice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class JuiceAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "JuiceAdapter";
    public static final int ANIMATION_DURATION = 500;
    private final ArrayList<Juice> juices;
    private Context context;
    private final LayoutInflater inflater;

    private int[] quantityNumbers = {R.id.one, R.id.two, R.id.three, R.id.four, R.id.five};

    public JuiceAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        juices = new ArrayList<>();

        for (int i = 0; i < 30; i ++) {
            juices.add(new Juice("Water Mellon" + i, "ಕಲ್ಲಂಗಡಿ", false));
        }
    }

    @Override
    public int getCount() {
        return juices.size();
    }

    @Override
    public Object getItem(int position) {
        return juices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? newView(parent) : convertView;
        bind(view, juices.get(position));
        return view;
    }

    private void bind(View view, Juice juice) {
        final ViewHolder h = (ViewHolder) view.getTag();

        if (h.multiSelectView.getVisibility() == View.INVISIBLE && juice.isMultiSelected == true) {
            h.multiSelectView.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(h.multiSelectView, "translationY", 500f, 0f);
            anim.setDuration(ANIMATION_DURATION);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.multiSelectView.setVisibility(View.VISIBLE);
                }
            });
            anim.start();

            ObjectAnimator anim1 = ObjectAnimator.ofFloat(h.singleItemView, "translationY", 0f, -500f);
            anim1.setDuration(ANIMATION_DURATION);
            anim1.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.singleItemView.setVisibility(View.INVISIBLE);
                }
            });
            anim1.start();

        } else if (h.multiSelectView.getVisibility() == View.VISIBLE && juice.isMultiSelected == false) {

            ObjectAnimator anim = ObjectAnimator.ofFloat(h.multiSelectView, "translationY",  -0f, 500f);
            anim.setDuration(ANIMATION_DURATION);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.multiSelectView.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();

            h.singleItemView.setVisibility(View.VISIBLE);
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(h.singleItemView, "translationY", -500f, 0f);
            anim1.setDuration(ANIMATION_DURATION);
            anim1.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    h.singleItemView.setVisibility(View.VISIBLE);
                }
            });
            anim1.start();
        }

        if (juice.isMultiSelected) {
            h.multiSelect.titleView.setText(juice.juiceName);
            for (View v : h.multiSelect.quantityViews) {
                v.setSelected(false);
                v.setTag(juice);
            }
            Log.d(TAG, "selected juice " + juice.juiceName + " qnty: " + juice.selectedQuantity);
            h.multiSelect.quantityViews.get(juice.selectedQuantity - 1).setSelected(true);
        } else {
            h.singleSelect.titleView.setText(juice.juiceName);
            h.singleSelect.titleInKanView.setText(juice.juiceNameInKan);
        }

    }

    private View newView(ViewGroup parent) {
        View juiceItemView = inflater.inflate(R.layout.juice_item, parent, false);

        ViewHolder h = new ViewHolder();
        h.singleItemView = (LinearLayout) juiceItemView.findViewById(R.id.single_select_layout);
        h.multiSelectView = (LinearLayout) juiceItemView.findViewById(R.id.multi_select_layout);
        h.multiSelect.titleView = (TextView) juiceItemView.findViewById(R.id.multi_select_title);
        h.singleSelect.titleView = (TextView) juiceItemView.findViewById(R.id.single_select_title);
        h.singleSelect.titleInKanView = (TextView) juiceItemView.findViewById(R.id.single_select_title_in_kan);

        List<View> quantityViews = new ArrayList<>();
        for (int id : quantityNumbers) {
            quantityViews.add(juiceItemView.findViewById(id));
        }
        h.multiSelect.quantityViews  = quantityViews;

        for (View view : quantityViews) {
            view.setOnClickListener(this);
        }

        juiceItemView.setTag(h);
        return juiceItemView;
    }


    @Override
    public void onClick(View view) {
        Juice selectedJuice = (Juice) view.getTag();
        Log.d(TAG, "clicked on juice : " + selectedJuice.juiceName
                + " qty: " + Integer.parseInt(((TextView) view).getText().toString()));

        selectedJuice.selectedQuantity = Integer.parseInt(((TextView) view).getText().toString());
        notifyDataSetChanged();
    }

    public void toggleSelectionChoice(int position) {
        juices.get(position).isMultiSelected = !juices.get(position).isMultiSelected;
        notifyDataSetChanged();
    }

    public Juice[] getSelectedJuices() {
        List<Juice> selectedJuices = new ArrayList<>();
        for (Juice item : juices) {
            if (item.isMultiSelected) {
                selectedJuices.add(item);
            }
        }

        Juice[] selectedJuicesArray = new Juice[selectedJuices.size()];
        selectedJuices.toArray(selectedJuicesArray);
        return selectedJuicesArray;
    }

    public void reset() {
        for(Juice juice : juices) {
            juice.isMultiSelected = false;
            juice.selectedQuantity = 1;
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public LinearLayout singleItemView;
        public LinearLayout multiSelectView;

        public SingleSelect singleSelect = new SingleSelect();
        public MultiSelect multiSelect = new MultiSelect();


        private class MultiSelect {
            public TextView titleView;
            public List<View> quantityViews;
        }

        public class SingleSelect {
            public TextView titleView;
            public TextView titleInKanView;
        }
    }

}
