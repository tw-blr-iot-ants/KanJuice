package com.example.kanjuice.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kanjuice.R;
import com.example.kanjuice.models.Juice;
import com.example.kanjuice.models.JuiceItem;
import com.example.kanjuice.utils.JuiceDecorator;

import java.util.ArrayList;
import java.util.List;

public class JuiceAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "JuiceAdapter";
    public static final int ANIMATION_DURATION = 500;
    private ArrayList<JuiceItem> juiceItems;
    private final LayoutInflater inflater;
    Context context;

    private int[] quantityNumbers = {R.id.one, R.id.two, R.id.three, R.id.sugarlessCheckbox};

    public JuiceAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        juiceItems = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return juiceItems.size();
    }

    @Override
    public Object getItem(int position) {
        return juiceItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? newView(parent) : convertView;
        bind(view, juiceItems.get(position));
        return view;
    }

    private void bind(View view, final JuiceItem juiceItem) {
        final ViewHolder h = (ViewHolder) view.getTag();

        if (juiceItem.animate) {
            showContentWithAnimation(h, juiceItem);
        } else {
            showContent(h, juiceItem);
        }

        if (juiceItem.isMultiSelected) {
            h.multiSelect.titleView.setText(juiceItem.juiceName);
            if (juiceItem.isSugarless) {
                h.multiSelect.sugarlessCheckbox.setChecked(true);
            }
            for (View v : h.multiSelect.quantityViews) {
                v.setSelected(false);
                v.setTag(juiceItem);
            }
            h.multiSelect.quantityViews.get(juiceItem.selectedQuantity - 1).setSelected(true);
        } else {
            h.singleSelect.titleView.setText(juiceItem.juiceName);
            h.singleSelect.titleInKanView.setText(juiceItem.kanResId);
            h.singleSelect.imageView.setImageResource(juiceItem.imageResId);
        }
    }

    private void showContentWithAnimation(final ViewHolder h, final JuiceItem juiceItem) {
        if (h.multiSelectView.getVisibility() == View.INVISIBLE && juiceItem.isMultiSelected) {
            h.multiSelectView.setVisibility(View.VISIBLE);
            h.multiSelect.sugarlessCheckbox.setChecked(false);
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
                    juiceItem.animate = false;
                }
            });
            anim1.start();

        } else if (h.multiSelectView.getVisibility() == View.VISIBLE && !juiceItem.isMultiSelected) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(h.multiSelectView, "translationY", -0f, 500f);
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
                    juiceItem.animate = false;
                }
            });
            anim1.start();
        }
    }

    private void showContent(ViewHolder h, JuiceItem juiceItem) {
        h.multiSelectView.setTranslationY(0f);
        h.multiSelectView.setTranslationY(0f);
        h.singleItemView.setTranslationY(0f);
        h.singleItemView.setTranslationY(0f);

        h.multiSelectView.setVisibility(juiceItem.isMultiSelected ? View.VISIBLE : View.INVISIBLE);
        h.singleItemView.setVisibility(juiceItem.isMultiSelected ? View.INVISIBLE : View.VISIBLE);
    }

    private View newView(ViewGroup parent) {
        View juiceItemView = inflater.inflate(R.layout.juice_item, parent, false);

        ViewHolder h = new ViewHolder();
        h.singleItemView = (LinearLayout) juiceItemView.findViewById(R.id.single_select_layout);
        h.multiSelectView = (LinearLayout) juiceItemView.findViewById(R.id.multi_select_layout);
        h.multiSelect.titleView = (TextView) juiceItemView.findViewById(R.id.multi_select_title);
        h.multiSelect.sugarlessCheckbox = (CheckBox) juiceItemView.findViewById(R.id.sugarlessCheckbox);
        h.singleSelect.titleView = (TextView) juiceItemView.findViewById(R.id.single_select_title);
        h.singleSelect.titleInKanView = (TextView) juiceItemView.findViewById(R.id.single_select_title_in_kan);
        h.singleSelect.imageView = (ImageView) juiceItemView.findViewById(R.id.image);

        h.multiSelect.sugarlessCheckbox.setChecked(false);


        List<View> quantityViews = new ArrayList<>();
        for (int id : quantityNumbers) {
            quantityViews.add(juiceItemView.findViewById(id));
        }
        h.multiSelect.quantityViews = quantityViews;

        for (View view : quantityViews) {
            view.setOnClickListener(this);
        }

        juiceItemView.setTag(h);
        return juiceItemView;
    }


    @Override
    public void onClick(View view) {
        final JuiceItem selectedJuiceItem = (JuiceItem) view.getTag();
        if (view.getId() == R.id.sugarlessCheckbox) {
            selectedJuiceItem.isSugarless = !((JuiceItem) view.getTag()).isSugarless;
            Toast.makeText(context, "You selected "+ (selectedJuiceItem.isSugarless ? "without sugar" : "with sugar"), Toast.LENGTH_SHORT).show();
            Log.d(TAG, " is sugarless : " + selectedJuiceItem.isSugarless);
        } else {
            Log.d(TAG, "clicked on juice : " + selectedJuiceItem.juiceName
                    + " qty: " + Integer.parseInt(((TextView) view).getText().toString()) + " is Sugarless : " + selectedJuiceItem.isSugarless);
            selectedJuiceItem.selectedQuantity = Integer.parseInt(((TextView) view).getText().toString());
        }
        notifyDataSetChanged();
    }

    public void toggleSelectionChoice(int position) {
        juiceItems.get(position).isMultiSelected = !juiceItems.get(position).isMultiSelected;
        juiceItems.get(position).animate = true;
        notifyDataSetChanged();
    }

    public JuiceItem[] getSelectedJuices() {
        List<JuiceItem> selectedJuiceItems = new ArrayList<>();
        for (JuiceItem item : juiceItems) {
            if (item.isMultiSelected) {
                selectedJuiceItems.add(item);
            }
        }

        JuiceItem[] selectedJuicesArray = new JuiceItem[selectedJuiceItems.size()];
        selectedJuiceItems.toArray(selectedJuicesArray);
        return selectedJuicesArray;
    }

    public void reset() {
        for (JuiceItem juiceItem : juiceItems) {
            juiceItem.animate = juiceItem.isMultiSelected;
            juiceItem.isMultiSelected = false;
            juiceItem.selectedQuantity = 1;
            juiceItem.isSugarless = false;
        }
        notifyDataSetChanged();
    }

    public void addAll(List<Juice> juices) {
        juiceItems = new ArrayList<>();

        for (Juice juice : juices) {
            if (juice.available) {
                juiceItems.add(new JuiceItem(juice.name, juice.imageId, juice.kanId, juice.isSugarless, false));
            }
        }
        addFruitSection();
        addRegisterOption();

        notifyDataSetChanged();
    }

    private void addRegisterOption() {
        String registerUser = "Register User";
        juiceItems.add(new JuiceItem(registerUser, JuiceDecorator.matchImage(registerUser),
                                     JuiceDecorator.matchKannadaName(registerUser),false, false));
    }

    private void addFruitSection() {
        String fruits = "Fruits";
        juiceItems.add(new JuiceItem(fruits, JuiceDecorator.matchImage(fruits),
                JuiceDecorator.matchKannadaName(fruits),false, false));
    }


    public static class ViewHolder {
        public LinearLayout singleItemView;
        public LinearLayout multiSelectView;

        public SingleSelect singleSelect = new SingleSelect();
        public MultiSelect multiSelect = new MultiSelect();


        private class MultiSelect {
            public TextView titleView;
            public List<View> quantityViews;
            public CheckBox sugarlessCheckbox;
        }

        public class SingleSelect {
            public TextView titleView;
            public TextView titleInKanView;
            public ImageView imageView;
        }
    }

}
