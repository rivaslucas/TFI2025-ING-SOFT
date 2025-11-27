package org.example.app.controllers.dto;

public class AuthRequest {
    private String email;
    private String contrasena;

    // Constructores
    public AuthRequest() {}

    public AuthRequest(String email, String contrasena) {
        this.email = email;
        this.contrasena = contrasena;
    }

    // Getters y setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}