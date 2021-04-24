package com.desarollo.salvavidasapp.Models;


import android.text.format.Time;

import java.io.Serializable;
import java.util.Date;

public class Productos implements Serializable {

     String idProducto;
     String nombreProducto;
     String descripcionProducto;
     String categoriaProducto;
     double precio;
     double descuento;
     String domicilio;
     String EstadoProducto;
    //pendiente definir tipo de dato correcto
     int foto;
     String fechaInicio;
     String horaInicio;
     String fechaFin;
     String horaFin;

    public Productos(){};

    public Productos(String idProducto  , String nombreProducto, String descripcionProducto, String categoriaProducto, double precio, double descuento, String domicilio, String estadoProducto, int foto, String fechaInicio, String horaInicio, String fechaFin, String horaFin) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.categoriaProducto = categoriaProducto;
        this.precio = precio;
        this.descuento = descuento;
        this.domicilio = domicilio;
        this.EstadoProducto = estadoProducto;
        this.foto = foto;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.fechaFin = fechaFin;
        this.horaFin = horaFin;
    }



    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public String getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getEstadoProducto() {
        return EstadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        EstadoProducto = estadoProducto;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
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

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
