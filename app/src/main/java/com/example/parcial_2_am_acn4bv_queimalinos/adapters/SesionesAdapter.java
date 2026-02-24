package com.example.parcial_2_am_acn4bv_queimalinos;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class SesionesAdapter extends RecyclerView.Adapter<SesionesAdapter.ViewHolder> {

    private Context context;
    private List<DocumentSnapshot> sesiones;

    public SesionesAdapter(Context context, List<DocumentSnapshot> sesiones) {
        this.context = context;
        this.sesiones = sesiones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_sesion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DocumentSnapshot doc = sesiones.get(position);

        holder.txtTitulo.setText(doc.getString("titulo"));

        holder.btnEditar.setOnClickListener(v -> {
            Intent i = new Intent(context, EditarSesionActivity.class);
            i.putExtra("sesionId", doc.getId());
            context.startActivity(i);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            ((EntrenadorActivity) context).eliminarSesion(doc.getId());
        });
    }

    @Override
    public int getItemCount() {
        return sesiones.size();
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