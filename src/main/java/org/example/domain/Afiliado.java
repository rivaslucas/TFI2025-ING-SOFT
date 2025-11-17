package org.example.domain;

public class Afiliado {
    private String numAfiliado;
    private ObraSocial obraSocial;

    public Afiliado( String numAfiliado, ObraSocial obraSocial) {
        this.numAfiliado = numAfiliado;
        this.obraSocial = obraSocial;
    }

    public Afiliado() {}

    public String getNumAfiliado() { return numAfiliado; }

    public void setNumAfiliado(String numAfiliado) {
        this.numAfiliado = numAfiliado;
    }

    public void setObraSocial(ObraSocial obraSocial) {
        this.obraSocial = obraSocial;
    }

    public ObraSocial getObraSocial() { return obraSocial; }
}