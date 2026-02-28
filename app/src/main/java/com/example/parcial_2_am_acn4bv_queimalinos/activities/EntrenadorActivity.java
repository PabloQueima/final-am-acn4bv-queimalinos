package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
    private DocumentSnapshot lastVisible = null;
    private boolean isLoading = false;

    private List<DocumentSnapshot> sesiones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenador);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerSesiones = findViewById(R.id.recyclerSesiones);
        inputBuscar = findViewById(R.id.inputBuscarSesion);
        Button btnCrear = findViewById(R.id.btnCrearSesion);
        Button btnBuscar = findViewById(R.id.btnBuscarSesion);

        adapter = new SesionesAdapter(this, sesiones, this::eliminarSesion);

        recyclerSesiones.setLayoutManager(new LinearLayoutManager(this));
        recyclerSesiones.setAdapter(adapter);

        btnCrear.setOnClickListener(v ->
                startActivity(new Intent(this, EditarSesionActivity.class))
        );

        btnBuscar.setOnClickListener(v -> {
            sesiones.clear();
            lastVisible = null;
            cargarSesiones(inputBuscar.getText().toString().trim());
        });

        cargarSesiones("");
    }

    private void cargarSesiones(String filtro) {

        if (isLoading) return;
        isLoading = true;

        Query query = db.collection("sesiones")
                .whereEqualTo("entrenadorUid", auth.getCurrentUser().getUid())
                .orderBy("titulo")
                .limit(10);

        if (!filtro.isEmpty()) {
            query = query
                    .whereGreaterThanOrEqualTo("titulo", filtro)
                    .whereLessThanOrEqualTo("titulo", filtro + "\uf8ff");
        }

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnSuccessListener(q -> {

            if (!q.isEmpty()) {
                lastVisible = q.getDocuments().get(q.size() - 1);
                sesiones.addAll(q.getDocuments());
                adapter.notifyDataSetChanged();
            }

            isLoading = false;
        });
    }

    private void eliminarSesion(String id) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar sesión")
                .setMessage("¿Confirmar eliminación?")
                .setPositiveButton("Sí", (d, w) -> {
                    db.collection("sesiones").document(id).delete();
                    sesiones.clear();
                    lastVisible = null;
                    cargarSesiones("");
                })
                .setNegativeButton("No", null)
                .show();
    }
}