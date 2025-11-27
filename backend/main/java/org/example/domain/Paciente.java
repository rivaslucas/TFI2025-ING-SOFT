package org.example.domain;

public class Paciente {
    private String cuil;
    private String nombre;
    private String apellido;
    private Afiliado afiliado;
    private Domicilio direccion;

    public Paciente(String cuil, String nombre, String apellido, ObraSocial obraSocial) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.afiliado = new Afiliado();
        this.afiliado.setObraSocial(obraSocial);
        validarCampos();
    }

    public Paciente(String cuil, String nombre, String apellido, Afiliado afiliado, Domicilio direccion) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.afiliado = afiliado;
        this.direccion = direccion;
        validarCampos();
    }

    public String getCuil() {
        return cuil;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Afiliado getAfiliado() {
        return afiliado;
    }

    public Domicilio getDireccion() {
        return direccion;
    }
    public void validarCampos(){
        if (this.cuil == null || cuil.trim().isEmpty()){
            throw new RuntimeException("Cuil es un campo obligatorio");
        }

        if (nombre == null || nombre.trim().isEmpty()){
            throw new RuntimeException("Nombre es un campo obligatorio");
        }

        if (apellido == null || apellido.trim().isEmpty()){
            throw new RuntimeException("Apellido es un campo obligatorio");
        }

        if (direccion == null){
            return;
        }

        var calle = this.direccion.getCalle();
        if (calle == null || calle.trim().isEmpty()){
            throw new RuntimeException("Calle es un campo obligatorio");
        }

        var numero = this.direccion.getNumero();
        if ( numero == 0 ) {
            throw new RuntimeException("Numero es un campo obligatorio");
        }

        var localidad = this.direccion.getLocalidad();
        if (localidad == null || localidad.trim().isEmpty()){
            throw new RuntimeException("Localidad es un campo obligatorio");
        }
    }
    public ObraSocial getObraSocial() {
        return afiliado != null ? afiliado.getObraSocial() : null;
    }

    public String getObraSocialNombre(){
        return afiliado != null && afiliado.getObraSocial() != null ?
                afiliado.getObraSocial().getNombre() : null;
    }
}