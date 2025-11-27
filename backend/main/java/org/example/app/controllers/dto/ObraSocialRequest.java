package org.example.app.controllers.dto;

public class ObraSocialRequest {
    private String nombre;
    private String identificador;

    // Constructores
    public ObraSocialRequest() {}

    public ObraSocialRequest(String nombre, String identificador) {
        this.nombre = nombre;
        this.identificador = identificador;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }
}