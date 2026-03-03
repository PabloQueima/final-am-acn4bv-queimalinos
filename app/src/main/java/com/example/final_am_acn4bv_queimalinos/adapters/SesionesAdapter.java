package com.example.final_am_acn4bv_queimalinos.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_am_acn4bv_queimalinos.R;
import com.example.final_am_acn4bv_queimalinos.activities.EditarSesionActivity;
import com.example.final_am_acn4bv_queimalinos.models.Sesion;
import com.example.final_am_acn4bv_queimalinos.models.Usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SesionesAdapter extends RecyclerView.Adapter<SesionesAdapter.ViewHolder> {

    public interface OnSesionActionListener {
        void onDelete(String sesionId);
    }

    private Context context;
    private List<Sesion> sesiones;
    private OnSesionActionListener listener;
    private Map<String, Usuario> usuariosMap = new HashMap<>();

    public SesionesAdapter(Context context,
                           List<Sesion> sesiones,
                           OnSesionActionListener listener) {
        this.context = context;
        this.sesiones = sesiones;
        this.listener = listener;
    }

    public void setUsuariosMap(Map<String, Usuario> map) {
        this.usuariosMap = map;
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

        Sesion sesion = sesiones.get(position);

        holder.txtTitulo.setText(sesion.getTitulo() != null ? sesion.getTitulo() : "");

        Usuario usuario = usuariosMap.get(sesion.getClienteUid());

        if (usuario != null) {
            holder.txtCliente.setText("Cliente: " +
                    usuario.getNombre() + " - " + usuario.getEmail());
        } else {
            holder.txtCliente.setText("Cliente: Cargando...");
        }

        holder.txtEjercicios.setText("Ejercicios: " +
                (sesion.getEjercicios() != null ? sesion.getEjercicios().size() : 0));

        holder.txtFecha.setText("Creado: " +
                (sesion.getCreatedAt() != null ? sesion.getCreatedAt() : ""));

        holder.btnEditar.setOnClickListener(v -> {
            if (sesion.getId() == null) return;

            Intent i = new Intent(context, EditarSesionActivity.class);
            i.putExtra("sesionId", sesion.getId());
            context.startActivity(i);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null && sesion.getId() != null) {
                listener.onDelete(sesion.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sesiones != null ? sesiones.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo, txtCliente, txtEjercicios, txtFecha;
        Button btnEditar, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtEjercicios = itemView.findViewById(R.id.txtEjercicios);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnEditar = itemView.findViewById(R.id.btnEditarItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
        }
    }

}
