package org.example.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AutoridadTest {

    @Test
    public void valoresAutoridadExisten() {
        // Verificacion de que los valores del enum existen
        assertNotNull(Autoridad.valueOf("MEDICO"));
        assertNotNull(Autoridad.valueOf("ENFERMERO"));
    }

    @Test
    public void soloDosRolesDisponibles() {
        // Verificacion de que solo existen dos roles
        Autoridad[] autoridades = Autoridad.values();
        assertEquals(2, autoridades.length);

        // Verificar que son los roles correctos
        assertEquals(Autoridad.MEDICO, autoridades[0]);
        assertEquals(Autoridad.ENFERMERO, autoridades[1]);
    }

    @Test
    public void conversionStringAAutoridad() {
        // Verificacion de conversión desde string
        assertEquals(Autoridad.MEDICO, Autoridad.valueOf("MEDICO"));
        assertEquals(Autoridad.ENFERMERO, Autoridad.valueOf("ENFERMERO"));
    }

    @Test
    public void conversionCaseInsensitive() {
        // La conversión debería funcionar sin importar el case
        assertEquals(Autoridad.MEDICO, Autoridad.valueOf("medico".toUpperCase()));
        assertEquals(Autoridad.ENFERMERO, Autoridad.valueOf("Enfermero".toUpperCase()));
    }
}