package com.example.kanjuice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;


public class JuiceMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_juice_menu);

        setupViews();
    }

    private void setupViews() {
        final GridView juicesView = (GridView) findViewById(R.id.grid);
        final JuiceAdapter adapter = new JuiceAdapter(this);
        juicesView.setAdapter(adapter);

        juicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(JuiceMenuActivity.this, UserInputActivity.class);
                intent.putExtra("juice_name", adapter.getItem(position).toString());
                startActivity(intent);
            }
        });
    }

    private static class JuiceAdapter extends BaseAdapter {

        private Context context;
        private final LayoutInflater inflater;

        public JuiceAdapter(Context context) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 25;
        }

        @Override
        public Object getItem(int position) {
            return "Mango";
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView == null ? inflater.inflate(R.layout.juice_item, parent, false)
                    : convertView;
            return view;
        }
    }
}
