package com.desarollo.salvavidasapp.Models;


import android.text.format.Time;

import java.util.Date;

public class Productos {

    private String nombreProducto;
    private String descripcionProducto;
    private String categoriaProducto;
    private double precio;
    private double descuento;
    private String domicilio;
    private String EstadoProducto;
    //pendiente definir tipo de dato correcto
    private int foto;
    private String fechaInicio;
    private Time horaInicio;
    private Date fechaFin;
    private Time horafin;

    public Productos(){

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

    public Time getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Time getHorafin() {
        return horafin;
    }

    public void setHorafin(Time horafin) {
        this.horafin = horafin;
    }
}
