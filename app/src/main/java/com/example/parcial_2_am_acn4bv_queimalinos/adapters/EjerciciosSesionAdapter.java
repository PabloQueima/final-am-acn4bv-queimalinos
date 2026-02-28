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
import com.example.parcial_2_am_acn4bv_queimalinos.models.Sesion;

import java.util.List;

public class EjerciciosSesionAdapter extends RecyclerView.Adapter<EjerciciosSesionAdapter.ViewHolder> {

    public interface OnRemoveListener {
        void onRemove(Sesion.EjercicioRef ejercicio);
    }

    private List<Sesion.EjercicioRef> ejercicios;
    private OnRemoveListener listener;

    public EjerciciosSesionAdapter(List<Sesion.EjercicioRef> ejercicios, OnRemoveListener listener) {
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

        Sesion.EjercicioRef ejercicio = ejercicios.get(position);

        holder.tvExerciseId.setText("ID: " + ejercicio.getId());
        holder.etSeries.setText(String.valueOf(ejercicio.getSeries()));
        holder.etReps.setText(String.valueOf(ejercicio.getReps()));

        holder.etSeries.addTextChangedListener(new SimpleWatcher(s ->
                ejercicio.setSeries(parseInt(s))));

        holder.etReps.addTextChangedListener(new SimpleWatcher(s ->
                ejercicio.setReps(parseInt(s))));

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(ejercicio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ejercicios != null ? ejercicios.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseId;
        EditText etSeries, etReps;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseId = itemView.findViewById(R.id.tvExerciseId);
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

        interface Callback {
            void onChange(String s);
        }

        private Callback callback;

        public SimpleWatcher(Callback callback) {
            this.callback = callback;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            callback.onChange(s.toString());
        }
    }
}