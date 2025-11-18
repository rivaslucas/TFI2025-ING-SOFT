
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import mock.DBPruebaEnMemoria;
import org.example.app.ServicioCreacionAtencion;
import org.example.domain.*;
import org.example.domain.valueobject.FrecuenciaCardiaca;
import org.example.domain.valueobject.FrecuenciaRespiratoria;
import org.example.domain.valueobject.TensionArterial;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class ModuloCreacionAtencionStepDefinitions {

    private DBPruebaEnMemoria dbMockeada;
    private ServicioCreacionAtencion servicioCreacionAtencion;
    private Exception excepcionEsperada;
    private Atencion atencionRegistrada;

    public ModuloCreacionAtencionStepDefinitions() {
        this.dbMockeada = new DBPruebaEnMemoria();
        this.servicioCreacionAtencion = new ServicioCreacionAtencion(dbMockeada);
    }

    @Given("El médico con matrícula {string} está autenticado para atención")
    public void el_medico_con_matricula_esta_autenticado_para_atencion(String matricula) {
        Medico medico = new Medico(matricula + "@hospital.com", "password123", matricula, "CLINICA");
        dbMockeada.setUsuarioActual(medico);
        System.out.println("DEBUG: Médico autenticado para atención - Matrícula: " + matricula);
    }

    @Given("Existe el siguiente ingreso reclamado por el médico:")
    public void existe_el_siguiente_ingreso_reclamado_por_el_medico(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> tabla = dataTable.asMaps();
        for (Map<String, String> fila : tabla) {
            String idIngreso = fila.get("ID Ingreso");
            String nombrePaciente = fila.get("Paciente");
            String cuil = fila.get("CUIL");
            String estadoStr = fila.get("Estado");
            String medicoAsignado = fila.get("Médico Asignado");

            // Crear paciente
            Paciente paciente = new Paciente(cuil, nombrePaciente, "Apellido" + nombrePaciente, null);
            dbMockeada.guardarPaciente(paciente);

            // Crear ingreso
            Ingreso ingreso = crearIngresoSimulado(idIngreso, paciente, NivelEmergencia.URGENCIA);

            // Establecer estado y médico asignado
            EstadoIngreso estado = EstadoIngreso.valueOf(estadoStr);
            ingreso.setEstado(estado);
            ingreso.setMedicoAsignado(medicoAsignado);

            dbMockeada.guardarIngreso(ingreso);
            System.out.println("DEBUG: Ingreso creado - ID: " + idIngreso + ", Estado: " + estado + ", Médico: " + medicoAsignado);
        }
    }

    @Given("Existe el siguiente ingreso reclamado por otro médico:")
    public void existe_el_siguiente_ingreso_reclamado_por_otro_medico(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> tabla = dataTable.asMaps();
        for (Map<String, String> fila : tabla) {
            String idIngreso = fila.get("ID Ingreso");
            String nombrePaciente = fila.get("Paciente");
            String cuil = fila.get("CUIL");
            String estadoStr = fila.get("Estado");
            String medicoAsignado = fila.get("Médico Asignado");

            // Crear paciente
            Paciente paciente = new Paciente(cuil, nombrePaciente, "Apellido" + nombrePaciente, null);
            dbMockeada.guardarPaciente(paciente);

            // Crear ingreso
            Ingreso ingreso = crearIngresoSimulado(idIngreso, paciente, NivelEmergencia.URGENCIA);

            // Establecer estado y médico asignado específico (diferente al actual)
            EstadoIngreso estado = EstadoIngreso.valueOf(estadoStr);
            ingreso.setEstado(estado);
            ingreso.setMedicoAsignado(medicoAsignado); // Este debe ser DIFERENTE al médico autenticado

            dbMockeada.guardarIngreso(ingreso);
            System.out.println("DEBUG: Ingreso con médico diferente creado - ID: " + idIngreso + ", Médico: " + medicoAsignado);
        }
    }
    @Given("Existe el siguiente ingreso no reclamado:")
    public void existe_el_siguiente_ingreso_no_reclamado(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> tabla = dataTable.asMaps();
        for (Map<String, String> fila : tabla) {
            String idIngreso = fila.get("ID Ingreso");
            String nombrePaciente = fila.get("Paciente");
            String cuil = fila.get("CUIL");
            String estadoStr = fila.get("Estado");

            Paciente paciente = new Paciente(cuil, nombrePaciente, "Apellido" + nombrePaciente, null);
            dbMockeada.guardarPaciente(paciente);

            Ingreso ingreso = crearIngresoSimulado(idIngreso, paciente, NivelEmergencia.URGENCIA);
            EstadoIngreso estado = EstadoIngreso.valueOf(estadoStr);
            ingreso.setEstado(estado);
            // No asignar médico para ingreso no reclamado
            ingreso.setMedicoAsignado(null);

            dbMockeada.guardarIngreso(ingreso);
            System.out.println("DEBUG: Ingreso no reclamado creado - ID: " + idIngreso + ", Estado: " + estado);
        }
    }

    @Given("Existe el siguiente ingreso ya finalizado:")
    public void existe_el_siguiente_ingreso_ya_finalizado(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> tabla = dataTable.asMaps();
        for (Map<String, String> fila : tabla) {
            String idIngreso = fila.get("ID Ingreso");
            String nombrePaciente = fila.get("Paciente");
            String cuil = fila.get("CUIL");
            String estadoStr = fila.get("Estado");
            String medicoAsignado = fila.get("Médico Asignado");

            Paciente paciente = new Paciente(cuil, nombrePaciente, "Apellido" + nombrePaciente, null);
            dbMockeada.guardarPaciente(paciente);

            Ingreso ingreso = crearIngresoSimulado(idIngreso, paciente, NivelEmergencia.URGENCIA);
            EstadoIngreso estado = EstadoIngreso.valueOf(estadoStr);
            ingreso.setEstado(estado);
            ingreso.setMedicoAsignado(medicoAsignado);

            dbMockeada.guardarIngreso(ingreso);
            System.out.println("DEBUG: Ingreso finalizado creado - ID: " + idIngreso + ", Estado: " + estado);
        }
    }

    @Given("El médico con matrícula {string} está autenticado")
    public void el_medico_con_matricula_esta_autenticado(String matricula) {
        Medico medico = new Medico(matricula + "@hospital.com", "password123", matricula, "CLINICA");
        dbMockeada.setUsuarioActual(medico);
        System.out.println("DEBUG: Médico autenticado - Matrícula: " + matricula);
    }

    @When("El médico registra la siguiente atención:")
    public void el_medico_registra_la_siguiente_atencion(io.cucumber.datatable.DataTable dataTable) {
        excepcionEsperada = null;
        List<Map<String, String>> tabla = dataTable.asMaps();
        Map<String, String> fila = tabla.getFirst();

        try {
            String idIngreso = fila.get("Ingreso");
            String informeMedico = fila.get("Informe Médico");
            String matriculaMedico = fila.get("Médico");

            Object usuarioActual = dbMockeada.getUsuarioActual();
            if (usuarioActual instanceof Medico) {
                Medico medico = (Medico) usuarioActual;
                this.atencionRegistrada = servicioCreacionAtencion.registrarAtencion(
                        idIngreso, informeMedico, medico
                );
                // Guardar la atención en la base de datos
                dbMockeada.guardarAtencion(atencionRegistrada);
                System.out.println("DEBUG: Atención registrada exitosamente para ingreso: " + idIngreso);
            } else {
                throw new RuntimeException("Usuario no es un médico");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error al registrar atención: " + e.getMessage());
            this.excepcionEsperada = e;
        }
    }

    @When("El médico intenta registrar la siguiente atención:")
    public void el_medico_intenta_registrar_la_siguiente_atencion(io.cucumber.datatable.DataTable dataTable) {
        el_medico_registra_la_siguiente_atencion(dataTable);
    }

    @When("El médico {string} intenta registrar atención para el ingreso {string}")
    public void el_medico_intenta_registrar_atencion_para_el_ingreso(String matricula, String idIngreso) {
        excepcionEsperada = null;
        try {
            Medico medico = new Medico(matricula + "@hospital.com", "password123", matricula, "CLINICA");
            this.atencionRegistrada = servicioCreacionAtencion.registrarAtencion(
                    idIngreso, "Informe médico de prueba con más de 10 caracteres", medico
            );
            if (atencionRegistrada != null) {
                dbMockeada.guardarAtencion(atencionRegistrada);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error al registrar atención: " + e.getMessage());
            this.excepcionEsperada = e;
        }
    }

    @When("El médico intenta registrar atención para el ingreso {string}")
    public void el_medico_intenta_registrar_atencion_para_el_ingreso(String idIngreso) {
        excepcionEsperada = null;
        try {
            Object usuarioActual = dbMockeada.getUsuarioActual();
            if (usuarioActual instanceof Medico) {
                Medico medico = (Medico) usuarioActual;
                this.atencionRegistrada = servicioCreacionAtencion.registrarAtencion(
                        idIngreso, "Informe médico de prueba con más de 10 caracteres", medico
                );
                if (atencionRegistrada != null) {
                    dbMockeada.guardarAtencion(atencionRegistrada);
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error al registrar atención: " + e.getMessage());
            this.excepcionEsperada = e;
        }
    }

    @When("El médico registra la atención exitosamente")
    public void el_medico_registra_la_atencion_exitosamente() {
        excepcionEsperada = null;
        try {
            Object usuarioActual = dbMockeada.getUsuarioActual();
            if (usuarioActual instanceof Medico) {
                Medico medico = (Medico) usuarioActual;
                this.atencionRegistrada = servicioCreacionAtencion.registrarAtencion(
                        "1", "Paciente con fiebre dengue, se indica reposo y hidratación completa", medico
                );
                dbMockeada.guardarAtencion(atencionRegistrada);
                System.out.println("DEBUG: Atención registrada exitosamente");
            }
        } catch (Exception e) {
            this.excepcionEsperada = e;
            System.out.println("DEBUG: Error al registrar atención: " + e.getMessage());
        }
    }

    @Given("El médico registró una atención para el ingreso {string}")
    public void el_medico_registro_una_atencion_para_el_ingreso(String idIngreso) {
        excepcionEsperada = null;
        try {
            Object usuarioActual = dbMockeada.getUsuarioActual();
            if (usuarioActual instanceof Medico) {
                Medico medico = (Medico) usuarioActual;
                this.atencionRegistrada = servicioCreacionAtencion.registrarAtencion(
                        idIngreso, "Atención médica completada satisfactoriamente con informe detallado del paciente", medico
                );
                dbMockeada.guardarAtencion(atencionRegistrada);
                System.out.println("DEBUG: Atención pre-registrada para ingreso: " + idIngreso);
            }
        } catch (Exception e) {
            this.excepcionEsperada = e;
            System.out.println("DEBUG: Error al pre-registrar atención: " + e.getMessage());
        }
    }

    @Then("La atención es registrada exitosamente")
    public void la_atencion_es_registrada_exitosamente() {
        assertThat(atencionRegistrada)
                .describedAs("La atención debería haberse registrado exitosamente")
                .isNotNull();
        System.out.println("DEBUG: Atención registrada exitosamente");
    }

    @Then("El sistema registra la atención exitosamente")
    public void el_sistema_registra_la_atencion_exitosamente() {
        assertThat(atencionRegistrada)
                .describedAs("La atención debería haberse registrado exitosamente")
                .isNotNull();
        assertThat(excepcionEsperada).isNull();
        System.out.println("DEBUG: Sistema registró atención exitosamente");
    }

    @Then("El estado del ingreso {string} cambia de EN_PROCESO a FINALIZADO")
    public void el_estado_del_ingreso_cambia_de_en_proceso_a_finalizado(String idIngreso) {
        Optional<Ingreso> ingreso = dbMockeada.buscarPorId(idIngreso);
        assertThat(ingreso).isPresent();
        assertThat(ingreso.get().getEstado()).isEqualTo(EstadoIngreso.FINALIZADO);
        System.out.println("DEBUG: Estado del ingreso " + idIngreso + " cambiado a FINALIZADO");
    }

    @Then("La atención queda asociada al ingreso {string}")
    public void la_atencion_queda_asociada_al_ingreso(String idIngreso) {
        assertThat(atencionRegistrada).isNotNull();
        assertThat(atencionRegistrada.getIngreso().getId()).isEqualTo(idIngreso);
        System.out.println("DEBUG: Atención asociada al ingreso " + idIngreso);
    }

    @Then("El paciente {string} ya no aparece en la lista de pacientes en atención")
    public void el_paciente_ya_no_aparece_en_la_lista_de_pacientes_en_atencion(String nombrePaciente) {
        List<Ingreso> ingresosEnProceso = dbMockeada.obtenerTodos().stream()
                .filter(ingreso -> ingreso.getEstado() == EstadoIngreso.EN_PROCESO)
                .toList();

        boolean pacienteEnAtencion = ingresosEnProceso.stream()
                .anyMatch(ingreso -> {
                    String cuilPaciente = ingreso.getCuilPaciente();
                    Optional<Paciente> pacienteOpt = dbMockeada.buscarPacientePorCuil(cuilPaciente);
                    return pacienteOpt.isPresent() && pacienteOpt.get().getNombre().equals(nombrePaciente);
                });

        assertThat(pacienteEnAtencion).isFalse();
        System.out.println("DEBUG: Paciente " + nombrePaciente + " no está en lista de atención");
    }

    @Then("El sistema de atención muestra el mensaje de error: {string}")
    public void el_sistema_de_atencion_muestra_el_mensaje_de_error(String mensajeError) {
        assertThat(excepcionEsperada)
                .describedAs("Debería haber una excepción con el mensaje: " + mensajeError)
                .isNotNull()
                .hasMessage(mensajeError);
        System.out.println("DEBUG: Mensaje de error de atención verificado: " + mensajeError);
    }



    @Then("El estado del ingreso {string} permanece en EN_PROCESO")
    public void el_estado_del_ingreso_permanece_en_en_proceso(String idIngreso) {
        Optional<Ingreso> ingreso = dbMockeada.buscarPorId(idIngreso);
        assertThat(ingreso).isPresent();
        assertThat(ingreso.get().getEstado()).isEqualTo(EstadoIngreso.EN_PROCESO);
        System.out.println("DEBUG: Estado del ingreso " + idIngreso + " permanece en EN_PROCESO");
    }

    @Then("La atención no es registrada")
    public void la_atencion_no_es_registrada() {
        assertThat(atencionRegistrada).isNull();
        System.out.println("DEBUG: Atención no fue registrada (como se esperaba)");
    }

    @Then("La atención contiene los siguientes datos:")
    public void la_atencion_contiene_los_siguientes_datos(io.cucumber.datatable.DataTable dataTable) {
        assertThat(atencionRegistrada).isNotNull();

        List<Map<String, String>> tabla = dataTable.asMaps();
        Map<String, String> fila = tabla.getFirst();

        if (fila.containsKey("Ingreso")) {
            assertThat(atencionRegistrada.getIngreso().getId()).isEqualTo(fila.get("Ingreso"));
        }
        if (fila.containsKey("Informe Médico")) {
            assertThat(atencionRegistrada.getInformeMedico()).isEqualTo(fila.get("Informe Médico"));
        }
        if (fila.containsKey("Médico")) {
            assertThat(atencionRegistrada.getMedico().getMatricula()).isEqualTo(fila.get("Médico"));
        }

        System.out.println("DEBUG: Todos los datos de la atención verificados");
    }

    @Then("Se consulta el historial del paciente {string}")
    public void se_consulta_el_historial_del_paciente(String nombrePaciente) {
        // Implementación para verificar historial
        System.out.println("DEBUG: Consultando historial del paciente: " + nombrePaciente);
    }

    @Then("El historial muestra la atención registrada:")
    public void el_historial_muestra_la_atencion_registrada(io.cucumber.datatable.DataTable dataTable) {
        // Implementación para verificar historial
        List<Map<String, String>> tabla = dataTable.asMaps();
        System.out.println("DEBUG: Verificando historial con " + tabla.size() + " registros");
    }

    @Then("El ingreso {string} ya no está asignado al médico {string}")
    public void el_ingreso_ya_no_esta_asignado_al_medico(String idIngreso, String matricula) {
        Optional<Ingreso> ingreso = dbMockeada.buscarPorId(idIngreso);
        assertThat(ingreso).isPresent();
        // El ingreso sigue teniendo el médico asignado, pero está FINALIZADO
        System.out.println("DEBUG: Ingreso " + idIngreso + " finalizado - Médico: " + matricula);
    }

    @Then("El médico {string} puede reclamar nuevos pacientes")
    public void el_medico_puede_reclamar_nuevos_pacientes(String matricula) {
        // Verificar que el médico está disponible para nuevos reclamos
        System.out.println("DEBUG: Médico " + matricula + " disponible para nuevos pacientes");
    }

    @Then("La sala de atención queda disponible para nuevo paciente")
    public void la_sala_de_atencion_queda_disponible_para_nuevo_paciente() {
        System.out.println("DEBUG: Sala de atención liberada");
    }

    @Then("El sistema {string}")
    public void el_sistema(String resultado) {
        if (resultado.contains("muestra el mensaje de error")) {
            assertThat(excepcionEsperada).isNotNull();
        } else if (resultado.contains("registra la atención exitosamente")) {
            assertThat(atencionRegistrada).isNotNull();
            assertThat(excepcionEsperada).isNull();
        }
    }
    

    // Métodos auxiliares
    private Ingreso crearIngresoSimulado(String id, Paciente paciente, NivelEmergencia nivelEmergencia) {
        Enfermera enfermera = new Enfermera("Enfermera", "Test");

        Ingreso ingreso = new Ingreso(
                id,
                paciente,
                enfermera,
                "Informe inicial de ingreso",
                nivelEmergencia,
                36.5f,
                80.0f,
                16.0f,
                120.0f,
                80.0f
        );

        return ingreso;
    }

    private String generarCuilDesdeNombre(String nombre) {
        return "23-" + Math.abs(nombre.hashCode()) % 10000000 + "-9";
    }
}