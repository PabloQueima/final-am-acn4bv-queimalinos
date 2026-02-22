package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private LinearLayout contenedorSesiones;
    private SharedPreferences prefs;

    private final Map<String, Integer> totalEjPorSesion = new HashMap<>();
    private final Map<String, TextView> progresoPorSesion = new HashMap<>();
    private final Set<String> completados = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences("progreso", MODE_PRIVATE);

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        contenedorSesiones = findViewById(R.id.contenedorSesiones);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        String uid = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String nombre = doc.getString("nombre");
                    String rol = doc.getString("rol");

                    TextView bienvenida = findViewById(R.id.txtBienvenida);
                    bienvenida.setText("Bienvenido " + nombre);

                    if (!"cliente".equals(rol)) {
                        Toast.makeText(this,
                                "Esta app es solo para clientes",
                                Toast.LENGTH_LONG).show();
                        auth.signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        return;
                    }

                    cargarSesiones(uid);
                });
    }

    private void cargarSesiones(String uid) {

        db.collection("sesiones")
                .whereEqualTo("clienteUid", uid)
                .get()
                .addOnSuccessListener(sesiones -> {

                    if (sesiones.isEmpty()) {
                        Toast.makeText(this,
                                "No tenés sesiones asignadas",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    for (DocumentSnapshot s : sesiones) {
                        crearSesion(s);
                    }
                });
    }

    private void crearSesion(DocumentSnapshot sesion) {

        String titulo = sesion.getString("titulo");
        List<Map<String, Object>> ejercicios =
                (List<Map<String, Object>>) sesion.get("ejercicios");

        if (ejercicios == null) ejercicios = new ArrayList<>();

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
        progreso.setText("0 de " + ejercicios.size() + " ejercicios completados");
        sesionLayout.addView(progreso);

        progresoPorSesion.put(titulo, progreso);
        totalEjPorSesion.put(titulo, ejercicios.size());

        LinearLayout lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        lista.setVisibility(View.GONE);

        for (Map<String, Object> e : ejercicios) {

            int id = ((Number) e.get("id")).intValue();
            int series = ((Number) e.get("series")).intValue();
            int reps = ((Number) e.get("reps")).intValue();

            crearEjercicio(titulo, id, series, reps, lista);
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
        terminarBtn.setOnClickListener(v -> finalizarSesion(titulo));

        sesionLayout.addView(toggleBtn);
        sesionLayout.addView(lista);
        sesionLayout.addView(terminarBtn);

        contenedorSesiones.addView(sesionLayout);
    }

    private void crearEjercicio(String sesionTitulo,
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
                    String imageUrl = e.getString("imageUrl");

                    LinearLayout card = new LinearLayout(this);
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setPadding(16, 16, 16, 16);

                    TextView nombreView = new TextView(this);
                    nombreView.setText(nombre);
                    card.addView(nombreView);

                    TextView detalle = new TextView(this);
                    detalle.setText(series + " x " + reps + "\n" + descripcion);
                    card.addView(detalle);

                    CheckBox check = new CheckBox(this);
                    card.addView(check);

                    String key = sesionTitulo + "|" + ejercicioId;
                    check.setChecked(prefs.getBoolean(key, false));

                    if (check.isChecked()) completados.add(key);

                    check.setOnCheckedChangeListener((b, checked) -> {
                        prefs.edit().putBoolean(key, checked).apply();
                        if (checked) completados.add(key);
                        else completados.remove(key);
                        actualizarProgreso(sesionTitulo);
                    });

                    card.setOnClickListener(v -> {
                        Intent i = new Intent(this, DetalleEjercicioActivity.class);
                        i.putExtra("nombre", nombre);
                        i.putExtra("descripcion", descripcion);
                        i.putExtra("series", series);
                        i.putExtra("repeticiones", reps);
                        i.putExtra("imageUrl", imageUrl);
                        startActivity(i);
                    });

                    lista.addView(card);
                });
    }

    private void actualizarProgreso(String sesionTitulo) {

        int total = totalEjPorSesion.get(sesionTitulo);

        long hechos = completados.stream()
                .filter(k -> k.startsWith(sesionTitulo + "|"))
                .count();

        progresoPorSesion.get(sesionTitulo)
                .setText(hechos + " de " + total + " ejercicios completados");

        if (hechos == total) finalizarSesion(sesionTitulo);
    }

    private void finalizarSesion(String sesionTitulo) {

        new AlertDialog.Builder(this)
                .setTitle("SESIÓN TERMINADA")
                .setPositiveButton("OK", (d, w) -> {
                    prefs.edit().clear().apply();
                    completados.clear();
                    recreate();
                })
                .show();
    }
}