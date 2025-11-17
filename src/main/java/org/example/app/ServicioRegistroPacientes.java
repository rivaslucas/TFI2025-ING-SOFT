package org.example.app;

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
            if (!dbPacientes.estaAfiliado(cuil, obraSocialNombre)) {
                throw new IllegalArgumentException("No se puede registrar el paciente dado que no esta afiliado a la obra social");
            }

            // 3. Verificar que el número de afiliado coincide
            if (afiliado.getNumAfiliado() != null &&
                    !dbPacientes.verificarNumeroAfiliado(cuil, obraSocialNombre, afiliado.getNumAfiliado())) {
                throw new IllegalArgumentException("Número de afiliado no válido");
            }
        }

        // Crear y guardar paciente
        Paciente paciente = new Paciente(cuil, nombre, apellido, afiliado, domicilio);
        dbPacientes.guardarPaciente(paciente);
    }
}