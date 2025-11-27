package org.example.app.controllers.dto;

import org.example.domain.Autoridad;

public class AuthResponse {
    private String email;
    private Autoridad autoridad;
    private String mensaje;
    private boolean success;

    // Constructores
    public AuthResponse() {}

    public AuthResponse(String email, Autoridad autoridad, String mensaje, boolean success) {
        this.email = email;
        this.autoridad = autoridad;
        this.mensaje = mensaje;
        this.success = success;
    }

    // Getters y setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Autoridad getAutoridad() { return autoridad; }
    public void setAutoridad(Autoridad autoridad) { this.autoridad = autoridad; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    // Métodos estáticos para respuestas comunes
    public static AuthResponse success(String email, Autoridad autoridad) {
        return new AuthResponse(email, autoridad, "Autenticación exitosa", true);
    }

    public static AuthResponse error(String mensaje) {
        return new AuthResponse(null, null, mensaje, false);
    }
}