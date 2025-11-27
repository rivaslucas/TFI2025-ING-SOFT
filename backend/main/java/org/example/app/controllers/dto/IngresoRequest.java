package org.example.app.controllers.dto;

public class IngresoRequest {
    private String cuilPaciente;
    private String enfermeraNombre;
    private String enfermeraApellido;
    private String informe;
    private String nivelEmergencia;
    private Float temperatura;
    private Float frecuenciaCardiaca;
    private Float frecuenciaRespiratoria;
    private Float tensionSistolica;
    private Float tensionDiastolica;

    // Constructores
    public IngresoRequest() {}

    public IngresoRequest(String cuilPaciente, String enfermeraNombre, String enfermeraApellido, String informe, String nivelEmergencia, Float temperatura, Float frecuenciaCardiaca, Float frecuenciaRespiratoria, Float tensionSistolica, Float tensionDiastolica) {
        this.cuilPaciente = cuilPaciente;
        this.enfermeraNombre = enfermeraNombre;
        this.enfermeraApellido = enfermeraApellido;
        this.informe = informe;
        this.nivelEmergencia = nivelEmergencia;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.tensionSistolica = tensionSistolica;
        this.tensionDiastolica = tensionDiastolica;
    }

    // Getters y setters
    public String getCuilPaciente() { return cuilPaciente; }
    public void setCuilPaciente(String cuilPaciente) { this.cuilPaciente = cuilPaciente; }
    public String getEnfermeraNombre() { return enfermeraNombre; }
    public void setEnfermeraNombre(String enfermeraNombre) { this.enfermeraNombre = enfermeraNombre; }
    public String getEnfermeraApellido() { return enfermeraApellido; }
    public void setEnfermeraApellido(String enfermeraApellido) { this.enfermeraApellido = enfermeraApellido; }
    public String getInforme() { return informe; }
    public void setInforme(String informe) { this.informe = informe; }
    public String getNivelEmergencia() { return nivelEmergencia; }
    public void setNivelEmergencia(String nivelEmergencia) { this.nivelEmergencia = nivelEmergencia; }
    public Float getTemperatura() { return temperatura; }
    public void setTemperatura(Float temperatura) { this.temperatura = temperatura; }
    public Float getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Float frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }
    public Float getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public void setFrecuenciaRespiratoria(Float frecuenciaRespiratoria) { this.frecuenciaRespiratoria = frecuenciaRespiratoria; }
    public Float getTensionSistolica() { return tensionSistolica; }
    public void setTensionSistolica(Float tensionSistolica) { this.tensionSistolica = tensionSistolica; }
    public Float getTensionDiastolica() { return tensionDiastolica; }
    public void setTensionDiastolica(Float tensionDiastolica) { this.tensionDiastolica = tensionDiastolica; }
}