package com.desarollo.salvavidasapp.Models;

import java.util.HashMap;
import java.util.Map;

public class Usuarios  {

    public String nombre;
    public String apellido;
    public String direccion;
    public String municipio;
    public String identificacion;
    public String celular;
    public String correo;
    public Boolean habilitado;
    public String[] comidas_preferidas ;
    public String tokenId;

    public Usuarios(){

    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String[] getComidas_preferidas() {
        return comidas_preferidas;
    }

    public void setComidas_preferidas(String[] comidas_preferidas) {
        this.comidas_preferidas = comidas_preferidas;
    }
}
