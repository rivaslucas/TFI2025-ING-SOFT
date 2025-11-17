package org.example.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    public void crearUsuarioComoMedico() {
        // Preparacion
        String email = "medico@hospital.com";
        String contrasena = "password123";
        Autoridad autoridad = Autoridad.MEDICO;

        // Ejecucion
        Usuario usuario = new Usuario(email, contrasena, autoridad);

        // Verificacion
        assertEquals(email, usuario.getEmail());
        assertEquals(Autoridad.MEDICO, usuario.getAutoridad());
        assertTrue(usuario.verificarContrasena(contrasena));
    }

    @Test
    public void crearUsuarioComoEnfermero() {
        // Preparacion
        String email = "enfermero@hospital.com";
        String contrasena = "password123";
        Autoridad autoridad = Autoridad.ENFERMERO;

        // Ejecucion
        Usuario usuario = new Usuario(email, contrasena, autoridad);

        // Verificacion
        assertEquals(email, usuario.getEmail());
        assertEquals(Autoridad.ENFERMERO, usuario.getAutoridad());
        assertTrue(usuario.verificarContrasena(contrasena));
    }

    @Test
    public void crearUsuarioSinRol() {
        // Preparacion
        String email = "usuario@test.com";
        String contrasena = "password123";
        Autoridad autoridad = null;

        // Ejecucion
        Usuario usuario = new Usuario(email, contrasena, autoridad);

        // Verificacion
        assertEquals(email, usuario.getEmail());
        assertNull(usuario.getAutoridad());
        assertTrue(usuario.verificarContrasena(contrasena));
    }
}