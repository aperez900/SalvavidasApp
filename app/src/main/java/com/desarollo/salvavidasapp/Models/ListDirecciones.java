package com.desarollo.salvavidasapp.Models;


public class ListDirecciones {
    public String nombreDireccion;
    public String direccionUsuario;
    public String municipioDireccion;
    public int imagenID;

    public ListDirecciones(){}

    public ListDirecciones(String nombreDireccion, String direccionUsuario, String municipioDireccion, int imagenID) {
        this.nombreDireccion = nombreDireccion;
        this.direccionUsuario = direccionUsuario;
        this.municipioDireccion = municipioDireccion;
        this.imagenID = imagenID;
    }

    public String getNombreDireccion() {
        return nombreDireccion;
    }

    public void setNombreDireccion(String nombreDireccion) {
        this.nombreDireccion = nombreDireccion;
    }

    public String getDireccionUsuario() {
        return direccionUsuario;
    }

    public void setDireccionUsuario(String direccionUsuario) {
        this.direccionUsuario = direccionUsuario;
    }

    public String getMunicipioDireccion() {
        return municipioDireccion;
    }

    public void setMunicipioDireccion(String municipioDireccion) {
        this.municipioDireccion = municipioDireccion;
    }

    public int getImagenID() {
        return imagenID;
    }

    public void setImagenID(int imagenID) {
        this.imagenID = imagenID;
    }
}
