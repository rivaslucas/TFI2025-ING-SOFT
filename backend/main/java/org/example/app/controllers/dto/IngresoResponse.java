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

    // ✅ NUEVOS CAMPOS: Datos de triaje
    private Float temperatura;
    private Float frecuenciaCardiaca;
    private Float frecuenciaRespiratoria;
    private Float tensionSistolica;
    private Float tensionDiastolica;
    private String informeEnfermeria;

    // Constructores
    public IngresoResponse() {}

    // Constructor original (para compatibilidad)
    public IngresoResponse(String id, String pacienteNombre, String pacienteApellido, String pacienteCuil,
                           String nivelEmergencia, String estado, LocalDateTime fechaIngreso,
                           String enfermeraNombre) {
        this.id = id;
        this.pacienteNombre = pacienteNombre;
        this.pacienteApellido = pacienteApellido;
        this.pacienteCuil = pacienteCuil;
        this.nivelEmergencia = nivelEmergencia;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
        this.enfermeraNombre = enfermeraNombre;
    }

    // ✅ NUEVO: Constructor completo con datos de triaje
    public IngresoResponse(String id, String pacienteNombre, String pacienteApellido, String pacienteCuil,
                           String nivelEmergencia, String estado, LocalDateTime fechaIngreso,
                           String enfermeraNombre, Float temperatura, Float frecuenciaCardiaca,
                           Float frecuenciaRespiratoria, Float tensionSistolica, Float tensionDiastolica,
                           String informeEnfermeria) {
        this.id = id;
        this.pacienteNombre = pacienteNombre;
        this.pacienteApellido = pacienteApellido;
        this.pacienteCuil = pacienteCuil;
        this.nivelEmergencia = nivelEmergencia;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
        this.enfermeraNombre = enfermeraNombre;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.tensionSistolica = tensionSistolica;
        this.tensionDiastolica = tensionDiastolica;
        this.informeEnfermeria = informeEnfermeria;
    }

    // Getters (añadir los nuevos)
    public String getId() { return id; }
    public String getPacienteNombre() { return pacienteNombre; }
    public String getPacienteApellido() { return pacienteApellido; }
    public String getPacienteCuil() { return pacienteCuil; }
    public String getNivelEmergencia() { return nivelEmergencia; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public String getEnfermeraNombre() { return enfermeraNombre; }

    // ✅ NUEVOS GETTERS
    public Float getTemperatura() { return temperatura; }
    public Float getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public Float getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public Float getTensionSistolica() { return tensionSistolica; }
    public Float getTensionDiastolica() { return tensionDiastolica; }
    public String getInformeEnfermeria() { return informeEnfermeria; }

    // Setters opcionales (puedes agregarlos si los necesitas)
    public void setTemperatura(Float temperatura) { this.temperatura = temperatura; }
    public void setFrecuenciaCardiaca(Float frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }
    public void setFrecuenciaRespiratoria(Float frecuenciaRespiratoria) { this.frecuenciaRespiratoria = frecuenciaRespiratoria; }
    public void setTensionSistolica(Float tensionSistolica) { this.tensionSistolica = tensionSistolica; }
    public void setTensionDiastolica(Float tensionDiastolica) { this.tensionDiastolica = tensionDiastolica; }
    public void setInformeEnfermeria(String informeEnfermeria) { this.informeEnfermeria = informeEnfermeria; }
}