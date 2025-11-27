package org.example.app.interfaces;

import org.example.domain.Ingreso;
import org.example.domain.EstadoIngreso;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RepositorioIngresos {
    List<Ingreso> obtenerIngresosPendientes();

    Optional<Ingreso> buscarPorId(String id);

    void guardarIngreso(Ingreso ingreso);

    void actualizarIngreso(Ingreso ingreso);

    List<Ingreso> obtenerTodos();

    // ✅ FUNCIONES NUEVAS
    List<Ingreso> buscarIngresosPorPaciente(String cuilPaciente);

    List<Ingreso> buscarIngresosPorMedico(String matriculaMedico);

    List<Ingreso> buscarIngresosPorEstado(EstadoIngreso estado);

    // ✅ MÉTODO CORREGIDO - Versión única y mejorada
    default boolean pacienteTieneIngresosActivos(String cuilPaciente) {
        if (cuilPaciente == null || cuilPaciente.trim().isEmpty()) {
            return false;
        }

        List<Ingreso> ingresos = buscarIngresosPorPaciente(cuilPaciente.trim());

        if (ingresos == null || ingresos.isEmpty()) {
            return false;
        }

        // Verificar si existe algún ingreso que NO esté finalizado
        return ingresos.stream()
                .anyMatch(ingreso ->
                        ingreso != null &&
                                ingreso.getEstado() != null &&
                                ingreso.getEstado() != EstadoIngreso.FINALIZADO
                );
    }

    // ✅ MÉTODO ALTERNATIVO - Si necesitas ambos nombres por compatibilidad
    default boolean tieneIngresosActivos(String cuilPaciente) {
        return pacienteTieneIngresosActivos(cuilPaciente);
    }

    default List<Ingreso> obtenerIngresosEnProcesoPorMedico(String matriculaMedico) {
        if (matriculaMedico == null || matriculaMedico.trim().isEmpty()) {
            return List.of();
        }

        List<Ingreso> ingresos = buscarIngresosPorMedico(matriculaMedico.trim());

        System.out.println("🔍 BUSCANDO INGRESOS EN PROCESO PARA MÉDICO: " + matriculaMedico);
        System.out.println("   Total ingresos encontrados: " + (ingresos != null ? ingresos.size() : 0));

        List<Ingreso> ingresosEnProceso = ingresos.stream()
                .filter(ingreso ->
                        ingreso != null &&
                                ingreso.getEstado() != null &&
                                ingreso.getEstado() == EstadoIngreso.EN_PROCESO
                )
                .collect(Collectors.toList());

        System.out.println("   Ingresos en proceso: " + ingresosEnProceso.size());
        ingresosEnProceso.forEach(ingreso ->
                System.out.println("   - ID: " + ingreso.getId() + ", Estado: " + ingreso.getEstado())
        );

        return ingresosEnProceso;
    }

    default boolean medicoTienePacientesEnProceso(String matriculaMedico) {
        if (matriculaMedico == null || matriculaMedico.trim().isEmpty()) {
            return false;
        }

        boolean resultado = !obtenerIngresosEnProcesoPorMedico(matriculaMedico).isEmpty();
        System.out.println("🔍 RESULTADO VERIFICACIÓN MÉDICO " + matriculaMedico + ": " +
                (resultado ? "TIENE pacientes en proceso" : "NO tiene pacientes en proceso"));
        return resultado;
    }

    default long contarIngresosActivos() {
        List<Ingreso> todos = obtenerTodos();
        if (todos == null) {
            return 0;
        }

        return todos.stream()
                .filter(ingreso ->
                        ingreso != null &&
                                ingreso.getEstado() != null &&
                                ingreso.getEstado() != EstadoIngreso.FINALIZADO
                )
                .count();
    }

    // ✅ MÉTODO ADICIONAL PARA DEBUGGING
    default void debugIngresosPaciente(String cuilPaciente) {
        if (cuilPaciente == null) {
            System.out.println("CUIL es nulo");
            return;
        }

        List<Ingreso> ingresos = buscarIngresosPorPaciente(cuilPaciente);
        System.out.println("=== DEBUG INGRESOS PARA CUIL: " + cuilPaciente + " ===");
        System.out.println("Total de ingresos encontrados: " + (ingresos != null ? ingresos.size() : 0));

        if (ingresos != null) {
            ingresos.forEach(ingreso -> {
                if (ingreso != null) {
                    System.out.println(" - ID: " + ingreso.getId() +
                            ", Estado: " + (ingreso.getEstado() != null ? ingreso.getEstado() : "NULO") +
                            ", Médico: " + ingreso.getMedicoAsignado());
                } else {
                    System.out.println(" - Ingreso: NULO");
                }
            });
        }

        boolean tieneActivos = pacienteTieneIngresosActivos(cuilPaciente);
        System.out.println("¿Tiene ingresos activos? " + tieneActivos);
        System.out.println("=====================================");
    }
}