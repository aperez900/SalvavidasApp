package com.desarollo.salvavidasapp.Models;


import android.net.Uri;
import android.text.format.Time;

import java.util.Date;

public class Productos {

    private String idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private String categoriaProducto;
    private String subCategoriaProducto;
    private double precio;
    private double descuento;
    private String domicilio;
    private String estadoProducto;
    private String foto;
    private String fechaInicio;
    private String horaInicio;
    private String fechaFin;
    private String horaFin;

    public Productos(){

    }

    public Productos(String idProducto, String nombreProducto, String descripcionProducto, String categoriaProducto, String subCategoriaProducto, double precio, double descuento, String domicilio, String estadoProducto, String foto, String fechaInicio, String horaInicio, String fechaFin, String horaFin) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.categoriaProducto = categoriaProducto;
        this.subCategoriaProducto = subCategoriaProducto;
        this.precio = precio;
        this.descuento = descuento;
        this.domicilio = domicilio;
        this.estadoProducto = estadoProducto;
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

    public String getSubCategoriaProducto() {
        return subCategoriaProducto;
    }

    public void setSubCategoriaProducto(String subCategoriaProducto) {
        this.subCategoriaProducto = subCategoriaProducto;
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

    public String getEstadoProducto() {
        return estadoProducto;
    }

    public void setEstadoProducto(String estadoProducto) {
        this.estadoProducto = estadoProducto;
    }

    public String getfoto() {
        return foto;
    }

    public void setfoto(String Foto) {
        this.foto = Foto;
    }
}
