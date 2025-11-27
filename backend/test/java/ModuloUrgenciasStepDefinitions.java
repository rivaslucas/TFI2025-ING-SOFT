import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import mock.DBPruebaEnMemoria;
import org.example.app.services.ServicioUrgencias;
import org.example.domain.Enfermera;
import org.example.domain.Ingreso;
import org.example.domain.NivelEmergencia;
import org.example.domain.Paciente;
import org.example.domain.ObraSocial;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class ModuloUrgenciasStepDefinitions {

    private Enfermera enfermera;
    private DBPruebaEnMemoria dbMockeada;
    private ServicioUrgencias servicioUrgencias;
    private Exception excepcionEsperada;

    public ModuloUrgenciasStepDefinitions() {
        this.dbMockeada = new DBPruebaEnMemoria();
        // ✅ CONSTRUCTOR ACTUALIZADO: ahora necesita 2 parámetros
        this.servicioUrgencias = new ServicioUrgencias(dbMockeada, dbMockeada);
    }

    // Los demás métodos se mantienen IGUALES
    @Given("que la siguiente enfermera esta registrada:")
    public void que_la_siguiente_enfermera_esta_registrada(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> tabla = dataTable.asMaps();
        String nombre = tabla.get(0).get("Nombre");
        String apellido = tabla.get(0).get("Apellido");

        enfermera = new Enfermera(nombre, apellido);
        System.out.println("DEBUG: Enfermera registrada - " + nombre + " " + apellido);
    }

    @Given("existen pacientes pre-registrados para urgencias:")
    public void existen_pacientes_pre_registrados_para_urgencias(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> tabla = dataTable.asMaps();
        for(Map<String, String> fila: tabla) {
            String cuil = fila.get("Cuil");
            String nombre = fila.get("Nombre");
            String apellido = fila.get("Apellido");
            String obraSocialNombre = fila.get("Obra Social");

            ObraSocial obraSocial = null;
            if (obraSocialNombre != null && !obraSocialNombre.trim().isEmpty()) {
                obraSocial = dbMockeada.buscarObraSocial(obraSocialNombre);
                if (obraSocial == null) {
                    obraSocial = new ObraSocial(obraSocialNombre, obraSocialNombre + "_CODIGO");
                    dbMockeada.guardarObraSocial(obraSocial);
                }
            }

            Paciente paciente = new Paciente(cuil, nombre, apellido, obraSocial);
            dbMockeada.guardarPaciente(paciente);
            System.out.println("DEBUG: Paciente pre-registrado - CUIL: " + cuil + ", Nombre: " + nombre + " " + apellido);
        }
        System.out.println("DEBUG: Total pacientes en DB: " + dbMockeada.getPacientes().size());
    }
    @Given("que estan registrados los siguientes pacientes:")
    public void que_estan_registrados_los_siguientes_pacientes(io.cucumber.datatable.DataTable dataTable) {
        // Reutilizar la misma lógica que existe_pacientes_pre_registrados_para_urgencias
        existen_pacientes_pre_registrados_para_urgencias(dataTable);
    }
    @When("Ingresan a urgencias los siguientes pacientes:")
    public void ingresan_a_urgencias_los_siguientes_pacientes(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> tabla = dataTable.asMaps();
        excepcionEsperada = null;

        for(int i = 0; i < tabla.size(); i++) {
            Map<String, String> fila = tabla.get(i);
            String cuil = fila.get("Cuil");
            String informe = fila.get("Informe");

            System.out.println("DEBUG: Procesando ingreso " + (i+1) + " para CUIL: " + cuil);

            // Verificar si el paciente existe
            boolean pacienteExiste = dbMockeada.buscarPacientePorCuil(cuil).isPresent();
            System.out.println("DEBUG: ¿Paciente " + cuil + " existe? " + pacienteExiste);

            // Manejar nivel de emergencia
            NivelEmergencia nivelEmergencia = null;
            String nivelEmergenciaStr = fila.get("Nivel de Emergencia");
            if (nivelEmergenciaStr != null && !nivelEmergenciaStr.trim().isEmpty()) {
                nivelEmergencia = Arrays.stream(NivelEmergencia.values())
                        .filter(nivel -> nivel.tieneNombre(nivelEmergenciaStr))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Nivel desconocido"));
            }
            System.out.println("DEBUG: Nivel de emergencia: " + nivelEmergencia);

            // Manejar temperatura
            Float temperatura = null;
            String temperaturaStr = fila.get("Temperatura");
            if (temperaturaStr != null && !temperaturaStr.trim().isEmpty()) {
                try {
                    temperatura = Float.parseFloat(temperaturaStr);
                } catch (NumberFormatException e) {
                    // Si no se puede parsear, se queda como null
                }
            }

            // Manejar frecuencia cardíaca
            Float frecuenciaCardiaca = null;
            String frecuenciaCardiacaStr = fila.get("Frecuencia Cardiaca");
            if (frecuenciaCardiacaStr != null && !frecuenciaCardiacaStr.trim().isEmpty()) {
                try {
                    frecuenciaCardiaca = Float.parseFloat(frecuenciaCardiacaStr);
                } catch (NumberFormatException e) {
                    // Si no se puede parsear, se queda como null
                }
            }

            // Manejar frecuencia respiratoria
            Float frecuenciaRespiratoria = null;
            String frecuenciaRespiratoriaStr = fila.get("Frecuencia Respiratoria");
            if (frecuenciaRespiratoriaStr != null && !frecuenciaRespiratoriaStr.trim().isEmpty()) {
                try {
                    frecuenciaRespiratoria = Float.parseFloat(frecuenciaRespiratoriaStr);
                } catch (NumberFormatException e) {
                    // Si no se puede parsear, se queda como null
                }
            }

            // Manejar tensión arterial
            Float tensionSistolica = null;
            Float tensionDiastolica = null;
            String tensionArterialStr = fila.get("Tension Arterial");
            if (tensionArterialStr != null && !tensionArterialStr.trim().isEmpty()) {
                try {
                    List<Float> tensionArterial = Arrays.stream(tensionArterialStr.split("/"))
                            .map(Float::parseFloat)
                            .toList();
                    tensionSistolica = tensionArterial.get(0);
                    tensionDiastolica = tensionArterial.get(1);
                } catch (NumberFormatException e) {
                    // Si no se puede parsear, se queda como null
                }
            }

            try {
                System.out.println("DEBUG: Llamando a servicioUrgencias.registrarUrgencia...");

                // ✅ CORREGIDO: Crear una pausa significativa para asegurar diferentes timestamps
                if (i > 0) {
                    try {
                        Thread.sleep(100); // 100ms de diferencia para asegurar fechas diferentes
                        System.out.println("DEBUG: Pausa de 100ms para diferenciar timestamps");
                    } catch (InterruptedException e) {
                        // Ignorar
                    }
                }

                servicioUrgencias.registrarUrgencia(
                        cuil,
                        enfermera,
                        informe,
                        nivelEmergencia,
                        temperatura,
                        frecuenciaCardiaca,
                        frecuenciaRespiratoria,
                        tensionSistolica,
                        tensionDiastolica
                );
                System.out.println("DEBUG: Ingreso registrado exitosamente para CUIL: " + cuil);

            } catch (Exception e) {
                System.out.println("DEBUG: Error al registrar ingreso: " + e.getMessage());
                this.excepcionEsperada = e;
                break;
            }
        }
    }
    @Then("La lista de espera esta ordenada por prioridad de la siguiente manera:")
    public void la_lista_de_espera_esta_ordenada_por_prioridad_de_la_siguiente_manera(io.cucumber.datatable.DataTable dataTable) {
        List<String> listaEsperada = dataTable.asList();

        // Obtener ingresos pendientes (ya ordenados por el servicio)
        List<Ingreso> ingresosPendientes = servicioUrgencias.obtenerIngresosPendientes();
        List<String> cuilsActuales = ingresosPendientes.stream()
                .map(ingreso -> ingreso.getPaciente().getCuil())
                .toList();

        System.out.println("DEBUG: Lista esperada por prioridad: " + listaEsperada);
        System.out.println("DEBUG: Lista actual de ingresos pendientes: " + cuilsActuales);
        System.out.println("DEBUG: Total ingresos pendientes: " + ingresosPendientes.size());

        // Mostrar detalles de cada ingreso para debugging
        System.out.println("DEBUG: Detalles de ingresos pendientes:");
        for (int i = 0; i < ingresosPendientes.size(); i++) {
            Ingreso ingreso = ingresosPendientes.get(i);
            System.out.println("DEBUG: [" + i + "] CUIL: " + ingreso.getPaciente().getCuil() +
                    ", Nivel: " + ingreso.getNivelEmergencia() +
                    ", Prioridad: " + ingreso.getNivelEmergencia().getPrioridad() +
                    ", Fecha: " + ingreso.getFechaIngreso());
        }

        assertThat(cuilsActuales)
                .hasSize(listaEsperada.size())
                .containsExactlyElementsOf(listaEsperada);
    }
    @Then("el sistema muestra el siguiente error: {string}")
    public void el_sistema_muestra_el_siguiente_error(String mensajeError) {
        assertThat(excepcionEsperada)
                .isNotNull()
                .hasMessage(mensajeError);
    }
}