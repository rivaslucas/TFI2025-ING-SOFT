Feature: Módulo de creación de atención
  Como médico
  Quiero registrar un informe de atención de un paciente ingresado en urgencias que he reclamado
  Para dejar constancia de la atención brindada a dicho paciente

  Background:
    Given El médico con matrícula "67890" está autenticado para atención
    And Existe el siguiente ingreso reclamado por el médico:
      | ID Ingreso | Paciente       | CUIL         | Estado    | Médico Asignado |
      | 1          | Juan Pérez     | 23-1234567-9 | EN_PROCESO | 67890          |

  Scenario: Registro exitoso de atención con informe completo
    When El médico registra la siguiente atención:
      | Ingreso | Informe Médico                                 | Médico |
      | 1       | Paciente con fiebre dengue, se indica reposo y hidratación | 67890  |
    Then La atención es registrada exitosamente
    And El estado del ingreso "1" cambia de EN_PROCESO a FINALIZADO
    And La atención queda asociada al ingreso "1"
    And El paciente "Juan Pérez" ya no aparece en la lista de pacientes en atención

  Scenario: Registro fallido por omisión del informe médico
    When El médico intenta registrar la siguiente atención:
      | Ingreso | Informe Médico | Médico |
      | 1       |                | 67890  |
    Then El sistema de atención muestra el mensaje de error: "El informe médico es obligatorio"
    And El estado del ingreso "1" permanece en EN_PROCESO
    And La atención no es registrada

  Scenario: Registro fallido por médico no asignado al ingreso
    Given El médico con matrícula "99999" está autenticado
    And Existe el siguiente ingreso reclamado por otro médico:
      | ID Ingreso | Paciente     | CUIL         | Estado     | Médico Asignado |
      | 2          | María García | 27-4567890-3 | EN_PROCESO | 67890           |
    When El médico "99999" intenta registrar atención para el ingreso "2"
    Then El sistema de atención muestra el mensaje de error: "No tiene permisos para atender este ingreso"
    And El estado del ingreso "2" permanece en EN_PROCESO

  Scenario: Registro fallido por ingreso no reclamado
    Given Existe el siguiente ingreso no reclamado:
      | ID Ingreso | Paciente   | CUIL         | Estado    | Médico Asignado |
      | 3          | Carlos López | 23-4567899-2 | PENDIENTE |                |
    When El médico intenta registrar atención para el ingreso "3"
    Then El sistema de atención muestra el mensaje de error: "El ingreso no está en proceso de atención"
    And La atención no es registrada

  Scenario: Registro fallido por ingreso ya finalizado
    Given Existe el siguiente ingreso ya finalizado:
      | ID Ingreso | Paciente | CUIL         | Estado    | Médico Asignado |
      | 4          | Ana Martínez | 27-1111111-1 | FINALIZADO | 67890          |
    When El médico intenta registrar atención para el ingreso "4"
    Then El sistema de atención muestra el mensaje de error: "No se puede atender un ingreso finalizado"
    And La atención no es registrada

  Scenario: Campos mandatorios de la atención
    When El médico registra la atención exitosamente
    Then La atención contiene los siguientes datos:
      | Campo | Valor | Obligatorio |
      | Ingreso | 1 | Sí |
      | Informe Médico | "Paciente con fiebre dengue, se indica reposo y hidratación" | Sí |
      | Médico | 67890 | Sí |
      | Fecha Atención | Fecha actual | Sí |
      | Hora Atención | Hora actual | Sí |

  Scenario: Verificación de historial después de atención completada
    Given El médico registró una atención para el ingreso "1"
    When Se consulta el historial del paciente "Juan Pérez"
    Then El historial muestra la atención registrada:
      | Fecha | Médico | Informe |
      | Fecha actual | Dr. Matrícula 67890 | "Paciente con fiebre dengue, se indica reposo y hidratación" |

  Scenario: Liberación de recursos después de atención
    Given El médico registró una atención para el ingreso "1"
    Then El ingreso "1" ya no está asignado al médico "67890"
    And El médico "67890" puede reclamar nuevos pacientes
    And La sala de atención queda disponible para nuevo paciente

  Scenario Outline: Validación de longitud del informe médico
    When El médico intenta registrar la siguiente atención:
      | Ingreso | Informe Médico | Médico |
      | 1       | <informe>      | 67890  |
    Then El sistema de atención muestra el mensaje de error: "<mensajeError>"

    Examples:
      | informe | mensajeError                                   |
      | A       | El informe médico debe tener al menos 10 caracteres |
      |         | El informe médico es obligatorio               |

  Scenario: Validación de informe médico válido
    When El médico intenta registrar la siguiente atención:
      | Ingreso | Informe Médico                   | Médico |
      | 1       | "Paciente estable, se da de alta" | 67890  |
    Then La atención es registrada exitosamente