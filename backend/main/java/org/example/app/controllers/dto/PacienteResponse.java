package org.example.app.controllers.dto;

public class PacienteResponse {
    private String cuil;
    private String nombre;
    private String apellido;
    private String obraSocialNombre;
    private String numeroAfiliado;
    private DireccionResponse direccion;

    // Clase interna para dirección
    public static class DireccionResponse {
        private String calle;
        private int numero;
        private String localidad;

        public DireccionResponse(String calle, int numero, String localidad) {
            this.calle = calle;
            this.numero = numero;
            this.localidad = localidad;
        }

        // Getters
        public String getCalle() { return calle; }
        public int getNumero() { return numero; }
        public String getLocalidad() { return localidad; }
    }

    // Constructores
    public PacienteResponse() {}

    public PacienteResponse(String cuil, String nombre, String apellido, String obraSocialNombre, String numeroAfiliado, DireccionResponse direccion) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.obraSocialNombre = obraSocialNombre;
        this.numeroAfiliado = numeroAfiliado;
        this.direccion = direccion;
    }

    // Getters
    public String getCuil() { return cuil; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getObraSocialNombre() { return obraSocialNombre; }
    public String getNumeroAfiliado() { return numeroAfiliado; }
    public DireccionResponse getDireccion() { return direccion; }
}