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

    public void setId(int id) {
        this.id = id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getMeInteresa() {
        return meInteresa;
    }

    public void setMeInteresa(String meInteresa) {
        this.meInteresa = meInteresa;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
