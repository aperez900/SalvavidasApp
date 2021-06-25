package com.desarollo.salvavidasapp.Models;

public class ProductosEnTramite {

    private String idProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private String categoriaProducto;
    private String subCategoriaProducto;
    private double precio;
    private double descuento;
    private String domicilio;
    private String direccion;
    private String estadoProducto;
    private String foto;
    private String fechaInicio;
    private String horaInicio;
    private String fechaFin;
    private String horaFin;
    private String nombreEmpresa;
    private int cantidad;
    private double precioDomicilio;
    private String IdVendedor;
    private String IdusuarioSolicitud;
    private String usuarioSolicitud;
    private String correoUsuarioSolicitud;
    private String estadoSolicitud;


    public ProductosEnTramite(){

    }

    public ProductosEnTramite(String idProducto, String nombreProducto, String descripcionProducto, String categoriaProducto,
                     String subCategoriaProducto, double precio, double descuento, String domicilio, String estadoProducto,
                     String foto, String fechaInicio, String horaInicio, String fechaFin, String horaFin, String nombreEmpresa,String direccion,
                     int cantidad, double precioDomicilio, String IdVendedor, String IdusuarioSolicitud, String usuarioSolicitud, String correoUsuarioSolicitud,
                     String estadoSolicitud) {
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
        this.nombreEmpresa = nombreEmpresa;
        this.direccion = direccion;
        this.cantidad = cantidad;
        this.precioDomicilio = precioDomicilio;
        this.IdVendedor = IdVendedor;
        this.IdusuarioSolicitud = IdusuarioSolicitud;
        this.usuarioSolicitud = usuarioSolicitud;
        this.correoUsuarioSolicitud = correoUsuarioSolicitud;
        this.estadoSolicitud = estadoSolicitud;
    }


    public double getPrecioDomicilio() {
        return precioDomicilio;
    }

    public void setPrecioDomicilio(double precioDomicilio) {
        this.precioDomicilio = precioDomicilio;
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

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getIdVendedor() {
        return IdVendedor;
    }

    public void setIdVendedor(String IdVendedor) {
        this.IdVendedor = IdVendedor;
    }

    public String getIdusuarioSolicitud() {
        return IdusuarioSolicitud;
    }

    public void setIdusuarioSolicitud(String idusuarioSolicitud) {
        IdusuarioSolicitud = idusuarioSolicitud;
    }

    public String getUsuarioSolicitud() {
        return usuarioSolicitud;
    }

    public void setUsuarioSolicitud(String usuarioSolicitud) {
        this.usuarioSolicitud = usuarioSolicitud;
    }

    public String getCorreoUsuarioSolicitud() {
        return correoUsuarioSolicitud;
    }

    public void setCorreoUsuarioSolicitud(String correoUsuarioSolicitud) {
        this.correoUsuarioSolicitud = correoUsuarioSolicitud;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }
}
