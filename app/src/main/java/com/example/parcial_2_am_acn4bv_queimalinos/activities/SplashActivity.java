package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        if (auth.getCurrentUser() != null) {
            redirigirPorRol(auth.getCurrentUser().getUid());
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);

            btnLogin.setOnClickListener(v ->
                    startActivity(new Intent(this, LoginActivity.class))
            );

            btnRegister.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class))
            );
        }
    }

    private void redirigirPorRol(String uid) {
        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        auth.signOut();
                        mostrarBotones();
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
                    mostrarBotones();
                });
    }

    private void mostrarBotones() {
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}