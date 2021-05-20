package com.desarollo.salvavidasapp.Models;

public class TipoComidas {

        private String TipoComida;
        private String foto;

        public TipoComidas(){

        }

        public TipoComidas(String TipoComida, String foto) {
            this.TipoComida = TipoComida;
            this.foto = foto;
        }

        public String getTipoComida() {
            return TipoComida;
        }

        public void setTipoComida(String tipoComida) {
            TipoComida = tipoComida;
        }

        public String getFoto() {
            return foto;
        }

        public void setFoto(String foto) {
            this.foto = foto;
        }
    }


