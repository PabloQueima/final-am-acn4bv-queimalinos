package com.tuapp.ui.entrenador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuapp.R;
import com.tuapp.model.Session;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    public interface OnSessionActionListener {
        void onEdit(Session session);
        void onDelete(Session session);
    }

    private List<Session> sessionList;
    private OnSessionActionListener listener;

    public SessionAdapter(List<Session> sessionList, OnSessionActionListener listener) {
        this.sessionList = sessionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Session session = sessionList.get(position);

        holder.tvNombre.setText(session.getNombre());
        holder.tvClienteUid.setText("Cliente: " + session.getClienteUid());
        holder.tvEjerciciosCount.setText("Ejercicios: " + 
                (session.getEjercicios() != null ? session.getEjercicios().size() : 0));

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(session));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(session));
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvClienteUid, tvEjerciciosCount;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreSesion);
            tvClienteUid = itemView.findViewById(R.id.tvClienteUid);
            tvEjerciciosCount = itemView.findViewById(R.id.tvEjerciciosCount);
            btnEdit = itemView.findViewById(R.id.btnEditSesion);
            btnDelete = itemView.findViewById(R.id.btnDeleteSesion);
        }
    }
}