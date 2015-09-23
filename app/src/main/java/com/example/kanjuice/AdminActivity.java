package com.example.kanjuice;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class AdminActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);

        setupViews();
    }

    private void setupViews() {

        ListView list = (ListView) findViewById(R.id.list);
        String[] juices = {"Mongo", "Lime", "Amla", "Grape", "Apple"};
        list.setAdapter(new ListAvailAdapter(this, juices));

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminActivity.this.finish();
            }
        });
    }

    public static class ListAvailAdapter extends BaseAdapter {

        private final String[] juices;
        private LayoutInflater inflater;

        public ListAvailAdapter(Context context, String[] juices) {
            this.juices = juices;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return juices.length;
        }

        @Override
        public Object getItem(int position) {
            return juices[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView == null ? inflater.inflate(R.layout.juice_avail_item, parent, false) : convertView;
            TextView titleView = (TextView) view.findViewById(R.id.title);
            titleView.setText(getItem(position).toString());
            return view;

        }
    }
}
