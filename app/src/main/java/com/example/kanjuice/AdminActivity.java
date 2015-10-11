package com.example.kanjuice;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;


public class AdminActivity extends Activity {


    private static final String TAG = "Admin";
    private ListAvailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);

        setupViews();
        fetchMenu();
    }
    private KanJuiceApp getApp() {
        return (KanJuiceApp) getApplication();
    }

    private JuiceServer getJuiceServer() {
        return getApp().getJuiceServer();
    }

    private void setupViews() {
        ListView list = (ListView) findViewById(R.id.list);
        List<Juice> juices = new ArrayList<>();
        adapter = new ListAvailAdapter(this, juices);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Juice juice = (Juice) view.getTag();
                if (juice != null) {
                    juice.available = !juice.available;
                    setJuiceAvailability(juice);
                }
            }
        });

    }

    private void setJuiceAvailability(final Juice juice) {
        getJuiceServer().updateJuice("id", new TypedString(juice.name), new Callback<Response>() {

            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void fetchMenu() {
        getJuiceServer().getJuices(new Callback<List<Juice>>() {
            @Override
            public void success(final List<Juice> juices, Response response) {
                AdminActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addAll(juices);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to fetch menu list : " + error);


            }
        });
    }
    public static class ListAvailAdapter extends BaseAdapter {

        private final List<Juice> juices;
        private LayoutInflater inflater;

        public ListAvailAdapter(Context context, List<Juice> juices) {
            this.juices = juices;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView == null ? inflater.inflate(R.layout.juice_avail_item, parent, false) : convertView;
            Juice juice = (Juice) getItem(position);

            TextView titleView = (TextView) view.findViewById(R.id.title);
            titleView.setText(juice.name);

            TextView kanTitleView = (TextView) view.findViewById(R.id.title_kan);
            kanTitleView.setText(JuiceDecorator.matchKannadaName(juice.name));

            CheckBox availabilityView = (CheckBox) view.findViewById(R.id.availability);
            availabilityView.setChecked(juice.available);

            view.setTag(juice);
            return view;

        }

        public void addAll(List<Juice> juices) {
            this.juices.addAll(juices);
            notifyDataSetChanged();
        }
    }
}
