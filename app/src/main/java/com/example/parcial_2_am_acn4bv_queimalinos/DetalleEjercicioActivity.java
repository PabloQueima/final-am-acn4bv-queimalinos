package com.example.parcial_2_am_acn4bv_queimalinos;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetalleEjercicioActivity extends AppCompatActivity {

    private TextView tvNombre, tvDescripcion;
    private ImageView ivImagen;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        tvNombre = findViewById(R.id.tvNombre);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        ivImagen = findViewById(R.id.ivImagen);

        db = FirebaseFirestore.getInstance();

        String ejercicioId = getIntent().getStringExtra("ejercicioId");

        if (ejercicioId == null) {
            Toast.makeText(this, "Ejercicio inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("ejercicios")
                .document(ejercicioId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Ejercicio no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    String nombre = doc.getString("nombre");
                    String descripcion = doc.getString("descripcion");
                    String imageUrl = doc.getString("imageUrl");

                    tvNombre.setText(nombre);
                    tvDescripcion.setText(descripcion);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(imageUrl)
                                .into(ivImagen);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar ejercicio", Toast.LENGTH_SHORT).show()
                );
    }
}