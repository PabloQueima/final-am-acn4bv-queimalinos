package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.example.parcial_2_am_acn4bv_queimalinos.models.Ejercicio;
import com.example.parcial_2_am_acn4bv_queimalinos.models.Sesion;
import com.example.parcial_2_am_acn4bv_queimalinos.models.SesionCompletada;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.*;

public class RealizarSesionActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private LinearLayout contenedorEjercicios;
    private TextView txtTituloSesion, txtTimer;
    private Button btnFinalizar, btnCancelar;

    private String sesionId;
    private Sesion sesion;

    private Map<Long, CheckBox> ejerciciosCheckMap = new HashMap<>();
    private long startTimeMillis;
    private Handler handler = new Handler();
    private Runnable timerRunnable;
    private String fechaInicioISO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_sesion);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        contenedorEjercicios = findViewById(R.id.contenedorEjercicios);
        txtTituloSesion = findViewById(R.id.txtTituloSesion);
        txtTimer = findViewById(R.id.txtTimer);
        btnFinalizar = findViewById(R.id.btnFinalizarSesion);
        btnCancelar = findViewById(R.id.btnCancelarSesion);

        sesionId = getIntent().getStringExtra("sesionId");
        fechaInicioISO = isoNow();
        startTimeMillis = System.currentTimeMillis();

        iniciarTimer();
        cargarSesion();

        btnFinalizar.setOnClickListener(v -> finalizarSesion());
        btnCancelar.setOnClickListener(v -> confirmarCancelar());
    }

    private void cargarSesion() {
        db.collection("sesiones").document(sesionId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Sesión no encontrada", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    sesion = doc.toObject(Sesion.class);
                    sesion.setId(doc.getId());
                    txtTituloSesion.setText(sesion.getTitulo());

                    List<Long> ejercicioIds = new ArrayList<>();
                    for (Sesion.EjercicioRef eRef : sesion.getEjercicios()) {
                        ejercicioIds.add(eRef.getId());
                    }
                    if (ejercicioIds.isEmpty()) return;

                    db.collection("ejercicios")
                            .whereIn("id", ejercicioIds)
                            .get()
                            .addOnSuccessListener(QuerySnapshot::getDocuments)
                            .addOnSuccessListener(docs -> {
                                Map<Long, Ejercicio> ejerciciosMap = new HashMap<>();
                                for (DocumentSnapshot d : docs) {
                                    Ejercicio ej = d.toObject(Ejercicio.class);
                                    if (ej != null) ejerciciosMap.put(ej.getId(), ej);
                                }

                                for (Sesion.EjercicioRef eRef : sesion.getEjercicios()) {
                                    Ejercicio ej = ejerciciosMap.get(eRef.getId());
                                    if (ej == null) continue;

                                    LinearLayout row = new LinearLayout(this);
                                    row.setOrientation(LinearLayout.HORIZONTAL);
                                    row.setPadding(8, 8, 8, 8);

                                    LinearLayout infoCol = new LinearLayout(this);
                                    infoCol.setOrientation(LinearLayout.VERTICAL);
                                    infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                                    TextView txtNombre = new TextView(this);
                                    txtNombre.setText(ej.getNombre() + " (" + eRef.getSeries() + "x" + eRef.getReps() + ")");
                                    txtNombre.setTextSize(16);
                                    infoCol.addView(txtNombre);

                                    TextView txtElemento = new TextView(this);
                                    txtElemento.setText("Elemento: " + ej.getElemento() + " | Parte del cuerpo: " + ej.getParteCuerpo());
                                    infoCol.addView(txtElemento);

                                    CheckBox cb = new CheckBox(this);
                                    cb.setText("Completado");
                                    infoCol.addView(cb);
                                    ejerciciosCheckMap.put(eRef.getId(), cb);

                                    Button btnDetalle = new Button(this);
                                    btnDetalle.setText("Detalle");
                                    btnDetalle.setOnClickListener(v -> {
                                        Intent intent = new Intent(this, DetalleEjercicioActivity.class);
                                        intent.putExtra("ejercicioId", ej.getId());
                                        startActivity(intent);
                                    });

                                    row.addView(infoCol);
                                    row.addView(btnDetalle);
                                    contenedorEjercicios.addView(row);

                                    View separador = new View(this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT, 2
                                    );
                                    params.setMargins(0, 4, 0, 4);
                                    separador.setLayoutParams(params);
                                    separador.setBackgroundColor(getResources().getColor(R.color.color_dark2));
                                    contenedorEjercicios.addView(separador);
                                }
                            });
                });
    }

    private void iniciarTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsed = (System.currentTimeMillis() - startTimeMillis) / 1000;
                long h = elapsed / 3600;
                long m = (elapsed % 3600) / 60;
                long s = elapsed % 60;
                txtTimer.setText(String.format("%02d:%02d:%02d", h, m, s));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timerRunnable);
    }

    private void finalizarSesion() {
        for (CheckBox cb : ejerciciosCheckMap.values()) {
            if (!cb.isChecked()) {
                Toast.makeText(this, "Debe completar todos los ejercicios", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Completar sesión de entrenamiento?")
                .setPositiveButton("Sí", (dialog, which) -> guardarSesionCompletada())
                .setNegativeButton("No", null)
                .show();
    }

    private void confirmarCancelar() {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar sesión")
                .setMessage("No se guardará como completada. ¿Continuar?")
                .setPositiveButton("Sí", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    private void guardarSesionCompletada() {
        handler.removeCallbacks(timerRunnable);

        long durationSec = (System.currentTimeMillis() - startTimeMillis) / 1000;

        SesionCompletada sc = new SesionCompletada();
        sc.setClienteUid(auth.getCurrentUser().getUid());
        sc.setSesionId(sesion.getId());
        sc.setTituloSesion(sesion.getTitulo());
        sc.setEjerciciosSnapshot(sesion.getEjercicios());
        sc.setFechaInicio(fechaInicioISO);
        sc.setFechaFin(isoNow());
        sc.setDuracionSegundos(durationSec);
        sc.setCreatedAt(isoNow());

        db.collection("sesionesCompletadas")
                .add(sc)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Sesión completada", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private String isoNow() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onBackPressed() {
        confirmarCancelar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }
}
