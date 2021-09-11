package com.desarollo.salvavidasapp.Models;

public class SubTipoProductos {

    public String descripcion;
    public String foto;
    public String tipo;


    public SubTipoProductos(){ }

    public SubTipoProductos(String descripcion, String foto, String tipo) {
        this.descripcion = descripcion;
        this.foto = foto;
        this.tipo = tipo;
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

    public String getTipo() {return tipo; }

    public void setTipo(String tipo) {this.tipo = tipo;}


}


