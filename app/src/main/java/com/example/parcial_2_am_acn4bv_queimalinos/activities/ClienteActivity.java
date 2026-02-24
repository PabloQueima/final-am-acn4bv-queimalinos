package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.parcial_2_am_acn4bv_queimalinos.models.Sesion;

public class ClienteActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private LinearLayout contenedorSesiones;
    private TextView txtSesionesCompletadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        contenedorSesiones = findViewById(R.id.contenedorSesiones);
        txtSesionesCompletadas = findViewById(R.id.txtSesionesCompletadas);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        cargarSesionesDisponibles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSesionesCompletadas();
    }

    private void cargarSesionesDisponibles() {

        contenedorSesiones.removeAllViews();

        String clienteUid = auth.getCurrentUser().getUid();

        db.collection("sesiones")
                .whereEqualTo("clienteUid", clienteUid)
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {

                        Sesion sesion = doc.toObject(Sesion.class);
                        sesion.setId(doc.getId());

                        Button btnSesion = new Button(this);
                        btnSesion.setText(sesion.getTitulo());

                        btnSesion.setOnClickListener(v -> {
                            Intent intent = new Intent(this, RealizarSesionActivity.class);
                            intent.putExtra("sesionId", sesion.getId());
                            startActivity(intent);
                        });

                        contenedorSesiones.addView(btnSesion);
                    }
                });
    }

    private void cargarSesionesCompletadas() {

        String clienteUid = auth.getCurrentUser().getUid();

        db.collection("sesionesCompletadas")
                .whereEqualTo("clienteUid", clienteUid)
                .get()
                .addOnSuccessListener(query -> {
                    int count = query.size();
                    txtSesionesCompletadas.setText("Sesiones completadas: " + count);
                });
    }
}