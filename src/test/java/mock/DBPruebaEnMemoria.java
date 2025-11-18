package mock;

import org.example.app.interfaces.RepositorioPacientes;
import org.example.app.interfaces.RepositorioObraSocial;
import org.example.app.interfaces.RepositorioUsuarios;
import org.example.app.interfaces.RepositorioIngresos;
import org.example.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class DBPruebaEnMemoria implements RepositorioPacientes, RepositorioObraSocial, RepositorioUsuarios, RepositorioIngresos {
    private Map<String, Paciente> pacientes;
    private Map<String, ObraSocial> obrasSociales;
    private Map<String, Usuario> usuarios;
    private Map<String, Ingreso> ingresos;
    private Map<String, Atencion> atenciones;
    private Usuario usuarioActual;

    public DBPruebaEnMemoria() {
        this.pacientes = new HashMap<>();
        this.obrasSociales = new HashMap<>();
        this.usuarios = new HashMap<>();
        this.ingresos = new HashMap<>();
        this.atenciones = new HashMap<>();
        this.usuarioActual = null;
    }

    // Implementación de RepositorioPacientes
    @Override
    public void guardarPaciente(Paciente paciente) {
        this.pacientes.put(paciente.getCuil(), paciente);
    }

    @Override
    public Optional<Paciente> buscarPacientePorCuil(String cuil) {
        return Optional.ofNullable(pacientes.get(cuil));
    }

    @Override
    public boolean existeObraSocial(String obraSocialNombre) {
        return obrasSociales.containsKey(obraSocialNombre);
    }

    @Override
    public boolean estaAfiliado(String cuil, String obraSocialNombre) {
        Optional<Paciente> paciente = buscarPacientePorCuil(cuil);
        if (paciente.isPresent() && paciente.get().getAfiliado() != null) {
            ObraSocial obraSocial = paciente.get().getAfiliado().getObraSocial();
            return obraSocial != null && obraSocial.getNombre().equals(obraSocialNombre);
        }
        return false;
    }

    @Override
    public boolean verificarNumeroAfiliado(String cuil, String obraSocialNombre, String nroAfiliado) {
        Optional<Paciente> paciente = buscarPacientePorCuil(cuil);
        if (paciente.isPresent() && paciente.get().getAfiliado() != null) {
            Afiliado afiliado = paciente.get().getAfiliado();
            ObraSocial obraSocial = afiliado.getObraSocial();
            return obraSocial != null &&
                    obraSocial.getNombre().equals(obraSocialNombre) &&
                    afiliado.getNumAfiliado() != null &&
                    afiliado.getNumAfiliado().equals(nroAfiliado);
        }
        return false;
    }

    // Implementación de RepositorioObraSocial
    @Override
    public void guardarObraSocial(ObraSocial obraSocial) {
        System.out.println("DEBUG: Guardando obra social - Nombre: " + obraSocial.getNombre() +
                ", ID: " + obraSocial.getIdentificador());
        this.obrasSociales.put(obraSocial.getNombre(), obraSocial);
        System.out.println("DEBUG: Obras sociales después de guardar: " + obrasSociales.keySet());
    }

    @Override
    public ObraSocial buscarObraSocial(String nombre) {
        System.out.println("DEBUG: Buscando obra social: '" + nombre + "'");
        System.out.println("DEBUG: Obras sociales disponibles: " + obrasSociales.keySet());
        ObraSocial encontrada = obrasSociales.get(nombre);
        System.out.println("DEBUG: Resultado búsqueda: " + (encontrada != null ? "ENCONTRADA" : "NO ENCONTRADA"));
        return encontrada;
    }

    // Implementación de RepositorioUsuarios
    @Override
    public void guardarUsuario(Usuario usuario) {
        this.usuarios.put(usuario.getEmail(), usuario);
    }

    @Override
    public Optional<Usuario> buscarUsuario(String email) {
        return Optional.ofNullable(usuarios.get(email));
    }

    @Override
    public Optional<Usuario> getUsuarioActual(String email, String contrasena) {
        Usuario usuario = usuarios.get(email);
        if (usuario != null && usuario.verificarContrasena(contrasena)) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    @Override
    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    // Implementación de RepositorioIngresos
    @Override
    public List<Ingreso> obtenerIngresosPendientes() {
        return ingresos.values().stream()
                .filter(ingreso -> ingreso.getEstado() == EstadoIngreso.PENDIENTE)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Ingreso> buscarPorId(String id) {
        return Optional.ofNullable(ingresos.get(id));
    }

    @Override
    public void guardarIngreso(Ingreso ingreso) {
        ingresos.put(ingreso.getId(), ingreso);
    }

    @Override
    public void actualizarIngreso(Ingreso ingreso) {
        ingresos.put(ingreso.getId(), ingreso);
    }

    @Override
    public List<Ingreso> obtenerTodos() {
        return new ArrayList<>(ingresos.values());
    }

    // Métodos para gestionar Atenciones
    public void guardarAtencion(Atencion atencion) {
        atenciones.put(atencion.getId(), atencion);
        System.out.println("DEBUG: Atención guardada - ID: " + atencion.getId());
    }

    public Optional<Atencion> buscarAtencionPorIngreso(String idIngreso) {
        return atenciones.values().stream()
                .filter(atencion -> atencion.getIngreso().getId().equals(idIngreso))
                .findFirst();
    }

    public List<Atencion> obtenerTodasAtenciones() {
        return new ArrayList<>(atenciones.values());
    }

    // Métodos auxiliares para pruebas
    public Map<String, Paciente> getPacientes() {
        return pacientes;
    }

    public Map<String, ObraSocial> getObrasSociales() {
        return obrasSociales;
    }

    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }

    public Map<String, Ingreso> getIngresos() {
        return ingresos;
    }

    public Map<String, Atencion> getAtenciones() {
        return atenciones;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // Método para establecer usuario actual (compatibilidad con step definitions)
    public void setUsuarioActual(Object usuario) {
        if (usuario instanceof Usuario) {
            this.usuarioActual = (Usuario) usuario;
        } else {
            this.usuarioActual = null;
        }
    }

    public void limpiarIngresos() {
        ingresos.clear();
    }

    public void limpiarAtenciones() {
        atenciones.clear();
    }

    public void limpiarTodo() {
        pacientes.clear();
        obrasSociales.clear();
        usuarios.clear();
        ingresos.clear();
        atenciones.clear();
        usuarioActual = null;
    }
}