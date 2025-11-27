package org.example.app.controllers.dto;

public class PacienteRequest {
    private String cuil;
    private String nombre;
    private String apellido;
    private String obraSocialNombre;
    private String numeroAfiliado;
    private DireccionRequest direccion;

    // Clase interna para dirección
    public static class DireccionRequest {
        private String calle;
        private int numero;
        private String localidad;

        // Constructores
        public DireccionRequest() {}

        public DireccionRequest(String calle, int numero, String localidad) {
            this.calle = calle;
            this.numero = numero;
            this.localidad = localidad;
        }

        // Getters y setters
        public String getCalle() { return calle; }
        public void setCalle(String calle) { this.calle = calle; }
        public int getNumero() { return numero; }
        public void setNumero(int numero) { this.numero = numero; }
        public String getLocalidad() { return localidad; }
        public void setLocalidad(String localidad) { this.localidad = localidad; }
    }

    // Constructores
    public PacienteRequest() {}

    public PacienteRequest(String cuil, String nombre, String apellido, String obraSocialNombre, String numeroAfiliado, DireccionRequest direccion) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.obraSocialNombre = obraSocialNombre;
        this.numeroAfiliado = numeroAfiliado;
        this.direccion = direccion;
    }

    // Getters y setters
    public String getCuil() { return cuil; }
    public void setCuil(String cuil) { this.cuil = cuil; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getObraSocialNombre() { return obraSocialNombre; }
    public void setObraSocialNombre(String obraSocialNombre) { this.obraSocialNombre = obraSocialNombre; }
    public String getNumeroAfiliado() { return numeroAfiliado; }
    public void setNumeroAfiliado(String numeroAfiliado) { this.numeroAfiliado = numeroAfiliado; }
    public DireccionRequest getDireccion() { return direccion; }
    public void setDireccion(DireccionRequest direccion) { this.direccion = direccion; }
}