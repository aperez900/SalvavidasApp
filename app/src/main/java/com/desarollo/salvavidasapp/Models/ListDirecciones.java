package com.desarollo.salvavidasapp.Models;


public class ListDirecciones {
    public String nombreDireccion;
    public String direccionUsuario;
    public String municipioDireccion;
    public int imagenID;
    public String seleccion;
    public double lat;
    public double lng;

    public ListDirecciones(){}

    public ListDirecciones(String nombreDireccion, String direccionUsuario, String municipioDireccion,
                           int imagenID,String seleccion, Double lat, Double lng) {
        this.nombreDireccion = nombreDireccion;
        this.direccionUsuario = direccionUsuario;
        this.municipioDireccion = municipioDireccion;
        this.imagenID = imagenID;
        this.seleccion = seleccion;
        this.lat = lat;
        this.lng = lng;
    }

    public String getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
