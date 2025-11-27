package org.example.app.repositories;

import org.example.app.interfaces.RepositorioIngresos;
import org.example.domain.Ingreso;
import org.example.domain.EstadoIngreso;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class RepositorioIngresosImpl implements RepositorioIngresos {

    // ✅ VERIFICACIÓN EXTREMA - BLOQUE DE INICIALIZACIÓN
    {
        System.out.println("🎯🎯🎯 BLOQUE DE INICIALIZACIÓN RepositorioIngresosImpl EJECUTADO 🎯🎯🎯");
    }

    // ✅ VERIFICACIÓN EXTREMA - CONSTRUCTOR
    public RepositorioIngresosImpl() {
        System.out.println("🎯🎯🎯 CONSTRUCTOR RepositorioIngresosImpl EJECUTADO 🎯🎯🎯");
        System.out.println("🎯🎯🎯 MÉTODO buscarIngresosPorMedico DEBERÍA MOSTRAR DEBUG MEJORADO 🎯🎯🎯");
    }

    private final Map<String, Ingreso> almacenamiento = new ConcurrentHashMap<>();

    @Override
    public List<Ingreso> obtenerIngresosPendientes() {
        System.out.println("🔍 MÉTODO obtenerIngresosPendientes EJECUTADO");
        return almacenamiento.values().stream()
                .filter(ingreso -> ingreso.getEstado() == EstadoIngreso.PENDIENTE)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Ingreso> buscarPorId(String id) {
        System.out.println("🔍 MÉTODO buscarPorId EJECUTADO - ID: " + id);
        return Optional.ofNullable(almacenamiento.get(id));
    }

    @Override
    public void guardarIngreso(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }
        almacenamiento.put(ingreso.getId(), ingreso);
        System.out.println("✅ INGRESO GUARDADO: " + ingreso.getId() + " para CUIL: " + ingreso.getPaciente().getCuil());
        System.out.println("📊 Total de ingresos en sistema: " + almacenamiento.size());
    }

    @Override
    public void actualizarIngreso(Ingreso ingreso) {
        System.out.println("🔄 MÉTODO actualizarIngreso EJECUTADO - ID: " + ingreso.getId());
        guardarIngreso(ingreso);
    }

    @Override
    public List<Ingreso> obtenerTodos() {
        System.out.println("🔍 MÉTODO obtenerTodos EJECUTADO");
        return new ArrayList<>(almacenamiento.values());
    }

    @Override
    public List<Ingreso> buscarIngresosPorPaciente(String cuilPaciente) {
        System.out.println("🔍 MÉTODO buscarIngresosPorPaciente EJECUTADO - CUIL: " + cuilPaciente);

        if (cuilPaciente == null) return List.of();

        List<Ingreso> resultado = almacenamiento.values().stream()
                .filter(ingreso -> ingreso.getPaciente().getCuil().equals(cuilPaciente.trim()))
                .collect(Collectors.toList());

        System.out.println("🔍 Búsqueda ingresos para CUIL: " + cuilPaciente + " - Encontrados: " + resultado.size());
        return resultado;
    }

    @Override
    public List<Ingreso> buscarIngresosPorMedico(String matriculaMedico) {
        System.out.println("🚨🚨🚨🚨🚨 MÉTODO buscarIngresosPorMedico EJECUTÁNDOSE 🚨🚨🚨🚨🚨");

        if (matriculaMedico == null || matriculaMedico.trim().isEmpty()) {
            System.out.println("   ❌ Matrícula nula o vacía");
            return List.of();
        }

        String matriculaBuscada = matriculaMedico.trim();
        System.out.println("   🔍 Buscando médico: '" + matriculaBuscada + "'");
        System.out.println("   📊 Total de ingresos en sistema: " + almacenamiento.size());

        // VERIFICACIÓN MANUAL de todos los ingresos
        System.out.println("   === VERIFICACIÓN MANUAL DE TODOS LOS INGRESOS ===");
        List<Ingreso> resultadoManual = new ArrayList<>();

        for (Ingreso ingreso : almacenamiento.values()) {
            if (ingreso != null) {
                String medicoAsignado = ingreso.getMedicoAsignado();
                boolean coincide = medicoAsignado != null && medicoAsignado.equals(matriculaBuscada);

                System.out.println("   - ID: " + ingreso.getId() +
                        ", Estado: " + ingreso.getEstado() +
                        ", Médico: '" + medicoAsignado + "'" +
                        ", ¿Coincide? " + coincide);

                if (coincide) {
                    System.out.println("   ✅✅✅ COINCIDENCIA ENCONTRADA ✅✅✅");
                    resultadoManual.add(ingreso);
                }
            }
        }

        System.out.println("   ============================================");
        System.out.println("   📊 Resultado manual: " + resultadoManual.size() + " ingresos encontrados");

        return resultadoManual;
    }

    @Override
    public List<Ingreso> buscarIngresosPorEstado(EstadoIngreso estado) {
        System.out.println("🔍 MÉTODO buscarIngresosPorEstado EJECUTADO - Estado: " + estado);
        return almacenamiento.values().stream()
                .filter(ingreso -> ingreso.getEstado() == estado)
                .collect(Collectors.toList());
    }
}