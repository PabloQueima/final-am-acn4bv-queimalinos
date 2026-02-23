package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class ClienteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout contenedorSesiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        contenedorSesiones = findViewById(R.id.contenedorSesiones);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        cargarSesiones();
    }

    private void cargarSesiones() {

        contenedorSesiones.removeAllViews();

        db.collection("sesiones")
                .whereEqualTo("clienteUid", auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(query -> {

                    for (QueryDocumentSnapshot doc : query) {

                        String titulo = doc.getString("titulo");
                        List<String> ejercicios = (List<String>) doc.get("ejercicios");

                        TextView tv = new TextView(this);
                        tv.setText(titulo);
                        tv.setPadding(8, 8, 8, 8);

                        tv.setOnClickListener(v -> {
                            Intent intent = new Intent(this, DetalleEjercicioActivity.class);
                            intent.putStringArrayListExtra(
                                    "ejerciciosIds",
                                    new java.util.ArrayList<>(ejercicios)
                            );
                            startActivity(intent);
                        });

                        contenedorSesiones.addView(tv);
                    }
                });
    }
}