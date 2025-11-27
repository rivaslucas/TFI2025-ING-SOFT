Feature: Autenticacion
  Como usuario del sistema
  Quiero poder registrarme e iniciar sesion en el sistema
  Para poder acceder a las actividades que me son otorgadas

  Background:
    Given Existen los siguientes usuarios:
      | Email             | Contrasena | Autoridad |
      | Nico@gmail.com    | Nico1234   | Medico    |
    And Que el usuario actual es:
      | Email | Contrasena | Autoridad |
      |       |            |           |

  Scenario Outline: Inicio de sesion fallido por credenciales incorrectas
    When Intenta iniciar sesion el siguiente usuario:
      | Email        | Contrasena |
      | <email>      | <contrasena> |
    Then El sistema muestra el mensaje de error "Usuario o contrasena invalido"

    Examples:
      | email           | contrasena |
      | Nicogmail.com   | Nico1234   |
      | Nico@gmail.com  | Nico12345  |

  Scenario Outline: Registro fallido por datos invalidos
    When Intenta crearse el siguiente usuario:
      | Email        | Contrasena | Autoridad |
      | <email>      | <contrasena> | <autoridad> |
    Then El sistema muestra el mensaje de error "<mensaje_error>"

    Examples:
      | email           | contrasena | autoridad | mensaje_error            |
      | anitagmail.com  | anita12354 |           | Email invalido         |
      | Nico@gmail.com  | lio10235   |           | Email existente        |
      | liomessi@10.com | 1234567    |           | Contrasena demasiado corta |

  Scenario Outline: Registro exitoso de usuario
    When Intenta crearse el siguiente usuario:
      | Email        | Contrasena | Autoridad |
      | <email>      | <contrasena> | <autoridad> |
    Then La lista de usuarios es:
      | Email             | Contrasena | Autoridad |
      | Nico@gmail.com    | Nico1234   | Medico    |
      | <email>           | <contrasena> | <autoridad_esperada> |

    Examples:
      | email           | contrasena | autoridad | autoridad_esperada |
      | liomessi@10.com | 12345678   | Enfermero | Enfermero          |
      | franco@43.com   | alpine43   |           |                    |

  Scenario: Inicio de sesion exitoso de un usuario
    When Intenta iniciar sesion el siguiente usuario:
      | Email          | Contrasena |
      | Nico@gmail.com | Nico1234   |
    Then El usuario actual es:
      | Email          | Contrasena | Autoridad |
      | Nico@gmail.com | Nico1234   | Medico    |