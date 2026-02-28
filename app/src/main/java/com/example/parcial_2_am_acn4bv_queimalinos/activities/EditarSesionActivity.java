package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.example.parcial_2_am_acn4bv_queimalinos.adapters.EjerciciosDisponiblesAdapter;
import com.example.parcial_2_am_acn4bv_queimalinos.adapters.EjerciciosSesionAdapter;
import com.example.parcial_2_am_acn4bv_queimalinos.models.Ejercicio;
import com.example.parcial_2_am_acn4bv_queimalinos.models.Sesion;
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

    private List<Ejercicio> ejerciciosDisponibles = new ArrayList<>();
    private List<Sesion.EjercicioRef> ejerciciosSesion = new ArrayList<>();
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

        adapterDisponibles = new EjerciciosDisponiblesAdapter(ejerciciosDisponibles, this::agregarEjercicio);

        adapterSesion = new EjerciciosSesionAdapter(ejerciciosSesion, ejercicio -> {
            ejerciciosSesion.remove(ejercicio);
            adapterSesion.notifyDataSetChanged();
        });

        recyclerEjercicios.setLayoutManager(new LinearLayoutManager(this));
        recyclerSesion.setLayoutManager(new LinearLayoutManager(this));

        recyclerEjercicios.setAdapter(adapterDisponibles);
        recyclerSesion.setAdapter(adapterSesion);

        findViewById(R.id.btnBuscarEjercicio)
                .setOnClickListener(v -> cargarEjercicios(inputBuscar.getText().toString()));

        findViewById(R.id.btnGuardarSesion)
                .setOnClickListener(v -> guardarSesion());

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

        Query query = db.collection("ejercicios")
                .orderBy("nombre")
                .limit(20);

        if (!filtro.isEmpty()) {
            query = query
                    .whereGreaterThanOrEqualTo("nombre", filtro)
                    .whereLessThanOrEqualTo("nombre", filtro + "\uf8ff");
        }

        query.get().addOnSuccessListener(q -> {

            ejerciciosDisponibles.clear();

            for (DocumentSnapshot doc : q.getDocuments()) {
                Ejercicio e = doc.toObject(Ejercicio.class);
                if (e != null) {
                    ejerciciosDisponibles.add(e);
                }
            }

            adapterDisponibles.notifyDataSetChanged();
        });
    }

    private void agregarEjercicio(Ejercicio ejercicio) {

        for (Sesion.EjercicioRef e : ejerciciosSesion) {
            if (e.getId() == ejercicio.getId()) return;
        }

        Sesion.EjercicioRef nuevo = new Sesion.EjercicioRef();
        nuevo.setId(ejercicio.getId());
        nuevo.setSeries(3);
        nuevo.setReps(10);

        ejerciciosSesion.add(nuevo);
        adapterSesion.notifyDataSetChanged();
    }

    private void cargarSesion() {

        db.collection("sesiones")
                .document(sesionId)
                .get()
                .addOnSuccessListener(doc -> {

                    inputTitulo.setText(doc.getString("titulo"));
                    createdAtOriginal = doc.getString("createdAt");

                    List<Map<String, Object>> lista =
                            (List<Map<String, Object>>) doc.get("ejercicios");

                    ejerciciosSesion.clear();

                    if (lista != null) {
                        for (Map<String, Object> m : lista) {

                            Sesion.EjercicioRef ref = new Sesion.EjercicioRef();
                            ref.setId(((Long) m.get("id")).intValue());
                            ref.setSeries(((Long) m.get("series")).intValue());
                            ref.setReps(((Long) m.get("reps")).intValue());

                            ejerciciosSesion.add(ref);
                        }
                    }

                    adapterSesion.notifyDataSetChanged();
                });
    }

    private void guardarSesion() {

        String titulo = inputTitulo.getText().toString().trim();
        if (titulo.isEmpty() || ejerciciosSesion.isEmpty()) return;

        String clienteUid = clientesIds.get(spinnerClientes.getSelectedItemPosition());

        Map<String, Object> sesion = new HashMap<>();
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