import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import mock.DBPruebaEnMemoria;
import org.example.app.ServicioAutenticacion;
import org.example.domain.Usuario;
import org.example.domain.Autoridad;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class ModuloAutenticacionStepDefinitions {

    private DBPruebaEnMemoria dbMockeada;
    private ServicioAutenticacion servicioAutenticacion;
    private Exception excepcionEsperada;

    public ModuloAutenticacionStepDefinitions() {
        this.dbMockeada = new DBPruebaEnMemoria();
        this.servicioAutenticacion = new ServicioAutenticacion(dbMockeada);
    }

    @Given("Existen los siguientes usuarios:")
    public void existenLosSiguientesUsuarios(List<Map<String, String>> tabla) {
        for (Map<String, String> fila : tabla) {
            String email = fila.get("Email");
            String contrasena = fila.get("Contrasena");
            String autoridadStr = fila.get("Autoridad");

            Autoridad autoridad = convertirStringAAutoridad(autoridadStr);
            Usuario usuario = new Usuario(email, contrasena, autoridad);
            dbMockeada.guardarUsuario(usuario);
            System.out.println("DEBUG: Usuario registrado - " + email + " como " + autoridad);
        }
    }

    @Given("Que el usuario actual es:")
    public void queElUsuarioActualEs(List<Map<String, String>> tabla) {
        Map<String, String> fila = tabla.getFirst();
        String email = fila.get("Email");
        String contrasena = fila.get("Contrasena");
        String autoridadStr = fila.get("Autoridad");

        if (email != null && !email.trim().isEmpty()) {
            Autoridad autoridad = convertirStringAAutoridad(autoridadStr);
            Usuario usuario = new Usuario(email, contrasena, autoridad);
            dbMockeada.setUsuarioActual(usuario);
        } else {
            dbMockeada.setUsuarioActual(null);
        }
    }

    @When("Intenta iniciar sesion el siguiente usuario:")
    public void intentaIniciarSesionElSiguienteUsuario(List<Map<String, String>> tabla) {
        excepcionEsperada = null;
        Map<String, String> fila = tabla.getFirst();
        String email = fila.get("Email");
        String contrasena = fila.get("Contrasena");

        try {
            servicioAutenticacion.iniciarSesion(email, contrasena);
        } catch (Exception e) {
            this.excepcionEsperada = e;
        }
    }

    @When("Intenta crearse el siguiente usuario:")
    public void intentaCrearseElSiguienteUsuario(List<Map<String, String>> tabla) {
        excepcionEsperada = null;
        Map<String, String> fila = tabla.getFirst();
        String email = fila.get("Email");
        String contrasena = fila.get("Contrasena");
        String autoridadStr = fila.get("Autoridad");

        System.out.println("DEBUG: Intentando crear usuario - Email: " + email + ", Contraseña: " + contrasena);

        try {
            // Solo convertir autoridad
            Autoridad autoridad = null;
            if (autoridadStr != null && !autoridadStr.trim().isEmpty()) {
                autoridad = convertirStringAAutoridad(autoridadStr);
            }

            // SOLO VERIFICAR SI EL EMAIL YA EXISTE
            Optional<Usuario> usuarioExistente = dbMockeada.buscarUsuario(email);
            if (usuarioExistente.isPresent()) {
                System.out.println("DEBUG: Email ya existe, lanzando excepción...");
                throw new RuntimeException("Email existente");
            }

            // DEJAR QUE LA CLASE USUARIO HAGA TODAS LAS VALIDACIONES
            Usuario nuevoUsuario = new Usuario(email, contrasena, autoridad);
            dbMockeada.guardarUsuario(nuevoUsuario);

            System.out.println("DEBUG: Usuario creado exitosamente - " + email + " como " + autoridad);

        } catch (Exception e) {
            System.out.println("DEBUG: Excepción capturada: " + e.getMessage());
            this.excepcionEsperada = e;
        }
    }

    @Then("El usuario actual es:")
    public void elUsuarioActualEs(List<Map<String, String>> tabla) {
        Map<String, String> filaEsperada = tabla.getFirst();
        String emailEsperado = filaEsperada.get("Email");
        String autoridadEsperadaStr = filaEsperada.get("Autoridad");

        Usuario usuarioActual = dbMockeada.getUsuarioActual();

        assertThat(usuarioActual).isNotNull();
        assertThat(usuarioActual.getEmail()).isEqualTo(emailEsperado);
        assertThat(usuarioActual.getContrasena()).startsWith("$2a$");

        Autoridad autoridadEsperada = convertirStringAAutoridad(autoridadEsperadaStr);
        assertThat(usuarioActual.getAutoridad()).isEqualTo(autoridadEsperada);
    }

    @Then("El sistema muestra el mensaje de error {string}")
    public void elSistemaMuestraElMensajeDeError(String mensajeError) {
        System.out.println("DEBUG: Verificando error - Esperado: '" + mensajeError + "', Actual: " +
                (excepcionEsperada != null ? "'" + excepcionEsperada.getMessage() + "'" : "null"));

        assertThat(excepcionEsperada)
                .isNotNull()
                .hasMessage(mensajeError);
    }

    @Then("La lista de usuarios es:")
    public void laListaDeUsuariosEs(List<Map<String, String>> tablaEsperada) {
        Map<String, Usuario> usuarios = dbMockeada.getUsuarios();

        System.out.println("DEBUG: Total usuarios en sistema: " + usuarios.size());
        assertThat(usuarios).hasSize(tablaEsperada.size());

        for (Map<String, String> filaEsperada : tablaEsperada) {
            String email = filaEsperada.get("Email");
            Usuario usuario = usuarios.get(email);

            assertThat(usuario).isNotNull();
            assertThat(usuario.getEmail()).isEqualTo(filaEsperada.get("Email"));
            assertThat(usuario.getContrasena()).startsWith("$2a$");

            String autoridadEsperadaStr = filaEsperada.get("Autoridad");
            Autoridad autoridadEsperada = convertirStringAAutoridad(autoridadEsperadaStr);
            assertThat(usuario.getAutoridad()).isEqualTo(autoridadEsperada);
        }
    }

    private Autoridad convertirStringAAutoridad(String autoridadStr) {
        if (autoridadStr == null || autoridadStr.trim().isEmpty()) {
            return null;
        }

        try {
            String autoridadNormalizada = autoridadStr.toUpperCase().trim();
            if (!autoridadNormalizada.equals("MEDICO") && !autoridadNormalizada.equals("ENFERMERO")) {
                throw new RuntimeException("Autoridad desconocida: " + autoridadStr);
            }
            return Autoridad.valueOf(autoridadNormalizada);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Autoridad desconocida: " + autoridadStr);
        }
    }
}