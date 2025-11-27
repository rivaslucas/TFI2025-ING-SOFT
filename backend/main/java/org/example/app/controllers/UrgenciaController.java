package org.example.app.controllers;

import org.example.app.controllers.dto.IngresoRequest;
import org.example.app.controllers.dto.IngresoResponse;
import org.example.app.interfaces.RepositorioIngresos;
import org.example.app.interfaces.RepositorioPacientes;
import org.example.app.services.ServicioUrgencias;
import org.example.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/urgencias")
public class UrgenciaController {

    private final ServicioUrgencias servicioUrgencias;
    private final RepositorioPacientes repositorioPacientes;
    private final RepositorioIngresos repositorioIngresos;

    public UrgenciaController(ServicioUrgencias servicioUrgencias,
                              RepositorioPacientes repositorioPacientes,
                              RepositorioIngresos repositorioIngresos) {
        this.servicioUrgencias = servicioUrgencias;
        this.repositorioPacientes = repositorioPacientes;
        this.repositorioIngresos = repositorioIngresos;

        System.out.println("=== CONTROLLER INICIALIZADO ===");
        System.out.println("RepositorioIngresos: " + repositorioIngresos.getClass().getSimpleName());
    }

    @PostMapping("/ingresos")
    public ResponseEntity<?> registrarIngreso(@RequestBody IngresoRequest request) {
        try {
            System.out.println("=== INICIO REGISTRO INGRESO ===");

            // Validar campos obligatorios del request
            if (request.getCuilPaciente() == null || request.getCuilPaciente().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El CUIL del paciente es obligatorio"));
            }

            String cuilPaciente = request.getCuilPaciente().trim();
            System.out.println("📋 CUIL recibido: " + cuilPaciente);

            // ✅ VERIFICAR SI EL PACIENTE YA TIENE INGRESOS ACTIVOS
            System.out.println("🔍 VERIFICANDO INGRESOS ACTIVOS...");
            boolean tieneIngresosActivos = repositorioIngresos.pacienteTieneIngresosActivos(cuilPaciente);
            System.out.println("📊 Resultado verificación: " + tieneIngresosActivos);

            if (tieneIngresosActivos) {
                System.out.println("🚫 BLOQUEADO - Paciente tiene ingresos activos");
                return ResponseEntity.badRequest().body(crearErrorResponse(
                        "El paciente ya tiene un ingreso activo/pendiente. No se puede registrar otro ingreso."
                ));
            }

            // Buscar paciente
            System.out.println("👤 BUSCANDO PACIENTE...");
            var pacienteOpt = repositorioPacientes.buscarPacientePorCuil(cuilPaciente);
            if (pacienteOpt.isEmpty()) {
                System.out.println("❌ PACIENTE NO ENCONTRADO");
                return ResponseEntity.badRequest().body(crearErrorResponse("Paciente no encontrado"));
            }
            System.out.println("✅ PACIENTE ENCONTRADO: " + pacienteOpt.get().getNombre());

            // Crear enfermera
            Enfermera enfermera = new Enfermera(
                    request.getEnfermeraNombre(),
                    request.getEnfermeraApellido()
            );

            // Convertir nivel de emergencia
            NivelEmergencia nivelEmergencia = convertirNivelEmergencia(request.getNivelEmergencia());

            // Registrar urgencia
            System.out.println("🎯 REGISTRANDO URGENCIA...");
            servicioUrgencias.registrarUrgencia(
                    cuilPaciente,
                    enfermera,
                    request.getInforme(),
                    nivelEmergencia,
                    request.getTemperatura(),
                    request.getFrecuenciaCardiaca(),
                    request.getFrecuenciaRespiratoria(),
                    request.getTensionSistolica(),
                    request.getTensionDiastolica()
            );

            System.out.println("✅ INGRESO REGISTRADO EXITOSAMENTE");
            return ResponseEntity.ok().body(crearSuccessResponse("Paciente registrado exitosamente en urgencias"));

        } catch (RuntimeException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println("💥 ERROR INTERNO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno del servidor"));
        }
    }

    @GetMapping("/lista-espera")
    public ResponseEntity<?> obtenerListaEspera() {
        try {
            List<Ingreso> ingresosPendientes = servicioUrgencias.obtenerIngresosPendientes();

            List<IngresoResponse> response = ingresosPendientes.stream()
                    .map(this::convertirIngresoAResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al obtener la lista de espera"));
        }
    }

    @GetMapping("/paciente/{cuil}/estado")
    public ResponseEntity<?> verificarEstadoPaciente(@PathVariable String cuil) {
        try {
            boolean tieneIngresosActivos = repositorioIngresos.pacienteTieneIngresosActivos(cuil);
            List<Ingreso> ingresos = repositorioIngresos.buscarIngresosPorPaciente(cuil);

            Map<String, Object> response = new HashMap<>();
            response.put("cuil", cuil);
            response.put("tieneIngresosActivos", tieneIngresosActivos);
            response.put("totalIngresos", ingresos.size());
            response.put("ingresos", ingresos.stream()
                    .map(ing -> Map.of(
                            "id", ing.getId(),
                            "estado", ing.getEstado().name(),
                            "fechaIngreso", ing.getFechaIngreso(),
                            "nivelEmergencia", ing.getNivelEmergencia().name()
                    ))
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al verificar estado del paciente"));
        }
    }

    private IngresoResponse convertirIngresoAResponse(Ingreso ingreso) {
        return new IngresoResponse(
                ingreso.getId(),
                ingreso.getPaciente().getNombre(),
                ingreso.getPaciente().getApellido(),
                ingreso.getPaciente().getCuil(),
                ingreso.getNivelEmergencia().name(),
                ingreso.getEstado().name(),
                ingreso.getFechaIngreso(),
                ingreso.getEnfermera().getNombre() + " " + ingreso.getEnfermera().getApellido()
        );
    }

    private NivelEmergencia convertirNivelEmergencia(String nivel) {
        if (nivel == null) return NivelEmergencia.SIN_URGENCIA;

        switch (nivel.toUpperCase()) {
            case "CRITICA": return NivelEmergencia.CRITICA;
            case "EMERGENCIA": return NivelEmergencia.EMERGENCIA;
            case "URGENCIA": return NivelEmergencia.URGENCIA;
            case "URGENCIA_MENOR": return NivelEmergencia.URGENCIA_MENOR;
            default: return NivelEmergencia.SIN_URGENCIA;
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