package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import com.example.parcial_2_am_acn4bv_queimalinos.R;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminEjerciciosActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private EditText inputNombreEjercicio, inputDescripcionEjercicio, inputElementoEjercicio, inputParteCuerpoEjercicio, inputImagenEjercicio;
    private LinearLayout contenedorEjercicios;

    private String ejercicioEditandoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ejercicios);

        db = FirebaseFirestore.getInstance();

        inputNombreEjercicio = findViewById(R.id.inputNombreEjercicio);
        inputDescripcionEjercicio = findViewById(R.id.inputDescripcionEjercicio);
        inputElementoEjercicio = findViewById(R.id.inputElementoEjercicio);
        inputParteCuerpoEjercicio = findViewById(R.id.inputParteCuerpoEjercicio);
        inputImagenEjercicio = findViewById(R.id.inputImagenEjercicio);
        contenedorEjercicios = findViewById(R.id.contenedorEjercicios);

        Button btnGuardarEjercicio = findViewById(R.id.btnGuardarEjercicio);
        Button btnVolver = findViewById(R.id.btnVolver);

        btnGuardarEjercicio.setOnClickListener(v -> guardarEjercicio());
        btnVolver.setOnClickListener(v -> finish());

        cargarEjercicios();
    }

    private void guardarEjercicio() {

        String nombre = inputNombreEjercicio.getText().toString().trim();
        String descripcion = inputDescripcionEjercicio.getText().toString().trim();
        String elemento = inputElementoEjercicio.getText().toString().trim();
        String parteCuerpo = inputParteCuerpoEjercicio.getText().toString().trim();
        String imagen = inputImagenEjercicio.getText().toString().trim();

        if (nombre.isEmpty()) return;

        Map<String, Object> ejercicio = new HashMap<>();
        ejercicio.put("nombre", nombre);
        ejercicio.put("descripcion", descripcion);
        ejercicio.put("elemento", elemento);
        ejercicio.put("parteCuerpo", parteCuerpo);
        ejercicio.put("imageUrl", imagen);

        if (ejercicioEditandoId == null) {
            db.collection("ejercicios").add(ejercicio);
        } else {
            db.collection("ejercicios").document(ejercicioEditandoId).update(ejercicio);
            ejercicioEditandoId = null;
        }

        limpiarEjercicioForm();
        cargarEjercicios();
    }

    private void cargarEjercicios() {

        contenedorEjercicios.removeAllViews();

        db.collection("ejercicios").get()
                .addOnSuccessListener(query -> {

                    for (QueryDocumentSnapshot doc : query) {

                        String id = doc.getId();
                        String nombre = doc.getString("nombre");

                        TextView tv = new TextView(this);
                        tv.setText(nombre);
                        tv.setPadding(8, 8, 8, 8);

                        tv.setOnClickListener(v -> {
                            inputNombreEjercicio.setText(nombre);
                            inputDescripcionEjercicio.setText(doc.getString("descripcion"));
                            inputElementoEjercicio.setText(doc.getString("elemento"));
                            inputParteCuerpoEjercicio.setText(doc.getString("parteCuerpo"));
                            inputImagenEjercicio.setText(doc.getString("imageUrl"));
                            ejercicioEditandoId = id;
                        });

                        tv.setOnLongClickListener(v -> {
                            db.collection("ejercicios").document(id).delete();
                            cargarEjercicios();
                            return true;
                        });

                        contenedorEjercicios.addView(tv);
                    }
                });
    }

    private void limpiarEjercicioForm() {
        inputNombreEjercicio.setText("");
        inputDescripcionEjercicio.setText("");
        inputElementoEjercicio.setText("");
        inputParteCuerpoEjercicio.setText("");
        inputImagenEjercicio.setText("");
    }
}