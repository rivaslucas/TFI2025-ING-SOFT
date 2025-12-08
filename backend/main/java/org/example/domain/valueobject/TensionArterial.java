package org.example.domain.valueobject;

public class TensionArterial {
    private FrecuenciaDiastolica frecuenciaDiastolica;
    private FrecuenciaSistolica frecuenciaSistolica;

    public TensionArterial(Float frecuenciaSistolica, Float frecuenciaDiastolica) {
        this.frecuenciaSistolica = new FrecuenciaSistolica(frecuenciaSistolica);
        this.frecuenciaDiastolica = new FrecuenciaDiastolica(frecuenciaDiastolica);
    }

    // ✅ AGREGAR GETTERS
    public Float getSistolica() {
        return frecuenciaSistolica != null ? frecuenciaSistolica.getValor() : null;
    }

    public Float getDiastolica() {
        return frecuenciaDiastolica != null ? frecuenciaDiastolica.getValor() : null;
    }

    // Getters para los objetos completos (opcional)
    public FrecuenciaSistolica getFrecuenciaSistolicaObject() {
        return frecuenciaSistolica;
    }

    public FrecuenciaDiastolica getFrecuenciaDiastolicaObject() {
        return frecuenciaDiastolica;
    }
}