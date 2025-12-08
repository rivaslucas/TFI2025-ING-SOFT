// FrecuenciaSistolica.java
package org.example.domain.valueobject;

public class FrecuenciaSistolica extends Frecuencia {

    public FrecuenciaSistolica(Float value) {
        super(value);
    }

    @Override
    protected RuntimeException notificarError() {
        return new RuntimeException("La frecuencia sistolica no puede ser negativa");
    }

    @Override
    public String getValorFormateado() {
        return "";
    }

    // ✅ AGREGAR GETTER PARA EL VALOR
    public Float getValor() {
        return value;
    }
}
