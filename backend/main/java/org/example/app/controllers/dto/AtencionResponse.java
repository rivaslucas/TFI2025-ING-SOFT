package org.example.app.controllers.dto;

import java.time.LocalDateTime;

public class AtencionResponse {
    private String id;
    private String idIngreso;
    private String pacienteNombre;
    private String medicoMatricula;
    private String informeMedico;
    private LocalDateTime fechaHora;

    // Constructores
    public AtencionResponse() {}

    public AtencionResponse(String id, String idIngreso, String pacienteNombre, String medicoMatricula, String informeMedico, LocalDateTime fechaHora) {
        this.id = id;
        this.idIngreso = idIngreso;
        this.pacienteNombre = pacienteNombre;
        this.medicoMatricula = medicoMatricula;
        this.informeMedico = informeMedico;
        this.fechaHora = fechaHora;
    }

    // Getters
    public String getId() { return id; }
    public String getIdIngreso() { return idIngreso; }
    public String getPacienteNombre() { return pacienteNombre; }
    public String getMedicoMatricula() { return medicoMatricula; }
    public String getInformeMedico() { return informeMedico; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}