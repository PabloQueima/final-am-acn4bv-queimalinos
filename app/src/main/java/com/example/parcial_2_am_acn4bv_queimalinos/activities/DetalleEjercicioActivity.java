package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.*;
import com.example.parcial_2_am_acn4bv_queimalinos.models.Ejercicio;
import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.bumptech.glide.Glide;

public class DetalleEjercicioActivity extends AppCompatActivity {

    private TextView txtNombre, txtDescripcion, txtElemento, txtParteCuerpo;
    private ImageView imgEjercicio;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        db = FirebaseFirestore.getInstance();

        txtNombre = findViewById(R.id.txtTituloEjercicio);
        txtDescripcion = findViewById(R.id.txtDescripcionEjercicio);
        txtElemento = findViewById(R.id.txtElementoEjercicio);
        txtParteCuerpo = findViewById(R.id.txtParteCuerpoEjercicio);
        imgEjercicio = findViewById(R.id.imgEjercicio);

        int ejercicioId = getIntent().getIntExtra("ejercicioId", -1);

        if (ejercicioId == -1) {
            Toast.makeText(this, "Ejercicio inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarEjercicio(ejercicioId);
    }

    private void cargarEjercicio(int ejercicioId) {

        db.collection("ejercicios")
                .whereEqualTo("id", ejercicioId)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        Toast.makeText(this, "Ejercicio no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    DocumentSnapshot doc = query.getDocuments().get(0);
                    Ejercicio ej = doc.toObject(Ejercicio.class);

                    if (ej == null) {
                        Toast.makeText(this, "Error al cargar ejercicio", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    txtNombre.setText(ej.getNombre());
                    txtDescripcion.setText(ej.getDescripcion());
                    txtElemento.setText("Elemento: " + ej.getElemento());
                    txtParteCuerpo.setText("Parte del cuerpo: " + ej.getParteCuerpo());

                    if (ej.getImageUrl() != null && !ej.getImageUrl().isEmpty()) {
                        Glide.with(this)
                                .load(ej.getImageUrl())
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.logo)
                                .into(imgEjercicio);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
                );
    }
}