package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private EditText inputNombreUsuario, inputEmailUsuario;
    private Spinner spinnerRol;
    private LinearLayout contenedorUsuarios;

    private EditText inputNombreEjercicio, inputDescripcionEjercicio, inputImagenEjercicio;
    private LinearLayout contenedorEjercicios;

    private String usuarioEditandoId = null;
    private String ejercicioEditandoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        inputNombreUsuario = findViewById(R.id.inputNombreUsuario);
        inputEmailUsuario = findViewById(R.id.inputEmailUsuario);
        spinnerRol = findViewById(R.id.spinnerRol);
        contenedorUsuarios = findViewById(R.id.contenedorUsuarios);

        inputNombreEjercicio = findViewById(R.id.inputNombreEjercicio);
        inputDescripcionEjercicio = findViewById(R.id.inputDescripcionEjercicio);
        inputImagenEjercicio = findViewById(R.id.inputImagenEjercicio);
        contenedorEjercicios = findViewById(R.id.contenedorEjercicios);

        Button btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        Button btnGuardarEjercicio = findViewById(R.id.btnGuardarEjercicio);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"cliente", "entrenador", "admin"}
        );
        spinnerRol.setAdapter(adapter);

        btnGuardarUsuario.setOnClickListener(v -> guardarUsuario());
        btnGuardarEjercicio.setOnClickListener(v -> guardarEjercicio());

        logoutBtn.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        cargarUsuarios();
        cargarEjercicios();
    }

    private void guardarUsuario() {

        String nombre = inputNombreUsuario.getText().toString().trim();
        String email = inputEmailUsuario.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();

        if (nombre.isEmpty() || email.isEmpty()) return;

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("email", email);
        usuario.put("rol", rol);

        if (usuarioEditandoId == null) {
            db.collection("usuarios").add(usuario);
        } else {
            db.collection("usuarios").document(usuarioEditandoId).update(usuario);
            usuarioEditandoId = null;
        }

        limpiarUsuarioForm();
        cargarUsuarios();
    }

    private void cargarUsuarios() {

        contenedorUsuarios.removeAllViews();

        db.collection("usuarios").get()
                .addOnSuccessListener(query -> {

                    for (QueryDocumentSnapshot doc : query) {

                        String id = doc.getId();
                        String nombre = doc.getString("nombre");
                        String rol = doc.getString("rol");

                        TextView tv = new TextView(this);
                        tv.setText(nombre + " - " + rol);
                        tv.setPadding(8, 8, 8, 8);

                        tv.setOnClickListener(v -> {
                            inputNombreUsuario.setText(nombre);
                            inputEmailUsuario.setText(doc.getString("email"));
                            spinnerRol.setSelection(
                                    ((ArrayAdapter) spinnerRol.getAdapter())
                                            .getPosition(rol)
                            );
                            usuarioEditandoId = id;
                        });

                        tv.setOnLongClickListener(v -> {
                            db.collection("usuarios").document(id).delete();
                            cargarUsuarios();
                            return true;
                        });

                        contenedorUsuarios.addView(tv);
                    }
                });
    }

    private void limpiarUsuarioForm() {
        inputNombreUsuario.setText("");
        inputEmailUsuario.setText("");
        spinnerRol.setSelection(0);
    }

    private void guardarEjercicio() {

        String nombre = inputNombreEjercicio.getText().toString().trim();
        String descripcion = inputDescripcionEjercicio.getText().toString().trim();
        String imagen = inputImagenEjercicio.getText().toString().trim();

        if (nombre.isEmpty()) return;

        Map<String, Object> ejercicio = new HashMap<>();
        ejercicio.put("nombre", nombre);
        ejercicio.put("descripcion", descripcion);
        ejercicio.put("imagen", imagen);

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
                            inputImagenEjercicio.setText(doc.getString("imagen"));
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
        inputImagenEjercicio.setText("");
    }
}