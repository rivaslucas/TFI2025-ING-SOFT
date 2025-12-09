// Frecuencia.java - Hacer el campo accesible
package org.example.domain.valueobject;

public abstract class Frecuencia {
    protected Float value; // ✅ Ya está como protected, lo cual es bueno

    public Frecuencia(Float value) {
        validarFrecuenciaNoNegativa(value);
        this.value = value;
    }

    private void validarFrecuenciaNoNegativa(Float value) {
        if (value < 0) {
            throw this.notificarError();
        }
    }

    protected abstract RuntimeException notificarError();
    public abstract String getValorFormateado();

    // ✅ OPCIONAL: Agregar un getter en la clase base también
    public Float getValor() {
        return value;
    }
}