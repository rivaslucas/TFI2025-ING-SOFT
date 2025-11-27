package org.example.app.repositories;

import org.example.app.interfaces.RepositorioObraSocial;
import org.example.domain.ObraSocial;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RepositorioObraSocialImpl implements RepositorioObraSocial {

    private final Map<String, ObraSocial> obrasSociales = new ConcurrentHashMap<>();

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
        // Si cambió el nombre, eliminar el antiguo y agregar el nuevo
        if (!nombreOriginal.equals(obraSocial.getNombre())) {
            obrasSociales.remove(nombreOriginal);
        }
        obrasSociales.put(obraSocial.getNombre(), obraSocial);
    }

    @Override
    public void actualizarObraSocial(ObraSocial obraSocial) {

    }

    @Override
    public boolean eliminarObraSocial(String nombre) {
        return obrasSociales.remove(nombre) != null;
    }

    @Override
    public int contarPacientesAfiliados(String nombreObraSocial) {
        // ✅ IMPLEMENTACIÓN SIMULADA - Debes conectar con tu repositorio de pacientes
        // Por ahora retornamos 0 para permitir la eliminación en pruebas
        return 0;
    }
}