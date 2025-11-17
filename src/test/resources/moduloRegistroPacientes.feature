Feature: Registro de pacientes
  Como enfermera
  Quiero registrar pacientes
  Para poder realizar el ingreso a urgencias o buscarlos durante un ingreso

  Background:
    Given Que la siguiente enfermera esta registrada:
      | Cuil         | Nombre | Apellido | E-mail           | Matricula |
        | 23-9876543-6 | Susana | Gimenez  | susana@gmail.com | 12345     |
    And Las siguientes obras sociales están registradas en el sistema:
      | Nombre            | ID            |
      | Subsidio de salud | SS            |
      | Swiss medical     | SM            |
      | OSPIA             | OP            |
      | OSDE              | OS            |
    And Existen los siguientes afiliados a obras sociales:
      | Cuil         | Obra social | Numero afiliado |
      | 27-4567890-3 | OSDE        | 87654321        |

  Scenario: Registro exitoso de paciente con todos los datos mandatorios y obra social existente
    When Se intenta registrar el siguiente paciente:
      | Cuil         | Apellido | Nombre  | Calle      | Numero | Localidad | Obra social | Numero afiliado |
      | 27-4567890-3 | Nunez    | Marcelo | San Martin | 123    | Tucuman   | OSDE        |  87654321        |
    Then  la lista de pacientes es :
      | Cuil         | Apellido | Nombre  | Obra social | Numero afiliado |
      | 27-4567890-3 | Nunez    | Marcelo | OSDE        |  87654321        |

  Scenario: Registro exitoso de paciente sin obra social
    When Se intenta registrar el siguiente paciente:
      | Cuil         | Apellido | Nombre  | Calle      | Numero | Localidad |
      | 23-1234567-9 | Gallardo | Ana     | Maipu      | 450    | Tucuman   |
    Then El paciente es registrado exitosamente
    And El paciente aparece en la lista de pacientes sin obra social:
      | Cuil         | Apellido | Nombre  |
      | 23-1234567-9 | Gallardo | Ana     |

  Scenario: Registro fallido por obra social inexistente
    When Se intenta registrar el siguiente paciente:
      | Cuil         | Apellido | Nombre  | Calle      | Numero | Localidad | Obra social | Numero afiliado |
      | 23-1234567-9 | Nunez    | Marcelo | San Martin | 123    | Tucuman   | SANCOR      | 12345678        |
    Then El sistema muestra el mensaje de error: "No se puede registrar al paciente con una obra social inexistente"

  Scenario: Registro fallido por paciente no afiliado a la obra social
    Given Existen los siguientes pacientes afiliados a obras sociales:
      | Cuil         | Obra social | Numero afiliado |
      | 27-4567890-3 | OSDE        | 87654321        |
    When Se intenta registrar el siguiente paciente:
      | Cuil         | Apellido | Nombre  | Calle      | Numero | Localidad | Obra social | Numero afiliado |
      | 23-1234567-9 | Nunez    | Marcelo | San Martin | 123    | Tucuman   | OSDE        | 12345678        |
    Then El sistema muestra el mensaje de error: "No se puede registrar el paciente dado que no esta afiliado a la obra social"



  Scenario Outline: Registro fallido por dato mandatorio omitido
    When Se intenta registrar el siguiente paciente:
      | Campo      | Valor        |
      | Cuil       | <cuil>       |
      | Apellido   | <apellido>   |
      | Nombre     | <nombre>     |
      | Calle      | <calle>      |
      | Numero     | <numero>     |
      | Localidad  | <localidad>  |
    Then El sistema muestra el mensaje de error: "<mensaje_error>"

    Examples:
      | campo_omitido | cuil         | apellido | nombre   | calle       | numero | localidad | mensaje_error                    |
      | CUIL          |              | Nunez    | Marcelo  | San Martin  | 123    | Tucuman   | "CUIL es un campo obligatorio"   |
      | Apellido      | 23-1234567-9 |          | Marcelo  | San Martin  | 123    | Tucuman   | "Apellido es un campo obligatorio" |
      | Nombre        | 23-1234567-9 | Nunez    |          | San Martin  | 123    | Tucuman   | "Nombre es un campo obligatorio" |
      | Calle         | 23-1234567-9 | Nunez    | Marcelo  |             | 123    | Tucuman   | "Calle es un campo obligatorio"  |
      | Número        | 23-1234567-9 | Nunez    | Marcelo  | San Martin  |        | Tucuman   | "Numero es un campo obligatorio" |
      | Localidad     | 23-1234567-9 | Nunez    | Marcelo  | San Martin  | 123    |           | "Localidad es un campo obligatorio" |