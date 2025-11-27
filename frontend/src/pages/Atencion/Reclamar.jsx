import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { atencionService } from '../../services/atencionService';
import styles from './ReclamarPaciente.module.css';

const ReclamarPaciente = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });
    const [pacienteReclamado, setPacienteReclamado] = useState(null);
    const [estadoMedico, setEstadoMedico] = useState(null);
    const [verificando, setVerificando] = useState(false);
    const [sincronizando, setSincronizando] = useState(true);

    const [formData, setFormData] = useState({
        medicoMatricula: '67890'
    });

    useEffect(() => {
        const sincronizarEstado = async () => {
            if (!formData.medicoMatricula.trim()) {
                setSincronizando(false);
                return;
            }

            setSincronizando(true);
            try {
                console.log('🔄 Sincronizando estado del médico...');

                const estadoResult = await atencionService.obtenerEstadoMedico(formData.medicoMatricula);
                if (estadoResult.success) {
                    setEstadoMedico(estadoResult.data);

                    if (estadoResult.data.pacienteActual) {
                        console.log('✅ Recuperando paciente actual del estado:', estadoResult.data.pacienteActual);
                        setPacienteReclamado(estadoResult.data.pacienteActual);
                    } else {
                        setPacienteReclamado(null);
                    }
                } else {
                    setEstadoMedico(null);
                    setPacienteReclamado(null);
                }

            } catch (error) {
                console.error('❌ Error sincronizando estado:', error);
                setEstadoMedico(null);
                setPacienteReclamado(null);
            } finally {
                setSincronizando(false);
            }
        };

        sincronizarEstado();
    }, [formData.medicoMatricula]);

    const verificarEstadoMedico = async () => {
        if (!formData.medicoMatricula.trim()) return;

        setVerificando(true);
        try {
            const result = await atencionService.obtenerEstadoMedico(formData.medicoMatricula);
            if (result.success) {
                setEstadoMedico(result.data);

                if (result.data.pacienteActual) {
                    console.log('🔄 Actualizando paciente actual:', result.data.pacienteActual);
                    setPacienteReclamado(result.data.pacienteActual);
                } else {
                    setPacienteReclamado(null);
                }
            } else {
                setEstadoMedico(null);
                setPacienteReclamado(null);
            }
        } catch (error) {
            console.error('Error verificando estado médico:', error);
            setEstadoMedico(null);
            setPacienteReclamado(null);
        } finally {
            setVerificando(false);
        }
    };

    const getPacienteValue = (paciente, key) => {
        if (!paciente) return 'N/A';

        const fieldMap = {
            'nombre': 'pacienteNombre',
            'apellido': 'pacienteApellido',
            'cuil': 'pacienteCuil',
            'enfermera': 'enfermeraNombre',
            'nivelEmergencia': 'nivelEmergencia'
        };

        const backendField = fieldMap[key] || key;
        return paciente[backendField] !== undefined ? paciente[backendField] : 'N/A';
    };

    const handleReclamar = async () => {
        if (!formData.medicoMatricula.trim()) {
            setMessage({ type: 'error', text: '❌ Por favor, ingrese su matrícula' });
            return;
        }

        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            const result = await atencionService.reclamarPaciente(formData.medicoMatricula);

            if (result.success) {
                setMessage({
                    type: 'success',
                    text: result.message || '✅ Paciente reclamado exitosamente'
                });
                setPacienteReclamado(result.data);
                await verificarEstadoMedico();
            } else {
                setMessage({
                    type: 'error',
                    text: result.error || '❌ No se pudo reclamar el paciente'
                });
                await verificarEstadoMedico();
            }
        } catch (err) {
            console.error('Error inesperado:', err);
            setMessage({
                type: 'error',
                text: '❌ Error inesperado al reclamar paciente'
            });
            await verificarEstadoMedico();
        } finally {
            setLoading(false);
        }
    };

    const handleLiberarPaciente = async () => {
        if (!pacienteReclamado) return;

        const idIngreso = pacienteReclamado.id || pacienteReclamado.idIngreso;
        if (!idIngreso) {
            setMessage({ type: 'error', text: '❌ No se puede identificar el paciente' });
            return;
        }

        setLoading(true);
        try {
            const liberacionData = {
                medicoMatricula: formData.medicoMatricula,
                motivo: 'Liberado por el médico desde la interfaz'
            };

            const result = await atencionService.liberarPaciente(idIngreso, liberacionData);

            if (result.success) {
                setMessage({
                    type: 'success',
                    text: '✅ Paciente liberado exitosamente'
                });
                setPacienteReclamado(null);
                await verificarEstadoMedico();
            } else {
                setMessage({
                    type: 'error',
                    text: result.error || '❌ Error al liberar paciente'
                });
            }
        } catch (error) {
            console.error('Error liberando paciente:', error);
            setMessage({
                type: 'error',
                text: '❌ Error al liberar paciente'
            });
        } finally {
            setLoading(false);
        }
    };

    const getNivelEmergenciaColor = (nivel) => {
        const nivelUpper = nivel?.toUpperCase();
        switch (nivelUpper) {
            case 'CRITICA':
                return { bg: styles.bgRed100, text: styles.textRed800 };
            case 'EMERGENCIA':
                return { bg: styles.bgOrange100, text: styles.textOrange800 };
            case 'URGENCIA':
                return { bg: styles.bgYellow100, text: styles.textYellow800 };
            case 'URGENCIA_MENOR':
                return { bg: styles.bgBlue100, text: styles.textBlue800 };
            default:
                return { bg: styles.bgGray100, text: styles.textGray800 };
        }
    };

    const getEstadoColor = (estado) => {
        switch (estado) {
            case 'PENDIENTE': return `${styles.bgYellow100} ${styles.textYellow800} ${styles.borderYellow200}`;
            case 'EN_PROCESO': return `${styles.bgBlue100} ${styles.textBlue800} ${styles.borderBlue200}`;
            case 'ATENDIDO': return `${styles.bgGreen100} ${styles.textGreen800} ${styles.borderGreen200}`;
            default: return `${styles.bgGray100} ${styles.textGray800} ${styles.borderGray200}`;
        }
    };

    const handleComenzarAtencion = () => {
        if (!pacienteReclamado) {
            setMessage({
                type: 'error',
                text: '❌ No hay paciente asignado para atender'
            });
            return;
        }

        const idIngreso = pacienteReclamado.id || pacienteReclamado.idIngreso;
        if (!idIngreso) {
            setMessage({
                type: 'error',
                text: '❌ ID de paciente inválido'
            });
            return;
        }

        navigate('/atencion/atender', {
            state: {
                ingreso: {
                    ...pacienteReclamado,
                    id: idIngreso,
                    nombre: getPacienteValue(pacienteReclamado, 'nombre'),
                    apellido: getPacienteValue(pacienteReclamado, 'apellido'),
                    cuil: getPacienteValue(pacienteReclamado, 'cuil'),
                    enfermera: getPacienteValue(pacienteReclamado, 'enfermera'),
                    nivelEmergencia: getPacienteValue(pacienteReclamado, 'nivelEmergencia')
                }
            }
        });
    };

    return (
        <div className={styles.container}>
            <div className={styles.maxW6xl}>
                {/* Header */}
                <div className={styles.header}>
                    <div className={styles.headerLeft}>
                        <div className={styles.headerLogo}>
                            <span className="text-2xl">👨‍⚕️</span>
                        </div>
                        <div>
                            <h1 className={styles.headerTitle}>
                                Reclamar Próximo Paciente
                            </h1>
                            <p className={styles.headerSubtitle}>
                                Tome el siguiente paciente de la lista de espera
                            </p>
                        </div>
                    </div>
                    <button
                        onClick={() => navigate('/')}
                        className={styles.backButton}
                    >
                        <span>←</span>
                        <span>Volver al Dashboard</span>
                    </button>
                </div>

                {/* Indicador de sincronización */}
                {sincronizando && (
                    <div className={styles.syncIndicator}>
                        <div className={styles.syncContent}>
                            <div className={styles.syncSpinner}></div>
                            <span className={styles.syncText}>Sincronizando estado...</span>
                        </div>
                    </div>
                )}

                <div className={styles.grid}>
                    {/* Panel izquierdo - Estado y acciones */}
                    <div className={styles.leftPanel}>
                        {/* Card de estado del médico */}
                        <div className={styles.statusCard}>
                            <div className={styles.statusHeader}>
                                <h2 className={styles.statusTitle}>Estado del Médico</h2>
                                <button
                                    onClick={verificarEstadoMedico}
                                    disabled={verificando || loading}
                                    className={styles.refreshButton}
                                >
                                    {verificando ? (
                                        <div className={styles.smallSpinner}></div>
                                    ) : (
                                        <span>↻</span>
                                    )}
                                    <span>Actualizar</span>
                                </button>
                            </div>

                            {estadoMedico ? (
                                <div className={styles.statusContent}>
                                    <div className={`${styles.statusAlert} ${
                                        estadoMedico.puedeReclamarPaciente
                                            ? styles.statusAvailable
                                            : styles.statusBusy
                                    }`}>
                                        <div className={styles.statusIndicator}>
                                            <div className={`${styles.statusDot} ${
                                                estadoMedico.puedeReclamarPaciente ? styles.dotAvailable : styles.dotBusy
                                            }`}></div>
                                            <div>
                                                <p className={styles.statusMessage}>
                                                    {estadoMedico.puedeReclamarPaciente
                                                        ? '✅ Disponible para reclamar paciente'
                                                        : '❌ Tiene paciente en atención'
                                                    }
                                                </p>
                                                <p className={styles.statusDescription}>
                                                    {estadoMedico.puedeReclamarPaciente
                                                        ? 'Puede reclamar el próximo paciente de la lista de espera'
                                                        : 'Complete la atención actual antes de reclamar otro paciente'
                                                    }
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            ) : (
                                <div className={styles.emptyState}>
                                    <div className={styles.emptyIcon}>🏥</div>
                                    <p className={styles.emptyText}>Ingrese una matrícula para ver el estado</p>
                                </div>
                            )}
                        </div>

                        {/* Card de reclamar paciente */}
                        <div className={styles.claimCard}>
                            <h2 className={styles.claimTitle}>Reclamar Paciente</h2>

                            {message.text && (
                                <div className={`${styles.message} ${
                                    message.type === 'success'
                                        ? styles.messageSuccess
                                        : styles.messageError
                                }`}>
                                    <div className={styles.messageContent}>
                                        <span className={styles.messageIcon}>
                                            {message.type === 'success' ? '✅' : '❌'}
                                        </span>
                                        <span>{message.text}</span>
                                    </div>
                                    <button
                                        onClick={() => setMessage({ type: '', text: '' })}
                                        className={styles.messageClose}
                                    >
                                        ✕
                                    </button>
                                </div>
                            )}

                            <div className={styles.claimForm}>
                                <div>
                                    <label className={styles.formLabel}>
                                        Matrícula del Médico *
                                    </label>
                                    <input
                                        type="text"
                                        value={formData.medicoMatricula}
                                        onChange={(e) => setFormData({ medicoMatricula: e.target.value })}
                                        className={styles.inputField}
                                        placeholder="Ingrese su matrícula"
                                        disabled={loading}
                                    />
                                </div>

                                <button
                                    onClick={handleReclamar}
                                    disabled={loading || (estadoMedico && !estadoMedico.puedeReclamarPaciente)}
                                    className={`${styles.claimButton} ${
                                        loading || (estadoMedico && !estadoMedico.puedeReclamarPaciente)
                                            ? styles.buttonDisabled
                                            : ''
                                    }`}
                                >
                                    {loading ? (
                                        <div className={styles.buttonLoading}>
                                            <div className={styles.buttonSpinner}></div>
                                            <span>Verificando...</span>
                                        </div>
                                    ) : estadoMedico && !estadoMedico.puedeReclamarPaciente ? (
                                        <div className={styles.buttonContent}>
                                            <span>❌</span>
                                            <span>No puede reclamar paciente</span>
                                        </div>
                                    ) : (
                                        <div className={styles.buttonContent}>
                                            <span>📋</span>
                                            <span>Reclamar Próximo Paciente</span>
                                        </div>
                                    )}
                                </button>
                            </div>

                            {/* Información */}
                            <div className={styles.infoCard}>
                                <h3 className={styles.infoTitle}>
                                    <span>💡</span>
                                    <span>Información importante</span>
                                </h3>
                                <ul className={styles.infoList}>
                                    <li className={styles.infoItem}>
                                        <span>•</span>
                                        <span>El sistema verificará automáticamente si puede reclamar un nuevo paciente</span>
                                    </li>
                                    <li className={styles.infoItem}>
                                        <span>•</span>
                                        <span>Solo podrá reclamar si no tiene pacientes en atención</span>
                                    </li>
                                    <li className={styles.infoItem}>
                                        <span>•</span>
                                        <span>Se asignará por nivel de urgencia y orden de llegada</span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    {/* Panel derecho - Paciente asignado */}
                    <div className={styles.rightPanel}>
                        <h2 className={styles.patientTitle}>Paciente Asignado</h2>

                        {pacienteReclamado ? (
                            <div className={styles.patientContent}>
                                <div className={styles.patientCard}>
                                    <div className={styles.patientHeader}>
                                        <div className={styles.patientStatus}>
                                            <div className={styles.statusDotActive}></div>
                                            <h3 className={styles.patientStatusTitle}>
                                                ✅ Paciente en Atención
                                            </h3>
                                        </div>
                                    </div>

                                    <div className={styles.patientInfo}>
                                        <div className={styles.infoRow}>
                                            <span className={styles.infoLabel}>Paciente:</span>
                                            <span className={styles.infoValue}>
                                                {getPacienteValue(pacienteReclamado, 'nombre')} {getPacienteValue(pacienteReclamado, 'apellido')}
                                            </span>
                                        </div>

                                        <div className={styles.infoRow}>
                                            <span className={styles.infoLabel}>CUIL:</span>
                                            <span className={styles.infoValueMono}>{getPacienteValue(pacienteReclamado, 'cuil')}</span>
                                        </div>

                                        <div className={styles.infoRow}>
                                            <span className={styles.infoLabel}>Nivel de Emergencia:</span>
                                            <span className={`${styles.emergencyBadge} ${
                                                getNivelEmergenciaColor(getPacienteValue(pacienteReclamado, 'nivelEmergencia')).bg
                                            } ${getNivelEmergenciaColor(getPacienteValue(pacienteReclamado, 'nivelEmergencia')).text}`}>
                                                {getPacienteValue(pacienteReclamado, 'nivelEmergencia')}
                                            </span>
                                        </div>

                                        <div className={styles.infoRow}>
                                            <span className={styles.infoLabel}>Enfermera:</span>
                                            <span className={styles.infoValue}>{getPacienteValue(pacienteReclamado, 'enfermera')}</span>
                                        </div>

                                        <div className={styles.infoRow}>
                                            <span className={styles.infoLabel}>ID Ingreso:</span>
                                            <span className={styles.infoValueMono}>
                                                {pacienteReclamado.id || pacienteReclamado.idIngreso}
                                            </span>
                                        </div>

                                        <div className={styles.infoRow}>
                                            <span className={styles.infoLabel}>Estado:</span>
                                            <span className={`${styles.stateBadge} ${getEstadoColor(pacienteReclamado.estado)}`}>
                                                {pacienteReclamado.estado || 'EN_PROCESO'}
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                <div className={styles.actionButtons}>
                                    <button
                                        onClick={handleComenzarAtencion}
                                        disabled={loading || !pacienteReclamado}
                                        className={styles.attendButton}
                                    >
                                        <span>🩺</span>
                                        <span>Comenzar Atención</span>
                                    </button>

                                    <button
                                        onClick={handleLiberarPaciente}
                                        disabled={loading}
                                        className={styles.releaseButton}
                                    >
                                        <span>🗑️</span>
                                        <span>Liberar</span>
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div className={styles.noPatient}>
                                <div className={styles.noPatientIcon}>👨‍⚕️</div>
                                <p className={styles.noPatientText}>
                                    {loading ? 'Buscando próximo paciente...' : 'No hay paciente asignado'}
                                </p>
                                <p className={styles.noPatientSubtext}>
                                    Use el botón "Reclamar" para asignarse el próximo paciente
                                </p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ReclamarPaciente;