package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        final String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
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
                    } else if ("cliente".equals(rol)) {
                        intent = new Intent(this, ClienteActivity.class);
                    } else {
                        auth.signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        return;
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