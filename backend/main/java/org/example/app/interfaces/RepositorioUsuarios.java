package org.example.app.interfaces;

import org.example.domain.Usuario;

import java.util.Optional;

public interface RepositorioUsuarios {
    void guardarUsuario(Usuario usuario);

    Optional<Usuario> buscarUsuario(String email);

    Optional<Usuario> getUsuarioActual(String email, String contrasena);

    void setUsuarioActual(Usuario usuarioActual);

    // ✅ FUNCIONES NUEVAS (si las necesitas)
    default boolean existeUsuario(String email) {
        return buscarUsuario(email).isPresent();
    }
}