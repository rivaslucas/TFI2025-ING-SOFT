package org.example.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PacienteTest {

    @Test
    public void crearPacienteExitosamente() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "Perez";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE"); // CORREGIDO: ID primero, nombre después

        // Ejecucion
        Paciente paciente = new Paciente(cuil, nombre, apellido, obraSocial);

        // Verificacion
        assertEquals(cuil, paciente.getCuil());
        assertEquals(nombre, paciente.getNombre());
        assertEquals(apellido, paciente.getApellido());
        assertEquals(obraSocial, paciente.getObraSocial());
    }

    @Test
    public void crearPacienteConNombreNulo() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = null;
        String apellido = "Perez";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, obraSocial);
        });

        assertEquals("Nombre es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConNombreVacio() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "";
        String apellido = "Perez";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, obraSocial);
        });

        assertEquals("Nombre es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConNombreEspaciado() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "   ";
        String apellido = "Perez";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, obraSocial);
        });

        assertEquals("Nombre es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConCuilNulo() {
        // Preparacion
        String cuil = null;
        String nombre = "Juan";
        String apellido = "Perez";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, obraSocial);
        });

        assertEquals("Cuil es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConCuilVacio() {
        // Preparacion
        String cuil = "";
        String nombre = "Juan";
        String apellido = "Perez";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, obraSocial);
        });

        assertEquals("Cuil es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConApellidoNulo() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = null;
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, obraSocial);
        });

        assertEquals("Apellido es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConApellidoVacio() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, obraSocial);
        });

        assertEquals("Apellido es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConDomicilioValido() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "Perez";
        Afiliado afiliado = new Afiliado("12345", new ObraSocial("OS", "OSDE"));
        Domicilio domicilio = new Domicilio("Calle Principal", 123, "CABA");

        // Ejecucion
        Paciente paciente = new Paciente(cuil, nombre, apellido, afiliado, domicilio);

        // Verificacion
        assertEquals(cuil, paciente.getCuil());
        assertEquals(nombre, paciente.getNombre());
        assertEquals(apellido, paciente.getApellido());
        assertEquals(domicilio, paciente.getDireccion());
        assertEquals(afiliado, paciente.getAfiliado());
    }

    @Test
    public void crearPacienteConDomicilioCalleNula() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "Perez";
        Afiliado afiliado = new Afiliado("12345", new ObraSocial("OS", "OSDE"));
        Domicilio domicilio = new Domicilio(null, 123, "CABA");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, afiliado, domicilio);
        });

        assertEquals("Calle es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConDomicilioNumeroCero() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "Perez";
        Afiliado afiliado = new Afiliado("12345", new ObraSocial("OS", "OSDE"));
        Domicilio domicilio = new Domicilio("Calle Principal", 0, "CABA");

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, afiliado, domicilio);
        });

        assertEquals("Numero es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void crearPacienteConDomicilioLocalidadNula() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "Perez";
        Afiliado afiliado = new Afiliado("12345", new ObraSocial("OS", "OSDE"));
        Domicilio domicilio = new Domicilio("Calle Principal", 123, null);

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Paciente(cuil, nombre, apellido, afiliado, domicilio);
        });

        assertEquals("Localidad es un campo obligatorio", exception.getMessage());
    }

    @Test
    public void obtenerObraSocialNombre() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "Perez";
        ObraSocial obraSocial = new ObraSocial("OS", "OSDE"); // CORREGIDO: ID primero

        // Ejecucion
        Paciente paciente = new Paciente(cuil, nombre, apellido, obraSocial);

        // Verificacion
        assertEquals("OSDE", paciente.getObraSocialNombre()); // Ahora debería devolver "OSDE"
    }

    @Test
    public void obtenerObraSocialNombreSinObraSocial() {
        // Preparacion
        String cuil = "20-12345678-9";
        String nombre = "Juan";
        String apellido = "Perez";
        Afiliado afiliado = null;
        Domicilio domicilio = new Domicilio("Calle Principal", 123, "CABA");

        // Ejecucion
        Paciente paciente = new Paciente(cuil, nombre, apellido, afiliado, domicilio);

        // Verificacion
        assertNull(paciente.getObraSocialNombre());
    }
}