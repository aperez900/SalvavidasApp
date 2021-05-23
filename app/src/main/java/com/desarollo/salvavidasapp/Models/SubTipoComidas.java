package com.desarollo.salvavidasapp.Models;

public class SubTipoComidas {

        private String SubTipoComida;
        private String foto;

        public SubTipoComidas(){

        }

    public SubTipoComidas(String subTipoComida, String foto) {
        SubTipoComida = subTipoComida;
        this.foto = foto;
    }

    public String getSubTipoComida() {
        return SubTipoComida;
    }

    public void setSubTipoComida(String subTipoComida) {
        SubTipoComida = subTipoComida;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}


