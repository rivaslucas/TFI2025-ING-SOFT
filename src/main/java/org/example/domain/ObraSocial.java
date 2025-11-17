package org.example.domain;

public class ObraSocial {
    private String nombre;
    private String identificador; // Cambiar a minúscula para seguir convenciones

    public ObraSocial(String identificador, String nombre) {
        this.identificador = identificador;
        this.nombre = nombre;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    // Agregar toString para debugging
    @Override
    public String toString() {
        return "ObraSocial{nombre='" + nombre + "', identificador='" + identificador + "'}";
    }
}