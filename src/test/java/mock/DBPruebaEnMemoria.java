package mock;

import org.example.app.interfaces.RepositorioPacientes;
import org.example.app.interfaces.RepositorioObraSocial;
import org.example.app.interfaces.RepositorioUsuarios;
import org.example.domain.Paciente;
import org.example.domain.ObraSocial;
import org.example.domain.Usuario;
import org.example.domain.Afiliado;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DBPruebaEnMemoria implements RepositorioPacientes, RepositorioObraSocial, RepositorioUsuarios {
    private Map<String, Paciente> pacientes;
    private Map<String, ObraSocial> obrasSociales;
    private Map<String, Usuario> usuarios;
    private Usuario usuarioActual;

    public DBPruebaEnMemoria() {
        this.pacientes = new HashMap<>();
        this.obrasSociales = new HashMap<>();
        this.usuarios = new HashMap<>();
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

    // Resto del código sin cambios...
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

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}