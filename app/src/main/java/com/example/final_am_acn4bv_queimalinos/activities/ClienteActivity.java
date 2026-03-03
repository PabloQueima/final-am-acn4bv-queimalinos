package com.example.final_am_acn4bv_queimalinos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.final_am_acn4bv_queimalinos.R;
import com.example.final_am_acn4bv_queimalinos.models.Sesion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ClienteActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView txtBienvenida;
    private TextView txtSesionesCompletadas;
    private LinearLayout contenedorSesiones;
    private LinearLayout contenedorSesionesCompletadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtBienvenida = findViewById(R.id.txtBienvenida);
        txtSesionesCompletadas = findViewById(R.id.txtSesionesCompletadas);
        contenedorSesiones = findViewById(R.id.contenedorSesiones);
        contenedorSesionesCompletadas = findViewById(R.id.contenedorSesionesCompletadas);

        String userUid = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userUid).get().addOnSuccessListener(doc -> {
            if(doc.exists() && doc.getString("nombre") != null){
                txtBienvenida.setText("Bienvenido, " + doc.getString("nombre"));
            } else {
                txtBienvenida.setText("Hola");
            }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, SplashActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });

        cargarSesionesDisponibles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarSesionesCompletadas();
    }

    private void cargarSesionesDisponibles() {
        contenedorSesiones.removeAllViews();

        TextView txtTituloSesionesDisponibles = new TextView(this);
        contenedorSesiones.addView(txtTituloSesionesDisponibles);

        String clienteUid = auth.getCurrentUser().getUid();

        db.collection("sesiones")
                .whereEqualTo("clienteUid", clienteUid)
                .get()
                .addOnSuccessListener(query -> {
                    if(query.isEmpty()){
                        TextView mensaje = new TextView(this);
                        mensaje.setText("Ponete en contacto con un entrenador para obtener sesiones de entrenamiento");
                        contenedorSesiones.addView(mensaje);
                        return;
                    }

                    for(QueryDocumentSnapshot doc : query){
                        Sesion sesion = doc.toObject(Sesion.class);
                        sesion.setId(doc.getId());

                        LinearLayout sesionLayout = new LinearLayout(this);
                        sesionLayout.setOrientation(LinearLayout.VERTICAL);
                        sesionLayout.setPadding(16,16,16,16);

                        TextView titulo = new TextView(this);
                        titulo.setText(sesion.getTitulo());
                        titulo.setTextSize(16);
                        sesionLayout.addView(titulo);

                        TextView entrenador = new TextView(this);
                        entrenador.setText("Cargando entrenador...");
                        sesionLayout.addView(entrenador);

                        db.collection("usuarios").document(sesion.getEntrenadorUid())
                                .get().addOnSuccessListener(ent -> {
                                    if(ent.exists()){
                                        String nombreEnt = ent.getString("nombre");
                                        String mailEnt = ent.getString("email");
                                        entrenador.setText("Creada por: " + nombreEnt + " (" + mailEnt + ")");
                                    } else {
                                        entrenador.setText("Creada por: desconocido");
                                    }
                                });

                        if(sesion.getCreatedAt() != null){
                            TextView txtCreado = new TextView(this);
                            txtCreado.setText("Creada: " + sesion.getCreatedAt());
                            sesionLayout.addView(txtCreado);
                        }

                        if(sesion.getUpdatedAt() != null){
                            TextView txtEditado = new TextView(this);
                            txtEditado.setText("Actualizada: " + sesion.getUpdatedAt());
                            sesionLayout.addView(txtEditado);
                        }

                        Button btnEntrenar = new Button(this);
                        btnEntrenar.setText("A Entrenar!");
                        btnEntrenar.setBackgroundColor(
                                ContextCompat.getColor(this, R.color.color_success)
                        );
                        btnEntrenar.setOnClickListener(v -> {
                            Intent intent = new Intent(this, RealizarSesionActivity.class);
                            intent.putExtra("sesionId", sesion.getId());
                            startActivity(intent);
                        });
                        sesionLayout.addView(btnEntrenar);

                        contenedorSesiones.addView(sesionLayout);

                        View separador = new View(this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                2
                        );
                        params.setMargins(0, 8, 0, 8);
                        separador.setLayoutParams(params);
                        separador.setBackgroundColor(getResources().getColor(R.color.color_primary));
                        contenedorSesiones.addView(separador);
                    }
                });
    }

    private void cargarSesionesCompletadas() {
        contenedorSesionesCompletadas.removeAllViews();
        String clienteUid = auth.getCurrentUser().getUid();

        db.collection("sesionesCompletadas")
                .whereEqualTo("clienteUid", clienteUid)
                .get()
                .addOnSuccessListener(query -> {
                    query.getDocuments().stream()
                            .sorted((a, b) -> {
                                String fa = a.getString("fechaFin");
                                String fb = b.getString("fechaFin");
                                if(fa == null) return 1;
                                if(fb == null) return -1;
                                return fb.compareTo(fa); // descendente
                            })
                            .limit(5)
                            .forEach(doc -> {
                                LinearLayout item = new LinearLayout(this);
                                item.setOrientation(LinearLayout.VERTICAL);
                                item.setPadding(16,16,16,16);

                                String titulo = doc.getString("tituloSesion");
                                String fechaFinStr = doc.getString("fechaFin");
                                Long duracionSegundos = doc.getLong("duracionSegundos");

                                TextView txtTitulo = new TextView(this);
                                txtTitulo.setText(titulo);
                                txtTitulo.setTextSize(16);
                                item.addView(txtTitulo);

                                if(fechaFinStr != null){
                                    TextView txtFecha = new TextView(this);
                                    txtFecha.setText("Completada: " + fechaFinStr);
                                    item.addView(txtFecha);
                                }

                                if(duracionSegundos != null){
                                    long min = duracionSegundos / 60;
                                    long seg = duracionSegundos % 60;
                                    TextView txtDuracion = new TextView(this);
                                    txtDuracion.setText("Duración: " + min + "m " + seg + "s");
                                    item.addView(txtDuracion);
                                }

                                contenedorSesionesCompletadas.addView(item);

                                View separador = new View(this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        1
                                );
                                params.setMargins(0, 4, 0, 4);
                                separador.setLayoutParams(params);
                                separador.setBackgroundColor(getResources().getColor(R.color.color_primary));
                                contenedorSesionesCompletadas.addView(separador);
                            });

                    txtSesionesCompletadas.setText("Sesiones completadas: " + query.size());
                });
    }


}
