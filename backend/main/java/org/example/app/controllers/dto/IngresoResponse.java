package org.example.app.controllers.dto;

import java.time.LocalDateTime;

public class IngresoResponse {
    private String id;
    private String pacienteNombre;
    private String pacienteApellido;
    private String pacienteCuil;
    private String nivelEmergencia;
    private String estado;
    private LocalDateTime fechaIngreso;
    private String enfermeraNombre;

    // Constructores
    public IngresoResponse() {}

    public IngresoResponse(String id, String pacienteNombre, String pacienteApellido, String pacienteCuil, String nivelEmergencia, String estado, LocalDateTime fechaIngreso, String enfermeraNombre) {
        this.id = id;
        this.pacienteNombre = pacienteNombre;
        this.pacienteApellido = pacienteApellido;
        this.pacienteCuil = pacienteCuil;
        this.nivelEmergencia = nivelEmergencia;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
        this.enfermeraNombre = enfermeraNombre;
    }

    // Getters
    public String getId() { return id; }
    public String getPacienteNombre() { return pacienteNombre; }
    public String getPacienteApellido() { return pacienteApellido; }
    public String getPacienteCuil() { return pacienteCuil; }
    public String getNivelEmergencia() { return nivelEmergencia; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public String getEnfermeraNombre() { return enfermeraNombre; }
}