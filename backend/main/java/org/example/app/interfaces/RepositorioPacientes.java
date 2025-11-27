package org.example.app.interfaces;

import org.example.domain.Paciente;

import java.util.List;
import java.util.Optional;

public interface RepositorioPacientes {
    void guardarPaciente(Paciente paciente);

    Optional<Paciente> buscarPacientePorCuil(String cuil);

    boolean existeObraSocial(String obraSocialNombre);

    boolean estaAfiliado(String cuil, String obraSocialNombre);

    boolean pacienteExiste(String cuil);

    boolean verificarNumeroAfiliado(String cuil, String obraSocialNombre, String nroAfiliado);

    // ✅ FUNCIONES NUEVAS
    List<Paciente> obtenerTodosLosPacientes();

    List<Paciente> buscarPacientesPorNombre(String nombre, String apellido);

    void actualizarPaciente(Paciente paciente);

    boolean eliminarPaciente(String cuil);

    default boolean existePaciente(String cuil) {
        return buscarPacientePorCuil(cuil).isPresent();
    }
}