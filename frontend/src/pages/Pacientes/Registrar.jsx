import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { pacientesService } from '../../services/pacientesService';
import { obrasSocialesService } from '../../services/obrasSocialesService';
import styles from './RegistrarPaciente.module.css';

const RegistrarPaciente = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [loadingObrasSociales, setLoadingObrasSociales] = useState(true);
    const [message, setMessage] = useState({ type: '', text: '' });
    const [errors, setErrors] = useState({});
    const [obrasSociales, setObrasSociales] = useState([]);

    const [formData, setFormData] = useState({
        cuil: '',
        nombre: '',
        apellido: '',
        direccion: {
            calle: '',
            numero: '',
            localidad: ''
        },
        obraSocialNombre: '',
        numeroAfiliado: ''
    });

    const obrasSocialesPorDefecto = [
        { nombre: 'OSDE' },
        { nombre: 'SWISS MEDICAL' },
        { nombre: 'GALENO' },
        { nombre: 'OMINT' },
        { nombre: 'MEDICUS' },
        { nombre: 'SANCOR SALUD' },
        { nombre: 'OSPACA' },
        { nombre: 'OSPEDYC' },
        { nombre: 'OSECAC' },
        { nombre: 'OSPERYH' }
    ];

    useEffect(() => {
        cargarObrasSociales();
    }, []);

    const cargarObrasSociales = async () => {
        try {
            setLoadingObrasSociales(true);
            console.log('🔄 Cargando obras sociales...');

            const obrasSocialesData = await obrasSocialesService.obtenerObrasSociales();
            console.log('🏥 Obras sociales cargadas del backend:', obrasSocialesData);

            if (Array.isArray(obrasSocialesData) && obrasSocialesData.length > 0) {
                setObrasSociales(obrasSocialesData);
                console.log(`✅ Se cargaron ${obrasSocialesData.length} obras sociales del backend`);
            } else {
                console.warn('⚠️ No se obtuvieron obras sociales del backend, usando lista por defecto');
                setObrasSociales(obrasSocialesPorDefecto);
            }
        } catch (error) {
            console.error('❌ Error cargando obras sociales:', error);
            console.log('🔄 Usando lista de obras sociales por defecto');

            setObrasSociales(obrasSocialesPorDefecto);

            if (error.response && error.response.status !== 404) {
                setMessage({
                    type: 'warning',
                    text: '⚠️ No se pudieron cargar las obras sociales. Usando lista predefinida.'
                });

                setTimeout(() => {
                    setMessage({ type: '', text: '' });
                }, 5000);
            }
        } finally {
            setLoadingObrasSociales(false);
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (!formData.cuil) {
            newErrors.cuil = 'El CUIL es obligatorio';
        } else if (!/^\d{2}-\d{8}-\d{1}$/.test(formData.cuil)) {
            newErrors.cuil = 'Formato de CUIL inválido (Ej: 20-12345678-9)';
        }

        if (!formData.nombre.trim()) {
            newErrors.nombre = 'El nombre es obligatorio';
        }

        if (!formData.apellido.trim()) {
            newErrors.apellido = 'El apellido es obligatorio';
        }

        if (!formData.direccion.calle.trim()) {
            newErrors['direccion.calle'] = 'La calle es obligatoria';
        }
        if (!formData.direccion.numero) {
            newErrors['direccion.numero'] = 'El número es obligatorio';
        }
        if (!formData.direccion.localidad.trim()) {
            newErrors['direccion.localidad'] = 'La localidad es obligatoria';
        }

        if (formData.numeroAfiliado && !formData.obraSocialNombre) {
            newErrors.obraSocialNombre = 'Debe seleccionar una obra social si ingresa número de afiliado';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }

        if (name.includes('.')) {
            const [parent, child] = name.split('.');
            setFormData(prev => ({
                ...prev,
                [parent]: {
                    ...prev[parent],
                    [child]: name === 'direccion.numero' ? parseInt(value) || 0 : value
                }
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: value
            }));
        }

        if (name === 'obraSocialNombre' && !value) {
            setFormData(prev => ({
                ...prev,
                numeroAfiliado: ''
            }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        console.log('🔄 Iniciando submit...');

        if (!validateForm()) {
            setMessage({ type: 'error', text: '❌ Por favor, corrija los errores en el formulario' });
            return;
        }

        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            console.log('📤 Enviando datos:', formData);

            const result = await pacientesService.registrarPaciente(formData);

            console.log('📨 Resultado COMPLETO recibido:', result);

            if (result.success) {
                console.log('✅ Éxito - Mostrando mensaje:', result.message);

                setMessage({
                    type: 'success',
                    text: `${result.message}`
                });

                setFormData({
                    cuil: '',
                    nombre: '',
                    apellido: '',
                    direccion: {
                        calle: '',
                        numero: '',
                        localidad: ''
                    },
                    obraSocialNombre: '',
                    numeroAfiliado: ''
                });

                setErrors({});

                setTimeout(() => {
                    setMessage({ type: '', text: '' });
                }, 4000);
            } else {
                console.log('❌ Error - Mostrando mensaje:', result.error);
                setMessage({
                    type: 'error',
                    text: `❌ ${result.error}`
                });
            }
        } catch (err) {
            console.error('💥 Error inesperado en handleSubmit:', err);
            setMessage({
                type: 'error',
                text: '❌ Error inesperado al registrar paciente'
            });
        } finally {
            console.log('🏁 Submit finalizado');
            setLoading(false);
        }
    };

    const getFieldError = (fieldName) => {
        return errors[fieldName];
    };

    return (
        <div className={styles.container}>
            <div className={styles.maxW4xl}>
                <div className={styles.header}>
                    <div>
                        <h1 className={styles.title}>👥 Registrar Paciente</h1>
                        <p className={styles.subtitle}>Complete los datos del nuevo paciente</p>
                    </div>
                    <button
                        onClick={() => navigate('/')}
                        className={styles.backButton}
                        disabled={loading}
                    >
                        Volver al Dashboard
                    </button>
                </div>

                <div className={styles.card}>
                    {/* Mensajes de éxito/error/aviso */}
                    {message.text && (
                        <div className={`${styles.message} ${
                            message.type === 'success'
                                ? styles.messageSuccess
                                : message.type === 'warning'
                                    ? styles.messageWarning
                                    : styles.messageError
                        }`}>
                            <div className={styles.messageContent}>
                                {message.type === 'success' ? (
                                    <span className={styles.messageIcon}>✅</span>
                                ) : message.type === 'warning' ? (
                                    <span className={styles.messageIcon}>⚠️</span>
                                ) : (
                                    <span className={styles.messageIcon}>❌</span>
                                )}
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

                    <form onSubmit={handleSubmit} className={styles.form}>
                        {/* Información Personal */}
                        <div className={styles.grid}>
                            <div>
                                <label className={styles.label}>
                                    CUIL *
                                </label>
                                <input
                                    type="text"
                                    name="cuil"
                                    value={formData.cuil}
                                    onChange={handleChange}
                                    className={`${styles.input} ${getFieldError('cuil') ? styles.inputError : ''}`}
                                    placeholder="20-12345678-9"
                                    disabled={loading}
                                />
                                {getFieldError('cuil') && (
                                    <p className={styles.errorText}>{getFieldError('cuil')}</p>
                                )}
                            </div>

                            <div>
                                <label className={styles.label}>
                                    Nombre *
                                </label>
                                <input
                                    type="text"
                                    name="nombre"
                                    value={formData.nombre}
                                    onChange={handleChange}
                                    className={`${styles.input} ${getFieldError('nombre') ? styles.inputError : ''}`}
                                    placeholder="Juan"
                                    disabled={loading}
                                />
                                {getFieldError('nombre') && (
                                    <p className={styles.errorText}>{getFieldError('nombre')}</p>
                                )}
                            </div>

                            <div>
                                <label className={styles.label}>
                                    Apellido *
                                </label>
                                <input
                                    type="text"
                                    name="apellido"
                                    value={formData.apellido}
                                    onChange={handleChange}
                                    className={`${styles.input} ${getFieldError('apellido') ? styles.inputError : ''}`}
                                    placeholder="Perez"
                                    disabled={loading}
                                />
                                {getFieldError('apellido') && (
                                    <p className={styles.errorText}>{getFieldError('apellido')}</p>
                                )}
                            </div>
                        </div>

                        {/* Dirección */}
                        <div className={styles.section}>
                            <h3 className={styles.sectionTitle}>Dirección</h3>
                            <div className={styles.grid}>
                                <div>
                                    <label className={styles.label}>
                                        Calle *
                                    </label>
                                    <input
                                        type="text"
                                        name="direccion.calle"
                                        value={formData.direccion.calle}
                                        onChange={handleChange}
                                        className={`${styles.input} ${getFieldError('direccion.calle') ? styles.inputError : ''}`}
                                        placeholder="Av. Siempre Viva"
                                        disabled={loading}
                                    />
                                    {getFieldError('direccion.calle') && (
                                        <p className={styles.errorText}>{getFieldError('direccion.calle')}</p>
                                    )}
                                </div>

                                <div>
                                    <label className={styles.label}>
                                        Número *
                                    </label>
                                    <input
                                        type="number"
                                        name="direccion.numero"
                                        value={formData.direccion.numero}
                                        onChange={handleChange}
                                        className={`${styles.input} ${getFieldError('direccion.numero') ? styles.inputError : ''}`}
                                        placeholder="123"
                                        disabled={loading}
                                    />
                                    {getFieldError('direccion.numero') && (
                                        <p className={styles.errorText}>{getFieldError('direccion.numero')}</p>
                                    )}
                                </div>

                                <div>
                                    <label className={styles.label}>
                                        Localidad *
                                    </label>
                                    <input
                                        type="text"
                                        name="direccion.localidad"
                                        value={formData.direccion.localidad}
                                        onChange={handleChange}
                                        className={`${styles.input} ${getFieldError('direccion.localidad') ? styles.inputError : ''}`}
                                        placeholder="Springfield"
                                        disabled={loading}
                                    />
                                    {getFieldError('direccion.localidad') && (
                                        <p className={styles.errorText}>{getFieldError('direccion.localidad')}</p>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Obra Social (Opcional) */}
                        <div className={styles.section}>
                            <h3 className={styles.sectionTitle}>Obra Social (Opcional)</h3>
                            <div className={styles.grid2}>
                                <div>
                                    <label className={styles.label}>
                                        Obra Social
                                    </label>
                                    <select
                                        name="obraSocialNombre"
                                        value={formData.obraSocialNombre}
                                        onChange={handleChange}
                                        className={`${styles.input} ${getFieldError('obraSocialNombre') ? styles.inputError : ''}`}
                                        disabled={loading || loadingObrasSociales}
                                    >
                                        <option value="">Seleccione una obra social</option>
                                        {obrasSociales.map((obraSocial, index) => (
                                            <option key={index} value={obraSocial.nombre}>
                                                {obraSocial.nombre}
                                            </option>
                                        ))}
                                    </select>
                                    {loadingObrasSociales && (
                                        <p className={styles.loadingText}>🔄 Cargando obras sociales...</p>
                                    )}
                                    {!loadingObrasSociales && obrasSociales.length > 0 && (
                                        <p className={styles.successText}>
                                            ✅ {obrasSociales.length} obras sociales disponibles
                                        </p>
                                    )}
                                    {getFieldError('obraSocialNombre') && (
                                        <p className={styles.errorText}>{getFieldError('obraSocialNombre')}</p>
                                    )}
                                </div>

                                <div>
                                    <label className={styles.label}>
                                        Número de Afiliado
                                    </label>
                                    <input
                                        type="text"
                                        name="numeroAfiliado"
                                        value={formData.numeroAfiliado}
                                        onChange={handleChange}
                                        className={styles.input}
                                        placeholder="Número de afiliado"
                                        disabled={loading || !formData.obraSocialNombre}
                                    />
                                    {!formData.obraSocialNombre && (
                                        <p className={styles.helperText}>
                                            Seleccione una obra social primero
                                        </p>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className={styles.actions}>
                            <button
                                type="submit"
                                disabled={loading}
                                className={styles.submitButton}
                            >
                                {loading ? (
                                    <>
                                        <span className={styles.spinner}></span>
                                        Registrando...
                                    </>
                                ) : (
                                    <>
                                        <span className={styles.buttonIcon}>✅</span>
                                        Registrar Paciente
                                    </>
                                )}
                            </button>

                            <button
                                type="button"
                                onClick={() => navigate('/')}
                                className={styles.cancelButton}
                                disabled={loading}
                            >
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default RegistrarPaciente;