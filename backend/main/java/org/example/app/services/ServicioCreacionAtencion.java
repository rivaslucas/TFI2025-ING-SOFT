package org.example.app.services;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.domain.*;

public class ServicioCreacionAtencion {

    private final RepositorioIngresos repositorioIngresos;

    public ServicioCreacionAtencion(RepositorioIngresos repositorioIngresos) {
        this.repositorioIngresos = repositorioIngresos;
    }

    public Atencion registrarAtencion(String idIngreso, String informeMedico, Medico medico) {
        System.out.println("🔍 BUSCANDO INGRESO: " + idIngreso);

        // Validar que el ingreso existe
        Ingreso ingreso = repositorioIngresos.buscarPorId(idIngreso)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));

        System.out.println("✅ INGRESO ENCONTRADO - ID: " + idIngreso +
                ", Estado: " + ingreso.getEstado() +
                ", Médico asignado: " + ingreso.getMedicoAsignado() +
                ", Paciente: " + ingreso.getPaciente().getNombre() + " " + ingreso.getPaciente().getApellido());

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
        System.out.println("🔍 VALIDANDO MÉDICO - Médico actual: " + medico.getMatricula() +
                ", Médico asignado: " + medicoAsignado);

        if (medicoAsignado == null || !medicoAsignado.equals(medico.getMatricula())) {
            // ✅ CORREGIDO: Mensaje exacto que espera el test
            throw new RuntimeException("No tiene permisos para atender este ingreso");
        }

        // Validar informe médico
        if (informeMedico == null || informeMedico.trim().isEmpty()) {
            throw new RuntimeException("El informe médico es obligatorio");
        }

        String informeLimpio = informeMedico.trim();
        if (informeLimpio.length() < 10) {
            throw new RuntimeException("El informe médico debe tener al menos 10 caracteres");
        }

        System.out.println("✅ TODAS LAS VALIDACIONES PASARON - CREANDO ATENCIÓN");

        // Crear la atención
        Atencion atencion = new Atencion(ingreso, informeLimpio, medico);

        // Cambiar estado del ingreso a FINALIZADO
        ingreso.setEstado(EstadoIngreso.FINALIZADO);
        repositorioIngresos.actualizarIngreso(ingreso);

        System.out.println("🎉 ATENCIÓN REGISTRADA EXITOSAMENTE");
        System.out.println("   - ID Atención: " + atencion.getId());
        System.out.println("   - Paciente: " + ingreso.getPaciente().getNombre() + " " + ingreso.getPaciente().getApellido());
        System.out.println("   - Médico: " + medico.getMatricula());
        System.out.println("   - Estado actualizado a: " + ingreso.getEstado());

        return atencion;
    }
}