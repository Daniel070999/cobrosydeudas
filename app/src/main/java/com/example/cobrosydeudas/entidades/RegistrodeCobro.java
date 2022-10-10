package com.example.cobrosydeudas.entidades;

import java.io.Serializable;

public class RegistrodeCobro implements Serializable {
    private Integer idregistro;
    private String cantidadaumentada;
    private String fechaaumentada;
    private String nuevacantidadaumentada;
    private String descripcionaumento;
    private String accionregistro;
    private String idcobro;

    public RegistrodeCobro(String idregistro, String cantidadaumentada, String fechaaumentada, String nuevacantidadaumentada, String descripcionaumento, String accionregistro, String idcobro) {
        this.idregistro = Integer.valueOf(idregistro);
        this.cantidadaumentada = cantidadaumentada;
        this.fechaaumentada = fechaaumentada;
        this.nuevacantidadaumentada = nuevacantidadaumentada;
        this.descripcionaumento = descripcionaumento;
        this.accionregistro = accionregistro;
        this.idcobro = idcobro;
    }

    public RegistrodeCobro() {
        this.idregistro = idregistro;
        this.cantidadaumentada = cantidadaumentada;
        this.fechaaumentada = fechaaumentada;
        this.nuevacantidadaumentada = nuevacantidadaumentada;
        this.descripcionaumento = descripcionaumento;
        this.accionregistro = accionregistro;
        this.idcobro = idcobro;
    }

    public Integer getIdregistro() {
        return idregistro;
    }

    public void setIdregistro(Integer idregistro) {
        this.idregistro = idregistro;
    }

    public String getCantidadaumentada() {
        return cantidadaumentada;
    }

    public String setCantidadaumentada(String cantidadaumentada) {
        this.cantidadaumentada = cantidadaumentada;
        return cantidadaumentada;
    }

    public String getFechaaumentada() {
        return fechaaumentada;
    }

    public void setFechaaumentada(String fechaaumentada) {
        this.fechaaumentada = fechaaumentada;
    }

    public String getNuevacantidadaumentada() {
        return nuevacantidadaumentada;
    }

    public void setNuevacantidadaumentada(String nuevacantidadaumentada) {
        this.nuevacantidadaumentada = nuevacantidadaumentada;
    }

    public String getDescripcionaumento() {
        return descripcionaumento;
    }

    public void setDescripcionaumento(String descripcionaumento) {
        this.descripcionaumento = descripcionaumento;
    }

    public String getAccionregistro() {
        return accionregistro;
    }

    public void setAccionregistro(String accionregistro) {
        this.accionregistro = accionregistro;
    }

    public String getIdcobro() {
        return idcobro;
    }

    public String setIdcobro(String idcobro) {
        this.idcobro = idcobro;
        return idcobro;
    }
}