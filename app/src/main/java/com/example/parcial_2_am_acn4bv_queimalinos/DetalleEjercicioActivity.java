package com.example.parcial_2_am_acn4bv_queimalinos;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.example.parcial_2_am_acn4bv_queimalinos.models.Ejercicio;
import com.squareup.picasso.Picasso;

public class DetalleEjercicioActivity extends AppCompatActivity {

    private TextView txtNombre, txtDescripcion, txtElemento, txtParteCuerpo;
    private ImageView imgEjercicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        txtNombre = findViewById(R.id.txtTituloEjercicio);
        txtDescripcion = findViewById(R.id.txtDescripcionEjercicio);
        txtElemento = findViewById(R.id.txtElementoEjercicio);
        txtParteCuerpo = findViewById(R.id.txtParteCuerpoEjercicio);
        imgEjercicio = findViewById(R.id.imgEjercicio);

        int ejercicioId = getIntent().getIntExtra("ejercicioId", -1);

        FirebaseFirestore.getInstance()
                .collection("ejercicios")
                .whereEqualTo("id", ejercicioId)
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        Ejercicio ej = doc.toObject(Ejercicio.class);
                        txtNombre.setText(ej.getNombre());
                        txtDescripcion.setText(ej.getDescripcion());
                        txtElemento.setText("Elemento: " + ej.getElemento());
                        txtParteCuerpo.setText("Parte del cuerpo: " + ej.getParteCuerpo());
                        Picasso.get().load(ej.getImageUrl()).into(imgEjercicio);
                        break;
                    }
                });
    }
}