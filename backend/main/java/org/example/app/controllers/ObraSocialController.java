package org.example.app.controllers;

import org.example.app.controllers.dto.ObraSocialRequest;
import org.example.domain.ObraSocial;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/obras-sociales")
public class ObraSocialController {

    private final org.example.app.interfaces.RepositorioObraSocial repositorioObraSocial;

    public ObraSocialController(org.example.app.interfaces.RepositorioObraSocial repositorioObraSocial) {
        this.repositorioObraSocial = repositorioObraSocial;
        System.out.println("✅ ObraSocialController inicializado");
    }

    @GetMapping
    public ResponseEntity<?> listarObrasSociales() {
        try {
            System.out.println("📋 LISTAR OBRAS SOCIALES - Iniciando...");

            List<ObraSocial> obrasSociales = repositorioObraSocial.obtenerTodasLasObrasSociales();

            System.out.println("✅ Obtuvimos " + (obrasSociales != null ? obrasSociales.size() : "null") + " obras sociales");

            if (obrasSociales != null) {
                for (ObraSocial os : obrasSociales) {
                    System.out.println("   - " + os.getNombre() + " (" + os.getIdentificador() + ")");
                }
            }

            return ResponseEntity.ok(obrasSociales);

        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO en listarObrasSociales:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> registrarObraSocial(@RequestBody ObraSocialRequest request) {
        try {
            System.out.println("📝 REGISTRAR OBRA SOCIAL: " + request.getNombre());

            // Validar campos obligatorios
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El nombre de la obra social es obligatorio"));
            }
            if (request.getIdentificador() == null || request.getIdentificador().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El identificador de la obra social es obligatorio"));
            }

            // Verificar si ya existe
            if (repositorioObraSocial.existeObraSocial(request.getNombre())) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Ya existe una obra social con ese nombre"));
            }

            ObraSocial obraSocial = new ObraSocial(
                    request.getIdentificador(),
                    request.getNombre()
            );

            repositorioObraSocial.guardarObraSocial(obraSocial);
            return ResponseEntity.ok().body(crearSuccessResponse("Obra social registrada exitosamente", obraSocial));

        } catch (Exception e) {
            System.err.println("❌ ERROR en registrarObraSocial:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<?> buscarObraSocial(@PathVariable String nombre) {
        try {
            System.out.println("🔍 BUSCAR OBRA SOCIAL: " + nombre);

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Nombre de obra social es requerido"));
            }

            ObraSocial obraSocial = repositorioObraSocial.buscarObraSocial(nombre);

            if (obraSocial == null) {
                System.out.println("⚠️ Obra social no encontrada: " + nombre);
                return ResponseEntity.notFound().build();
            }

            System.out.println("✅ Obra social encontrada: " + obraSocial.getNombre());
            return ResponseEntity.ok(obraSocial);

        } catch (Exception e) {
            System.err.println("❌ ERROR en buscarObraSocial:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/{nombreOriginal}")
    public ResponseEntity<?> actualizarObraSocial(@PathVariable String nombreOriginal, @RequestBody ObraSocialRequest request) {
        try {
            System.out.println("✏️ ACTUALIZAR OBRA SOCIAL: " + nombreOriginal + " -> " + request.getNombre());

            if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Nombre original de obra social es requerido"));
            }

            // Validar campos obligatorios
            if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El nombre de la obra social es obligatorio"));
            }
            if (request.getIdentificador() == null || request.getIdentificador().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("El identificador de la obra social es obligatorio"));
            }

            // Buscar obra social existente
            ObraSocial obraSocialExistente = repositorioObraSocial.buscarObraSocial(nombreOriginal);
            if (obraSocialExistente == null) {
                return ResponseEntity.notFound().build();
            }

            // Si cambió el nombre, verificar que no exista otro con el nuevo nombre
            if (!nombreOriginal.equals(request.getNombre()) && repositorioObraSocial.existeObraSocial(request.getNombre())) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Ya existe una obra social con el nuevo nombre"));
            }

            // Crear obra social actualizada
            ObraSocial obraSocialActualizada = new ObraSocial(
                    request.getIdentificador(),
                    request.getNombre()
            );

            // Actualizar en el repositorio
            repositorioObraSocial.actualizarObraSocial(nombreOriginal, obraSocialActualizada);

            return ResponseEntity.ok().body(crearSuccessResponse("Obra social actualizada exitosamente", obraSocialActualizada));

        } catch (Exception e) {
            System.err.println("❌ ERROR en actualizarObraSocial:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<?> eliminarObraSocial(@PathVariable String nombre) {
        try {
            System.out.println("🗑️ ELIMINAR OBRA SOCIAL: " + nombre);

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(crearErrorResponse("Nombre de obra social es requerido"));
            }

            // Verificar si existe
            ObraSocial obraSocial = repositorioObraSocial.buscarObraSocial(nombre);
            if (obraSocial == null) {
                return ResponseEntity.notFound().build();
            }

            // Verificar si tiene pacientes afiliados
            int pacientesAfiliados = repositorioObraSocial.contarPacientesAfiliados(nombre);
            if (pacientesAfiliados > 0) {
                return ResponseEntity.badRequest().body(crearErrorResponse(
                        "No se puede eliminar la obra social porque tiene " + pacientesAfiliados + " pacientes afiliados"
                ));
            }

            // Eliminar obra social
            boolean eliminado = repositorioObraSocial.eliminarObraSocial(nombre);
            if (eliminado) {
                return ResponseEntity.ok().body(crearSuccessResponse("Obra social eliminada exitosamente", null));
            } else {
                return ResponseEntity.internalServerError().body(crearErrorResponse("Error al eliminar la obra social"));
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR en eliminarObraSocial:");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(crearErrorResponse("Error interno: " + e.getMessage()));
        }
    }

    private Map<String, Object> crearErrorResponse(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", mensaje);
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    private Map<String, Object> crearSuccessResponse(String mensaje, ObraSocial obraSocial) {
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", mensaje);
        response.put("success", true);
        response.put("timestamp", System.currentTimeMillis());
        if (obraSocial != null) {
            response.put("data", obraSocial);
        }
        return response;
    }
}