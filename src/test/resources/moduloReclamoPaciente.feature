Feature: Módulo de reclamo de paciente
  Como médico
  Quiero reclamar el próximo paciente que debe ser atendido
  Para sacarlo de la lista de espera y poder registrar un informe de atención

  Background:
    Given El médico con matrícula "67890" está autenticado para reclamo
    And Existen los siguientes ingresos en estado PENDIENTE ordenados por prioridad:
      | ID | Paciente       | Nivel Emergencia | Hora Ingreso | Cuil         |
      | 1  | Juan Pérez     | Emergencia       | 08:30        | 23-1234567-9 |
      | 2  | María García   | Emergencia       | 09:15        | 27-4567890-3 |
      | 3  | Carlos López   | Sin Urgencia     | 10:00        | 23-4567899-2 |

  Scenario: Médico reclama próximo paciente respetando prioridad de emergencia
    When El médico reclama el próximo paciente de la lista de espera
    Then El sistema asigna el ingreso con ID "1" al médico
    And El estado del ingreso "1" cambia de PENDIENTE a EN_PROCESO
    And El ingreso "1" ya no aparece en la lista de espera
    And Se registra el médico "67890" como responsable de la atención

  Scenario: Reclamo respeta orden entre emergencias del mismo nivel
    Given Existen múltiples ingresos con nivel "Emergencia":
      | ID | Paciente     | Nivel Emergencia | Hora Ingreso | Cuil         |
      | 4  | Ana Martínez | Emergencia       | 11:00        | 27-1111111-1 |
      | 5  | Pedro Rojas  | Emergencia       | 10:45        | 27-2222222-2 |
    When El médico reclama el próximo paciente de la lista de espera
    Then El sistema asigna el ingreso con ID "5" (más antiguo)
    And El estado del ingreso "5" cambia a EN_PROCESO


  Scenario: Lista de espera vacía
    Given No existen ingresos en estado PENDIENTE
    When El médico reclama el próximo paciente de la lista de espera
    Then El sistema de reclamo muestra el mensaje de error: "No hay pacientes en lista de espera"

  Scenario: Paciente reclamado no aparece para otros médicos
    Given Existen ingresos en lista de espera
    When El médico "67890" reclama el próximo paciente
    And Otro médico consulta la lista de espera
    Then El paciente reclamado no aparece en la lista
    And Solo se muestran ingresos en estado PENDIENTE

  Scenario: Reclamo fallido por médico no autenticado
    Given El médico no está autenticado
    When Se intenta reclamar el próximo paciente
    Then El sistema de reclamo muestra: "Debe autenticarse para reclamar pacientes"

  Scenario: Verificación de datos del paciente reclamado
    When El médico reclama el próximo paciente
    Then El sistema muestra los datos completos del paciente:
      | Campo | Valor |
      | Nombre | Juan Pérez |
      | CUIL | 23-1234567-9 |
      | Nivel Emergencia | Emergencia |
      | Temperatura | 38 |
      | Frecuencia Cardiaca | 70 |
    And Los signos vitales están disponibles para la atención