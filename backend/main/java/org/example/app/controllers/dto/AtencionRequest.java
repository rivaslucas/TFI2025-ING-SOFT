package org.example.app.controllers.dto;

public class AtencionRequest {
    private String idIngreso;
    private String informeMedico;
    private String medicoMatricula;

    // Constructores
    public AtencionRequest() {}

    public AtencionRequest(String idIngreso, String informeMedico, String medicoMatricula) {
        this.idIngreso = idIngreso;
        this.informeMedico = informeMedico;
        this.medicoMatricula = medicoMatricula;
    }

    // Getters y setters
    public String getIdIngreso() { return idIngreso; }
    public void setIdIngreso(String idIngreso) { this.idIngreso = idIngreso; }
    public String getInformeMedico() { return informeMedico; }
    public void setInformeMedico(String informeMedico) { this.informeMedico = informeMedico; }
    public String getMedicoMatricula() { return medicoMatricula; }
    public void setMedicoMatricula(String medicoMatricula) { this.medicoMatricula = medicoMatricula; }
}