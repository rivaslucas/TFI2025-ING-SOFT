package org.example.app.services;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.domain.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ServicioReclamoPacientes {

    private final RepositorioIngresos repositorioIngresos;

    public ServicioReclamoPacientes(RepositorioIngresos repositorioIngresos) {
        this.repositorioIngresos = repositorioIngresos;
    }

    public Ingreso reclamarProximoPaciente(Medico medico) {
        if (medico == null) {
            throw new RuntimeException("Debe estar autenticado como médico para reclamar pacientes");
        }

        String matriculaMedico = medico.getMatricula();

        // ✅ VERIFICACIÓN MEJORADA: Verificar si el médico ya tiene pacientes en proceso
        System.out.println("🔍 VERIFICANDO SI EL MÉDICO YA TIENE PACIENTES EN PROCESO...");

        // Primero verificamos directamente sin depender de métodos default
        boolean tienePacientesEnProceso = verificarPacientesEnProcesoDirectamente(matriculaMedico);

        // DEBUG DETALLADO
        List<Ingreso> todosIngresosMedico = repositorioIngresos.buscarIngresosPorMedico(matriculaMedico);
        System.out.println("📊 DEBUG - Todos los ingresos del médico " + matriculaMedico + ": " +
                (todosIngresosMedico != null ? todosIngresosMedico.size() : 0));

        if (todosIngresosMedico != null) {
            todosIngresosMedico.forEach(ingreso ->
                    System.out.println("   - ID: " + ingreso.getId() +
                            ", Estado: " + ingreso.getEstado() +
                            ", Médico: '" + ingreso.getMedicoAsignado() + "'")
            );
        }

        System.out.println("📊 Médico " + matriculaMedico + " tiene pacientes en proceso: " + tienePacientesEnProceso);

        if (tienePacientesEnProceso) {
            List<Ingreso> ingresosEnProceso = obtenerIngresosEnProcesoDirectamente(matriculaMedico);
            System.out.println("🚫 BLOQUEADO - Médico tiene " + ingresosEnProceso.size() + " paciente(s) en proceso:");
            ingresosEnProceso.forEach(ingreso ->
                    System.out.println("   - Paciente: " + ingreso.getPaciente().getNombre() + " " +
                            ingreso.getPaciente().getApellido() + " (ID: " + ingreso.getId() + ")")
            );

            throw new RuntimeException("No puede reclamar otro paciente. Primero debe finalizar la atención del paciente actual.");
        }

        // Obtener ingresos pendientes
        List<Ingreso> ingresosPendientes = repositorioIngresos.obtenerIngresosPendientes();

        // DEBUG: Ver qué hay en el repositorio
        System.out.println("DEBUG - Ingresos pendientes en repositorio: " + ingresosPendientes.size());
        ingresosPendientes.forEach(ingreso ->
                System.out.println("DEBUG - Ingreso: " + ingreso.getId() +
                        ", Estado: " + ingreso.getEstado() +
                        ", Médico Asignado: '" + ingreso.getMedicoAsignado() + "'")
        );

        if (ingresosPendientes.isEmpty()) {
            throw new RuntimeException("No hay pacientes en lista de espera");
        }

        // Ordenar por prioridad (nivel de emergencia) y luego por fecha de ingreso
        ingresosPendientes.sort(Comparator
                .comparing(Ingreso::getNivelEmergencia,
                        Comparator.comparing(NivelEmergencia::getPrioridad))
                .thenComparing(Ingreso::getFechaIngreso));

        // Tomar el ingreso más prioritario (primero de la lista ordenada)
        Ingreso ingresoAReclamar = ingresosPendientes.get(0);

        // Verificar que no esté ya en proceso
        if (ingresoAReclamar.getEstado() != EstadoIngreso.PENDIENTE) {
            throw new RuntimeException("Paciente ya asignado a otro médico");
        }

        // Realizar el reclamo
        ingresoAReclamar.setEstado(EstadoIngreso.EN_PROCESO);
        ingresoAReclamar.setMedicoAsignado(matriculaMedico);

        // ✅ VERIFICACIÓN MEJORADA
        System.out.println("✅✅✅ ASIGNANDO MÉDICO AL INGRESO:");
        System.out.println("   - ID Ingreso: " + ingresoAReclamar.getId());
        System.out.println("   - Médico Asignado: '" + ingresoAReclamar.getMedicoAsignado() + "'");
        System.out.println("   - Estado: " + ingresoAReclamar.getEstado());
        System.out.println("   - ¿Médico asignado es null? " + (ingresoAReclamar.getMedicoAsignado() == null));
        System.out.println("   - ¿Médico asignado es igual a '" + matriculaMedico + "'? " +
                matriculaMedico.equals(ingresoAReclamar.getMedicoAsignado()));

        // Actualizar en el repositorio
        repositorioIngresos.actualizarIngreso(ingresoAReclamar);

        // ✅ VERIFICACIÓN INMEDIATA Y DETALLADA POST-GUARDADO
        System.out.println("🚨🚨🚨 VERIFICACIÓN INMEDIATA POST-GUARDADO 🚨🚨🚨");
        System.out.println("   - Médico que debería tener: " + matriculaMedico);

        // Llamar directamente al método de búsqueda con debug mejorado
        List<Ingreso> verificacion = repositorioIngresos.buscarIngresosPorMedico(matriculaMedico);
        System.out.println("   - Ingresos encontrados: " + verificacion.size());

        // Verificar también buscando por ID específico
        Optional<Ingreso> ingresoVerificado = repositorioIngresos.buscarPorId(ingresoAReclamar.getId());
        if (ingresoVerificado.isPresent()) {
            Ingreso ing = ingresoVerificado.get();
            System.out.println("   - VERIFICACIÓN POR ID:");
            System.out.println("     * ID: " + ing.getId());
            System.out.println("     * Estado: " + ing.getEstado());
            System.out.println("     * Médico Asignado: '" + ing.getMedicoAsignado() + "'");
            System.out.println("     * ¿Es igual a '" + matriculaMedico + "'? " +
                    matriculaMedico.equals(ing.getMedicoAsignado()));
        } else {
            System.out.println("   - ❌ INGRESO NO ENCONTRADO POR ID");
        }

        System.out.println("✅ PACIENTE RECLAMADO EXITOSAMENTE por médico " + matriculaMedico);
        System.out.println("   - Paciente: " + ingresoAReclamar.getPaciente().getNombre() + " " +
                ingresoAReclamar.getPaciente().getApellido());
        System.out.println("   - ID Ingreso: " + ingresoAReclamar.getId());
        System.out.println("   - Nivel Emergencia: " + ingresoAReclamar.getNivelEmergencia());

        return ingresoAReclamar;
    }

    // ✅ MÉTODO CORREGIDO: Verificación directa SIN CAST problemático
    private boolean verificarPacientesEnProcesoDirectamente(String matriculaMedico) {
        System.out.println("🔍🔍🔍 INICIANDO VERIFICACIÓN DIRECTA PARA MÉDICO: " + matriculaMedico);

        // ✅ SOLUCIÓN: Solo usar la interfaz, sin cast a implementación específica
        List<Ingreso> ingresosMedico = repositorioIngresos.buscarIngresosPorMedico(matriculaMedico);

        boolean tieneEnProceso = ingresosMedico.stream()
                .anyMatch(ingreso ->
                        ingreso != null &&
                                ingreso.getEstado() == EstadoIngreso.EN_PROCESO
                );

        System.out.println("🔍 VERIFICACIÓN DIRECTA - Médico " + matriculaMedico +
                " tiene pacientes en proceso: " + tieneEnProceso);

        return tieneEnProceso;
    }

    // ✅ MÉTODO MEJORADO: Obtener ingresos en proceso directamente
    private List<Ingreso> obtenerIngresosEnProcesoDirectamente(String matriculaMedico) {
        List<Ingreso> ingresosMedico = repositorioIngresos.buscarIngresosPorMedico(matriculaMedico);

        List<Ingreso> ingresosEnProceso = ingresosMedico.stream()
                .filter(ingreso ->
                        ingreso != null &&
                                ingreso.getEstado() == EstadoIngreso.EN_PROCESO
                )
                .collect(Collectors.toList());

        System.out.println("🔍 OBTENCIÓN DIRECTA - Ingresos en proceso para médico " +
                matriculaMedico + ": " + ingresosEnProceso.size());

        return ingresosEnProceso;
    }

    public List<Ingreso> obtenerIngresosPendientes() {
        return repositorioIngresos.obtenerIngresosPendientes();
    }

    // ✅ MÉTODO MEJORADO: Obtener pacientes en proceso por médico
    public List<Ingreso> obtenerPacientesEnProcesoPorMedico(String matriculaMedico) {
        if (matriculaMedico == null || matriculaMedico.trim().isEmpty()) {
            throw new RuntimeException("La matrícula del médico es obligatoria");
        }
        return obtenerIngresosEnProcesoDirectamente(matriculaMedico.trim());
    }

    // ✅ MÉTODO MEJORADO: Verificar si médico puede reclamar paciente
    public boolean medicoPuedeReclamarPaciente(String matriculaMedico) {
        if (matriculaMedico == null || matriculaMedico.trim().isEmpty()) {
            return false;
        }
        return !verificarPacientesEnProcesoDirectamente(matriculaMedico.trim());
    }
}