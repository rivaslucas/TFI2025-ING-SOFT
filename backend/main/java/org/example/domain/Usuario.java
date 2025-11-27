package org.example.domain;

import org.mindrot.jbcrypt.BCrypt;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Usuario {
    private String email;
    private String contrasena;
    private Autoridad autoridad;

    public Usuario(String email, String contrasena, Autoridad autoridad) {
        this.email = email;
        // PRIMERO validar, LUEGO hashear
        validarCampos(contrasena);
        this.contrasena = hashContrasena(contrasena);
        this.autoridad = autoridad;
    }

    // Constructor para cuando ya tenemos el hash (útil para cargar desde DB)
    public Usuario(String email, String contrasenaHash, Autoridad autoridad, boolean esHash) {
        this.email = email;
        this.contrasena = contrasenaHash;
        this.autoridad = autoridad;
        // No validamos campos si ya es un hash
    }

    public String getEmail() { return email; }
    public String getContrasena() { return contrasena; }
    public Autoridad getAutoridad() { return autoridad; }

    public void validarCampos(String contrasenaPlana) {
        Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher emailMatcher = emailPattern.matcher(this.email);
        if (!emailMatcher.find()) {
            throw new RuntimeException("Email invalido");
        }
        if (contrasenaPlana == null) {
            throw new RuntimeException("Contrasena es un campo obligatorio");
        } else if (contrasenaPlana.length() < 8) {
            throw new RuntimeException("Contrasena demasiado corta");
        }
    }

    // Método para hashear contraseñas
    private String hashContrasena(String contrasenaPlana) {
        if (contrasenaPlana == null) {
            throw new RuntimeException("Contrasena es un campo obligatorio");
        }
        return BCrypt.hashpw(contrasenaPlana, BCrypt.gensalt());
    }

    // Método para verificar contraseñas
    public boolean verificarContrasena(String contrasenaPlana) {
        if (contrasenaPlana == null || this.contrasena == null) {
            return false;
        }
        return BCrypt.checkpw(contrasenaPlana, this.contrasena);
    }
}