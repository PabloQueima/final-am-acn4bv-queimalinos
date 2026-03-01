package com.example.final_am_acn4bv_queimalinos.models;

import java.util.List;

public class SesionCompletada {

    private String id; // documentId
    private String clienteUid;
    private String sesionId;
    private String tituloSesion;
    private List<Sesion.EjercicioRef> ejerciciosSnapshot;
    private String fechaInicio;
    private String fechaFin;
    private long duracionSegundos;
    private String createdAt;

    public SesionCompletada() {
        // Constructor vacío obligatorio para Firestore
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteUid() {
        return clienteUid;
    }

    public void setClienteUid(String clienteUid) {
        this.clienteUid = clienteUid;
    }

    public String getSesionId() {
        return sesionId;
    }

    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    public String getTituloSesion() {
        return tituloSesion;
    }

    public void setTituloSesion(String tituloSesion) {
        this.tituloSesion = tituloSesion;
    }

    public List<Sesion.EjercicioRef> getEjerciciosSnapshot() {
        return ejerciciosSnapshot;
    }

    public void setEjerciciosSnapshot(List<Sesion.EjercicioRef> ejerciciosSnapshot) {
        this.ejerciciosSnapshot = ejerciciosSnapshot;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public long getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(long duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}