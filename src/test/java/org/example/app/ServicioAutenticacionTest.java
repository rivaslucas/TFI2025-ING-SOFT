package org.example.app;

import mock.DBPruebaEnMemoria;
import org.example.domain.Autoridad;
import org.example.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioAutenticacionTest {

    private DBPruebaEnMemoria dbMockeada;
    private ServicioAutenticacion servicioAutenticacion;

    @BeforeEach
    void setUp() {
        dbMockeada = new DBPruebaEnMemoria();
        servicioAutenticacion = new ServicioAutenticacion(dbMockeada);
    }

    @Test
    public void iniciarSesionExitosoComoMedico() {
        // Preparacion
        String email = "medico@test.com";
        String contrasena = "password123";
        Usuario usuario = new Usuario(email, contrasena, Autoridad.MEDICO);
        dbMockeada.guardarUsuario(usuario);

        // Ejecucion
        servicioAutenticacion.iniciarSesion(email, contrasena);

        // Verificacion
        Usuario usuarioActual = servicioAutenticacion.getUsuarioActual();
        assertNotNull(usuarioActual);
        assertEquals(email, usuarioActual.getEmail());
        assertEquals(Autoridad.MEDICO, usuarioActual.getAutoridad());

        // Verificar que la contraseña está hasheada
        assertTrue(usuarioActual.getContrasena().startsWith("$2a$"));
        assertTrue(usuarioActual.verificarContrasena(contrasena));
    }

    @Test
    public void iniciarSesionExitosoComoEnfermero() {
        // Preparacion
        String email = "enfermero@test.com";
        String contrasena = "password123";
        Usuario usuario = new Usuario(email, contrasena, Autoridad.ENFERMERO);
        dbMockeada.guardarUsuario(usuario);

        // Ejecucion
        servicioAutenticacion.iniciarSesion(email, contrasena);

        // Verificacion
        Usuario usuarioActual = servicioAutenticacion.getUsuarioActual();
        assertNotNull(usuarioActual);
        assertEquals(email, usuarioActual.getEmail());
        assertEquals(Autoridad.ENFERMERO, usuarioActual.getAutoridad());

        // Verificar que la contraseña está hasheada
        assertTrue(usuarioActual.getContrasena().startsWith("$2a$"));
        assertTrue(usuarioActual.verificarContrasena(contrasena));
    }

    @Test
    public void iniciarSesionConEmailInexistente() {
        // Preparacion
        String email = "noexiste@test.com";
        String contrasena = "password123";

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioAutenticacion.iniciarSesion(email, contrasena);
        });

        assertEquals("Usuario o contrasena invalido", exception.getMessage());
        assertNull(servicioAutenticacion.getUsuarioActual());
    }

    @Test
    public void iniciarSesionConContrasenaIncorrecta() {
        // Preparacion
        String email = "medico@test.com";
        String contrasenaCorrecta = "password123";
        String contrasenaIncorrecta = "wrongpassword";
        Usuario usuario = new Usuario(email, contrasenaCorrecta, Autoridad.MEDICO);
        dbMockeada.guardarUsuario(usuario);

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioAutenticacion.iniciarSesion(email, contrasenaIncorrecta);
        });

        assertEquals("Usuario o contrasena invalido", exception.getMessage());
        assertNull(servicioAutenticacion.getUsuarioActual());
    }

    @Test
    public void crearUsuarioComoMedicoExitoso() {
        // Preparacion
        String email = "nuevomedico@test.com";
        String contrasena = "password123";
        Autoridad autoridad = Autoridad.MEDICO;

        // Ejecucion
        servicioAutenticacion.crearUsuario(email, contrasena, autoridad);

        // Verificacion
        List<Usuario> usuarios = servicioAutenticacion.obtenerUsuarios();
        assertEquals(1, usuarios.size());

        Usuario usuarioCreado = usuarios.get(0);
        assertEquals(email, usuarioCreado.getEmail());
        assertEquals(Autoridad.MEDICO, usuarioCreado.getAutoridad());

        // Verificar que la contraseña está hasheada
        assertTrue(usuarioCreado.getContrasena().startsWith("$2a$"));
        assertTrue(usuarioCreado.verificarContrasena(contrasena));
    }

    @Test
    public void crearUsuarioComoEnfermeroExitoso() {
        // Preparacion
        String email = "nuevoenfermero@test.com";
        String contrasena = "password123";
        Autoridad autoridad = Autoridad.ENFERMERO;

        // Ejecucion
        servicioAutenticacion.crearUsuario(email, contrasena, autoridad);

        // Verificacion
        List<Usuario> usuarios = servicioAutenticacion.obtenerUsuarios();
        assertEquals(1, usuarios.size());

        Usuario usuarioCreado = usuarios.get(0);
        assertEquals(email, usuarioCreado.getEmail());
        assertEquals(Autoridad.ENFERMERO, usuarioCreado.getAutoridad());

        // Verificar que la contraseña está hasheada
        assertTrue(usuarioCreado.getContrasena().startsWith("$2a$"));
        assertTrue(usuarioCreado.verificarContrasena(contrasena));
    }

    @Test
    public void crearUsuarioConEmailExistente() {
        // Preparacion
        String email = "existente@test.com";
        String contrasena = "password123";
        Autoridad autoridad = Autoridad.MEDICO;

        Usuario usuarioExistente = new Usuario(email, contrasena, autoridad);
        dbMockeada.guardarUsuario(usuarioExistente);

        // Ejecucion y Verificacion
        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioAutenticacion.crearUsuario(email, "nuevapassword", autoridad);
        });

        assertEquals("Email existente", exception.getMessage());
    }

    @Test
    public void crearUsuarioSinAutoridad() {
        // Preparacion
        String email = "usuario@test.com";
        String contrasena = "password123";
        Autoridad autoridad = null;

        // Ejecucion
        servicioAutenticacion.crearUsuario(email, contrasena, autoridad);

        // Verificacion
        List<Usuario> usuarios = servicioAutenticacion.obtenerUsuarios();
        assertEquals(1, usuarios.size());

        Usuario usuarioCreado = usuarios.get(0);
        assertEquals(email, usuarioCreado.getEmail());
        assertNull(usuarioCreado.getAutoridad());

        // Verificar que la contraseña está hasheada
        assertTrue(usuarioCreado.getContrasena().startsWith("$2a$"));
        assertTrue(usuarioCreado.verificarContrasena(contrasena));
    }

    @Test
    public void obtenerUsuariosCuandoNoHayUsuarios() {
        // Ejecucion
        List<Usuario> usuarios = servicioAutenticacion.obtenerUsuarios();

        // Verificacion
        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }


    @Test
    public void hashesUnicosParaMismosUsuarios() {
        // Preparacion
        String contrasena = "mismaContrasena123";

        // Ejecucion
        servicioAutenticacion.crearUsuario("medico1@test.com", contrasena, Autoridad.MEDICO);
        servicioAutenticacion.crearUsuario("medico2@test.com", contrasena, Autoridad.MEDICO);

        // Verificacion
        List<Usuario> usuarios = servicioAutenticacion.obtenerUsuarios();
        assertEquals(2, usuarios.size());

        // Los hashes deben ser diferentes debido al salt único
        assertNotEquals(usuarios.get(0).getContrasena(), usuarios.get(1).getContrasena());

        // Pero ambas contraseñas deben verificarse correctamente
        assertTrue(usuarios.get(0).verificarContrasena(contrasena));
        assertTrue(usuarios.get(1).verificarContrasena(contrasena));
    }

    @Test
    public void usuarioActualSeMantieneEntreSesiones() {
        // Preparacion
        String email = "medico@test.com";
        String contrasena = "password123";
        servicioAutenticacion.crearUsuario(email, contrasena, Autoridad.MEDICO);

        // Ejecucion
        servicioAutenticacion.iniciarSesion(email, contrasena);
        Usuario usuarioActual1 = servicioAutenticacion.getUsuarioActual();

        // Simular otra operación
        Usuario usuarioActual2 = servicioAutenticacion.getUsuarioActual();

        // Verificacion - Debe ser la misma instancia
        assertSame(usuarioActual1, usuarioActual2);
    }

    @Test
    public void verificarContrasenaDirectamenteDesdeUsuario() {
        // Preparacion
        String email = "medico@test.com";
        String contrasena = "miContrasenaSegura";
        servicioAutenticacion.crearUsuario(email, contrasena, Autoridad.MEDICO);

        // Obtener el usuario creado
        List<Usuario> usuarios = servicioAutenticacion.obtenerUsuarios();
        Usuario usuario = usuarios.get(0);

        // Ejecucion y Verificacion
        assertTrue(usuario.verificarContrasena(contrasena));
        assertFalse(usuario.verificarContrasena("contrasenaIncorrecta"));
    }
}