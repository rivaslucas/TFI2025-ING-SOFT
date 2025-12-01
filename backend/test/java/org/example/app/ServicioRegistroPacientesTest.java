package org.example.app;

import mock.DBPruebaEnMemoria;
import org.example.app.services.ServicioRegistroPacientes;
import org.example.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioRegistroPacientesTest {

    private DBPruebaEnMemoria dbMockeada;
    private ServicioRegistroPacientes servicioRegistro;
    private Domicilio domicilio;
    private ObraSocial obraSocial;
    private Afiliado afiliado;
    private Paciente pacienteExistente;

    @BeforeEach
    void setUp() {
        dbMockeada = new DBPruebaEnMemoria();
        servicioRegistro = new ServicioRegistroPacientes(dbMockeada);
        domicilio = new Domicilio("Calle Falsa 123", 123, "Springfield");
        obraSocial = new ObraSocial("OSDE", "OSDE");
        dbMockeada.guardarObraSocial(obraSocial);
        afiliado = new Afiliado("87654321", obraSocial);

        // Pre-cargamos un paciente para las pruebas de duplicados
        pacienteExistente = new Paciente("20-42499668-9", "Nico", "B", afiliado, domicilio);
        dbMockeada.guardarPaciente(pacienteExistente);
    }

    @Test
    public void registrarPacienteExitosamente() {
        String cuil = "27-4567890-3";
        servicioRegistro.registrarPaciente(cuil, "Juan", "Perez", domicilio, afiliado);

        assertTrue(dbMockeada.existePaciente(cuil));
        Optional<Paciente> pacienteOpt = dbMockeada.buscarPacientePorCuil(cuil);
        assertTrue(pacienteOpt.isPresent(), "El paciente debería existir en la base de datos");
        Paciente pacienteGuardado = pacienteOpt.get();
        assertEquals("Juan", pacienteGuardado.getNombre());
        assertEquals("Perez", pacienteGuardado.getApellido());
        assertEquals("Calle Falsa 123", pacienteGuardado.getDireccion().getCalle());
        assertEquals("OSDE", pacienteGuardado.getObraSocialNombre());
    }

    @Test
    public void registrarPacienteSinObraSocialExitosamente() {
        String cuil = "20-12345678-9";
        servicioRegistro.registrarPaciente(cuil, "Ana", "Gomez", domicilio, null);

        assertTrue(dbMockeada.existePaciente(cuil));
        Optional<Paciente> pacienteOpt = dbMockeada.buscarPacientePorCuil(cuil);
        assertTrue(pacienteOpt.isPresent(), "El paciente debería existir en la base de datos");
        Paciente pacienteGuardado = pacienteOpt.get();
        assertEquals("Ana", pacienteGuardado.getNombre());
        assertNull(pacienteGuardado.getAfiliado());
    }

    @Test
    public void registrarPacienteYaExistenteLanzaExcepcion() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente(pacienteExistente.getCuil(), "Nico", "B", domicilio, afiliado);
        });
        assertEquals("El paciente ya existe", exception.getMessage());
    }

    @Test
    public void registrarPacienteConCuilNuloLanzaExcepcion() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente(null, "Carlos", "Sanchez", domicilio, null);
        });
        assertEquals("CUIL es un campo obligatorio", exception.getMessage());
    }
    
    @Test
    public void registrarPacienteConNombreNuloLanzaExcepcion() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente("20-11223344-5", null, "Lopez", domicilio, null);
        });
        assertEquals("Nombre es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void registrarPacienteConApellidoNuloLanzaExcepcion() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente("20-11223344-5", "Maria", null, domicilio, null);
        });
        assertEquals("Apellido es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void registrarPacienteConDomicilioNuloLanzaExcepcion() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente("20-11223344-5", "Maria", "Lopez", null, null);
        });
        assertEquals("Domicilio es obligatorio", exception.getMessage());
    }

    @Test
    public void registrarPacienteConCalleNulaEnDomicilioLanzaExcepcion() {
        Domicilio domicilioInvalido = new Domicilio(null, 456, "Ciudad");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente("20-11223344-5", "Maria", "Lopez", domicilioInvalido, null);
        });
        assertEquals("Calle es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void registrarPacienteConObraSocialInexistenteLanzaExcepcion() {
        ObraSocial osInexistente = new ObraSocial("INEXISTENTE", "OS no registrada");
        Afiliado afiliadoConOSInexistente = new Afiliado("12345", osInexistente);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente("20-11223344-5", "Laura", "Fernandez", domicilio, afiliadoConOSInexistente);
        });
        assertEquals("No se puede registrar al paciente con una obra social inexistente", exception.getMessage());
    }

    @Test
    public void registrarPacienteNoAfiliadoAObraSocialLanzaExcepcion() {
        // Usamos el caso de prueba definido en el servicio
        String cuil = "23-1234567-9";
        String numAfiliadoIncorrecto = "999999";
        Afiliado afiliadoIncorrecto = new Afiliado(numAfiliadoIncorrecto, obraSocial);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioRegistro.registrarPaciente(cuil, "Test", "Afiliacion", domicilio, afiliadoIncorrecto);
        });
        assertEquals("No se puede registrar el paciente dado que no esta afiliado a la obra social", exception.getMessage());
    }
}