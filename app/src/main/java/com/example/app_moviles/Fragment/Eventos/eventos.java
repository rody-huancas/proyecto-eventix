package com.example.app_moviles.Fragment.Eventos;

public class eventos {
    private int id,estado;
    private Double precio;
    private String nombre, descripcion, direccion,categoria,fechaEvento,meInteresa,usuario,foto;

    public eventos(int id, int estado, Double precio, String nombre, String descripcion, String direccion, String categoria, String fechaEvento, String meInteresa, String usuario, String foto) {
        this.id = id;
        this.estado = estado;
        this.precio = precio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.categoria = categoria;
        this.fechaEvento = fechaEvento;
        this.meInteresa = meInteresa;
        this.usuario = usuario;
        this.foto = foto;
    }

    public int getId() {
        return id;
    }


    public int getEstado() {
        return estado;
    }


    public Double getPrecio() {
        return precio;
    }


    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }


    public String getDireccion() {
        return direccion;
    }


    public String getCategoria() {
        return categoria;
    }


    public String getFechaEvento() {
        return fechaEvento;
    }


    public String getMeInteresa() {
        return meInteresa;
    }


    public String getUsuario() {
        return usuario;
    }

    public String getFoto() {
        return foto;
    }

}
