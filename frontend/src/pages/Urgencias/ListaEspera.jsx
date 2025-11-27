import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { urgenciasService } from '../../services/urgenciasService';
import { useAuth } from '../../context/AuthContext';
import styles from './ListaEspera.module.css';

const ListaEspera = () => {
    const navigate = useNavigate();
    const { isMedico, isEnfermero } = useAuth();
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });
    const [listaEspera, setListaEspera] = useState([]);
    const [filtroNivel, setFiltroNivel] = useState('TODOS');

    const cargarListaEspera = async (isRefresh = false) => {
        if (isRefresh) {
            setRefreshing(true);
        } else {
            setLoading(true);
        }

        setMessage({ type: '', text: '' });

        try {
            const result = await urgenciasService.obtenerListaEspera();

            if (result.success) {
                setListaEspera(result.data || []);
                if (isRefresh) {
                    setMessage({ type: 'success', text: '✅ Lista actualizada' });
                    setTimeout(() => setMessage({ type: '', text: '' }), 3000);
                }
            } else {
                setMessage({ type: 'error', text: result.error });
                setListaEspera([]);
            }
        } catch (err) {
            setMessage({ type: 'error', text: 'Error al cargar la lista de espera' });
            setListaEspera([]);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useEffect(() => {
        cargarListaEspera();

        const interval = setInterval(() => {
            cargarListaEspera(true);
        }, 30000);

        return () => clearInterval(interval);
    }, []);

    const getPacienteValue = (paciente, key) => {
        if (!paciente) return 'N/A';

        const fieldMap = {
            'nombre': 'pacienteNombre',
            'apellido': 'pacienteApellido',
            'cuil': 'pacienteCuil',
            'enfermera': 'enfermeraNombre'
        };

        const backendField = fieldMap[key] || key;
        return paciente[backendField] !== undefined ? paciente[backendField] : 'N/A';
    };

    const listaFiltrada = filtroNivel === 'TODOS'
        ? listaEspera
        : listaEspera.filter(paciente =>
            getPacienteValue(paciente, 'nivelEmergencia') === filtroNivel
        );

    const getNivelEmergenciaColor = (nivel) => {
        if (!nivel) return `${styles.bgGray100} ${styles.textGray800} ${styles.borderGray300}`;

        switch (nivel.toUpperCase()) {
            case 'CRITICA':
                return `${styles.bgRed100} ${styles.textRed800} ${styles.borderRed300}`;
            case 'EMERGENCIA':
                return `${styles.bgOrange100} ${styles.textOrange800} ${styles.borderOrange300}`;
            case 'URGENCIA':
                return `${styles.bgYellow100} ${styles.textYellow800} ${styles.borderYellow300}`;
            case 'URGENCIA_MENOR':
                return `${styles.bgBlue100} ${styles.textBlue800} ${styles.borderBlue300}`;
            case 'SIN_URGENCIA':
                return `${styles.bgGreen100} ${styles.textGreen800} ${styles.borderGreen300}`;
            default:
                return `${styles.bgGray100} ${styles.textGray800} ${styles.borderGray300}`;
        }
    };

    const getNivelEmergenciaIcono = (nivel) => {
        if (!nivel) return '⚪';

        switch (nivel.toUpperCase()) {
            case 'CRITICA': return '🔴';
            case 'EMERGENCIA': return '🟠';
            case 'URGENCIA': return '🟡';
            case 'URGENCIA_MENOR': return '🔵';
            case 'SIN_URGENCIA': return '🟢';
            default: return '⚪';
        }
    };

    const calcularTiempoEspera = (fechaIngreso) => {
        if (!fechaIngreso) return 'N/A';

        try {
            const ingreso = new Date(fechaIngreso);
            const ahora = new Date();
            const diffMinutos = Math.floor((ahora - ingreso) / (1000 * 60));

            if (diffMinutos < 1) return 'Recién llegado';
            if (diffMinutos < 60) return `${diffMinutos} min`;

            const horas = Math.floor(diffMinutos / 60);
            const minutos = diffMinutos % 60;
            return `${horas}h ${minutos}m`;
        } catch (error) {
            return 'N/A';
        }
    };

    const getEstadoFormateado = (estado) => {
        if (!estado) return 'N/A';

        switch (estado.toUpperCase()) {
            case 'PENDIENTE':
                return <span className={`${styles.badge} ${styles.badgePending}`}>PENDIENTE</span>;
            case 'EN_ATENCION':
                return <span className={`${styles.badge} ${styles.badgeEnAtencion}`}>EN ATENCIÓN</span>;
            case 'ATENDIDO':
                return <span className={`${styles.badge} ${styles.badgeAtendido}`}>ATENDIDO</span>;
            default:
                return <span className={`${styles.badge} ${styles.badgeDefault}`}>{estado}</span>;
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.maxW7xl}>
                {/* Header */}
                <div className={styles.header}>
                    <div className={styles.headerLeft}>
                        <h1 className={styles.title}>⏱️ Lista de Espera</h1>
                        <p className={styles.subtitle}>
                            Pacientes pendientes de atención médica -
                            <span className={styles.count}> {listaFiltrada.length} paciente(s)</span>
                        </p>
                    </div>

                    <div className={styles.headerRight}>
                        {/* Filtro por nivel de emergencia */}
                        <select
                            value={filtroNivel}
                            onChange={(e) => setFiltroNivel(e.target.value)}
                            className={styles.filterSelect}
                            disabled={loading}
                        >
                            <option value="TODOS">Todos los niveles</option>
                            <option value="CRITICA">Crítica</option>
                            <option value="EMERGENCIA">Emergencia</option>
                            <option value="URGENCIA">Urgencia</option>
                            <option value="URGENCIA_MENOR">Urgencia Menor</option>
                            <option value="SIN_URGENCIA">Sin Urgencia</option>
                        </select>

                        {/* Botones de acción */}
                        <div className={styles.actions}>
                            <button
                                onClick={() => cargarListaEspera(true)}
                                disabled={loading || refreshing}
                                className={styles.refreshButton}
                            >
                                {refreshing ? '🔄' : '🔁'} Actualizar
                            </button>

                            {isMedico && (
                                <button
                                    onClick={() => navigate('/atencion/reclamar')}
                                    className={styles.primaryButton}
                                >
                                    👨‍⚕️ Reclamar Paciente
                                </button>
                            )}

                            {isEnfermero && (
                                <button
                                    onClick={() => navigate('/urgencias/registrar')}
                                    className={styles.primaryButton}
                                >
                                    🚑 Nueva Urgencia
                                </button>
                            )}

                            <button
                                onClick={() => navigate('/')}
                                className={styles.secondaryButton}
                            >
                                Volver
                            </button>
                        </div>
                    </div>
                </div>

                {/* Mensajes */}
                {message.text && (
                    <div className={`${styles.message} ${
                        message.type === 'success'
                            ? styles.messageSuccess
                            : styles.messageError
                    }`}>
                        <span>{message.text}</span>
                        <button
                            onClick={() => setMessage({ type: '', text: '' })}
                            className={styles.messageClose}
                        >
                            ✕
                        </button>
                    </div>
                )}

                {/* Contenido principal */}
                <div className={styles.card}>
                    {loading ? (
                        // Estado de carga
                        <div className={styles.loadingState}>
                            <div className={styles.loadingSpinner}>⏳</div>
                            <p className={styles.loadingText}>Cargando lista de espera...</p>
                            <p className={styles.loadingSubtext}>Obteniendo información de pacientes</p>
                        </div>
                    ) : listaFiltrada.length === 0 ? (
                        // Lista vacía
                        <div className={styles.emptyState}>
                            <div className={styles.emptyIcon}>✅</div>
                            <h3 className={styles.emptyTitle}>
                                {filtroNivel === 'TODOS'
                                    ? 'No hay pacientes en espera'
                                    : `No hay pacientes con nivel ${filtroNivel}`
                                }
                            </h3>
                            <p className={styles.emptyDescription}>
                                {filtroNivel === 'TODOS'
                                    ? 'Todos los pacientes han sido atendidos'
                                    : 'Intente con otro filtro o verifique la lista completa'
                                }
                            </p>
                            {filtroNivel !== 'TODOS' && (
                                <button
                                    onClick={() => setFiltroNivel('TODOS')}
                                    className={styles.primaryButton}
                                >
                                    Ver Todos los Pacientes
                                </button>
                            )}
                        </div>
                    ) : (
                        // Tabla de pacientes
                        <div className={styles.tableContainer}>
                            <table className={styles.table}>
                                <thead>
                                <tr className={styles.tableHeader}>
                                    <th className={styles.tableHead}>
                                        Paciente
                                    </th>
                                    <th className={styles.tableHead}>
                                        CUIL
                                    </th>
                                    <th className={styles.tableHead}>
                                        Nivel Emergencia
                                    </th>
                                    <th className={styles.tableHead}>
                                        Estado
                                    </th>
                                    <th className={styles.tableHead}>
                                        Tiempo Espera
                                    </th>
                                    <th className={styles.tableHead}>
                                        Enfermera
                                    </th>
                                    <th className={styles.tableHead}>
                                        ID Ingreso
                                    </th>
                                </tr>
                                </thead>
                                <tbody className={styles.tableBody}>
                                {listaFiltrada.map((paciente, index) => (
                                    <tr
                                        key={paciente.id || index}
                                        className={styles.tableRow}
                                    >
                                        <td className={styles.tableCell}>
                                            <div className={styles.patientName}>
                                                {getPacienteValue(paciente, 'nombre')} {getPacienteValue(paciente, 'apellido')}
                                            </div>
                                        </td>
                                        <td className={styles.tableCell}>
                                            <span className={styles.cuil}>
                                                {getPacienteValue(paciente, 'cuil')}
                                            </span>
                                        </td>
                                        <td className={styles.tableCell}>
                                            <div className={styles.emergencyLevel}>
                                                <span className={styles.emergencyIcon}>
                                                    {getNivelEmergenciaIcono(getPacienteValue(paciente, 'nivelEmergencia'))}
                                                </span>
                                                <span className={`${styles.emergencyBadge} ${
                                                    getNivelEmergenciaColor(getPacienteValue(paciente, 'nivelEmergencia'))
                                                }`}>
                                                    {getPacienteValue(paciente, 'nivelEmergencia')}
                                                </span>
                                            </div>
                                        </td>
                                        <td className={styles.tableCell}>
                                            {getEstadoFormateado(paciente.estado)}
                                        </td>
                                        <td className={styles.tableCell}>
                                            <span className={styles.waitingTime}>
                                                {calcularTiempoEspera(paciente.fechaIngreso)}
                                            </span>
                                        </td>
                                        <td className={styles.tableCell}>
                                            {getPacienteValue(paciente, 'enfermera')}
                                        </td>
                                        <td className={styles.tableCell}>
                                            <span className={styles.ingresoId}>
                                                {paciente.id ? paciente.id.substring(0, 8) + '...' : 'N/A'}
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                    {/* Información del pie */}
                    {!loading && listaFiltrada.length > 0 && (
                        <div className={styles.footer}>
                            <div className={styles.footerContent}>
                                <div className={styles.footerText}>
                                    Mostrando <strong>{listaFiltrada.length}</strong> de <strong>{listaEspera.length}</strong> paciente(s)
                                    {filtroNivel !== 'TODOS' && ` filtrados por ${filtroNivel}`}
                                </div>
                                <div className={styles.legend}>
                                    <div className={styles.legendItem}>
                                        <span className={`${styles.legendDot} ${styles.critical}`}></span>
                                        <span>Crítica</span>
                                    </div>
                                    <div className={styles.legendItem}>
                                        <span className={`${styles.legendDot} ${styles.emergency}`}></span>
                                        <span>Emergencia</span>
                                    </div>
                                    <div className={styles.legendItem}>
                                        <span className={`${styles.legendDot} ${styles.urgent}`}></span>
                                        <span>Urgencia</span>
                                    </div>
                                    <div className={styles.legendItem}>
                                        <span className={`${styles.legendDot} ${styles.minor}`}></span>
                                        <span>Urgencia Menor</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ListaEspera;