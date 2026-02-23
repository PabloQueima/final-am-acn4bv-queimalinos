package com.example.parcial_2_am_acn4bv_queimalinos;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminUsuariosActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private EditText inputNombreUsuario, inputEmailUsuario;
    private Spinner spinnerRol;
    private LinearLayout contenedorUsuarios;

    private String usuarioEditandoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_usuarios);

        db = FirebaseFirestore.getInstance();

        inputNombreUsuario = findViewById(R.id.inputNombreUsuario);
        inputEmailUsuario = findViewById(R.id.inputEmailUsuario);
        spinnerRol = findViewById(R.id.spinnerRol);
        contenedorUsuarios = findViewById(R.id.contenedorUsuarios);

        Button btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        Button btnVolver = findViewById(R.id.btnVolver);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"cliente", "entrenador", "admin"}
        );
        spinnerRol.setAdapter(adapter);

        btnGuardarUsuario.setOnClickListener(v -> guardarUsuario());

        btnVolver.setOnClickListener(v -> finish());

        cargarUsuarios();
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

                        // Click para editar
                        tv.setOnClickListener(v -> {
                            inputNombreUsuario.setText(nombre);
                            inputEmailUsuario.setText(doc.getString("email"));
                            spinnerRol.setSelection(
                                    ((ArrayAdapter) spinnerRol.getAdapter())
                                            .getPosition(rol)
                            );
                            usuarioEditandoId = id;
                        });

                        // Long click para eliminar
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
}