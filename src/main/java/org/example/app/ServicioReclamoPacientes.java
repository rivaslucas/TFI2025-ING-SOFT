package org.example.app;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.domain.*;

import java.util.Comparator;
import java.util.List;

public class ServicioReclamoPacientes {

    private final RepositorioIngresos repositorioIngresos;

    public ServicioReclamoPacientes(RepositorioIngresos repositorioIngresos) {
        this.repositorioIngresos = repositorioIngresos;
    }

    public Ingreso reclamarProximoPaciente(Medico medico) {
        if (medico == null) {
            throw new RuntimeException("Debe estar autenticado como médico para reclamar pacientes");
        }

        // Obtener ingresos pendientes
        List<Ingreso> ingresosPendientes = repositorioIngresos.obtenerIngresosPendientes();

        if (ingresosPendientes.isEmpty()) {
            throw new RuntimeException("No hay pacientes en lista de espera");
        }

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
        ingresoAReclamar.setMedicoAsignado(medico.getMatricula());

        // Actualizar en el repositorio
        repositorioIngresos.actualizarIngreso(ingresoAReclamar);

        return ingresoAReclamar;
    }

    public List<Ingreso> obtenerIngresosPendientes() {
        return repositorioIngresos.obtenerIngresosPendientes();
    }
}