package org.example.app.services;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.app.interfaces.RepositorioPacientes;
import org.example.domain.*;

import java.time.LocalDateTime;
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
                                  Float frecuenciaSistolica,  // ⚠️ IMPORTANTE: Este parámetro se llama frecuenciaSistolica en el constructor
                                  Float frecuenciaDiastolica) { // ⚠️ IMPORTANTE: Este parámetro se llama frecuenciaDiastolica en el constructor

        if (repositorioIngresos.pacienteTieneIngresosActivos(cuilPaciente)) {
            throw new RuntimeException("El paciente ya tiene un ingreso activo");
        }

        validarCampos(cuilPaciente, enfermera, informe, emergencia, temperatura,
                frecuenciaCardiaca, frecuenciaRespiratoria, frecuenciaSistolica, frecuenciaDiastolica);

        Paciente paciente = dbPacientes.buscarPacientePorCuil(cuilPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // GENERAR UN ID ÚNICO PARA EL INGRESO
        String idIngreso = UUID.randomUUID().toString();

        // ✅ CORRECCIÓN: Usar el constructor con 11 parámetros (incluyendo LocalDateTime)
        Ingreso ingreso = new Ingreso(
                idIngreso,                    // 1. id
                paciente,                     // 2. paciente
                enfermera,                    // 3. enfermera
                informe,                      // 4. informe
                emergencia,                   // 5. nivelEmergencia
                temperatura,                  // 6. temperatura
                frecuenciaCardiaca,           // 7. frecuenciaCardiaca
                frecuenciaRespiratoria,       // 8. frecuenciaRespiratoria
                frecuenciaSistolica,          // 9. frecuenciaSistolica ⚠️ Esto es lo que espera el constructor
                frecuenciaDiastolica,         // 10. frecuenciaDiastolica ⚠️ Esto es lo que espera el constructor
                LocalDateTime.now()           // 11. horaEspecifica - ¡Este es el parámetro que falta!
        );

        // Guardar en el repositorio compartido
        repositorioIngresos.guardarIngreso(ingreso);
    }

    // También necesitas actualizar el método validarCampos:
    public void validarCampos(String cuilPaciente,
                              Enfermera enfermera,
                              String informe,
                              NivelEmergencia emergencia,
                              Float temperatura,
                              Float frecuenciaCardiaca,
                              Float frecuenciaRespiratoria,
                              Float frecuenciaSistolica,   // ⚠️ Cambiar nombre aquí también
                              Float frecuenciaDiastolica) { // ⚠️ Cambiar nombre aquí también

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

        // ✅ CORREGIR: Usar los nombres correctos
        // Validar tensión arterial sistólica
        if (frecuenciaSistolica == null) {  // ⚠️ Cambiado de tensionSistolica
            throw new RuntimeException("La tensión arterial es obligatoria");
        }
        if (frecuenciaSistolica < 0 || frecuenciaSistolica > 300) {
            throw new RuntimeException("La tensión arterial sistólica está fuera de rangos válidos (0-300)");
        }

        // Validar tensión arterial diastólica
        if (frecuenciaDiastolica == null) {  // ⚠️ Cambiado de tensionDiastolica
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
}