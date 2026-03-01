package com.example.final_am_acn4bv_queimalinos.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_am_acn4bv_queimalinos.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class AdminEjerciciosActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private EditText inputBuscar, inputNombre, inputDescripcion, inputElemento, inputParteCuerpo, inputImagen;
    private RecyclerView recycler;
    private TextView txtPagina;
    private Button btnAnterior, btnSiguiente, btnGuardar;

    private List<Map<String,Object>> listaCompleta = new ArrayList<>();
    private List<Map<String,Object>> listaFiltrada = new ArrayList<>();

    private int paginaActual = 0;
    private final int TAM_PAGINA = 10;

    private String editandoId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ejercicios);

        db = FirebaseFirestore.getInstance();

        inputBuscar = findViewById(R.id.inputBuscarEjercicio);
        inputNombre = findViewById(R.id.inputNombreEjercicio);
        inputDescripcion = findViewById(R.id.inputDescripcionEjercicio);
        inputElemento = findViewById(R.id.inputElementoEjercicio);
        inputParteCuerpo = findViewById(R.id.inputParteCuerpoEjercicio);
        inputImagen = findViewById(R.id.inputImagenEjercicio);

        recycler = findViewById(R.id.recyclerEjercicios);
        txtPagina = findViewById(R.id.txtPagina);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnGuardar = findViewById(R.id.btnGuardarEjercicio);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        btnGuardar.setOnClickListener(v -> confirmarGuardar());
        btnAnterior.setOnClickListener(v -> cambiarPagina(-1));
        btnSiguiente.setOnClickListener(v -> cambiarPagina(1));

        inputBuscar.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { filtrar(); }
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {}
        });

        cargarDatos();
    }

    private void cargarDatos() {
        db.collection("ejercicios").get().addOnSuccessListener(query -> {

            listaCompleta.clear();

            for (QueryDocumentSnapshot doc : query) {
                Map<String,Object> map = doc.getData();
                map.put("id", doc.getId());
                listaCompleta.add(map);
            }

            filtrar();
        });
    }

    private void filtrar() {

        String texto = inputBuscar.getText().toString().trim().toLowerCase();
        listaFiltrada.clear();

        for (Map<String,Object> e : listaCompleta) {

            String nombre = String.valueOf(e.get("nombre")).toLowerCase();
            String parteCuerpo = String.valueOf(e.get("parteCuerpo")).toLowerCase();
            String elemento = String.valueOf(e.get("elemento")).toLowerCase();

            if (texto.isEmpty()
                    || nombre.contains(texto)
                    || parteCuerpo.contains(texto)
                    || elemento.contains(texto)) {

                listaFiltrada.add(e);
            }
        }

        paginaActual = 0;
        actualizarRecycler();
    }


    private void actualizarRecycler() {

        int inicio = paginaActual * TAM_PAGINA;
        int fin = Math.min(inicio + TAM_PAGINA, listaFiltrada.size());

        List<Map<String,Object>> sub = listaFiltrada.subList(
                Math.min(inicio, listaFiltrada.size()),
                fin
        );

        recycler.setAdapter(new EjercicioAdapter(sub));

        int totalPaginas = (int) Math.ceil((double) listaFiltrada.size() / TAM_PAGINA);
        txtPagina.setText("Página " + (paginaActual + 1) + " / " + Math.max(totalPaginas,1));
    }

    private void cambiarPagina(int delta) {
        int totalPaginas = (int) Math.ceil((double) listaFiltrada.size() / TAM_PAGINA);
        paginaActual += delta;

        if (paginaActual < 0) paginaActual = 0;
        if (paginaActual >= totalPaginas) paginaActual = totalPaginas - 1;

        actualizarRecycler();
    }

    private void confirmarGuardar() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage(editandoId == null ? "¿Crear ejercicio?" : "¿Guardar cambios?")
                .setPositiveButton("Sí", (d,w) -> guardar())
                .setNegativeButton("No", null)
                .show();
    }

    private void guardar() {

        Map<String,Object> map = new HashMap<>();
        map.put("nombre", inputNombre.getText().toString());
        map.put("descripcion", inputDescripcion.getText().toString());
        map.put("elemento", inputElemento.getText().toString());
        map.put("parteCuerpo", inputParteCuerpo.getText().toString());
        map.put("imageUrl", inputImagen.getText().toString());

        if (editandoId == null) {
            db.collection("ejercicios").add(map);
        } else {
            db.collection("ejercicios").document(editandoId).update(map);
            editandoId = null;
        }

        limpiarForm();
        cargarDatos();
    }

    private void limpiarForm() {
        inputNombre.setText("");
        inputDescripcion.setText("");
        inputElemento.setText("");
        inputParteCuerpo.setText("");
        inputImagen.setText("");
    }

    private class EjercicioAdapter extends RecyclerView.Adapter<EjercicioViewHolder> {

        private final List<Map<String,Object>> lista;

        EjercicioAdapter(List<Map<String,Object>> lista) {
            this.lista = lista;
        }

        public EjercicioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = getLayoutInflater()
                    .inflate(R.layout.item_admin_ejercicio, parent, false);
            return new EjercicioViewHolder(view);
        }

        public void onBindViewHolder(EjercicioViewHolder holder, int position) {
            Map<String,Object> e = lista.get(position);

            holder.txtNombre.setText(String.valueOf(e.get("nombre")));

            holder.btnEditar.setOnClickListener(v -> {
                inputNombre.setText(String.valueOf(e.get("nombre")));
                inputDescripcion.setText(String.valueOf(e.get("descripcion")));
                inputElemento.setText(String.valueOf(e.get("elemento")));
                inputParteCuerpo.setText(String.valueOf(e.get("parteCuerpo")));
                inputImagen.setText(String.valueOf(e.get("imageUrl")));
                editandoId = String.valueOf(e.get("id"));
            });

            holder.btnEliminar.setOnClickListener(v -> {
                new AlertDialog.Builder(AdminEjerciciosActivity.this)
                        .setTitle("Eliminar")
                        .setMessage("¿Eliminar ejercicio?")
                        .setPositiveButton("Sí", (d,w) -> {
                            db.collection("ejercicios")
                                    .document(String.valueOf(e.get("id")))
                                    .delete();
                            cargarDatos();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }

        public int getItemCount() { return lista.size(); }
    }

    private static class EjercicioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        Button btnEditar, btnEliminar;

        EjercicioViewHolder(android.view.View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreItem);
            btnEditar = itemView.findViewById(R.id.btnEditarItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}
