package org.example.app.controllers;

import org.example.app.controllers.dto.AtencionRequest;
import org.example.app.controllers.dto.AtencionResponse;
import org.example.app.controllers.dto.IngresoResponse;
import org.example.app.controllers.dto.LiberarPacienteRequest;
import org.example.app.interfaces.RepositorioIngresos;
import org.example.app.services.ServicioCreacionAtencion;
import org.example.app.services.ServicioReclamoPacientes;
import org.example.domain.Atencion;
import org.example.domain.EstadoIngreso;
import org.example.domain.Ingreso;
import org.example.domain.Medico;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atenciones")
public class AtencionController {

    private final ServicioReclamoPacientes servicioReclamoPacientes;
    private final ServicioCreacionAtencion servicioCreacionAtencion;
    private final RepositorioIngresos repositorioIngresos;

    public AtencionController(ServicioReclamoPacientes servicioReclamoPacientes,
                              ServicioCreacionAtencion servicioCreacionAtencion,
                              RepositorioIngresos repositorioIngresos) {
        this.servicioReclamoPacientes = servicioReclamoPacientes;
        this.servicioCreacionAtencion = servicioCreacionAtencion;
        this.repositorioIngresos = repositorioIngresos;

        System.out.println("=== ATENCION CONTROLLER INICIALIZADO ===");
    }

    @PostMapping("/reclamar")
    public ResponseEntity<?> reclamarProximoPaciente(@RequestParam String medicoMatricula) {
        try {
            System.out.println("=== INICIO RECLAMO PACIENTE ===");
            System.out.println("📋 Matrícula recibida: " + medicoMatricula);

            // Validar parámetros
            if (medicoMatricula == null || medicoMatricula.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("La matrícula del médico es obligatoria"));
            }

            // En una implementación real, el médico vendría de la autenticación
            Medico medico = new Medico(
                    medicoMatricula + "@hospital.com",
                    "password",
                    medicoMatricula,
                    "CLINICA"
            );

            System.out.println("🎯 Intentando reclamar paciente para médico: " + medicoMatricula);
            Ingreso ingresoReclamado = servicioReclamoPacientes.reclamarProximoPaciente(medico);

            IngresoResponse response = new IngresoResponse(
                    ingresoReclamado.getId(),
                    ingresoReclamado.getPaciente().getNombre(),
                    ingresoReclamado.getPaciente().getApellido(),
                    ingresoReclamado.getPaciente().getCuil(),
                    ingresoReclamado.getNivelEmergencia().name(),
                    ingresoReclamado.getEstado().name(),
                    ingresoReclamado.getFechaIngreso(),
                    ingresoReclamado.getEnfermera().getNombre() + " " + ingresoReclamado.getEnfermera().getApellido()
            );

            System.out.println("✅ RECLAMO EXITOSO - Paciente asignado: " +
                    ingresoReclamado.getPaciente().getNombre() + " " +
                    ingresoReclamado.getPaciente().getApellido());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ ERROR en reclamo: " + e.getMessage());
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println("💥 ERROR INTERNO en reclamo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno al reclamar paciente"));
        }
    }

    @PostMapping("/{idIngreso}/atender")
    public ResponseEntity<?> registrarAtencion(@PathVariable String idIngreso,
                                               @RequestBody AtencionRequest request) {
        try {
            System.out.println("=== INICIO REGISTRO ATENCIÓN ===");
            System.out.println("📋 ID Ingreso: " + idIngreso);
            System.out.println("📋 Matrícula médico: " + request.getMedicoMatricula());

            // Validar parámetros
            if (idIngreso == null || idIngreso.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("ID de ingreso es obligatorio"));
            }
            if (request.getMedicoMatricula() == null || request.getMedicoMatricula().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Matrícula del médico es obligatoria"));
            }
            if (request.getInformeMedico() == null || request.getInformeMedico().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El informe médico es obligatorio"));
            }

            // En una implementación real, el médico vendría de la autenticación
            Medico medico = new Medico(
                    request.getMedicoMatricula() + "@hospital.com",
                    "password",
                    request.getMedicoMatricula(),
                    "CLINICA"
            );

            System.out.println("🎯 Registrando atención para ingreso: " + idIngreso);
            Atencion atencion = servicioCreacionAtencion.registrarAtencion(
                    idIngreso,
                    request.getInformeMedico(),
                    medico
            );

            AtencionResponse response = new AtencionResponse(
                    atencion.getId(),
                    atencion.getIngreso().getId(),
                    atencion.getIngreso().getPaciente().getNombre() + " " + atencion.getIngreso().getPaciente().getApellido(),
                    atencion.getMedico().getMatricula(),
                    atencion.getInformeMedico(),
                    atencion.getFechaHora()
            );

            System.out.println("✅ ATENCIÓN REGISTRADA EXITOSAMENTE");
            System.out.println("   - ID Atención: " + atencion.getId());
            System.out.println("   - Paciente: " + response.getPacienteNombre());
            System.out.println("   - Médico: " + response.getMedicoMatricula());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ ERROR en atención: " + e.getMessage());
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println("💥 ERROR INTERNO en atención: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno al registrar atención"));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> obtenerPacientesPendientes() {
        try {
            System.out.println("📋 SOLICITANDO LISTA DE PACIENTES PENDIENTES");
            List<Ingreso> ingresosPendientes = servicioReclamoPacientes.obtenerIngresosPendientes();

            List<IngresoResponse> response = ingresosPendientes.stream()
                    .map(ingreso -> new IngresoResponse(
                            ingreso.getId(),
                            ingreso.getPaciente().getNombre(),
                            ingreso.getPaciente().getApellido(),
                            ingreso.getPaciente().getCuil(),
                            ingreso.getNivelEmergencia().name(),
                            ingreso.getEstado().name(),
                            ingreso.getFechaIngreso(),
                            ingreso.getEnfermera().getNombre() + " " + ingreso.getEnfermera().getApellido()
                    ))
                    .collect(Collectors.toList());

            System.out.println("✅ Lista de pendientes obtenida - Total: " + response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ ERROR obteniendo pendientes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al obtener pacientes pendientes"));
        }
    }

    // ✅ CORREGIDO: Verificar estado del médico con pacienteActual
    @GetMapping("/medico/{matricula}/estado")
    public ResponseEntity<?> verificarEstadoMedico(@PathVariable String matricula) {
        try {
            System.out.println("🔍 VERIFICANDO ESTADO DEL MÉDICO: " + matricula);

            if (matricula == null || matricula.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("La matrícula del médico es obligatoria"));
            }

            List<Ingreso> pacientesEnProceso = servicioReclamoPacientes.obtenerPacientesEnProcesoPorMedico(matricula);
            boolean puedeReclamar = servicioReclamoPacientes.medicoPuedeReclamarPaciente(matricula);

            // ✅ CORREGIDO: Eliminar duplicados
            List<Ingreso> pacientesUnicos = pacientesEnProceso.stream()
                    .distinct()
                    .collect(Collectors.toList());

            // ✅ CORREGIDO: Obtener paciente actual (el primero sin duplicados)
            Map<String, Object> pacienteActual = null;
            if (!pacientesUnicos.isEmpty()) {
                Ingreso ingresoActual = pacientesUnicos.get(0);
                pacienteActual = crearMapPaciente(ingresoActual);
                System.out.println("✅ PACIENTE ACTUAL CONFIGURADO: " + pacienteActual.get("pacienteNombre"));
            } else {
                System.out.println("ℹ️ No hay paciente actual para médico: " + matricula);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("medicoMatricula", matricula);
            response.put("tienePacientesEnProceso", !pacientesUnicos.isEmpty());
            response.put("puedeReclamarPaciente", puedeReclamar);
            response.put("totalPacientesEnProceso", pacientesUnicos.size());
            response.put("pacienteActual", pacienteActual); // ✅ NUEVO: Campo agregado
            response.put("pacientesEnProceso", pacientesUnicos.stream()
                    .map(this::crearMapPaciente)
                    .collect(Collectors.toList()));

            System.out.println("✅ Estado médico obtenido - Puede reclamar: " + puedeReclamar);
            System.out.println("✅ Paciente actual: " + (pacienteActual != null ? pacienteActual.get("pacienteNombre") : "Ninguno"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ ERROR verificando estado médico: " + e.getMessage());
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al verificar estado del médico"));
        }
    }

    // ✅ NUEVO: Método auxiliar para crear mapa de paciente
    private Map<String, Object> crearMapPaciente(Ingreso ingreso) {
        Map<String, Object> pacienteMap = new HashMap<>();
        pacienteMap.put("id", ingreso.getId());
        pacienteMap.put("idIngreso", ingreso.getId());
        pacienteMap.put("pacienteNombre", ingreso.getPaciente().getNombre());
        pacienteMap.put("pacienteApellido", ingreso.getPaciente().getApellido());
        pacienteMap.put("pacienteCuil", ingreso.getPaciente().getCuil());
        pacienteMap.put("nivelEmergencia", ingreso.getNivelEmergencia().name());
        pacienteMap.put("estado", ingreso.getEstado().name());
        pacienteMap.put("fechaIngreso", ingreso.getFechaIngreso());
        pacienteMap.put("enfermeraNombre", ingreso.getEnfermera().getNombre() + " " + ingreso.getEnfermera().getApellido());
        return pacienteMap;
    }

    // ✅ NUEVO ENDPOINT: Obtener pacientes en proceso por médico
    @GetMapping("/medico/{matricula}/en-proceso")
    public ResponseEntity<?> obtenerPacientesEnProceso(@PathVariable String matricula) {
        try {
            System.out.println("📋 SOLICITANDO PACIENTES EN PROCESO PARA MÉDICO: " + matricula);

            if (matricula == null || matricula.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("La matrícula del médico es obligatoria"));
            }

            List<Ingreso> pacientesEnProceso = servicioReclamoPacientes.obtenerPacientesEnProcesoPorMedico(matricula);

            List<IngresoResponse> response = pacientesEnProceso.stream()
                    .map(ingreso -> new IngresoResponse(
                            ingreso.getId(),
                            ingreso.getPaciente().getNombre(),
                            ingreso.getPaciente().getApellido(),
                            ingreso.getPaciente().getCuil(),
                            ingreso.getNivelEmergencia().name(),
                            ingreso.getEstado().name(),
                            ingreso.getFechaIngreso(),
                            ingreso.getEnfermera().getNombre() + " " + ingreso.getEnfermera().getApellido()
                    ))
                    .collect(Collectors.toList());

            System.out.println("✅ Pacientes en proceso obtenidos - Total: " + response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ ERROR obteniendo pacientes en proceso: " + e.getMessage());
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al obtener pacientes en proceso"));
        }
    }

    // ✅ NUEVO ENDPOINT: Verificar si médico puede reclamar paciente
    @GetMapping("/medico/{matricula}/puede-reclamar")
    public ResponseEntity<?> verificarPuedeReclamar(@PathVariable String matricula) {
        try {
            System.out.println("❓ VERIFICANDO SI MÉDICO PUEDE RECLAMAR: " + matricula);

            if (matricula == null || matricula.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("La matrícula del médico es obligatoria"));
            }

            boolean puedeReclamar = servicioReclamoPacientes.medicoPuedeReclamarPaciente(matricula);

            Map<String, Object> response = new HashMap<>();
            response.put("medicoMatricula", matricula);
            response.put("puedeReclamarPaciente", puedeReclamar);
            response.put("mensaje", puedeReclamar ?
                    "El médico puede reclamar un nuevo paciente" :
                    "El médico no puede reclamar otro paciente. Tiene pacientes en proceso.");

            System.out.println("✅ Verificación completada - Puede reclamar: " + puedeReclamar);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ ERROR en verificación: " + e.getMessage());
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al verificar si puede reclamar"));
        }
    }

    // ✅ NUEVO ENDPOINT: Liberar paciente
    @PostMapping("/{idIngreso}/liberar")
    public ResponseEntity<?> liberarPaciente(
            @PathVariable String idIngreso,
            @RequestBody LiberarPacienteRequest request) {

        try {
            System.out.println("=== INICIO LIBERACIÓN PACIENTE ===");
            System.out.println("📋 ID Ingreso: " + idIngreso);
            System.out.println("👨‍⚕️ Médico: " + request.getMedicoMatricula());
            System.out.println("📝 Motivo: " + request.getMotivo());

            // Validaciones
            if (request.getMedicoMatricula() == null || request.getMedicoMatricula().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("La matrícula del médico es obligatoria"));
            }

            if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El motivo de liberación es obligatorio"));
            }

            // Buscar el ingreso
            Ingreso ingreso = repositorioIngresos.buscarPorId(idIngreso)
                    .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));

            System.out.println("✅ INGRESO ENCONTRADO:");
            System.out.println("   - Estado: " + ingreso.getEstado());
            System.out.println("   - Médico asignado: " + ingreso.getMedicoAsignado());
            System.out.println("   - Paciente: " + ingreso.getPaciente().getNombre());

            // Validar que el médico es el asignado
            if (!request.getMedicoMatricula().equals(ingreso.getMedicoAsignado())) {
                return ResponseEntity.badRequest().body(crearErrorResponse("No tiene permisos para liberar este paciente"));
            }

            // Validar que el ingreso está en proceso
            if (ingreso.getEstado() != EstadoIngreso.EN_PROCESO) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El paciente no está en proceso de atención"));
            }

            // ✅ SOLUCIÓN SIMPLIFICADA: Solo cambiar estado sin crear Atencion
            // Cambiar estado del ingreso a FINALIZADO
            ingreso.setEstado(EstadoIngreso.FINALIZADO);
            repositorioIngresos.actualizarIngreso(ingreso);

            System.out.println("🎉 PACIENTE LIBERADO EXITOSAMENTE");
            System.out.println("   - Paciente: " + ingreso.getPaciente().getNombre());
            System.out.println("   - Estado actualizado: " + ingreso.getEstado());
            System.out.println("   - Motivo: " + request.getMotivo());

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Paciente liberado exitosamente");
            response.put("paciente", ingreso.getPaciente().getNombre() + " " + ingreso.getPaciente().getApellido());
            response.put("estado", ingreso.getEstado().name());
            response.put("motivo", request.getMotivo());
            response.put("fechaLiberacion", LocalDateTime.now());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body(crearErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println("💥 ERROR INTERNO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno del servidor"));
        }
    }

    // ✅ NUEVO ENDPOINT: Obtener paciente actual del médico
    @GetMapping("/medico/{matricula}/paciente-actual")
    public ResponseEntity<?> obtenerPacienteActual(@PathVariable String matricula) {
        try {
            System.out.println("🔍 SOLICITANDO PACIENTE ACTUAL DEL MÉDICO: " + matricula);

            if (matricula == null || matricula.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("La matrícula del médico es obligatoria"));
            }

            List<Ingreso> pacientesEnProceso = servicioReclamoPacientes.obtenerPacientesEnProcesoPorMedico(matricula);

            if (pacientesEnProceso.isEmpty()) {
                return ResponseEntity.status(404).body(crearErrorResponse("No hay paciente actual asignado"));
            }

            // Tomar el primer paciente en proceso como paciente actual
            Ingreso ingresoActual = pacientesEnProceso.get(0);

            IngresoResponse response = new IngresoResponse(
                    ingresoActual.getId(),
                    ingresoActual.getPaciente().getNombre(),
                    ingresoActual.getPaciente().getApellido(),
                    ingresoActual.getPaciente().getCuil(),
                    ingresoActual.getNivelEmergencia().name(),
                    ingresoActual.getEstado().name(),
                    ingresoActual.getFechaIngreso(),
                    ingresoActual.getEnfermera().getNombre() + " " + ingresoActual.getEnfermera().getApellido()
            );

            System.out.println("✅ Paciente actual obtenido: " + response.getPacienteNombre());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ ERROR obteniendo paciente actual: " + e.getMessage());
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error al obtener paciente actual"));
        }
    }

    private Map<String, String> crearErrorResponse(String mensaje) {
        Map<String, String> response = new HashMap<>();
        response.put("error", mensaje);
        return response;
    }
}