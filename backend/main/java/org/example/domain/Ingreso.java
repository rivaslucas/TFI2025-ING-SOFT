package org.example.domain;

import org.example.domain.valueobject.FrecuenciaCardiaca;
import org.example.domain.valueobject.FrecuenciaRespiratoria;
import org.example.domain.valueobject.TensionArterial;
import java.time.LocalDateTime;

public class Ingreso implements Comparable<Ingreso> {

    // Campos
    private String id;
    private Paciente paciente;
    private Enfermera enfermera;
    private LocalDateTime fechaIngreso;
    private String informe;
    private NivelEmergencia nivelEmergencia;
    private EstadoIngreso estado;
    private Float temperatura;
    private FrecuenciaCardiaca frecuenciaCardiaca;
    private FrecuenciaRespiratoria frecuenciaRespiratoria;
    private TensionArterial tensionArterial;
    private String medicoAsignado;
    private LocalDateTime horaCreacion;

    // ✅ CONSTRUCTOR PRINCIPAL (11 parámetros)
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
                   LocalDateTime horaEspecifica) {

        this.id = id;
        this.paciente = paciente;
        this.enfermera = enfermera;
        this.informe = informe;
        this.nivelEmergencia = nivelEmergencia;
        this.temperatura = temperatura;
        this.frecuenciaCardiaca = new FrecuenciaCardiaca(frecuenciaCardiaca);
        this.frecuenciaRespiratoria = new FrecuenciaRespiratoria(frecuenciaRespiratoria);
        this.tensionArterial = new TensionArterial(frecuenciaSistolica, frecuenciaDiastolica);
        this.fechaIngreso = horaEspecifica != null ? horaEspecifica : LocalDateTime.now();
        this.horaCreacion = this.fechaIngreso;
        this.medicoAsignado = null;
        this.estado = EstadoIngreso.PENDIENTE;

        System.out.println("✅ Ingreso creado - ID: " + id +
                ", Paciente: " + paciente.getNombre() +
                ", Temperatura: " + temperatura);
    }

    // ✅ CONSTRUCTOR ALTERNATIVO (10 parámetros - sin hora específica)
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
                frecuenciaDiastolica, LocalDateTime.now());
    }

    // ✅ CONSTRUCTOR POR DEFECTO (para casos especiales)
    public Ingreso() {
        this.estado = EstadoIngreso.PENDIENTE;
        this.medicoAsignado = null;
        this.fechaIngreso = LocalDateTime.now();
        this.horaCreacion = this.fechaIngreso;
    }

    // ✅ MÉTODOS DE ACCESO (GETTERS)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Enfermera getEnfermera() {
        return enfermera;
    }

    public void setEnfermera(Enfermera enfermera) {
        this.enfermera = enfermera;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public NivelEmergencia getNivelEmergencia() {
        return nivelEmergencia;
    }

    public void setNivelEmergencia(NivelEmergencia nivelEmergencia) {
        this.nivelEmergencia = nivelEmergencia;
    }

    public EstadoIngreso getEstado() {
        return estado;
    }

    public void setEstado(EstadoIngreso estado) {
        this.estado = estado;
    }

    public Float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Float temperatura) {
        this.temperatura = temperatura;
    }

    // ✅ GETTERS PARA LOS VALORES NUMÉRICOS DE LOS OBJETOS DE VALOR

    public Float getFrecuenciaCardiaca() {
        return frecuenciaCardiaca != null ? frecuenciaCardiaca.getValor() : null;
    }

    public void setFrecuenciaCardiaca(Float valor) {
        this.frecuenciaCardiaca = new FrecuenciaCardiaca(valor);
    }

    public Float getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria != null ? frecuenciaRespiratoria.getValor() : null;
    }

    public void setFrecuenciaRespiratoria(Float valor) {
        this.frecuenciaRespiratoria = new FrecuenciaRespiratoria(valor);
    }

    public Float getTensionSistolica() {
        return tensionArterial != null ? tensionArterial.getSistolica() : null;
    }

    public Float getTensionDiastolica() {
        return tensionArterial != null ? tensionArterial.getDiastolica() : null;
    }

    public void setTensionArterial(Float sistolica, Float diastolica) {
        this.tensionArterial = new TensionArterial(sistolica, diastolica);
    }

    // ✅ GETTERS PARA LOS OBJETOS COMPLETOS

    public FrecuenciaCardiaca getFrecuenciaCardiacaObject() {
        return frecuenciaCardiaca;
    }

    public FrecuenciaRespiratoria getFrecuenciaRespiratoriaObject() {
        return frecuenciaRespiratoria;
    }

    public TensionArterial getTensionArterialObject() {
        return tensionArterial;
    }

    public String getMedicoAsignado() {
        return medicoAsignado;
    }

    public void setMedicoAsignado(String medicoAsignado) {
        this.medicoAsignado = medicoAsignado;
    }

    public LocalDateTime getHoraCreacion() {
        return horaCreacion;
    }

    public void setHoraCreacion(LocalDateTime horaCreacion) {
        this.horaCreacion = horaCreacion;
    }

    // ✅ MÉTODO PARA OBTENER CUIL DEL PACIENTE
    public String getCuilPaciente() {
        return paciente != null ? paciente.getCuil() : null;
    }

    // ✅ MÉTODO compareTo PARA ORDENAR
    @Override
    public int compareTo(Ingreso o) {
        if (this.nivelEmergencia == null || o.nivelEmergencia == null) {
            return 0;
        }
        return this.nivelEmergencia.compararCon(o.nivelEmergencia);
    }

    // ✅ MÉTODO toString PARA DEBUG
    @Override
    public String toString() {
        return "Ingreso{" +
                "id='" + id + '\'' +
                ", paciente=" + (paciente != null ? paciente.getNombre() + " " + paciente.getApellido() : "null") +
                ", cuil='" + getCuilPaciente() + '\'' +
                ", nivelEmergencia=" + nivelEmergencia +
                ", estado=" + estado +
                ", temperatura=" + temperatura +
                ", frecuenciaCardiaca=" + getFrecuenciaCardiaca() +
                ", frecuenciaRespiratoria=" + getFrecuenciaRespiratoria() +
                ", tensionArterial=" + getTensionSistolica() + "/" + getTensionDiastolica() +
                ", medicoAsignado='" + medicoAsignado + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                '}';
    }

    // ✅ MÉTODO PARA CREAR UNA COPIA (útil para testing)
    public Ingreso copy() {
        Ingreso copy = new Ingreso();
        copy.id = this.id;
        copy.paciente = this.paciente;
        copy.enfermera = this.enfermera;
        copy.fechaIngreso = this.fechaIngreso;
        copy.informe = this.informe;
        copy.nivelEmergencia = this.nivelEmergencia;
        copy.estado = this.estado;
        copy.temperatura = this.temperatura;

        if (this.frecuenciaCardiaca != null) {
            copy.frecuenciaCardiaca = new FrecuenciaCardiaca(this.getFrecuenciaCardiaca());
        }

        if (this.frecuenciaRespiratoria != null) {
            copy.frecuenciaRespiratoria = new FrecuenciaRespiratoria(this.getFrecuenciaRespiratoria());
        }

        if (this.tensionArterial != null) {
            copy.tensionArterial = new TensionArterial(
                    this.getTensionSistolica(),
                    this.getTensionDiastolica()
            );
        }

        copy.medicoAsignado = this.medicoAsignado;
        copy.horaCreacion = this.horaCreacion;

        return copy;
    }
}