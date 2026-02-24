package com.example.parcial_2_am_acn4bv_queimalinos;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class EditarSesionActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private EditText inputTitulo, inputBuscar;
    private Spinner spinnerClientes;
    private RecyclerView recyclerEjercicios, recyclerSesion;

    private List<DocumentSnapshot> ejerciciosDisponibles = new ArrayList<>();
    private List<Map<String,Object>> ejerciciosSesion = new ArrayList<>();
    private List<String> clientesIds = new ArrayList<>();

    private EjerciciosDisponiblesAdapter adapterDisponibles;
    private EjerciciosSesionAdapter adapterSesion;

    private String sesionId = null;
    private String createdAtOriginal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_sesion);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        inputTitulo = findViewById(R.id.inputTitulo);
        inputBuscar = findViewById(R.id.inputBuscarEjercicio);
        spinnerClientes = findViewById(R.id.spinnerClientes);
        recyclerEjercicios = findViewById(R.id.recyclerEjercicios);
        recyclerSesion = findViewById(R.id.recyclerSesion);

        adapterDisponibles = new EjerciciosDisponiblesAdapter(this, ejerciciosDisponibles, this::agregarEjercicio);
        adapterSesion = new EjerciciosSesionAdapter(this, ejerciciosSesion);

        recyclerEjercicios.setLayoutManager(new LinearLayoutManager(this));
        recyclerSesion.setLayoutManager(new LinearLayoutManager(this));

        recyclerEjercicios.setAdapter(adapterDisponibles);
        recyclerSesion.setAdapter(adapterSesion);

        findViewById(R.id.btnBuscarEjercicio).setOnClickListener(v -> cargarEjercicios(inputBuscar.getText().toString()));
        findViewById(R.id.btnGuardarSesion).setOnClickListener(v -> guardarSesion());

        sesionId = getIntent().getStringExtra("sesionId");

        cargarClientes();
        cargarEjercicios("");

        if (sesionId != null) cargarSesion();
    }

    private void cargarClientes() {
        db.collection("usuarios")
                .whereEqualTo("rol", "cliente")
                .get()
                .addOnSuccessListener(q -> {
                    List<String> nombres = new ArrayList<>();
                    clientesIds.clear();

                    for (DocumentSnapshot d : q) {
                        nombres.add(d.getString("nombre"));
                        clientesIds.add(d.getId());
                    }

                    spinnerClientes.setAdapter(
                            new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    nombres)
                    );
                });
    }

    private void cargarEjercicios(String filtro) {
        Query query = db.collection("ejercicios").orderBy("nombre").limit(20);

        if (!filtro.isEmpty()) {
            query = query
                    .whereGreaterThanOrEqualTo("nombre", filtro)
                    .whereLessThanOrEqualTo("nombre", filtro + "\uf8ff");
        }

        query.get().addOnSuccessListener(q -> {
            ejerciciosDisponibles.clear();
            ejerciciosDisponibles.addAll(q.getDocuments());
            adapterDisponibles.notifyDataSetChanged();
        });
    }

    private void agregarEjercicio(DocumentSnapshot doc) {

        int id = doc.getLong("id").intValue();

        for (Map<String,Object> e : ejerciciosSesion) {
            if ((int)e.get("id") == id) return;
        }

        Map<String,Object> nuevo = new HashMap<>();
        nuevo.put("id", id);
        nuevo.put("series", 3);
        nuevo.put("reps", 10);

        ejerciciosSesion.add(nuevo);
        adapterSesion.notifyDataSetChanged();
    }

    private void cargarSesion() {
        db.collection("sesiones").document(sesionId)
                .get()
                .addOnSuccessListener(doc -> {

                    inputTitulo.setText(doc.getString("titulo"));
                    createdAtOriginal = doc.getString("createdAt");

                    ejerciciosSesion.clear();
                    ejerciciosSesion.addAll((List<Map<String,Object>>) doc.get("ejercicios"));

                    adapterSesion.notifyDataSetChanged();
                });
    }

    private void guardarSesion() {

        String titulo = inputTitulo.getText().toString().trim();
        if (titulo.isEmpty() || ejerciciosSesion.isEmpty()) return;

        String clienteUid = clientesIds.get(spinnerClientes.getSelectedItemPosition());

        Map<String,Object> sesion = new HashMap<>();
        sesion.put("titulo", titulo);
        sesion.put("clienteUid", clienteUid);
        sesion.put("entrenadorUid", auth.getCurrentUser().getUid());
        sesion.put("ejercicios", ejerciciosSesion);
        sesion.put("updatedAt", isoNow());

        if (sesionId == null) {
            sesion.put("createdAt", isoNow());
            db.collection("sesiones").add(sesion);
        } else {
            sesion.put("createdAt", createdAtOriginal);
            db.collection("sesiones").document(sesionId).set(sesion);
        }

        finish();
    }

    private String isoNow() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                .format(new Date());
    }
}