package com.example.loob.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {

    private String codigoPUCP;
    private String correo;
    private String dni;
    private String ocupacion;
    private String rol;

    public UsuarioDTO(){
    }

    public UsuarioDTO(String codigoPUCP, String correo, String dni, String ocupacion, String rol) {
        this.codigoPUCP = codigoPUCP;
        this.correo = correo;
        this.dni = dni;
        this.ocupacion = ocupacion;
        this.rol = rol;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCodigoPUCP() {
        return codigoPUCP;
    }

    public void setCodigoPUCP(String codigoPUCP) {
        this.codigoPUCP = codigoPUCP;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDNI() {
        return dni;
    }

    public void setDNI(String DNI) {
        this.dni = DNI;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}
