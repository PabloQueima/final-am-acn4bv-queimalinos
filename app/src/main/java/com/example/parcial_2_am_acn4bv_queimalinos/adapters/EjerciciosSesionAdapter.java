package com.example.parcial_2_am_acn4bv_queimalinos.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.example.parcial_2_am_acn4bv_queimalinos.activities.EditarSesionActivity;

import java.util.List;

public class EjerciciosSesionAdapter extends RecyclerView.Adapter<EjerciciosSesionAdapter.ViewHolder> {

    public interface OnRemoveListener {
        void onRemove(EditarSesionActivity.EjercicioEnSesion ejercicio);
    }

    private List<EditarSesionActivity.EjercicioEnSesion> ejercicios;
    private OnRemoveListener listener;

    public EjerciciosSesionAdapter(List<EditarSesionActivity.EjercicioEnSesion> ejercicios, OnRemoveListener listener) {
        this.ejercicios = ejercicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio_sesion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EditarSesionActivity.EjercicioEnSesion ejercicio = ejercicios.get(position);

        // Nombre del ejercicio
        holder.tvNombre.setText(ejercicio.getEjercicio().getNombre());

        // Elemento y parte del cuerpo reales
        String elemento = ejercicio.getEjercicio().getElemento() != null ? ejercicio.getEjercicio().getElemento() : "";
        String parte = ejercicio.getEjercicio().getParteCuerpo() != null ? ejercicio.getEjercicio().getParteCuerpo() : "";
        holder.tvInfo.setText(elemento + "-" + parte);

        // Series y reps
        holder.etSeries.setText(String.valueOf(ejercicio.getSeries()));
        holder.etReps.setText(String.valueOf(ejercicio.getReps()));

        holder.etSeries.addTextChangedListener(new SimpleWatcher(s -> ejercicio.setSeries(parseInt(s))));
        holder.etReps.addTextChangedListener(new SimpleWatcher(s -> ejercicio.setReps(parseInt(s))));

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(ejercicio);
        });
    }

    @Override
    public int getItemCount() {
        return ejercicios != null ? ejercicios.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvInfo;
        EditText etSeries, etReps;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvExerciseId);
            tvInfo = itemView.findViewById(R.id.tvExerciseInfo); // <-- nuevo
            etSeries = itemView.findViewById(R.id.etSeries);
            etReps = itemView.findViewById(R.id.etReps);
            btnRemove = itemView.findViewById(R.id.btnRemoveExercise);
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static class SimpleWatcher implements TextWatcher {
        interface Callback { void onChange(String s); }
        private Callback callback;
        public SimpleWatcher(Callback cb) { callback = cb; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) { callback.onChange(s.toString()); }
    }
}
