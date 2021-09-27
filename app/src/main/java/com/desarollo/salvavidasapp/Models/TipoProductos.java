package com.desarollo.salvavidasapp.Models;

public class TipoProductos {

        private String TipoComida;
        private String foto;

        public TipoProductos(){

        }

        public TipoProductos(String TipoComida, String foto) {
            this.TipoComida = TipoComida;
            this.foto = foto;
        }

        public String getTipoComida() {
            return TipoComida;
        }

        public void setTipoComida(String tipoComida) {
            this.TipoComida = tipoComida;
        }

        public String getFoto() {
            return foto;
        }

        public void setFoto(String foto) {
            this.foto = foto;
        }
    }


