package com.example.parcial_2_am_acn4bv_queimalinos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.example.parcial_2_am_acn4bv_queimalinos.model.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    public interface OnExerciseClickListener {
        void onAdd(Exercise exercise);
    }

    private List<Exercise> exerciseList;
    private OnExerciseClickListener listener;

    public ExerciseAdapter(List<Exercise> exerciseList, OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_selectable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);

        holder.tvNombre.setText(exercise.getNombre());
        holder.tvParteCuerpo.setText(exercise.getParteCuerpo());
        holder.tvElemento.setText(exercise.getElemento());

        holder.btnAdd.setOnClickListener(v -> listener.onAdd(exercise));
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvParteCuerpo, tvElemento;
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