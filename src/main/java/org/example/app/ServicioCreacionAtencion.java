package org.example.app;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.domain.*;

public class ServicioCreacionAtencion {

    private final RepositorioIngresos repositorioIngresos;

    public ServicioCreacionAtencion(RepositorioIngresos repositorioIngresos) {
        this.repositorioIngresos = repositorioIngresos;
    }
    public Atencion registrarAtencion(String idIngreso, String informeMedico, Medico medico) {
        // Validar que el ingreso existe
        Ingreso ingreso = repositorioIngresos.buscarPorId(idIngreso)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));

        System.out.println("DEBUG: Validando ingreso - ID: " + idIngreso + ", Estado: " + ingreso.getEstado() + ", Médico asignado: " + ingreso.getMedicoAsignado());

        // Validar que el ingreso está en proceso
        if (ingreso.getEstado() != EstadoIngreso.EN_PROCESO) {
            if (ingreso.getEstado() == EstadoIngreso.PENDIENTE) {
                throw new RuntimeException("El ingreso no está en proceso de atención");
            } else if (ingreso.getEstado() == EstadoIngreso.FINALIZADO) {
                throw new RuntimeException("No se puede atender un ingreso finalizado");
            } else {
                throw new RuntimeException("El ingreso no está en un estado válido para atención");
            }
        }

        // Validar que el médico es el asignado al ingreso
        String medicoAsignado = ingreso.getMedicoAsignado();
        System.out.println("DEBUG: Validando médico - Médico actual: " + medico.getMatricula() + ", Médico asignado: " + medicoAsignado);

        if (medicoAsignado == null || !medicoAsignado.equals(medico.getMatricula())) {
            throw new RuntimeException("No tiene permisos para atender este ingreso");
        }

        // Validar informe médico - CORREGIDO
        if (informeMedico == null || informeMedico.trim().isEmpty()) {
            throw new RuntimeException("El informe médico es obligatorio");
        }

        String informeLimpio = informeMedico.trim();
        if (informeLimpio.length() < 10) {
            throw new RuntimeException("El informe médico debe tener al menos 10 caracteres");
        }

        System.out.println("DEBUG: Todas las validaciones pasaron - Creando atención");

        // Crear la atención
        Atencion atencion = new Atencion(ingreso, informeLimpio, medico);

        // Cambiar estado del ingreso a FINALIZADO
        ingreso.setEstado(EstadoIngreso.FINALIZADO);
        repositorioIngresos.actualizarIngreso(ingreso);

        return atencion;
    }
}