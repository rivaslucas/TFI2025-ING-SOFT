package org.example.app;

import mock.DBPruebaEnMemoria;
import org.example.app.services.ServicioUrgencias;
import org.example.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioUrgenciasTest {

    private DBPruebaEnMemoria dbMockeada;
    private ServicioUrgencias servicioUrgencias;
    private Paciente paciente;
    private Enfermera enfermera;
    private ObraSocial obraSocial;
    private Afiliado afiliado;
    private Domicilio domicilio;

    @BeforeEach
    void setUp() {
        dbMockeada = new DBPruebaEnMemoria();
        servicioUrgencias = new ServicioUrgencias(dbMockeada, dbMockeada);
        obraSocial = new ObraSocial("OS","OSDE");
        afiliado = new Afiliado("123", obraSocial);
        domicilio = new Domicilio("Calle Falsa 123", 123, "Springfield");
        paciente = new Paciente("20-12345678-9", "Juan", "Perez", afiliado, domicilio);
        dbMockeada.guardarPaciente(paciente);
        enfermera = new Enfermera("Maria", "Gomez");
    }

    @Test
    public void registrarUrgenciaExitosa() {
        servicioUrgencias.registrarUrgencia(paciente.getCuil(), enfermera, "Dolor de pecho", NivelEmergencia.EMERGENCIA, 38.5f, 100f, 20f, 120f, 80f);
        List<Ingreso> ingresos = dbMockeada.obtenerIngresosPendientes();
        assertEquals(1, ingresos.size());
        Ingreso ingreso = ingresos.get(0);
        assertEquals(paciente.getCuil(), ingreso.getPaciente().getCuil());
        assertEquals(enfermera.getNombre(), ingreso.getEnfermera().getNombre());
        assertEquals("Dolor de pecho", ingreso.getInforme());
        assertEquals(NivelEmergencia.EMERGENCIA, ingreso.getNivelEmergencia());
    }

    @Test
    public void registrarUrgenciaConPacienteNoExistente() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioUrgencias.registrarUrgencia("20-12345678-7", enfermera, "Dolor de cabeza", NivelEmergencia.SIN_URGENCIA, 37.0f, 80f, 18f, 110f, 70f);
        });
        assertEquals("Paciente no encontrado", exception.getMessage());
    }

    @Test
    public void registrarUrgenciaConCamposInvalidos() {
        // CUIL nulo
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioUrgencias.registrarUrgencia(null, enfermera, "Informe", NivelEmergencia.SIN_URGENCIA, 36.5f, 70f, 16f, 100f, 60f);
        });
        assertEquals("El CUIL del paciente es obligatorio", exception.getMessage());

        // Informe corto
        exception = assertThrows(RuntimeException.class, () -> {
            servicioUrgencias.registrarUrgencia(paciente.getCuil(), enfermera, "Corto", NivelEmergencia.SIN_URGENCIA, 36.5f, 70f, 16f, 100f, 60f);
        });
        assertEquals("El informe médico debe tener al menos 10 caracteres", exception.getMessage());

        // Frecuencia cardíaca negativa
        exception = assertThrows(RuntimeException.class, () -> {
            servicioUrgencias.registrarUrgencia(paciente.getCuil(), enfermera, "Informe detallado", NivelEmergencia.SIN_URGENCIA, 36.5f, -70f, 16f, 100f, 60f);
        });
        assertEquals("La frecuencia cardíaca no puede ser negativa", exception.getMessage());
    }

    @Test
    public void obtenerIngresosPendientesOrdenadosCorrectamente() {
        Domicilio domicilio2 = new Domicilio("Avenida Siempreviva 742", 742, "Springfield");
        Afiliado afiliado2 = new Afiliado("456", new ObraSocial("SM","Swiss Medical"));
        Paciente paciente2 = new Paciente("87654321", "Maria", "Lopez", afiliado2, domicilio2);
        dbMockeada.guardarPaciente(paciente2);

        servicioUrgencias.registrarUrgencia(paciente.getCuil(), enfermera, "Informe Prueba 1", NivelEmergencia.URGENCIA_MENOR, 37.0f, 80f, 18f, 110f, 70f);
        // Para asegurar un orden por fecha, esperamos un momento
        try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        servicioUrgencias.registrarUrgencia(paciente2.getCuil(), enfermera, "Informe Prueba 2", NivelEmergencia.CRITICA, 39.0f, 110f, 22f, 130f, 85f);

        List<Ingreso> ingresos = servicioUrgencias.obtenerIngresosPendientes();
        assertEquals(2, ingresos.size());
        assertEquals(NivelEmergencia.CRITICA, ingresos.get(0).getNivelEmergencia());
        assertEquals(NivelEmergencia.URGENCIA_MENOR, ingresos.get(1).getNivelEmergencia());
    }

    @Test
    public void obtenerIngresosPendientesCuandoNoHayIngresos() {
        List<Ingreso> ingresos = servicioUrgencias.obtenerIngresosPendientes();
        assertTrue(ingresos.isEmpty());
    }
}
