package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.parcial_2_am_acn4bv_queimalinos.models.Sesion;
import com.example.parcial_2_am_acn4bv_queimalinos.models.SesionCompletada;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.util.*;

public class ClienteActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private LinearLayout contenedorSesiones;

    private final Map<String, Integer> totalEjPorSesion = new HashMap<>();
    private final Map<String, TextView> progresoPorSesion = new HashMap<>();
    private final Set<String> completados = new HashSet<>();

    private long inicioSesionMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_cliente);

        db = FirebaseFirestore.getInstance();
        contenedorSesiones = findViewById(R.id.contenedorSesiones);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        String uid = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String nombre = doc.getString("nombre");
                    TextView bienvenida = findViewById(R.id.txtBienvenida);
                    bienvenida.setText("Bienvenido " + nombre);
                    cargarSesiones(uid);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando usuario", Toast.LENGTH_SHORT).show()
                );
    }

    private void cargarSesiones(String uid) {

        db.collection("sesiones")
                .whereEqualTo("clienteUid", uid)
                .get()
                .addOnSuccessListener(sesiones -> {
                    for (DocumentSnapshot s : sesiones) {
                        crearSesion(s);
                    }
                });
    }

    private void crearSesion(DocumentSnapshot sesionDoc) {

        inicioSesionMillis = System.currentTimeMillis();

        String sesionId = sesionDoc.getId();
        String titulo = sesionDoc.getString("titulo");

        List<Map<String, Object>> ejercicios =
                (List<Map<String, Object>>) sesionDoc.get("ejercicios");

        if (ejercicios == null) ejercicios = new ArrayList<>();

        final List<Map<String, Object>> ejerciciosFinal = ejercicios;

        LinearLayout sesionLayout = new LinearLayout(this);
        sesionLayout.setOrientation(LinearLayout.VERTICAL);
        sesionLayout.setPadding(24, 24, 24, 24);
        sesionLayout.setBackgroundColor(
                ContextCompat.getColor(this, R.color.color_light));

        TextView tituloView = new TextView(this);
        tituloView.setText(titulo);
        tituloView.setTextSize(18f);
        sesionLayout.addView(tituloView);

        TextView progreso = new TextView(this);
        progreso.setText("0 de " + ejerciciosFinal.size() + " ejercicios completados");
        sesionLayout.addView(progreso);

        progresoPorSesion.put(sesionId, progreso);
        totalEjPorSesion.put(sesionId, ejerciciosFinal.size());

        LinearLayout lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        lista.setVisibility(View.GONE);

        for (Map<String, Object> e : ejerciciosFinal) {
            int id = ((Number) e.get("id")).intValue();
            int series = ((Number) e.get("series")).intValue();
            int reps = ((Number) e.get("reps")).intValue();
            crearEjercicio(sesionId, id, series, reps, lista);
        }

        Button toggleBtn = new Button(this);
        toggleBtn.setText("Ver ejercicios");
        toggleBtn.setOnClickListener(v -> {
            boolean visible = lista.getVisibility() == View.VISIBLE;
            lista.setVisibility(visible ? View.GONE : View.VISIBLE);
            toggleBtn.setText(visible ? "Ver ejercicios" : "Ocultar ejercicios");
        });

        Button terminarBtn = new Button(this);
        terminarBtn.setText("Terminar sesión");
        terminarBtn.setOnClickListener(v ->
                finalizarSesion(sesionId, titulo, ejerciciosFinal)
        );

        sesionLayout.addView(toggleBtn);
        sesionLayout.addView(lista);
        sesionLayout.addView(terminarBtn);

        contenedorSesiones.addView(sesionLayout);
    }

    private void crearEjercicio(String sesionId,
                                int ejercicioId,
                                int series,
                                int reps,
                                LinearLayout lista) {

        db.collection("ejercicios")
                .whereEqualTo("id", ejercicioId)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) return;

                    DocumentSnapshot e = query.getDocuments().get(0);

                    String nombre = e.getString("nombre");
                    String descripcion = e.getString("descripcion");

                    LinearLayout card = new LinearLayout(this);
                    card.setOrientation(LinearLayout.VERTICAL);

                    TextView nombreView = new TextView(this);
                    nombreView.setText(nombre);
                    card.addView(nombreView);

                    TextView detalle = new TextView(this);
                    detalle.setText(series + " x " + reps + "\n" + descripcion);
                    card.addView(detalle);

                    CheckBox check = new CheckBox(this);
                    card.addView(check);

                    String key = sesionId + "|" + ejercicioId;

                    check.setOnCheckedChangeListener((b, checked) -> {
                        if (checked) completados.add(key);
                        else completados.remove(key);
                        actualizarProgreso(sesionId);
                    });

                    lista.addView(card);
                });
    }

    private void actualizarProgreso(String sesionId) {

        int total = totalEjPorSesion.get(sesionId);
        int hechos = 0;

        for (String k : completados) {
            if (k.startsWith(sesionId + "|")) {
                hechos++;
            }
        }

        progresoPorSesion.get(sesionId)
                .setText(hechos + " de " + total + " ejercicios completados");
    }

    private void finalizarSesion(String sesionId,
                                 String titulo,
                                 List<Map<String, Object>> ejercicios) {

        String uid = auth.getCurrentUser().getUid();

        long finMillis = System.currentTimeMillis();
        long duracionSegundos = (finMillis - inicioSesionMillis) / 1000;

        List<Sesion.EjercicioRef> snapshot = new ArrayList<>();

        for (Map<String, Object> e : ejercicios) {
            Sesion.EjercicioRef ref = new Sesion.EjercicioRef();
            ref.setId(((Number) e.get("id")).intValue());
            ref.setSeries(((Number) e.get("series")).intValue());
            ref.setReps(((Number) e.get("reps")).intValue());
            snapshot.add(ref);
        }

        SesionCompletada completada = new SesionCompletada();
        completada.setClienteUid(uid);
        completada.setSesionId(sesionId);
        completada.setTituloSesion(titulo);
        completada.setEjerciciosSnapshot(snapshot);
        completada.setFechaInicio(Instant.ofEpochMilli(inicioSesionMillis).toString());
        completada.setFechaFin(Instant.ofEpochMilli(finMillis).toString());
        completada.setDuracionSegundos(duracionSegundos);
        completada.setCreatedAt(Instant.now().toString());

        db.collection("sesionesCompletadas")
                .add(completada)
                .addOnSuccessListener(r -> {
                    new AlertDialog.Builder(this)
                            .setTitle("SESIÓN TERMINADA")
                            .setPositiveButton("OK", (d, w) -> recreate())
                            .show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error guardando sesión", Toast.LENGTH_SHORT).show()
                );
    }
}