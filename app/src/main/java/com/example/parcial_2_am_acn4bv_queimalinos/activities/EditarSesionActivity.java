package com.example.parcial_2_am_acn4bv_queimalinos.activities;

import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
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
    private List<Ejercicio> ejerciciosFiltrados = new ArrayList<>();
    private List<EjercicioEnSesion> ejerciciosSesion = new ArrayList<>();
    private List<String> clientesIds = new ArrayList<>();

    private EjerciciosDisponiblesAdapter adapterDisponibles;
    private EjerciciosSesionAdapter adapterSesion;

    private String sesionId = null;
    private String createdAtOriginal = null;
    private String clienteUidOriginal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_sesion);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        sesionId = getIntent().getStringExtra("sesionId");

        inputTitulo = findViewById(R.id.inputTitulo);
        inputBuscar = findViewById(R.id.inputBuscarEjercicio);
        spinnerClientes = findViewById(R.id.spinnerClientes);
        recyclerEjercicios = findViewById(R.id.recyclerEjercicios);
        recyclerSesion = findViewById(R.id.recyclerSesion);

        adapterDisponibles = new EjerciciosDisponiblesAdapter(ejerciciosFiltrados, this::agregarEjercicio);
        adapterSesion = new EjerciciosSesionAdapter(ejerciciosSesion, ejercicio -> {
            ejerciciosSesion.remove(ejercicio);
            adapterSesion.notifyDataSetChanged();
        });

        recyclerEjercicios.setLayoutManager(new LinearLayoutManager(this));
        recyclerSesion.setLayoutManager(new LinearLayoutManager(this));
        recyclerEjercicios.setAdapter(adapterDisponibles);
        recyclerSesion.setAdapter(adapterSesion);

        findViewById(R.id.btnGuardarSesion).setOnClickListener(v -> guardarSesion());

        inputBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarEjercicios(s.toString().trim());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        cargarEjercicios();
    }

    private void cargarEjercicios() {
        db.collection("ejercicios").get().addOnSuccessListener(q -> {
            ejerciciosDisponibles.clear();
            for (DocumentSnapshot doc : q.getDocuments()) {
                Ejercicio e = doc.toObject(Ejercicio.class);
                if (e != null) ejerciciosDisponibles.add(e);
            }
            filtrarEjercicios("");
            if (sesionId != null) cargarSesion();
            else cargarClientes();
        });
    }

    private void filtrarEjercicios(String texto) {
        ejerciciosFiltrados.clear();
        if (texto.isEmpty()) ejerciciosFiltrados.addAll(ejerciciosDisponibles);
        else {
            for (Ejercicio e : ejerciciosDisponibles) {
                if (e.getNombre() != null && e.getNombre().toLowerCase().contains(texto.toLowerCase()))
                    ejerciciosFiltrados.add(e);
            }
        }
        adapterDisponibles.notifyDataSetChanged();
    }

    private void cargarClientes() {
        db.collection("usuarios").whereEqualTo("rol","cliente").get().addOnSuccessListener(q -> {
            List<String> nombres = new ArrayList<>();
            clientesIds.clear();
            for (DocumentSnapshot d : q) {
                String nombre = d.getString("nombre");
                if (nombre != null) {
                    nombres.add(nombre);
                    clientesIds.add(d.getId());
                }
            }
            spinnerClientes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombres));
            if (clienteUidOriginal != null) {
                int index = clientesIds.indexOf(clienteUidOriginal);
                if (index >= 0) spinnerClientes.setSelection(index);
            }
        });
    }

    private void agregarEjercicio(Ejercicio ejercicio) {
        if (ejercicio == null) return;
        for (EjercicioEnSesion e : ejerciciosSesion) {
            if (Objects.equals(e.getEjercicio().getId(), ejercicio.getId())) return;
        }
        ejerciciosSesion.add(new EjercicioEnSesion(ejercicio, 3, 10));
        adapterSesion.notifyDataSetChanged();
    }

    private void cargarSesion() {
        db.collection("sesiones").document(sesionId).get().addOnSuccessListener(doc -> {
            if (!doc.exists()) return;

            inputTitulo.setText(doc.getString("titulo"));
            createdAtOriginal = doc.getString("createdAt");
            clienteUidOriginal = doc.getString("clienteUid");

            List<Map<String,Object>> lista = (List<Map<String,Object>>) doc.get("ejercicios");
            ejerciciosSesion.clear();

            if (lista != null) {
                for (Map<String,Object> m : lista) {
                    if (m == null) continue;

                    Long idLong = m.get("id") instanceof Long ? (Long)m.get("id") : null;
                    int series = m.get("series") != null ? ((Long)m.get("series")).intValue() : 3;
                    int reps = m.get("reps") != null ? ((Long)m.get("reps")).intValue() : 10;

                    Ejercicio ej = null;
                    for (Ejercicio e : ejerciciosDisponibles) {
                        if (idLong != null && Objects.equals(e.getId(), idLong)) { ej = e; break; }
                    }
                    if (ej != null) ejerciciosSesion.add(new EjercicioEnSesion(ej, series, reps));
                }
            }

            adapterSesion.notifyDataSetChanged();
            cargarClientes();
        });
    }

    private void guardarSesion() {
        String titulo = inputTitulo.getText().toString().trim();
        if (titulo.isEmpty()) { Toast.makeText(this, "Ingrese un título", Toast.LENGTH_SHORT).show(); return; }
        if (ejerciciosSesion.isEmpty()) { Toast.makeText(this, "Agregue al menos un ejercicio", Toast.LENGTH_SHORT).show(); return; }
        if (spinnerClientes.getSelectedItemPosition() < 0) { Toast.makeText(this, "Seleccione un cliente", Toast.LENGTH_SHORT).show(); return; }
        if (auth.getCurrentUser() == null) { Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show(); return; }

        List<Map<String,Object>> ejerciciosMap = new ArrayList<>();
        for (EjercicioEnSesion e : ejerciciosSesion) {
            if (e.getSeries() <= 0 || e.getReps() <= 0) { Toast.makeText(this,"Series y reps deben ser mayores a 0",Toast.LENGTH_SHORT).show(); return; }
            Map<String,Object> map = new HashMap<>();
            map.put("id", e.getEjercicio().getId());
            map.put("series", e.getSeries());
            map.put("reps", e.getReps());
            ejerciciosMap.add(map);
        }

        String clienteUid = clientesIds.get(spinnerClientes.getSelectedItemPosition());

        Map<String,Object> sesion = new HashMap<>();
        sesion.put("titulo", titulo);
        sesion.put("clienteUid", clienteUid);
        sesion.put("entrenadorUid", auth.getCurrentUser().getUid());
        sesion.put("ejercicios", ejerciciosMap);
        sesion.put("updatedAt", isoNow());
        sesion.put("createdAt", sesionId == null ? isoNow() : createdAtOriginal);

        if (sesionId == null || sesionId.isEmpty()) {
            db.collection("sesiones").add(sesion).addOnSuccessListener(r -> finish())
                    .addOnFailureListener(e -> Toast.makeText(this,"Error al guardar",Toast.LENGTH_SHORT).show());
        } else {
            db.collection("sesiones").document(sesionId).set(sesion)
                    .addOnSuccessListener(r -> finish())
                    .addOnFailureListener(e -> Toast.makeText(this,"Error al actualizar",Toast.LENGTH_SHORT).show());
        }
    }

    private String isoNow() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(new Date());
    }

    public static class EjercicioEnSesion {
        private Ejercicio ejercicio;
        private int series;
        private int reps;
        public EjercicioEnSesion(Ejercicio e,int s,int r){ this.ejercicio=e; this.series=s; this.reps=r; }
        public Ejercicio getEjercicio(){return ejercicio;}
        public int getSeries(){return series;}
        public void setSeries(int s){series=s;}
        public int getReps(){return reps;}
        public void setReps(int r){reps=r;}
    }
}
