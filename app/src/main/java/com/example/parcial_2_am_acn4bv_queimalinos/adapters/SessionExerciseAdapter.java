package com.tuapp.ui.entrenador;

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

import com.tuapp.R;
import com.tuapp.model.SessionExercise;

import java.util.List;

public class SessionExerciseAdapter extends RecyclerView.Adapter<SessionExerciseAdapter.ViewHolder> {

    public interface OnRemoveListener {
        void onRemove(SessionExercise exercise);
    }

    private List<SessionExercise> sessionExercises;
    private OnRemoveListener listener;

    public SessionExerciseAdapter(List<SessionExercise> sessionExercises, OnRemoveListener listener) {
        this.sessionExercises = sessionExercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SessionExercise exercise = sessionExercises.get(position);

        holder.tvExerciseId.setText("ID: " + exercise.getExerciseId());
        holder.etSeries.setText(String.valueOf(exercise.getSeries()));
        holder.etReps.setText(String.valueOf(exercise.getReps()));

        holder.etSeries.addTextChangedListener(new SimpleWatcher(s ->
                exercise.setSeries(parseInt(s))));

        holder.etReps.addTextChangedListener(new SimpleWatcher(s ->
                exercise.setReps(parseInt(s))));

        holder.btnRemove.setOnClickListener(v -> listener.onRemove(exercise));
    }

    @Override
    public int getItemCount() {
        return sessionExercises.size();
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