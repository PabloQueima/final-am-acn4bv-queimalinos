package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class EntrenadorActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Spinner spinnerClientes, spinnerEjercicios;
    private EditText inputTituloSesion;
    private LinearLayout contenedorSesiones;

    private List<String> clientesIds = new ArrayList<>();
    private List<String> ejerciciosIds = new ArrayList<>();

    private String sesionEditandoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenador);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        spinnerClientes = findViewById(R.id.spinnerClientes);
        spinnerEjercicios = findViewById(R.id.spinnerEjercicios);
        inputTituloSesion = findViewById(R.id.inputTituloSesion);
        contenedorSesiones = findViewById(R.id.contenedorSesiones);

        Button btnCrearSesion = findViewById(R.id.btnCrearSesion);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        btnCrearSesion.setOnClickListener(v -> guardarSesion());

        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        cargarClientes();
        cargarEjercicios();
        cargarSesiones();
    }

    private void cargarClientes() {
        db.collection("usuarios")
                .whereEqualTo("rol", "cliente")
                .get()
                .addOnSuccessListener(query -> {

                    List<String> nombres = new ArrayList<>();
                    clientesIds.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        nombres.add(doc.getString("nombre"));
                        clientesIds.add(doc.getId());
                    }

                    spinnerClientes.setAdapter(
                            new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    nombres)
                    );
                });
    }

    private void cargarEjercicios() {
        db.collection("ejercicios")
                .get()
                .addOnSuccessListener(query -> {

                    List<String> nombres = new ArrayList<>();
                    ejerciciosIds.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        nombres.add(doc.getString("nombre"));
                        ejerciciosIds.add(doc.getId());
                    }

                    spinnerEjercicios.setAdapter(
                            new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    nombres)
                    );
                });
    }

    private void guardarSesion() {

        String titulo = inputTituloSesion.getText().toString().trim();
        if (titulo.isEmpty()) return;

        int clientePos = spinnerClientes.getSelectedItemPosition();
        int ejercicioPos = spinnerEjercicios.getSelectedItemPosition();

        if (clientePos < 0 || ejercicioPos < 0) return;

        String clienteUid = clientesIds.get(clientePos);
        String ejercicioId = ejerciciosIds.get(ejercicioPos);

        Map<String, Object> sesion = new HashMap<>();
        sesion.put("titulo", titulo);
        sesion.put("entrenadorUid", auth.getCurrentUser().getUid());
        sesion.put("clienteUid", clienteUid);
        sesion.put("ejercicios", Collections.singletonList(ejercicioId));

        if (sesionEditandoId == null) {
            db.collection("sesiones").add(sesion);
        } else {
            db.collection("sesiones").document(sesionEditandoId).update(sesion);
            sesionEditandoId = null;
        }

        inputTituloSesion.setText("");
        cargarSesiones();
    }

    private void cargarSesiones() {

        contenedorSesiones.removeAllViews();

        db.collection("sesiones")
                .whereEqualTo("entrenadorUid", auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(query -> {

                    for (QueryDocumentSnapshot doc : query) {

                        String id = doc.getId();
                        String titulo = doc.getString("titulo");

                        TextView tv = new TextView(this);
                        tv.setText(titulo);
                        tv.setPadding(8, 8, 8, 8);

                        tv.setOnClickListener(v -> {
                            inputTituloSesion.setText(titulo);
                            sesionEditandoId = id;
                        });

                        tv.setOnLongClickListener(v -> {
                            db.collection("sesiones").document(id).delete();
                            cargarSesiones();
                            return true;
                        });

                        contenedorSesiones.addView(tv);
                    }
                });
    }
}