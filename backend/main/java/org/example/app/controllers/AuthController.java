package org.example.app.controllers;

import org.example.app.controllers.dto.AuthRequest;
import org.example.app.controllers.dto.AuthResponse;
import org.example.app.services.ServicioAutenticacion;
import org.example.domain.Autoridad;
import org.example.domain.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ServicioAutenticacion servicioAutenticacion;

    public AuthController(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            servicioAutenticacion.iniciarSesion(request.getEmail(), request.getContrasena());
            Usuario usuarioActual = servicioAutenticacion.getUsuarioActual();

            return ResponseEntity.ok(AuthResponse.success(
                    usuarioActual.getEmail(),
                    usuarioActual.getAutoridad()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(AuthResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        try {

            servicioAutenticacion.crearUsuario(
                    request.getEmail(),
                    request.getContrasena(),
                    Autoridad.ENFERMERO
            );

            return ResponseEntity.ok(AuthResponse.success(
                    request.getEmail(),
                    Autoridad.ENFERMERO
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(AuthResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        Usuario usuarioActual = servicioAutenticacion.getUsuarioActual();
        if (usuarioActual == null) {
            return ResponseEntity.ok(AuthResponse.error("No hay usuario autenticado"));
        }

        return ResponseEntity.ok(AuthResponse.success(
                usuarioActual.getEmail(),
                usuarioActual.getAutoridad()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        // En una implementación real, aquí se invalidaría el token/sesión
        return ResponseEntity.ok(AuthResponse.success(null, null));
    }
}