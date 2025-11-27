package org.example.app.services;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.app.interfaces.RepositorioPacientes;
import org.example.domain.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ServicioUrgencias {

    private RepositorioPacientes dbPacientes;
    private final RepositorioIngresos repositorioIngresos;

    public ServicioUrgencias(RepositorioPacientes dbPacientes, RepositorioIngresos repositorioIngresos) {
        this.dbPacientes = dbPacientes;
        this.repositorioIngresos = repositorioIngresos;
    }



    public void registrarUrgencia(String cuilPaciente,
                                  Enfermera enfermera,
                                  String informe,
                                  NivelEmergencia emergencia,
                                  Float temperatura,
                                  Float frecuenciaCardiaca,
                                  Float frecuenciaRespiratoria,
                                  Float frecuenciaSistolica,
                                  Float frecuenciaDiastolica){
        if (repositorioIngresos.pacienteTieneIngresosActivos(cuilPaciente)) {
            throw new RuntimeException("El paciente ya tiene un ingreso activo");
        }
        validarCampos(cuilPaciente, enfermera, informe, emergencia, temperatura, frecuenciaCardiaca, frecuenciaRespiratoria, frecuenciaSistolica, frecuenciaDiastolica);

        Paciente paciente = dbPacientes.buscarPacientePorCuil(cuilPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));



        // GENERAR UN ID ÚNICO PARA EL INGRESO
        String idIngreso = UUID.randomUUID().toString();

        Ingreso ingreso = new Ingreso(
                idIngreso,
                paciente,
                enfermera,
                informe,
                emergencia,
                temperatura,
                frecuenciaCardiaca,
                frecuenciaRespiratoria,
                frecuenciaSistolica,
                frecuenciaDiastolica);

        // Guardar en el repositorio compartido
        repositorioIngresos.guardarIngreso(ingreso);
    }

    public List<Ingreso> obtenerIngresosPendientes(){
        List<Ingreso> ingresos = repositorioIngresos.obtenerIngresosPendientes();

        System.out.println("DEBUG: Lista ANTES de ordenar:");
        for (Ingreso ingreso : ingresos) {
            System.out.println("DEBUG: - CUIL: " + ingreso.getPaciente().getCuil() +
                    ", Nivel: " + ingreso.getNivelEmergencia() +
                    ", Prioridad: " + ingreso.getNivelEmergencia().getPrioridad() +
                    ", Fecha: " + ingreso.getFechaIngreso());
        }

        // ✅ CORREGIDO: Ordenar por prioridad y luego por fecha de ingreso
        ingresos.sort(Comparator
                .comparing((Ingreso ingreso) -> ingreso.getNivelEmergencia().getPrioridad())
                .thenComparing(Ingreso::getFechaIngreso));

        System.out.println("DEBUG: Lista DESPUÉS de ordenar:");
        for (Ingreso ingreso : ingresos) {
            System.out.println("DEBUG: - CUIL: " + ingreso.getPaciente().getCuil() +
                    ", Nivel: " + ingreso.getNivelEmergencia() +
                    ", Prioridad: " + ingreso.getNivelEmergencia().getPrioridad() +
                    ", Fecha: " + ingreso.getFechaIngreso());
        }

        return ingresos;
    }

    public void validarCampos(String cuilPaciente,
                              Enfermera enfermera,
                              String informe,
                              NivelEmergencia emergencia,
                              Float temperatura,
                              Float frecuenciaCardiaca,
                              Float frecuenciaRespiratoria,
                              Float frecuenciaSistolica,
                              Float frecuenciaDiastolica){
        // Validar CUIL
        if (cuilPaciente == null || cuilPaciente.trim().isEmpty()) {
            throw new RuntimeException("El CUIL del paciente es obligatorio");
        }

        // Validar enfermera
        if (enfermera == null) {
            throw new RuntimeException("La enfermera es obligatoria");
        }

        // Validar informe médico
        if (informe == null || informe.trim().isEmpty()) {
            throw new RuntimeException("El informe médico es obligatorio");
        }
        if (informe.trim().length() < 10) {
            throw new RuntimeException("El informe médico debe tener al menos 10 caracteres");
        }

        // Validar nivel de emergencia
        if (emergencia == null) {
            throw new RuntimeException("El nivel de emergencia es obligatorio");
        }

        // Validar temperatura
        if (temperatura == null) {
            throw new RuntimeException("La temperatura es obligatoria");
        }

        // ✅ CORREGIDO: Mensajes de error para valores negativos
        // Validar frecuencia cardíaca
        if (frecuenciaCardiaca == null) {
            throw new RuntimeException("La frecuencia cardíaca es obligatoria");
        }
        if (frecuenciaCardiaca < 0) {
            throw new RuntimeException("La frecuencia cardíaca no puede ser negativa");
        }
        if (frecuenciaCardiaca > 300) {
            throw new RuntimeException("La frecuencia cardíaca está fuera de rangos válidos (0-300)");
        }

        // Validar frecuencia respiratoria
        if (frecuenciaRespiratoria == null) {
            throw new RuntimeException("La frecuencia respiratoria es obligatoria");
        }
        if (frecuenciaRespiratoria < 0) {
            throw new RuntimeException("La frecuencia respiratoria no puede ser negativa");
        }
        if (frecuenciaRespiratoria > 100) {
            throw new RuntimeException("La frecuencia respiratoria está fuera de rangos válidos (0-100)");
        }

        // ✅ CORREGIDO: Mensajes de error para tensión arterial
        // Validar tensión arterial sistólica
        if (frecuenciaSistolica == null) {
            throw new RuntimeException("La tensión arterial es obligatoria");
        }
        if (frecuenciaSistolica < 0 || frecuenciaSistolica > 300) {
            throw new RuntimeException("La tensión arterial sistólica está fuera de rangos válidos (0-300)");
        }

        // Validar tensión arterial diastólica
        if (frecuenciaDiastolica == null) {
            throw new RuntimeException("La tensión arterial es obligatoria");
        }
        if (frecuenciaDiastolica < 0 || frecuenciaDiastolica > 200) {
            throw new RuntimeException("La tensión arterial diastólica está fuera de rangos válidos (0-200)");
        }

        // Validar que la sistólica sea mayor que la diastólica
        if (frecuenciaSistolica <= frecuenciaDiastolica) {
            throw new RuntimeException("La tensión sistólica debe ser mayor que la diastólica");
        }
    }
}