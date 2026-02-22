package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class EntrenadorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView tv = new TextView(this);
        tv.setText("Tablero de Entrenadores en construcción");
        tv.setTextSize(20f);
        setContentView(tv);
    }
}