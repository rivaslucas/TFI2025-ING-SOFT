package org.example;

import org.example.app.interfaces.RepositorioObraSocial;
import org.example.app.interfaces.RepositorioPacientes;
import org.example.app.interfaces.RepositorioUsuarios;
import org.example.domain.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("🚀 Aplicación Spring Boot iniciada en http://localhost:8081");
    }

    @Bean
    public CommandLineRunner initData(RepositorioUsuarios repositorioUsuarios,
                                      RepositorioPacientes repositorioPacientes,
                                      RepositorioObraSocial repositorioObraSocial) {
        return args -> {
            try {
                System.out.println("=== INICIANDO CARGA DE DATOS DE PRUEBA ===");

                // Crear usuarios de prueba
                Usuario medico = new Usuario("medico@hospital.com", "password123", Autoridad.MEDICO);
                Usuario enfermero = new Usuario("enfermero@hospital.com", "password123", Autoridad.ENFERMERO);

                repositorioUsuarios.guardarUsuario(medico);
                repositorioUsuarios.guardarUsuario(enfermero);
                System.out.println("✅ Usuarios creados");

                // Crear obras sociales de prueba
                ObraSocial osde = new ObraSocial("OS", "OSDE");
                ObraSocial swiss = new ObraSocial("SM", "Swiss Medical");
                ObraSocial galeno = new ObraSocial("GL", "Galeno");
                ObraSocial omint = new ObraSocial("OM", "Omint");

                repositorioObraSocial.guardarObraSocial(osde);
                repositorioObraSocial.guardarObraSocial(swiss);
                repositorioObraSocial.guardarObraSocial(galeno);
                repositorioObraSocial.guardarObraSocial(omint);

                System.out.println("✅ " + repositorioObraSocial.obtenerTodasLasObrasSociales().size() + " obras sociales creadas");

                // Crear pacientes de prueba
                String cuil1 = "23-12345678-9";
                String nombre1 = "Juan";
                String apellido1 = "Perez";

                Paciente paciente1 = new Paciente(cuil1, nombre1, apellido1, new Afiliado("12345", osde), new Domicilio("Calle Principal", 123, "CABA"));
                repositorioPacientes.guardarPaciente(paciente1);
                System.out.println("✅ Paciente 1 creado exitosamente");

                String cuil2 = "27-87654321-0";
                String nombre2 = "Maria";
                String apellido2 = "Garcia";

                Paciente paciente2 = new Paciente(cuil2, nombre2, apellido2, new Afiliado("67890", swiss), new Domicilio("Avenida Siempre Viva", 456, "CABA"));
                repositorioPacientes.guardarPaciente(paciente2);
                System.out.println("✅ Paciente 2 creado exitosamente");

                System.out.println("🎉 Todos los datos de prueba inicializados correctamente");

            } catch (Exception e) {
                System.err.println("❌ Error crítico inicializando datos: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error inicializando datos de prueba", e);
            }
        };
    }
}