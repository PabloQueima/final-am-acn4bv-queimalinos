package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nombreInput, emailInput, passInput;
    private Button registerBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nombreInput = findViewById(R.id.nombreInput);
        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(v -> register());
    }

    private void register() {

        String nombre = nombreInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pass = passInput.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {

                    String uid = result.getUser().getUid();

                    Map<String, Object> datos = new HashMap<>();
                    datos.put("email", email);
                    datos.put("nombre", nombre);
                    datos.put("rol", "cliente");
                    datos.put("createdAt", System.currentTimeMillis());

                    db.collection("usuarios")
                            .document(uid)
                            .set(datos)
                            .addOnSuccessListener(v -> {
                                startActivity(new Intent(this, SplashActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}