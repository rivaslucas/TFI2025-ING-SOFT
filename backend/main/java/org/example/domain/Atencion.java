package org.example.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Atencion {
    private String id;
    private Ingreso ingreso;
    private Medico medico;
    private String informeMedico;
    private LocalDateTime fechaHora;

    public Atencion(Ingreso ingreso, String informeMedico, Medico medico) {
        this.id = UUID.randomUUID().toString();
        this.ingreso = ingreso;
        this.medico = medico;
        this.informeMedico = informeMedico;
        this.fechaHora = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public Ingreso getIngreso() { return ingreso; }
    public Medico getMedico() { return medico; }
    public String getInformeMedico() { return informeMedico; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}