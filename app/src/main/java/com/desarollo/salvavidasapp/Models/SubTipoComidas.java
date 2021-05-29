package com.desarollo.salvavidasapp.Models;

public class SubTipoComidas {

        private String descripcion;
        private String foto;

        public SubTipoComidas(){

        }

    public SubTipoComidas(String descripcion, String foto) {
        this.descripcion = descripcion;
        this.foto = foto;
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


