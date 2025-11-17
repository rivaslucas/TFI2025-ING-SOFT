package org.example.app.interfaces;

import org.example.domain.Usuario;
import java.util.Optional;
public interface RepositorioUsuarios {
    public void guardarUsuario(Usuario usuario);
    public Optional<Usuario> buscarUsuario(String email);
    public Optional<Usuario> getUsuarioActual(String email, String contrasena);
    public void setUsuarioActual(Usuario usuarioActual);

}
