package com.example.final_am_acn4bv_queimalinos.activities;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_am_acn4bv_queimalinos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminUsuariosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private EditText inputNombre, inputEmail;
    private Spinner spinnerRol;
    private RecyclerView recycler;
    private TextView txtPagina;
    private Button btnAnterior, btnSiguiente, btnGuardar, btnReset;

    private List<Map<String,Object>> listaCompleta = new ArrayList<>();
    private int paginaActual = 0;
    private final int TAM_PAGINA = 10;

    private String editandoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_usuarios);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        inputNombre = findViewById(R.id.inputNombreUsuario);
        inputEmail = findViewById(R.id.inputEmailUsuario);
        spinnerRol = findViewById(R.id.spinnerRol);

        recycler = findViewById(R.id.recyclerUsuarios);
        txtPagina = findViewById(R.id.txtPagina);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnGuardar = findViewById(R.id.btnGuardarUsuario);
        btnReset = findViewById(R.id.btnResetPassword);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"cliente","entrenador","admin"}
        );
        spinnerRol.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> confirmarGuardar());
        btnAnterior.setOnClickListener(v -> cambiarPagina(-1));
        btnSiguiente.setOnClickListener(v -> cambiarPagina(1));
        btnReset.setOnClickListener(v -> resetPassword());

        cargarDatos();
    }

    private void cargarDatos() {
        db.collection("usuarios").get().addOnSuccessListener(query -> {

            listaCompleta.clear();

            for (QueryDocumentSnapshot doc : query) {
                Map<String,Object> map = doc.getData();
                listaCompleta.add(map);
            }

            actualizarRecycler();
        });
    }

    private void actualizarRecycler() {

        int inicio = paginaActual * TAM_PAGINA;
        int fin = Math.min(inicio + TAM_PAGINA, listaCompleta.size());

        List<Map<String,Object>> sub = listaCompleta.subList(
                Math.min(inicio, listaCompleta.size()),
                fin
        );

        recycler.setAdapter(new UsuarioAdapter(sub));

        int totalPaginas = (int) Math.ceil((double) listaCompleta.size() / TAM_PAGINA);
        txtPagina.setText("Página " + (paginaActual + 1) + " / " + Math.max(totalPaginas,1));
    }

    private void cambiarPagina(int delta) {
        int totalPaginas = (int) Math.ceil((double) listaCompleta.size() / TAM_PAGINA);
        paginaActual += delta;

        if (paginaActual < 0) paginaActual = 0;
        if (paginaActual >= totalPaginas) paginaActual = totalPaginas - 1;

        actualizarRecycler();
    }

    private void confirmarGuardar() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage(editandoId == null ? "¿Crear usuario?" : "¿Guardar cambios?")
                .setPositiveButton("Sí", (d,w) -> guardar())
                .setNegativeButton("No", null)
                .show();
    }

    private void guardar() {

        String nombre = inputNombre.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();

        if (nombre.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Completar nombre y email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editandoId == null) {

            String passwordTemporal = "123456";

            FirebaseOptions options = FirebaseApp.getInstance().getOptions();
            FirebaseApp secondaryApp;

            try {
                secondaryApp = FirebaseApp.getInstance("Secondary");
            } catch (IllegalStateException e) {
                secondaryApp = FirebaseApp.initializeApp(this, options, "Secondary");
            }

            FirebaseAuth secondaryAuth = FirebaseAuth.getInstance(secondaryApp);

            secondaryAuth.createUserWithEmailAndPassword(email, passwordTemporal)
                    .addOnSuccessListener(result -> {

                        String uid = result.getUser().getUid();

                        Map<String,Object> map = new HashMap<>();
                        map.put("uid", uid);
                        map.put("email", email);
                        map.put("nombre", nombre);
                        map.put("rol", rol);

                        String createdAt = new SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                Locale.getDefault()
                        ).format(new Date());

                        map.put("createdAt", createdAt);

                        db.collection("usuarios")
                                .document(uid)
                                .set(map)
                                .addOnSuccessListener(aVoid -> {
                                    AdminUsuariosActivity.this.limpiarForm();
                                    AdminUsuariosActivity.this.cargarDatos();
                                    Toast.makeText(AdminUsuariosActivity.this,
                                            "Usuario creado",
                                            Toast.LENGTH_SHORT).show();
                                });

                        secondaryAuth.signOut();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(AdminUsuariosActivity.this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );

        } else {

            Map<String,Object> update = new HashMap<>();
            update.put("nombre", nombre);
            update.put("rol", rol);

            db.collection("usuarios")
                    .document(editandoId)
                    .update(update)
                    .addOnSuccessListener(aVoid -> {
                        editandoId = null;
                        inputEmail.setEnabled(true);
                        limpiarForm();
                        cargarDatos();
                        Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void resetPassword() {

        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty()) return;

        auth.sendPasswordResetEmail(email);
        Toast.makeText(this, "Email de recuperación enviado", Toast.LENGTH_SHORT).show();
    }

    private void limpiarForm() {
        inputNombre.setText("");
        inputEmail.setText("");
        spinnerRol.setSelection(0);
    }

    private class UsuarioAdapter extends RecyclerView.Adapter<UsuarioViewHolder> {

        private final List<Map<String,Object>> lista;

        UsuarioAdapter(List<Map<String,Object>> lista) {
            this.lista = lista;
        }

        @Override
        public UsuarioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = getLayoutInflater()
                    .inflate(R.layout.item_admin_simple, parent, false);
            return new UsuarioViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UsuarioViewHolder holder, int position) {
            Map<String,Object> u = lista.get(position);

            holder.txtNombre.setText(
                    u.get("nombre") + " - " + u.get("rol")
            );

            holder.btnEditar.setOnClickListener(v -> {
                inputNombre.setText(String.valueOf(u.get("nombre")));
                inputEmail.setText(String.valueOf(u.get("email")));
                inputEmail.setEnabled(false);
                spinnerRol.setSelection(
                        ((ArrayAdapter) spinnerRol.getAdapter())
                                .getPosition(String.valueOf(u.get("rol")))
                );
                editandoId = String.valueOf(u.get("uid"));
            });

            holder.btnEliminar.setOnClickListener(v -> {
                db.collection("usuarios")
                        .document(String.valueOf(u.get("uid")))
                        .delete();
                cargarDatos();
            });
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }
    }

    private static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        Button btnEditar, btnEliminar;

        UsuarioViewHolder(android.view.View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreItem);
            btnEditar = itemView.findViewById(R.id.btnEditarItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}
