package org.example.app.interfaces;



import org.example.domain.ObraSocial;

public interface RepositorioObraSocial {
    public void guardarObraSocial(ObraSocial obraSocial);
    public ObraSocial buscarObraSocial(String nombre);
}
