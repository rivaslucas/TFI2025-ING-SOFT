package org.example.domain;

public class Medico extends Usuario {
    private String matricula;
    private String especialidad;

    public Medico(String email, String contrasena, String matricula, String especialidad) {
        super(email, contrasena, Autoridad.MEDICO);
        this.matricula = matricula;
        this.especialidad = especialidad;
    }

    // Constructor para cuando ya existe el hash
    public Medico(String email, String contrasenaHash, String matricula, String especialidad, boolean esHash) {
        super(email, contrasenaHash, Autoridad.MEDICO, esHash);
        this.matricula = matricula;
        this.especialidad = especialidad;
    }

    public String getMatricula() { return matricula; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}