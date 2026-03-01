package com.example.final_am_acn4bv_queimalinos.models;

import java.util.List;

public class Sesion {

    private String id; // documentId de Firestore
    private String titulo;
    private String clienteUid;
    private String entrenadorUid;
    private List<EjercicioRef> ejercicios;
    private String createdAt;
    private String updatedAt;

    public Sesion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getClienteUid() {
        return clienteUid;
    }

    public void setClienteUid(String clienteUid) {
        this.clienteUid = clienteUid;
    }

    public String getEntrenadorUid() {
        return entrenadorUid;
    }

    public void setEntrenadorUid(String entrenadorUid) {
        this.entrenadorUid = entrenadorUid;
    }

    public List<EjercicioRef> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<EjercicioRef> ejercicios) {
        this.ejercicios = ejercicios;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class EjercicioRef {

        private long id;
        private int series;
        private int reps;

        public EjercicioRef() {
            // Constructor vacío obligatorio
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getSeries() {
            return series;
        }

        public void setSeries(int series) {
            this.series = series;
        }

        public int getReps() {
            return reps;
        }

        public void setReps(int reps) {
            this.reps = reps;
        }
    }
}