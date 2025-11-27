package org.example.app.config;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.app.interfaces.RepositorioObraSocial;
import org.example.app.interfaces.RepositorioPacientes;
import org.example.app.interfaces.RepositorioUsuarios;
import org.example.app.services.*;
import org.example.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    // Repositorios
    @Bean
    public RepositorioUsuarios repositorioUsuarios() {
        return new RepositorioUsuarios() {
            private Map<String, org.example.domain.Usuario> usuarios = new HashMap<>();

            @Override
            public void guardarUsuario(org.example.domain.Usuario usuario) {
                usuarios.put(usuario.getEmail(), usuario);
            }

            @Override
            public Optional<org.example.domain.Usuario> buscarUsuario(String email) {
                return Optional.ofNullable(usuarios.get(email));
            }

            @Override
            public Optional<org.example.domain.Usuario> getUsuarioActual(String email, String contrasena) {
                org.example.domain.Usuario usuario = usuarios.get(email);
                if (usuario != null && usuario.verificarContrasena(contrasena)) {
                    return Optional.of(usuario);
                }
                return Optional.empty();
            }

            @Override
            public void setUsuarioActual(org.example.domain.Usuario usuarioActual) {
                // Implementación simple para desarrollo
            }
        };
    }

    @Bean
    public RepositorioPacientes repositorioPacientes() {
        return new RepositorioPacientes() {
            private Map<String, Paciente> pacientes = new HashMap<>();

            @Override
            public void guardarPaciente(Paciente paciente) {
                pacientes.put(paciente.getCuil(), paciente);
                System.out.println("✅ PACIENTE GUARDADO: " + paciente.getCuil() + " - Total: " + pacientes.size());
            }

            @Override
            public Optional<Paciente> buscarPacientePorCuil(String cuil) {
                return Optional.ofNullable(pacientes.get(cuil));
            }

            @Override
            public boolean existeObraSocial(String obraSocialNombre) {
                return true; // Para desarrollo
            }

            @Override
            public boolean estaAfiliado(String cuil, String obraSocialNombre) {
                return true; // Para desarrollo
            }

            @Override
            public boolean pacienteExiste(String cuil) {
                return pacientes.containsKey(cuil);
            }

            @Override
            public boolean verificarNumeroAfiliado(String cuil, String obraSocialNombre, String nroAfiliado) {
                return true; // Para desarrollo
            }

            @Override
            public List<Paciente> obtenerTodosLosPacientes() {
                return new ArrayList<>(pacientes.values());
            }

            @Override
            public List<Paciente> buscarPacientesPorNombre(String nombre, String apellido) {
                return pacientes.values().stream()
                        .filter(p -> p.getNombre().equalsIgnoreCase(nombre) && p.getApellido().equalsIgnoreCase(apellido))
                        .collect(Collectors.toList());
            }

            @Override
            public void actualizarPaciente(Paciente paciente) {
                guardarPaciente(paciente);
            }

            @Override
            public boolean eliminarPaciente(String cuil) {
                return pacientes.remove(cuil) != null;
            }
        };
    }

    @Bean
    public RepositorioObraSocial repositorioObraSocial() {
        return new RepositorioObraSocial() {
            private Map<String, ObraSocial> obrasSociales = new HashMap<>();

            @Override
            public void guardarObraSocial(ObraSocial obraSocial) {
                obrasSociales.put(obraSocial.getNombre(), obraSocial);
            }

            @Override
            public ObraSocial buscarObraSocial(String nombre) {
                return obrasSociales.get(nombre);
            }

            @Override
            public List<ObraSocial> obtenerTodasLasObrasSociales() {
                return new ArrayList<>(obrasSociales.values());
            }

            @Override
            public boolean existeObraSocial(String nombre) {
                return obrasSociales.containsKey(nombre);
            }

            @Override
            public void actualizarObraSocial(String nombreOriginal, ObraSocial obraSocial) {

            }

            @Override
            public void actualizarObraSocial(ObraSocial obraSocial) {
                guardarObraSocial(obraSocial);
            }

            @Override
            public boolean eliminarObraSocial(String nombre) {
                return obrasSociales.remove(nombre) != null;
            }

            @Override
            public int contarPacientesAfiliados(String nombreObraSocial) {
                return 0; // Para desarrollo
            }
        };
    }
    @Bean
    public RepositorioIngresos repositorioIngresos() {
        return new RepositorioIngresos() {
            private Map<String, Ingreso> ingresosMap = new HashMap<>();
            private Map<String, List<String>> ingresosPorPaciente = new HashMap<>();
            private Map<String, List<String>> ingresosPorMedico = new HashMap<>(); // ✅ NUEVO MAPA PARA MÉDICOS

            @Override
            public List<Ingreso> obtenerIngresosPendientes() {
                return ingresosMap.values().stream()
                        .filter(ingreso -> ingreso.getEstado() == EstadoIngreso.PENDIENTE)
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<Ingreso> buscarPorId(String id) {
                return Optional.ofNullable(ingresosMap.get(id));
            }

            @Override
            public void guardarIngreso(Ingreso ingreso) {
                // Guardar en mapa principal
                String idIngreso = ingreso.getId();
                ingresosMap.put(idIngreso, ingreso);

                // Guardar relación paciente -> ingresos
                String cuilPaciente = ingreso.getPaciente().getCuil();
                ingresosPorPaciente.computeIfAbsent(cuilPaciente, k -> new ArrayList<>())
                        .add(idIngreso);

                // ✅ GUARDAR RELACIÓN MÉDICO -> INGRESOS (SOLUCIÓN AL PROBLEMA)
                String matriculaMedico = ingreso.getMedicoAsignado();
                if (matriculaMedico != null && !matriculaMedico.trim().isEmpty()) {
                    String medicoKey = matriculaMedico.trim();
                    ingresosPorMedico.computeIfAbsent(medicoKey, k -> new ArrayList<>())
                            .add(idIngreso);

                    System.out.println("🔗 RELACIÓN MÉDICO-GUARDADA - Médico: " + medicoKey +
                            ", ID Ingreso: " + idIngreso);
                }

                System.out.println("💾 INGRESO GUARDADO - ID: " + idIngreso);
                System.out.println("   CUIL Paciente: " + cuilPaciente);
                System.out.println("   Médico Asignado: " + matriculaMedico);
                System.out.println("   Estado: " + ingreso.getEstado());
                System.out.println("   Total ingresos: " + ingresosMap.size());
            }

            @Override
            public void actualizarIngreso(Ingreso ingreso) {
                String idIngreso = ingreso.getId();
                Ingreso ingresoExistente = ingresosMap.get(idIngreso);

                if (ingresoExistente != null) {
                    // ✅ ACTUALIZAR RELACIONES MÉDICO AL ACTUALIZAR
                    String medicoAnterior = ingresoExistente.getMedicoAsignado();
                    String medicoNuevo = ingreso.getMedicoAsignado();

                    // Remover del médico anterior si existe y cambió
                    if (medicoAnterior != null && !medicoAnterior.equals(medicoNuevo)) {
                        List<String> ingresosMedicoAnterior = ingresosPorMedico.get(medicoAnterior);
                        if (ingresosMedicoAnterior != null) {
                            ingresosMedicoAnterior.remove(idIngreso);
                            if (ingresosMedicoAnterior.isEmpty()) {
                                ingresosPorMedico.remove(medicoAnterior);
                            }
                            System.out.println("🔗 RELACIÓN MÉDICO-ELIMINADA - Médico anterior: " + medicoAnterior);
                        }
                    }

                    // Agregar al médico nuevo si existe
                    if (medicoNuevo != null && !medicoNuevo.trim().isEmpty()) {
                        String medicoKey = medicoNuevo.trim();
                        ingresosPorMedico.computeIfAbsent(medicoKey, k -> new ArrayList<>())
                                .add(idIngreso);
                        System.out.println("🔗 RELACIÓN MÉDICO-ACTUALIZADA - Médico nuevo: " + medicoKey);
                    }
                }

                // Guardar cambios en mapa principal
                guardarIngreso(ingreso);
            }

            @Override
            public List<Ingreso> obtenerTodos() {
                return new ArrayList<>(ingresosMap.values());
            }

            @Override
            public List<Ingreso> buscarIngresosPorPaciente(String cuilPaciente) {
                if (cuilPaciente == null || cuilPaciente.trim().isEmpty()) {
                    return List.of();
                }

                String cuil = cuilPaciente.trim();
                List<String> idsIngresos = ingresosPorPaciente.get(cuil);

                System.out.println("🔍 BUSCANDO INGRESOS PARA CUIL: " + cuil);
                System.out.println("   IDs encontrados: " + (idsIngresos != null ? idsIngresos.size() : 0));
                System.out.println("   Total ingresos en sistema: " + ingresosMap.size());

                if (idsIngresos == null || idsIngresos.isEmpty()) {
                    return List.of();
                }

                List<Ingreso> resultado = idsIngresos.stream()
                        .map(ingresosMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                System.out.println("   Ingresos encontrados: " + resultado.size());
                return resultado;
            }

            @Override
            public List<Ingreso> buscarIngresosPorMedico(String matriculaMedico) {
                if (matriculaMedico == null || matriculaMedico.trim().isEmpty()) {
                    return List.of();
                }

                String matricula = matriculaMedico.trim();
                List<String> idsIngresos = ingresosPorMedico.get(matricula);

                System.out.println("🎯 BUSCANDO INGRESOS PARA MÉDICO: " + matricula);
                System.out.println("   IDs encontrados en mapa médico: " + (idsIngresos != null ? idsIngresos.size() : 0));
                System.out.println("   Total médicos en sistema: " + ingresosPorMedico.size());

                if (idsIngresos == null || idsIngresos.isEmpty()) {
                    return List.of();
                }

                // Obtener los ingresos completos desde el mapa principal
                List<Ingreso> resultado = idsIngresos.stream()
                        .map(ingresosMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                System.out.println("   Ingresos del médico encontrados: " + resultado.size());
                resultado.forEach(ingreso ->
                        System.out.println("     - ID: " + ingreso.getId() +
                                ", Estado: " + ingreso.getEstado() +
                                ", Paciente: " + ingreso.getPaciente().getNombre())
                );

                return resultado;
            }

            @Override
            public List<Ingreso> buscarIngresosPorEstado(EstadoIngreso estado) {
                return ingresosMap.values().stream()
                        .filter(ingreso -> ingreso.getEstado() == estado)
                        .collect(Collectors.toList());
            }
        };
    }
    // Servicios - ✅ ORDEN CORREGIDO
    @Bean
    public ServicioAutenticacion servicioAutenticacion(RepositorioUsuarios repositorioUsuarios) {
        return new ServicioAutenticacion(repositorioUsuarios);
    }

    @Bean
    public ServicioRegistroPacientes servicioRegistroPacientes(RepositorioPacientes repositorioPacientes) {
        return new ServicioRegistroPacientes(repositorioPacientes);
    }

    @Bean
    public ServicioUrgencias servicioUrgencias(RepositorioPacientes repositorioPacientes,
                                               RepositorioIngresos repositorioIngresos) {
        return new ServicioUrgencias(repositorioPacientes, repositorioIngresos); // ✅ ORDEN CORRECTO
    }

    @Bean
    public ServicioReclamoPacientes servicioReclamoPacientes(RepositorioIngresos repositorioIngresos) {
        return new ServicioReclamoPacientes(repositorioIngresos);
    }

    @Bean
    public ServicioCreacionAtencion servicioCreacionAtencion(RepositorioIngresos repositorioIngresos) {
        return new ServicioCreacionAtencion(repositorioIngresos);
    }
}