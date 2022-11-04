package appcyb.danielpativas.cobrosydeudas.entidades;

import java.io.Serializable;

public class Cobro implements Serializable {

    private Integer id;
    private String nombre;
    private String apellido;
    private String fechacobro;
    private String cantidad;
    private String tipo;
    private String estado;
    private String contacto;

    public Cobro(String id, String nombre, String apellido, String fechacobro, String cantidad, String tipo, String estado, String contacto) {
        this.id = Integer.valueOf(id);
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechacobro = fechacobro;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.estado = estado;
        this.contacto = contacto;
    }

    public Cobro() {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechacobro = fechacobro;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.estado = estado;
        this.contacto = contacto;
    }

    public String getContacto() {
        return contacto;
    }

    public String setContacto(String contacto) {
        this.contacto = contacto;
        return contacto;
    }

    public String getEstado() {
        return estado;
    }

    public String setEstado(String estado) {
        this.estado = estado;
        return estado;
    }



    public Integer getId() {
        return id;
    }

    public String setId(Integer id) {
        this.id = id;
        return null;
    }

    public String getNombre() {
        return nombre;
    }

    public String setNombre(String nombre) {
        this.nombre = nombre;
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String setApellido(String apellido) {
        this.apellido = apellido;
        return apellido;
    }

    public String getFechacobro() {
        return fechacobro;
    }

    public String setFechacobro(String fechacobro) {
        this.fechacobro = fechacobro;
        return fechacobro;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String setCantidad(String cantidad) {
        this.cantidad = cantidad;
        return cantidad;
    }

    public String getTipo() {
        return tipo;
    }
    public String setTipo(String tipo) {
        this.tipo = tipo;
        return tipo;
    }

    public String setDescripcion(String descripcion) {
        this.tipo = descripcion;
        return descripcion;
    }
}
