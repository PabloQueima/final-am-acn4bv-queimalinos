package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passInput;
    private Button loginBtn, goRegisterBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
        loginBtn = findViewById(R.id.loginBtn);
        goRegisterBtn = findViewById(R.id.goRegisterBtn);

        loginBtn.setOnClickListener(v -> login());
        goRegisterBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void login() {

        String email = emailInput.getText().toString().trim();
        String pass = passInput.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completar email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}