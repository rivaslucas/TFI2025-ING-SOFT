package org.example.domain.valueobject;

public class FrecuenciaCardiaca extends Frecuencia {

    public FrecuenciaCardiaca(Float value) {
        super(value);
    }

    @Override
    protected RuntimeException notificarError() {
        return new RuntimeException("La frecuencia cardíaca no puede ser negativa");
    }

    @Override
    public String getValorFormateado() {
        return "";
    }

    // ✅ AGREGAR GETTER PARA EL VALOR
    public Float getValor() {
        return value; // Accede al campo 'value' de la clase padre
    }
}