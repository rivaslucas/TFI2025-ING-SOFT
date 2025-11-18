package org.example.app.interfaces;

import org.example.domain.Ingreso;

import java.util.List;
import java.util.Optional;

public interface RepositorioIngresos {
    List<Ingreso> obtenerIngresosPendientes();
    Optional<Ingreso> buscarPorId(String id);
    void guardarIngreso(Ingreso ingreso);
    void actualizarIngreso(Ingreso ingreso);
    List<Ingreso> obtenerTodos();
}