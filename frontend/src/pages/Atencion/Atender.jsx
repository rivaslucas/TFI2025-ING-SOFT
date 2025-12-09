import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { atencionService } from '../../services/atencionService';
import { useAuth } from '../../context/AuthContext.jsx'; // Importar contexto de autenticación
import styles from './AtenderPaciente.module.css';

const Atender = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { user } = useAuth(); // Obtener usuario autenticado

    const [loading, setLoading] = useState(true); // Iniciar en true para mostrar loading
    const [message, setMessage] = useState({ type: '', text: '' });
    const [datosTriaje, setDatosTriaje] = useState(null);
    const [pacienteInfo, setPacienteInfo] = useState(null);

    // Obtener datos del estado de navegación
    const ingresoFromState = location.state?.ingreso;

    // Usar la matrícula del usuario autenticado
    const medicoMatricula = user?.matricula || '67890';

    const [formData, setFormData] = useState({
        idIngreso: '',
        medicoMatricula: medicoMatricula,
        informeMedico: ''
    });

    useEffect(() => {
        const cargarDatosPaciente = async () => {
            setLoading(true);
            setMessage({ type: '', text: '' });

            try {
                // CASO 1: Si tenemos datos del estado de navegación
                if (ingresoFromState?.id) {
                    await cargarDatosDesdeEstado(ingresoFromState);
                }
                // CASO 2: Si no tenemos datos del estado, obtener el paciente actual del médico
                else if (formData.medicoMatricula) {
                    await cargarPacienteActual();
                }
                // CASO 3: No hay datos disponibles
                else {
                    setLoading(false);
                }
            } catch (error) {
                console.error('Error cargando datos del paciente:', error);
                setMessage({
                    type: 'error',
                    text: '❌ Error al cargar datos del paciente'
                });
                setLoading(false);
            }
        };

        cargarDatosPaciente();
    }, [ingresoFromState, formData.medicoMatricula]);

    const cargarDatosDesdeEstado = async (ingresoData) => {
        // Configurar datos básicos del paciente desde el estado
        setPacienteInfo({
            id: ingresoData.id,
            nombre: ingresoData.nombre || ingresoData.pacienteNombre,
            apellido: ingresoData.apellido || ingresoData.pacienteApellido,
            cuil: ingresoData.cuil || ingresoData.pacienteCuil
        });

        setFormData(prev => ({
            ...prev,
            idIngreso: ingresoData.id
        }));

        // Verificar si ya vienen datos de triaje en el estado
        if (ingresoData.datosTriaje) {
            setDatosTriaje(ingresoData.datosTriaje);
            setLoading(false);
        } else {
            // Si no, intentar obtener datos completos del backend
            await cargarDatosCompletos(ingresoData.id);
        }
    };

    const cargarPacienteActual = async () => {
        try {
            console.log('🔍 Obteniendo paciente actual del médico:', formData.medicoMatricula);

            const result = await atencionService.obtenerPacienteActual(formData.medicoMatricula);

            if (result.success && result.data) {
                const pacienteData = result.data;

                // Configurar información del paciente
                setPacienteInfo({
                    id: pacienteData.id,
                    nombre: pacienteData.pacienteNombre,
                    apellido: pacienteData.pacienteApellido,
                    cuil: pacienteData.pacienteCuil
                });

                // Configurar datos de triaje desde la respuesta del backend
                const triajeData = {
                    temperatura: pacienteData.temperatura,
                    frecuenciaCardiaca: pacienteData.frecuenciaCardiaca,
                    frecuenciaRespiratoria: pacienteData.frecuenciaRespiratoria,
                    tensionSistolica: pacienteData.tensionSistolica,
                    tensionDiastolica: pacienteData.tensionDiastolica,
                    nivelEmergencia: pacienteData.nivelEmergencia,
                    informeEnfermeria: pacienteData.informeEnfermeria || pacienteData.informe || 'Sin observaciones',
                    fechaTriaje: pacienteData.fechaIngreso || new Date().toISOString(),
                    enfermera: pacienteData.enfermeraNombre || 'Enfermera no especificada'
                };

                setDatosTriaje(triajeData);
                setFormData(prev => ({
                    ...prev,
                    idIngreso: pacienteData.id
                }));

                console.log('✅ Datos de paciente cargados desde backend:', pacienteData);
                console.log('✅ Datos de triaje cargados:', triajeData);
            } else {
                setMessage({
                    type: 'error',
                    text: '❌ No hay paciente asignado actualmente. Por favor, reclame un paciente primero.'
                });
            }
        } catch (error) {
            console.error('Error cargando paciente actual:', error);
            setMessage({
                type: 'error',
                text: '❌ Error al cargar datos del paciente'
            });
        } finally {
            setLoading(false);
        }
    };

    const cargarDatosCompletos = async (idIngreso) => {
        try {
            console.log('📋 Obteniendo datos completos del ingreso:', idIngreso);

            // Usar el endpoint de ingreso completo (si está disponible)
            // O alternativamente, obtener paciente actual y verificar que coincida el ID
            const result = await atencionService.obtenerPacienteActual(formData.medicoMatricula);

            if (result.success && result.data && result.data.id === idIngreso) {
                const pacienteData = result.data;
                const triajeData = {
                    temperatura: pacienteData.temperatura,
                    frecuenciaCardiaca: pacienteData.frecuenciaCardiaca,
                    frecuenciaRespiratoria: pacienteData.frecuenciaRespiratoria,
                    tensionSistolica: pacienteData.tensionSistolica,
                    tensionDiastolica: pacienteData.tensionDiastolica,
                    nivelEmergencia: pacienteData.nivelEmergencia,
                    informeEnfermeria: pacienteData.informeEnfermeria || pacienteData.informe || 'Sin observaciones',
                    fechaTriaje: pacienteData.fechaIngreso || new Date().toISOString(),
                    enfermera: pacienteData.enfermeraNombre || 'Enfermera no especificada'
                };

                setDatosTriaje(triajeData);
                console.log('✅ Datos de triaje obtenidos del backend:', triajeData);
            }
        } catch (error) {
            console.error('Error cargando datos completos:', error);
            // En caso de error, mostrar mensaje pero continuar
            setMessage({
                type: 'warning',
                text: '⚠️ No se pudieron cargar todos los datos de triaje. Continúe con la atención.'
            });
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.idIngreso || !formData.informeMedico.trim()) {
            setMessage({ type: 'error', text: '❌ Complete el informe médico para continuar' });
            return;
        }

        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            const result = await atencionService.registrarAtencion(formData.idIngreso, {
                medicoMatricula: formData.medicoMatricula,
                informeMedico: formData.informeMedico
            });

            if (result.success) {
                setMessage({
                    type: 'success',
                    text: '✅ Atención médica registrada exitosamente'
                });

                setTimeout(() => {
                    navigate('/');
                }, 2000);
            } else {
                setMessage({
                    type: 'error',
                    text: `❌ ${result.error}`
                });
            }
        } catch (err) {
            setMessage({
                type: 'error',
                text: '❌ Error inesperado al registrar atención'
            });
        } finally {
            setLoading(false);
        }
    };

    const getNivelEmergenciaColor = (nivel) => {
        switch (nivel) {
            case 'CRITICA': return `${styles.bgRed100} ${styles.textRed800} ${styles.borderRed300}`;
            case 'EMERGENCIA': return `${styles.bgOrange100} ${styles.textOrange800} ${styles.borderOrange300}`;
            case 'URGENCIA': return `${styles.bgYellow100} ${styles.textYellow800} ${styles.borderYellow300}`;
            case 'URGENCIA_MENOR': return `${styles.bgBlue100} ${styles.textBlue800} ${styles.borderBlue300}`;
            default: return `${styles.bgGray100} ${styles.textGray800} ${styles.borderGray300}`;
        }
    };

    const evaluarSignoVital = (valor, tipo) => {
        if (!valor && valor !== 0) return { estado: 'normal', texto: 'No registrado' };

        switch (tipo) {
            case 'temperatura':
                if (valor < 36) return { estado: 'bajo', texto: 'Hipotermia' };
                if (valor > 37.5) return { estado: 'alto', texto: 'Fiebre' };
                return { estado: 'normal', texto: 'Normal' };

            case 'frecuenciaCardiaca':
                if (valor < 60) return { estado: 'bajo', texto: 'Bradicardia' };
                if (valor > 100) return { estado: 'alto', texto: 'Taquicardia' };
                return { estado: 'normal', texto: 'Normal' };

            case 'frecuenciaRespiratoria':
                if (valor < 12) return { estado: 'bajo', texto: 'Bradipnea' };
                if (valor > 20) return { estado: 'alto', texto: 'Taquipnea' };
                return { estado: 'normal', texto: 'Normal' };

            case 'tensionArterial':
                if (valor > 140) return { estado: 'alto', texto: 'Hipertensión' };
                if (valor < 90) return { estado: 'bajo', texto: 'Hipotensión' };
                return { estado: 'normal', texto: 'Normal' };

            default:
                return { estado: 'normal', texto: 'Normal' };
        }
    };

    // Mostrar estado de carga
    if (loading) {
        return (
            <div className={`${styles.minHScreen} ${styles.bgGray50} flex items-center justify-center p-6`}>
                <div className={`${styles.card} p-8 text-center max-w-md`}>
                    <div className={styles.spinner} style={{ margin: '0 auto 20px' }}></div>
                    <h1 className="text-2xl font-bold text-gray-800 mb-4">Cargando datos del paciente...</h1>
                    <p className="text-gray-600">
                        Obteniendo información de triaje y signos vitales
                    </p>
                </div>
            </div>
        );
    }

    // Verificar si tenemos información del paciente
    const paciente = pacienteInfo || ingresoFromState;

    if (!paciente) {
        return (
            <div className={`${styles.minHScreen} ${styles.bgGray50} flex items-center justify-center p-6`}>
                <div className={`${styles.card} p-8 text-center max-w-md`}>
                    <div className="text-6xl mb-4">❌</div>
                    <h1 className="text-2xl font-bold text-gray-800 mb-4">Paciente No Encontrado</h1>
                    <p className="text-gray-600 mb-6">
                        {message.text || 'No se encontró información del paciente. Por favor, reclame un paciente primero.'}
                    </p>
                    <button
                        onClick={() => navigate('/atencion/reclamar')}
                        className={styles.btnPrimary}
                    >
                        👨‍⚕️ Reclamar Paciente
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className={`${styles.minHScreen} ${styles.bgGradient} p-6`}>
            <div className={styles.maxW7xl}>
                {/* Header */}
                <div className={styles.textCenter}>
                    <h1 className={styles.title}>🩺 Atención Médica</h1>
                    <p className={styles.subtitle}>
                        Registro de diagnóstico y tratamiento del paciente
                    </p>
                </div>

                {/* Mostrar mensajes de error o advertencia */}
                {message.text && message.type !== 'success' && (
                    <div className={`${styles.message} ${
                        message.type === 'error' ? styles.messageError : styles.messageWarning
                    } mb-6`}>
                        <div className={styles.messageContent}>
                            <span className={styles.messageIcon}>
                                {message.type === 'error' ? '❌' : '⚠️'}
                            </span>
                            <span className={styles.messageText}>{message.text}</span>
                        </div>
                        <button
                            onClick={() => setMessage({ type: '', text: '' })}
                            className={styles.messageClose}
                        >
                            ✕
                        </button>
                    </div>
                )}

                <div className={styles.grid}>
                    {/* Columna 1: Información del Paciente y Triaje */}
                    <div className={styles.col1}>
                        {/* Tarjeta de Información del Paciente */}
                        <div className={`${styles.card} ${styles.patientCard}`}>
                            <h2 className={styles.cardTitle}>
                                <span className={styles.iconBlue}>
                                    👤
                                </span>
                                Información del Paciente
                            </h2>

                            <div className={styles.spaceY4}>
                                <div className={styles.grid2}>
                                    <div>
                                        <label className={styles.label}>
                                            Nombre
                                        </label>
                                        <div className={styles.dataField}>
                                            {paciente.nombre} {paciente.apellido}
                                        </div>
                                    </div>
                                    <div>
                                        <label className={styles.label}>
                                            CUIL
                                        </label>
                                        <div className={styles.dataFieldMono}>
                                            {paciente.cuil}
                                        </div>
                                    </div>
                                </div>

                                <div className={styles.grid2}>
                                    <div>
                                        <label className={styles.label}>
                                            ID Ingreso
                                        </label>
                                        <div className={styles.dataFieldId}>
                                            {formData.idIngreso}
                                        </div>
                                    </div>
                                    <div>
                                        <label className={styles.label}>
                                            Matrícula Médico
                                        </label>
                                        <div className={styles.dataFieldMono}>
                                            {formData.medicoMatricula}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Tarjeta de Triaje */}
                        {datosTriaje && (
                            <div className={`${styles.card} ${styles.triageCard}`}>
                                <h2 className={styles.cardTitle}>
                                  <span className={styles.iconOrange}>
                                    📊
                                  </span>
                                    Datos de Triaje
                                </h2>

                                {/* Nivel de Emergencia */}
                                <div className={styles.mb4}>
                                    <label className={styles.label}>
                                        Nivel de Emergencia
                                    </label>
                                    <div className={`${styles.emergencyBadge} ${getNivelEmergenciaColor(datosTriaje.nivelEmergencia)}`}>
                                        {datosTriaje.nivelEmergencia}
                                    </div>
                                </div>

                                {/* Signos Vitales */}
                                <div className={styles.spaceY3}>
                                    <h3 className={styles.sectionTitle}>Signos Vitales</h3>

                                    <div className={styles.vitalsGrid}>
                                        {/* Temperatura */}
                                        <div className={styles.vitalCard}>
                                            <div className={styles.vitalLabel}>Temperatura</div>
                                            <div className={styles.vitalValue}>
                                                {datosTriaje.temperatura != null ? `${datosTriaje.temperatura}°C` : 'No registrado'}
                                            </div>
                                            <div className={`${styles.vitalStatus} ${
                                                evaluarSignoVital(datosTriaje.temperatura, 'temperatura').estado === 'alto' ? styles.textRed600 :
                                                    evaluarSignoVital(datosTriaje.temperatura, 'temperatura').estado === 'bajo' ? styles.textBlue600 :
                                                        styles.textGreen600
                                            }`}>
                                                {evaluarSignoVital(datosTriaje.temperatura, 'temperatura').texto}
                                            </div>
                                        </div>

                                        {/* Frecuencia Cardíaca */}
                                        <div className={styles.vitalCard}>
                                            <div className={styles.vitalLabel}>FC</div>
                                            <div className={styles.vitalValue}>
                                                {datosTriaje.frecuenciaCardiaca != null ? `${datosTriaje.frecuenciaCardiaca} lpm` : 'No registrado'}
                                            </div>
                                            <div className={`${styles.vitalStatus} ${
                                                evaluarSignoVital(datosTriaje.frecuenciaCardiaca, 'frecuenciaCardiaca').estado === 'alto' ? styles.textRed600 :
                                                    evaluarSignoVital(datosTriaje.frecuenciaCardiaca, 'frecuenciaCardiaca').estado === 'bajo' ? styles.textBlue600 :
                                                        styles.textGreen600
                                            }`}>
                                                {evaluarSignoVital(datosTriaje.frecuenciaCardiaca, 'frecuenciaCardiaca').texto}
                                            </div>
                                        </div>

                                        {/* Frecuencia Respiratoria */}
                                        <div className={styles.vitalCard}>
                                            <div className={styles.vitalLabel}>FR</div>
                                            <div className={styles.vitalValue}>
                                                {datosTriaje.frecuenciaRespiratoria != null ? `${datosTriaje.frecuenciaRespiratoria} rpm` : 'No registrado'}
                                            </div>
                                            <div className={`${styles.vitalStatus} ${
                                                evaluarSignoVital(datosTriaje.frecuenciaRespiratoria, 'frecuenciaRespiratoria').estado === 'alto' ? styles.textRed600 :
                                                    evaluarSignoVital(datosTriaje.frecuenciaRespiratoria, 'frecuenciaRespiratoria').estado === 'bajo' ? styles.textBlue600 :
                                                        styles.textGreen600
                                            }`}>
                                                {evaluarSignoVital(datosTriaje.frecuenciaRespiratoria, 'frecuenciaRespiratoria').texto}
                                            </div>
                                        </div>

                                        {/* Tensión Arterial */}
                                        <div className={styles.vitalCard}>
                                            <div className={styles.vitalLabel}>Tensión</div>
                                            <div className={styles.vitalValue}>
                                                {datosTriaje.tensionSistolica != null && datosTriaje.tensionDiastolica != null
                                                    ? `${datosTriaje.tensionSistolica}/${datosTriaje.tensionDiastolica} mmHg`
                                                    : 'No registrado'}
                                            </div>
                                            <div className={`${styles.vitalStatus} ${
                                                evaluarSignoVital(datosTriaje.tensionSistolica, 'tensionArterial').estado === 'alto' ? styles.textRed600 :
                                                    evaluarSignoVital(datosTriaje.tensionSistolica, 'tensionArterial').estado === 'bajo' ? styles.textBlue600 :
                                                        styles.textGreen600
                                            }`}>
                                                {evaluarSignoVital(datosTriaje.tensionSistolica, 'tensionArterial').texto}
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                {/* Informe de Enfermería */}
                                <div className={styles.mt4}>
                                    <label className={styles.label}>
                                        Observaciones de Enfermería
                                    </label>
                                    <div className={styles.nurseNotes}>
                                        "{datosTriaje.informeEnfermeria || 'Sin observaciones registradas'}"
                                    </div>
                                    <div className={styles.nurseSignature}>
                                        Por: {datosTriaje.enfermera}
                                    </div>
                                </div>
                            </div>
                        )}

                        {/* Mensaje si no hay datos de triaje */}
                        {!datosTriaje && (
                            <div className={`${styles.card} ${styles.triageCard}`}>
                                <h2 className={styles.cardTitle}>
                                    <span className={styles.iconOrange}>
                                        📊
                                    </span>
                                    Datos de Triaje
                                </h2>
                                <div className="p-4 text-center text-gray-500">
                                    <p>No se pudieron cargar los datos de triaje</p>
                                    <p className="text-sm mt-2">Puede continuar con la atención médica</p>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Columna 2: Formulario de Atención Médica */}
                    <div className={styles.col2}>
                        <div className={`${styles.card} ${styles.formCard}`}>
                            <h2 className={styles.cardTitle}>
                                <span className={styles.iconGreen}>
                                  📝
                                </span>
                                Registro de Atención Médica
                            </h2>

                            {/* Mostrar mensajes de éxito dentro del formulario */}
                            {message.text && message.type === 'success' && (
                                <div className={`${styles.message} ${styles.messageSuccess} mb-4`}>
                                    <div className={styles.messageContent}>
                                        <span className={styles.messageIcon}>
                                            ✅
                                        </span>
                                        <span className={styles.messageText}>{message.text}</span>
                                    </div>
                                    <button
                                        onClick={() => setMessage({ type: '', text: '' })}
                                        className={styles.messageClose}
                                    >
                                        ✕
                                    </button>
                                </div>
                            )}

                            <form onSubmit={handleSubmit} className={styles.form}>
                                {/* Informe Médico */}
                                <div>
                                    <label className={styles.formLabel}>
                                        <span className={styles.required}>*</span> Informe Médico - Diagnóstico y Tratamiento
                                    </label>
                                    <textarea
                                        value={formData.informeMedico}
                                        onChange={(e) => setFormData(prev => ({ ...prev, informeMedico: e.target.value }))}
                                        rows="12"
                                        className={styles.textarea}
                                        placeholder="Describa el diagnóstico, hallazgos clínicos, tratamiento indicado, medicamentos recetados, estudios complementarios solicitados, recomendaciones y seguimiento..."
                                        required
                                        disabled={loading || message.type === 'success'}
                                    />
                                    <div className={styles.helperText}>
                                        Incluya: Diagnóstico, Tratamiento, Medicamentos, Estudios, Recomendaciones
                                    </div>
                                </div>

                                {/* Botones de Acción */}
                                <div className={styles.actionButtons}>
                                    <button
                                        type="submit"
                                        disabled={loading || message.type === 'success'}
                                        className={styles.submitButton}
                                    >
                                        {loading ? (
                                            <>
                                                <div className={styles.spinner}></div>
                                                <span>Registrando atención...</span>
                                            </>
                                        ) : (
                                            <>
                                                <span className="text-lg">✅</span>
                                                <span>Finalizar Atención</span>
                                            </>
                                        )}
                                    </button>

                                    <button
                                        type="button"
                                        onClick={() => navigate('/atencion/reclamar')}
                                        className={styles.cancelButton}
                                        disabled={loading || message.type === 'success'}
                                    >
                                        <span>↶</span>
                                        <span>Volver</span>
                                    </button>
                                </div>
                            </form>
                        </div>

                        {/* Información de Ayuda */}
                        <div className={styles.helpCard}>
                            <h4 className={styles.helpTitle}>
                                <span>💡</span>
                                Guía para el Informe Médico
                            </h4>
                            <ul className={styles.helpList}>
                                <li>• <strong>Diagnóstico:</strong> Enfermedad o condición principal</li>
                                <li>• <strong>Hallazgos:</strong> Signos y síntomas relevantes</li>
                                <li>• <strong>Tratamiento:</strong> Procedimientos realizados</li>
                                <li>• <strong>Medicamentos:</strong> Fármacos recetados con dosis</li>
                                <li>• <strong>Estudios:</strong> Análisis o imágenes solicitadas</li>
                                <li>• <strong>Recomendaciones:</strong> Indicaciones para el paciente</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Atender;