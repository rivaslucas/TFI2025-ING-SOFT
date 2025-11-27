import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { urgenciasService } from '../services/urgenciasService';
import { atencionService } from '../services/atencionService';
import styles from './Dashboard.module.css';

const Dashboard = () => {
    const { user, logout, isMedico, isEnfermero, userRole } = useAuth();
    const navigate = useNavigate();
    const [stats, setStats] = useState({
        pacientesEnEspera: 0,
        urgenciasHoy: 0,
        atencionesCompletadas: 0
    });
    const [loading, setLoading] = useState(true);
    const [pacienteActual, setPacienteActual] = useState(null);
    const [verificandoPaciente, setVerificandoPaciente] = useState(false);

    const obtenerMatriculaMedico = () => {
        if (!isMedico) return null;
        return user?.matricula || '67890';
    };

    useEffect(() => {
        cargarEstadisticas();
        if (isMedico) {
            const matricula = obtenerMatriculaMedico();
            if (matricula) verificarPacienteActual(matricula);
        }
    }, [isMedico, user]);

    const verificarPacienteActual = async (matricula) => {
        setVerificandoPaciente(true);
        try {
            const result = await atencionService.obtenerEstadoMedico(matricula);
            if (result.success && result.data.pacienteActual) {
                setPacienteActual(result.data.pacienteActual);
            } else {
                setPacienteActual(null);
            }
        } catch (error) {
            setPacienteActual(null);
        } finally {
            setVerificandoPaciente(false);
        }
    };

    const cargarEstadisticas = async () => {
        try {
            setLoading(true);
            const response = await urgenciasService.obtenerListaEspera();

            let listaEspera = [];
            if (Array.isArray(response)) listaEspera = response;
            else if (response?.data) listaEspera = response.data;

            const pacientesEnEspera = listaEspera.length;
            const hoy = new Date().toDateString();

            // Contar urgencias de hoy (esto es aproximado)
            const urgenciasHoy = listaEspera.filter(ingreso => {
                if (!ingreso.fechaIngreso) return false;
                return new Date(ingreso.fechaIngreso).toDateString() === hoy;
            }).length;

            // SOLUCIÓN TEMPORAL: Usar un valor fijo basado en estadísticas típicas
            // En un sistema real, esto vendría de un endpoint del backend
            const atencionesCompletadas = 0; // Por ahora 0 hasta que tengamos el endpoint real

            setStats({
                pacientesEnEspera,
                urgenciasHoy,
                atencionesCompletadas
            });
        } catch (error) {
            console.error('Error cargando estadísticas:', error);
            setStats({
                pacientesEnEspera: 0,
                urgenciasHoy: 0,
                atencionesCompletadas: 0
            });
        } finally {
            setLoading(false);
        }
    };

    // Resto del código igual...
    const handleAtenderPaciente = async () => {
        const matricula = obtenerMatriculaMedico();
        if (!matricula) {
            alert('No se pudo identificar su matrícula');
            return;
        }

        setVerificandoPaciente(true);
        try {
            const estadoResult = await atencionService.obtenerEstadoMedico(matricula);
            if (estadoResult.success && estadoResult.data.pacienteActual) {
                navigate('/atencion/atender', {
                    state: { ingreso: estadoResult.data.pacienteActual }
                });
            } else {
                alert('No tiene paciente asignado');
                navigate('/atencion/reclamar');
            }
        } catch (error) {
            alert('Error al verificar estado');
        } finally {
            setVerificandoPaciente(false);
        }
    };

    const medicoActions = [
        {
            icon: '📋',
            label: 'Reclamar Paciente',
            description: 'Tomar próximo paciente de la lista',
            onClick: () => navigate('/atencion/reclamar'),
        },
        {
            icon: '👨‍⚕️',
            label: verificandoPaciente ? 'Verificando...' : (pacienteActual ? 'Atender Paciente Actual' : 'Atender Paciente'),
            description: pacienteActual ? `Atender a ${pacienteActual.pacienteNombre}` : 'Registrar atención médica',
            onClick: handleAtenderPaciente,
            disabled: verificandoPaciente
        },
        {
            icon: '⏱️',
            label: 'Lista de Espera',
            description: 'Ver pacientes pendientes',
            onClick: () => navigate('/urgencias/lista-espera'),
        }
    ];

    const enfermeroActions = [
        {
            icon: '👥',
            label: 'Registrar Paciente',
            description: 'Nuevo paciente en el sistema',
            onClick: () => navigate('/pacientes/registrar'),
        },
        {
            icon: '🚑',
            label: 'Registrar Urgencia',
            description: 'Nuevo ingreso por urgencia',
            onClick: () => navigate('/urgencias/registrar'),
        },
        {
            icon: '📊',
            label: 'Lista de Espera',
            description: 'Estado actual de espera',
            onClick: () => navigate('/urgencias/lista-espera'),
        },
        {
            icon: '🏢',
            label: 'Obras Sociales',
            description: 'Gestionar obras sociales',
            onClick: () => navigate('/obras-sociales'),
        }
    ];

    const currentActions = isMedico ? medicoActions : enfermeroActions;
    const matriculaMedico = obtenerMatriculaMedico();

    return (
        <div className={styles.container}>
            {/* Header */}
            <header className={styles.header}>
                <div className={styles.headerContent}>
                    <div className={styles.headerLeft}>
                        <div className={styles.logo}>
                            🏥
                        </div>
                        <div>
                            <h1 className={styles.headerTitle}>Clínica Emergencias</h1>
                            <p className={styles.headerSubtitle}>
                                {userRole} {isMedico && matriculaMedico && `• Matrícula: ${matriculaMedico}`}
                            </p>
                        </div>
                    </div>

                    <div className={styles.headerRight}>
                        <div className={styles.userInfo}>
                            <div className={styles.userEmail}>{user?.email}</div>
                        </div>
                        <button
                            onClick={logout}
                            className={styles.logoutButton}
                        >
                            Cerrar Sesión
                        </button>
                    </div>
                </div>
            </header>

            {/* Paciente Actual */}
            {isMedico && pacienteActual && (
                <div className={styles.patientBanner}>
                    <div className={styles.bannerContent}>
                        <div className={styles.bannerInfo}>
                            <div className={styles.pulseDot}></div>
                            <div className={styles.bannerText}>
                                <strong>Paciente actual:</strong> {pacienteActual.pacienteNombre} {pacienteActual.pacienteApellido}
                            </div>
                        </div>
                        <button
                            onClick={handleAtenderPaciente}
                            className={styles.bannerButton}
                        >
                            Comenzar atención
                        </button>
                    </div>
                </div>
            )}

            {/* Contenido Principal */}
            <main className={styles.main}>





                <section className={styles.actionsSection}>
                    <h2 className={`${styles.sectionTitle} ${styles.actionsTitle}`}>
                        {isMedico ? 'Acciones Médicas' : 'Acciones de Enfermería'}
                    </h2>
                    <div className={styles.actionsGrid}>
                        {currentActions.map((action, index) => (
                            <button
                                key={index}
                                onClick={action.onClick}
                                disabled={action.disabled}
                                className={styles.actionCard}
                            >
                                <div className={styles.actionContent}>
                                    <div className={styles.actionIcon}>
                                        {action.icon}
                                    </div>
                                    <div className={styles.actionText}>
                                        <h3 className={styles.actionTitle}>
                                            {action.label}
                                        </h3>
                                        <p className={styles.actionDescription}>
                                            {action.description}
                                        </p>
                                    </div>
                                </div>
                            </button>
                        ))}
                    </div>
                </section>

                {/* Información del Rol */}
                <section className={styles.roleSection}>
                    <div className={styles.roleCard}>
                        <h3 className={styles.roleTitle}>Información del Rol</h3>
                        {isMedico ? (
                            <div>
                                <p className={styles.roleDescription}>
                                    Como <strong>Médico</strong>, tiene acceso completo al sistema de atención de emergencias.
                                </p>
                                <div className={styles.roleGrid}>
                                    <div>
                                        <h4 className={styles.roleSubtitle}>Capacidades:</h4>
                                        <ul className={styles.roleList}>
                                            <li>Reclamar pacientes de la lista</li>
                                            <li>Registrar diagnósticos</li>
                                            <li>Gestionar atenciones</li>
                                        </ul>
                                    </div>
                                    <div>
                                        <h4 className={styles.roleSubtitle}>Flujo:</h4>
                                        <ul className={styles.roleList}>
                                            <li>1. Reclamar paciente</li>
                                            <li>2. Revisar triaje</li>
                                            <li>3. Registrar atención</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div>
                                <p className={styles.roleDescription}>
                                    Como <strong>Enfermero</strong>, es responsable del registro inicial y triaje.
                                </p>
                                <div className={styles.roleGrid}>
                                    <div>
                                        <h4 className={styles.roleSubtitle}>Funciones:</h4>
                                        <ul className={styles.roleList}>
                                            <li>Registrar pacientes</li>
                                            <li>Realizar triaje</li>
                                            <li>Gestionar obras sociales</li>
                                        </ul>
                                    </div>
                                    <div>
                                        <h4 className={styles.roleSubtitle}>Responsabilidades:</h4>
                                        <ul className={styles.roleList}>
                                            <li>Evaluar signos vitales</li>
                                            <li>Clasificar urgencias</li>
                                            <li>Documentación inicial</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>
                </section>
            </main>
        </div>
    );
};

export default Dashboard;