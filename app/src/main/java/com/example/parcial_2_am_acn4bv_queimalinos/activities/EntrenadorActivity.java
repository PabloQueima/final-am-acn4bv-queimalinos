package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.example.parcial_2_am_acn4bv_queimalinos.adapters.SesionesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class EntrenadorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private RecyclerView recyclerSesiones;
    private SesionesAdapter adapter;
    private EditText inputBuscar;

    private List<DocumentSnapshot> sesiones = new ArrayList<>();
    private String filtroActual = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenador);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerSesiones = findViewById(R.id.recyclerSesiones);
        inputBuscar = findViewById(R.id.inputBuscarSesion);

        Button btnCrear = findViewById(R.id.btnCrearSesion);
        Button btnBuscar = findViewById(R.id.btnBuscarSesion);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        adapter = new SesionesAdapter(this, sesiones, this::eliminarSesion);
        recyclerSesiones.setLayoutManager(new LinearLayoutManager(this));
        recyclerSesiones.setAdapter(adapter);

        btnCrear.setOnClickListener(v ->
                startActivity(new Intent(this, EditarSesionActivity.class))
        );

        btnBuscar.setOnClickListener(v -> {
            filtroActual = inputBuscar.getText().toString().trim();
            cargarSesiones();
        });

        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        cargarSesiones();
    }

    private void cargarSesiones() {

        String uid = auth.getCurrentUser().getUid();

        Query query = db.collection("sesiones")
                .whereEqualTo("entrenadorUid", uid);

        if (!filtroActual.isEmpty()) {
            query = query
                    .whereGreaterThanOrEqualTo("titulo", filtroActual)
                    .whereLessThanOrEqualTo("titulo", filtroActual + "\uf8ff");
        }

        query.get()
                .addOnSuccessListener(q -> {

                    sesiones.clear();
                    sesiones.addAll(q.getDocuments());
                    adapter.notifyDataSetChanged();

                    if (q.isEmpty()) {
                        Toast.makeText(this, "No hay sesiones", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE_ERROR", e.getMessage());
                    Toast.makeText(this, "Error al cargar sesiones", Toast.LENGTH_LONG).show();
                });
    }

    private void eliminarSesion(String id) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar sesión")
                .setMessage("¿Confirmar eliminación?")
                .setPositiveButton("Sí", (d, w) -> {

                    db.collection("sesiones").document(id).delete()
                            .addOnSuccessListener(a -> cargarSesiones())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            );

                })
                .setNegativeButton("No", null)
                .show();
    }
}