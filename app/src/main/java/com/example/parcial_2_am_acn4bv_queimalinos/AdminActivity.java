package com.example.parcial_2_am_acn4bv_queimalinos;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("Panel Admin en construcción");
        tv.setTextSize(20f);
        setContentView(tv);
    }
}