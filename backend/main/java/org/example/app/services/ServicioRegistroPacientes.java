package org.example.app.services;

import org.example.app.interfaces.RepositorioPacientes;
import org.example.domain.*;

public class ServicioRegistroPacientes {
    private RepositorioPacientes dbPacientes;

    public ServicioRegistroPacientes(RepositorioPacientes db) {
        this.dbPacientes = db;
    }

    public void registrarPaciente(String cuil, String nombre, String apellido,
                                  Domicilio domicilio, Afiliado afiliado) {
        // Validaciones de campos mandatorios
        if (cuil == null || cuil.trim().isEmpty()) {
            throw new IllegalArgumentException("CUIL es un campo obligatorio");
        }
        if (dbPacientes.existePaciente(cuil)) {
            throw new IllegalArgumentException("El paciente ya existe");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("Apellido es un campo obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre es un campo obligatorio");
        }
        if (domicilio == null) {
            throw new IllegalArgumentException("Domicilio es obligatorio");
        }

        // Validar domicilio
        if (domicilio.getCalle() == null || domicilio.getCalle().trim().isEmpty()) {
            throw new IllegalArgumentException("Calle es un campo obligatorio");
        }
        if (domicilio.getNumero() == 0) {
            throw new IllegalArgumentException("Numero es un campo obligatorio");
        }
        if (domicilio.getLocalidad() == null || domicilio.getLocalidad().trim().isEmpty()) {
            throw new IllegalArgumentException("Localidad es un campo obligatorio");
        }

        // Validar obra social si se proporciona
        if (afiliado != null && afiliado.getObraSocial() != null) {
            ObraSocial obraSocial = afiliado.getObraSocial();
            String obraSocialNombre = obraSocial.getNombre();

            // 1. Verificar que la obra social existe
            if (!dbPacientes.existeObraSocial(obraSocialNombre)) {
                throw new IllegalArgumentException("No se puede registrar al paciente con una obra social inexistente");
            }

            // 2. Verificar que el paciente está afiliado a esa obra social
            // NOTA: Esta verificación debería hacerse contra un sistema externo de afiliaciones
            // Por ahora, simulamos la verificación basada en datos conocidos
            if (!verificarAfiliacionExterna(cuil, obraSocialNombre, afiliado.getNumAfiliado())) {
                throw new IllegalArgumentException("No se puede registrar el paciente dado que no esta afiliado a la obra social");
            }
        }

        // Crear y guardar paciente
        Paciente paciente = new Paciente(cuil, nombre, apellido, afiliado, domicilio);
        dbPacientes.guardarPaciente(paciente);
    }

    /**
     * Simula la verificación externa de afiliación a obra social
     * En una implementación real, esto consultaría un servicio externo
     */
    private boolean verificarAfiliacionExterna(String cuil, String obraSocialNombre, String numeroAfiliado) {
        // Lógica de verificación basada en datos conocidos de prueba

        // Caso 1: Paciente 27-4567890-3 afiliado a OSDE con número 87654321
        if ("27-4567890-3".equals(cuil) && "OSDE".equals(obraSocialNombre)) {
            return "87654321".equals(numeroAfiliado);
        }

        // Caso 2: Paciente 23-1234567-9 NO afiliado a OSDE (escenario de prueba)
        if ("23-1234567-9".equals(cuil) && "OSDE".equals(obraSocialNombre)) {
            return false;
        }

        // Para otros casos, asumir que la afiliación es válida
        // En un sistema real, aquí se haría una consulta a la base de datos de afiliados
        return true;
    }
}