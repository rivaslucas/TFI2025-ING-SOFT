package org.example.app;

import org.example.app.interfaces.RepositorioUsuarios;
import org.example.domain.Autoridad;
import org.example.domain.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioAutenticacion {
    private RepositorioUsuarios dbUsuarios;
    private Usuario usuarioActual;
    private final List<Usuario> usuarios;

    public ServicioAutenticacion(RepositorioUsuarios dbUsuarios) {
        this.dbUsuarios = dbUsuarios;
        this.usuarioActual = null;
        this.usuarios = new ArrayList<>();
    }

    public void iniciarSesion(String email, String contrasena) {
        Optional<Usuario> usuario = dbUsuarios.buscarUsuario(email);
        if (usuario.isPresent()) {
            // USAR verificarContrasena EN LUGAR DE COMPARACIÓN DIRECTA
            if (contrasena != null && usuario.get().verificarContrasena(contrasena)) {
                this.usuarioActual = usuario.get();
                dbUsuarios.setUsuarioActual(usuarioActual);
            } else {
                throw new RuntimeException("Usuario o contrasena invalido");
            }
        } else {
            throw new RuntimeException("Usuario o contrasena invalido");
        }
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void crearUsuario(String email, String contrasena, Autoridad autoridad) {
        Optional<Usuario> usuario = dbUsuarios.buscarUsuario(email);
        if (usuario.isPresent()) {
            throw new RuntimeException("Email existente");
        } else {
            // El constructor de Usuario valida y hashea automáticamente
            Usuario usuarioNuevo = new Usuario(email, contrasena, autoridad);
            this.usuarios.add(usuarioNuevo);
            dbUsuarios.guardarUsuario(usuarioNuevo);
        }
    }

    public List<Usuario> obtenerUsuarios() {
        return this.usuarios;
    }
}