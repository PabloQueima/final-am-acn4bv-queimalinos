package com.example.parcial_2_am_acn4bv_queimalinos.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parcial_2_am_acn4bv_queimalinos.R;
import com.example.parcial_2_am_acn4bv_queimalinos.activities.EditarSesionActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class SesionesAdapter extends RecyclerView.Adapter<SesionesAdapter.ViewHolder> {

    public interface OnSesionActionListener {
        void onDelete(String sesionId);
    }

    private Context context;
    private List<DocumentSnapshot> sesiones;
    private OnSesionActionListener listener;

    public SesionesAdapter(Context context,
                           List<DocumentSnapshot> sesiones,
                           OnSesionActionListener listener) {
        this.context = context;
        this.sesiones = sesiones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_sesion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DocumentSnapshot doc = sesiones.get(position);

        holder.txtTitulo.setText(doc.getString("titulo"));

        holder.btnEditar.setOnClickListener(v -> {
            Intent i = new Intent(context, EditarSesionActivity.class);
            i.putExtra("sesionId", doc.getId());
            context.startActivity(i);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(doc.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sesiones != null ? sesiones.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo;
        Button btnEditar, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}