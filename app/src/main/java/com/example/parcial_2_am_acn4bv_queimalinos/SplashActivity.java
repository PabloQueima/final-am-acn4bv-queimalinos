package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        auth.signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        return;
                    }

                    String rol = doc.getString("rol");
                    Intent intent;

                    if ("admin".equals(rol)) {
                        intent = new Intent(this, AdminActivity.class);
                    } else if ("entrenador".equals(rol)) {
                        intent = new Intent(this, EntrenadorActivity.class);
                    } else {
                        intent = new Intent(this, ClienteActivity.class);
                    }

                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    auth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
    }
}