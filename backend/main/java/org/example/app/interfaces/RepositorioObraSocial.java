package org.example.app.interfaces;

import org.example.domain.ObraSocial;

import java.util.List;

public interface RepositorioObraSocial {
    void guardarObraSocial(ObraSocial obraSocial);

    ObraSocial buscarObraSocial(String nombre);

    List<ObraSocial> obtenerTodasLasObrasSociales();

    boolean existeObraSocial(String nombre);

    // ✅ ACTUALIZADO: Agregar parámetro nombreOriginal
    void actualizarObraSocial(String nombreOriginal, ObraSocial obraSocial);

    void actualizarObraSocial(ObraSocial obraSocial);

    boolean eliminarObraSocial(String nombre);

    int contarPacientesAfiliados(String nombreObraSocial);
}