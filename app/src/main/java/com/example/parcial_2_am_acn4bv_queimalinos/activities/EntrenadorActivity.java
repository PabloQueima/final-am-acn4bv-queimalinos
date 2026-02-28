package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.example.parcial_2_am_acn4bv_queimalinos.adapters.SesionesAdapter;
import com.example.parcial_2_am_acn4bv_queimalinos.models.Sesion;
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

    private List<Sesion> sesiones = new ArrayList<>();
    private List<Sesion> sesionesFiltradas = new ArrayList<>();

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
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        adapter = new SesionesAdapter(this, sesionesFiltradas, this::eliminarSesion);
        recyclerSesiones.setLayoutManager(new LinearLayoutManager(this));
        recyclerSesiones.setAdapter(adapter);

        btnCrear.setOnClickListener(v ->
                startActivity(new Intent(this, EditarSesionActivity.class))
        );

        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        inputBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarSesiones(s.toString().trim());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarSesiones();
    }

    private void cargarSesiones() {

        String uid = auth.getCurrentUser().getUid();

        db.collection("sesiones")
                .whereEqualTo("entrenadorUid", uid)
                .get()
                .addOnSuccessListener(q -> {

                    sesiones.clear();

                    for (DocumentSnapshot doc : q.getDocuments()) {
                        Sesion sesion = doc.toObject(Sesion.class);
                        if (sesion != null) {
                            sesion.setId(doc.getId());
                            sesiones.add(sesion);
                        }
                    }

                    filtrarSesiones(inputBuscar.getText().toString().trim());
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE_ERROR", e.getMessage());
                    Toast.makeText(this, "Error al cargar sesiones", Toast.LENGTH_LONG).show();
                });
    }

    private void filtrarSesiones(String texto) {

        sesionesFiltradas.clear();

        if (texto.isEmpty()) {
            sesionesFiltradas.addAll(sesiones);
        } else {
            for (Sesion s : sesiones) {
                if (s.getTitulo() != null &&
                        s.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                    sesionesFiltradas.add(s);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void eliminarSesion(String id) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar sesión")
                .setMessage("¿Confirmar eliminación?")
                .setPositiveButton("Sí", (d, w) ->
                        db.collection("sesiones").document(id).delete()
                                .addOnSuccessListener(a -> cargarSesiones())
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                )
                )
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSesiones();
    }
}
