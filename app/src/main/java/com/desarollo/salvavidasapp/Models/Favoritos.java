package com.desarollo.salvavidasapp.Models;

public class Favoritos {

    public String descripcion;
    public String foto;
    public Boolean estado;

    public Favoritos(){ }

    public Favoritos(String descripcion, String foto, Boolean estado) {
        this.descripcion = descripcion;
        this.foto = foto;
        this.estado = estado;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getSubTipoComida() {
        return descripcion;
    }

    public void setSubTipoComida(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

}


