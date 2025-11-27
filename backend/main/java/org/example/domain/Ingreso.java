package org.example.domain;

import org.example.domain.valueobject.FrecuenciaCardiaca;
import org.example.domain.valueobject.FrecuenciaRespiratoria;
import org.example.domain.valueobject.TensionArterial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ingreso implements Comparable<Ingreso>{
    Paciente paciente;
    Enfermera enfermera;
    LocalDateTime fechaIngreso;
    String informe;
    NivelEmergencia nivelEmergencia;
    EstadoIngreso estado;
    Float temperatura;
    FrecuenciaCardiaca frecuenciaCardiaca;
    FrecuenciaRespiratoria frecuenciaRespiratoria;
    TensionArterial tensionArterial;
    private String id;
    private String medicoAsignado; // ✅ Debe inicializarse en null
    private LocalDateTime horaCreacion;

    public Ingreso(String id,
                   Paciente paciente,
                   Enfermera enfermera,
                   String informe,
                   NivelEmergencia nivelEmergencia,
                   Float temperatura,
                   Float frecuenciaCardiaca,
                   Float frecuenciaRespiratoria,
                   Float frecuenciaSistolica,
                   Float frecuenciaDiastolica,
                   LocalDateTime horaEspecifica){
        this.id = id;
        this.paciente = paciente;
        this.enfermera = enfermera;
        this.fechaIngreso = LocalDateTime.now();
        this.informe = informe;
        this.nivelEmergencia = nivelEmergencia;
        this.estado = EstadoIngreso.PENDIENTE;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = new FrecuenciaCardiaca(frecuenciaCardiaca);
        this.frecuenciaRespiratoria = new FrecuenciaRespiratoria(frecuenciaRespiratoria);
        this.tensionArterial = new TensionArterial(frecuenciaSistolica,frecuenciaDiastolica);
        this.fechaIngreso = horaEspecifica != null ? horaEspecifica : LocalDateTime.now();
        this.horaCreacion = this.fechaIngreso;
        this.medicoAsignado = null; // ✅ INICIALIZADO EN NULL
    }

    public Ingreso(String id,
                   Paciente paciente,
                   Enfermera enfermera,
                   String informe,
                   NivelEmergencia nivelEmergencia,
                   Float temperatura,
                   Float frecuenciaCardiaca,
                   Float frecuenciaRespiratoria,
                   Float frecuenciaSistolica,
                   Float frecuenciaDiastolica) {
        this(id, paciente, enfermera, informe, nivelEmergencia, temperatura,
                frecuenciaCardiaca, frecuenciaRespiratoria, frecuenciaSistolica,
                frecuenciaDiastolica, null);
    }

    public String getCuilPaciente(){
        return this.paciente.getCuil();
    }
    public String getId() { return id; }
    public EstadoIngreso getEstado() { return estado; }
    public void setEstado(EstadoIngreso estado) { this.estado = estado; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public NivelEmergencia getNivelEmergencia() { return nivelEmergencia; }
    public String getMedicoAsignado() { return medicoAsignado; }
    public void setMedicoAsignado(String medicoAsignado) {
        this.medicoAsignado = medicoAsignado;
    }

    @Override
    public int compareTo(Ingreso o) {
        return this.nivelEmergencia.compararCon(o.nivelEmergencia);
    }
    public LocalDateTime getHoraCreacion() {
        return horaCreacion;
    }
    public Paciente getPaciente() {
        return paciente;
    }

    public Enfermera getEnfermera() {
        return enfermera;
    }

    public String getInforme() {
        return informe;
    }

    public Float getTemperatura() {
        return temperatura;
    }
}