package com.example.final_am_acn4bv_queimalinos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_am_acn4bv_queimalinos.R;
import com.example.final_am_acn4bv_queimalinos.models.Ejercicio;

import java.util.List;

public class EjerciciosDisponiblesAdapter
        extends RecyclerView.Adapter<EjerciciosDisponiblesAdapter.ViewHolder> {

    public interface OnExerciseClickListener {
        void onAdd(Ejercicio exercise);
    }

    private List<Ejercicio> exerciseList;
    private OnExerciseClickListener listener;

    public EjerciciosDisponiblesAdapter(List<Ejercicio> exerciseList,
                                        OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio_disponible, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Ejercicio exercise = exerciseList.get(position);

        String nombre = exercise.getNombre();
        String parteCuerpo = exercise.getParteCuerpo();
        String elemento = exercise.getElemento();

        holder.tvNombre.setText(nombre != null ? nombre : "");
        holder.tvParteCuerpo.setText(parteCuerpo != null ? parteCuerpo : "");
        holder.tvElemento.setText(elemento != null ? elemento : "");

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdd(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList != null ? exerciseList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        TextView tvParteCuerpo;
        TextView tvElemento;
        Button btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            tvParteCuerpo = itemView.findViewById(R.id.tvParteCuerpo);
            tvElemento = itemView.findViewById(R.id.tvElemento);
            btnAdd = itemView.findViewById(R.id.btnAgregarEjercicio);
        }
    }
}