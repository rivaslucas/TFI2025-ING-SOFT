package org.example.app.controllers;

import org.example.app.controllers.dto.PacienteRequest;
import org.example.app.controllers.dto.PacienteResponse;
import org.example.app.services.ServicioRegistroPacientes;
import org.example.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final ServicioRegistroPacientes servicioRegistroPacientes;
    private final org.example.app.interfaces.RepositorioPacientes repositorioPacientes;

    public PacienteController(ServicioRegistroPacientes servicioRegistroPacientes,
                              org.example.app.interfaces.RepositorioPacientes repositorioPacientes) {
        this.servicioRegistroPacientes = servicioRegistroPacientes;
        this.repositorioPacientes = repositorioPacientes;
    }

    @PostMapping
    public ResponseEntity<?> registrarPaciente(@RequestBody PacienteRequest request) {
        try {
            // Validar campos obligatorios del request
            if (request.getCuil() == null || request.getCuil().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("CUIL es un campo obligatorio"));
            }
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Nombre es un campo obligatorio"));
            }
            if (request.getApellido() == null || request.getApellido().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Apellido es un campo obligatorio"));
            }
            if (request.getDireccion() == null) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Dirección es obligatoria"));
            }
            if (request.getDireccion().getCalle() == null || request.getDireccion().getCalle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Calle es un campo obligatorio"));
            }
            if (request.getDireccion().getNumero() == 0) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Número es un campo obligatorio"));
            }
            if (request.getDireccion().getLocalidad() == null || request.getDireccion().getLocalidad().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Localidad es un campo obligatorio"));
            }

            // Convertir DTO a objetos de dominio
            Domicilio domicilio = new Domicilio(
                    request.getDireccion().getCalle(),
                    request.getDireccion().getNumero(),
                    request.getDireccion().getLocalidad()
            );

            Afiliado afiliado = null;
            if (request.getObraSocialNombre() != null && !request.getObraSocialNombre().trim().isEmpty()) {
                // En una implementación real, buscaríamos la obra social del repositorio
                ObraSocial obraSocial = new ObraSocial(request.getObraSocialNombre(), request.getObraSocialNombre());
                afiliado = new Afiliado(request.getNumeroAfiliado(), obraSocial);
            }

            // Registrar paciente usando el servicio existente
            servicioRegistroPacientes.registrarPaciente(
                    request.getCuil(),
                    request.getNombre(),
                    request.getApellido(),
                    domicilio,
                    afiliado
            );

            return ResponseEntity.ok().body(crearSuccessResponse("Paciente registrado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno del servidor al registrar paciente"));
        }
    }

    @GetMapping("/{cuil}")
    public ResponseEntity<?> buscarPaciente(@PathVariable String cuil) {
        try {
            if (cuil == null || cuil.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("CUIL es requerido"));
            }

            Optional<Paciente> pacienteOpt = repositorioPacientes.buscarPacientePorCuil(cuil);

            if (pacienteOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Paciente paciente = pacienteOpt.get();
            PacienteResponse.DireccionResponse direccion = null;
            if (paciente.getDireccion() != null) {
                direccion = new PacienteResponse.DireccionResponse(
                        paciente.getDireccion().getCalle(),
                        paciente.getDireccion().getNumero(),
                        paciente.getDireccion().getLocalidad()
                );
            }

            PacienteResponse response = new PacienteResponse(
                    paciente.getCuil(),
                    paciente.getNombre(),
                    paciente.getApellido(),
                    paciente.getObraSocialNombre(),
                    paciente.getAfiliado() != null ? paciente.getAfiliado().getNumAfiliado() : null,
                    direccion
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al buscar paciente"));
        }
    }

    @GetMapping
    public ResponseEntity<?> listarPacientes() {
        try {
            // En una implementación real, el repositorio tendría un método para listar todos
            List<Paciente> pacientes = repositorioPacientes.obtenerTodosLosPacientes();

            List<PacienteResponse> response = pacientes.stream()
                    .map(paciente -> {
                        PacienteResponse.DireccionResponse direccion = null;
                        if (paciente.getDireccion() != null) {
                            direccion = new PacienteResponse.DireccionResponse(
                                    paciente.getDireccion().getCalle(),
                                    paciente.getDireccion().getNumero(),
                                    paciente.getDireccion().getLocalidad()
                            );
                        }

                        return new PacienteResponse(
                                paciente.getCuil(),
                                paciente.getNombre(),
                                paciente.getApellido(),
                                paciente.getObraSocialNombre(),
                                paciente.getAfiliado() != null ? paciente.getAfiliado().getNumAfiliado() : null,
                                direccion
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al listar pacientes"));
        }
    }

    private Map<String, String> crearErrorResponse(String mensaje) {
        Map<String, String> response = new HashMap<>();
        response.put("error", mensaje);
        return response;
    }

    private Map<String, String> crearSuccessResponse(String mensaje) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", mensaje);
        return response;
    }
}