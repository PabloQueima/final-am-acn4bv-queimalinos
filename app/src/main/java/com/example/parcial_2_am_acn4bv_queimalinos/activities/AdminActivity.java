package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView tvTotalUsuarios;
    private TextView tvTotalEjercicios;
    private TextView tvTotalSesiones;
    private TextView tvTotalSesionesCompletadas;
    private TextView tvTotalClientes;
    private TextView tvTotalEntrenadores;
    private TextView tvPromedioSesionesPorCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        tvTotalUsuarios = findViewById(R.id.tvTotalUsuarios);
        tvTotalEjercicios = findViewById(R.id.tvTotalEjercicios);
        tvTotalSesiones = findViewById(R.id.tvTotalSesiones);
        tvTotalSesionesCompletadas = findViewById(R.id.tvTotalSesionesCompletadas);
        tvTotalClientes = findViewById(R.id.tvTotalClientes);
        tvTotalEntrenadores = findViewById(R.id.tvTotalEntrenadores);
        tvPromedioSesionesPorCliente = findViewById(R.id.tvPromedioSesionesPorCliente);

        Button btnUsuarios = findViewById(R.id.btnGestionUsuarios);
        Button btnEjercicios = findViewById(R.id.btnGestionEjercicios);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnUsuarios.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUsuariosActivity.class))
        );

        btnEjercicios.setOnClickListener(v ->
                startActivity(new Intent(this, AdminEjerciciosActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        cargarMetricas();
    }

    private void cargarMetricas() {

        db.collection("usuarios").get().addOnSuccessListener(q ->
                tvTotalUsuarios.setText("Usuarios: " + q.size())
        );

        db.collection("ejercicios").get().addOnSuccessListener(q ->
                tvTotalEjercicios.setText("Ejercicios: " + q.size())
        );

        db.collection("sesiones").get().addOnSuccessListener(q ->
                tvTotalSesiones.setText("Sesiones: " + q.size())
        );

        db.collection("sesionesCompletadas").get().addOnSuccessListener(q -> {
            int totalCompletadas = q.size();
            tvTotalSesionesCompletadas.setText("Sesiones completadas: " + totalCompletadas);

            db.collection("usuarios")
                    .whereEqualTo("rol", "cliente")
                    .get()
                    .addOnSuccessListener(clientesQuery -> {

                        int totalClientes = clientesQuery.size();
                        tvTotalClientes.setText("Clientes: " + totalClientes);

                        if (totalClientes > 0) {
                            double promedio = (double) totalCompletadas / totalClientes;
                            tvPromedioSesionesPorCliente.setText(
                                    "Promedio sesiones por cliente: " + String.format("%.2f", promedio)
                            );
                        }
                    });
        });

        db.collection("usuarios")
                .whereEqualTo("rol", "entrenador")
                .get()
                .addOnSuccessListener(q ->
                        tvTotalEntrenadores.setText("Entrenadores: " + q.size())
                );
    }
}