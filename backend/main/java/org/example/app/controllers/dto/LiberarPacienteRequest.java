package org.example.app.controllers.dto;

public class LiberarPacienteRequest {
    private String medicoMatricula;
    private String motivo;

    // Constructores
    public LiberarPacienteRequest() {}

    public LiberarPacienteRequest(String medicoMatricula, String motivo) {
        this.medicoMatricula = medicoMatricula;
        this.motivo = motivo;
    }

    // Getters y setters
    public String getMedicoMatricula() {
        return medicoMatricula;
    }

    public void setMedicoMatricula(String medicoMatricula) {
        this.medicoMatricula = medicoMatricula;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    // Métodos útiles
    public boolean isValid() {
        return medicoMatricula != null && !medicoMatricula.trim().isEmpty() &&
                motivo != null && !motivo.trim().isEmpty();
    }

    public String getMotivoLimpio() {
        return motivo != null ? motivo.trim() : "";
    }

    public String getMatriculaLimpia() {
        return medicoMatricula != null ? medicoMatricula.trim() : "";
    }

    @Override
    public String toString() {
        return "LiberarPacienteRequest{" +
                "medicoMatricula='" + medicoMatricula + '\'' +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}