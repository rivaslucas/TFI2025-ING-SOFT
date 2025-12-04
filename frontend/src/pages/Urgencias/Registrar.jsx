import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { urgenciasService } from '../../services/urgenciasService';
import styles from './RegistrarUrgencia.module.css';

const RegistrarUrgencia = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    const [formData, setFormData] = useState({
        cuilPaciente: '',
        enfermeraNombre: '',
        enfermeraApellido: '',
        informe: '',
        nivelEmergencia: 'EMERGENCIA',
        temperatura: 0,
        frecuenciaCardiaca: 0,
        frecuenciaRespiratoria: 0,
        tensionSistolica: 0,
        tensionDiastolica: 0
    });

    const handleChange = (e) => {
        const { name, value, type } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'number' ? parseFloat(value) : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            const result = await urgenciasService.registrarUrgencia(formData);

            if (result.success) {
                setMessage({
                    type: 'success',
                    text: result.message || '✅ Urgencia registrada exitosamente. Paciente agregado a la lista de espera.'
                });

                setTimeout(() => {
                    setMessage({ type: '', text: '' });
                }, 4000);

                setFormData(prev => ({
                    ...prev,
                    cuilPaciente: '',
                    informe: '',
                    temperatura: 36.5,
                    frecuenciaCardiaca: 80,
                    frecuenciaRespiratoria: 16,
                    tensionSistolica: 120,
                    tensionDiastolica: 80
                }));
            } else {
                setMessage({
                    type: 'error',
                    text: `❌ ${result.error}`
                });

                setTimeout(() => {
                    setMessage({ type: '', text: '' });
                }, 5000);
            }
        } catch (err) {
            setMessage({
                type: 'error',
                text: '❌ Error inesperado al registrar urgencia'
            });

            setTimeout(() => {
                setMessage({ type: '', text: '' });
            }, 5000);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.maxW4xl}>
                <div className={styles.header}>
                    <div>
                        <h1 className={styles.title}>🚑 Registrar Urgencia</h1>
                        <p className={styles.subtitle}>Registre el ingreso de un paciente por urgencia</p>
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
                    {/* Mensaje único que maneja éxito y error */}
                    {message.text && (
                        <div className={`${styles.message} ${
                            message.type === 'success'
                                ? styles.messageSuccess
                                : styles.messageError
                        }`}>
                            <div className={styles.messageContent}>
                                {message.type === 'success' ? (
                                    <span className={styles.messageIcon}>✅</span>
                                ) : (
                                    <span className={styles.messageIcon}>❌</span>
                                )}
                                <span>{message.text}</span>
                            </div>
                            <button
                                onClick={() => setMessage({ type: '', text: '' })}
                                className={styles.messageClose}
                                title="Cerrar mensaje"
                            >
                                ✕
                            </button>
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className={styles.form}>
                        {/* Datos del Paciente y Enfermera */}
                        <div className={styles.grid}>
                            <div>
                                <label className={styles.label}>
                                    CUIL Paciente *
                                </label>
                                <input
                                    type="text"
                                    name="cuilPaciente"
                                    value={formData.cuilPaciente}
                                    onChange={handleChange}
                                    className={styles.input}
                                    required
                                    placeholder=""
                                    disabled={loading}
                                />
                            </div>

                            <div>
                                <label className={styles.label}>
                                    Enfermera Nombre *
                                </label>
                                <input
                                    type="text"
                                    name="enfermeraNombre"
                                    value={formData.enfermeraNombre}
                                    onChange={handleChange}
                                    className={styles.input}
                                    required
                                    disabled={loading}
                                />
                            </div>

                            <div>
                                <label className={styles.label}>
                                    Enfermera Apellido *
                                </label>
                                <input
                                    type="text"
                                    name="enfermeraApellido"
                                    value={formData.enfermeraApellido}
                                    onChange={handleChange}
                                    className={styles.input}
                                    required
                                    disabled={loading}
                                />
                            </div>
                        </div>

                        {/* Informe y Nivel de Emergencia */}
                        <div className={styles.grid2}>
                            <div>
                                <label className={styles.label}>
                                    Informe de Urgencia *
                                </label>
                                <textarea
                                    name="informe"
                                    value={formData.informe}
                                    onChange={handleChange}
                                    rows="3"
                                    className={styles.textarea}
                                    required
                                    disabled={loading}
                                />
                            </div>

                            <div>
                                <label className={styles.label}>
                                    Nivel de Emergencia *
                                </label>
                                <select
                                    name="nivelEmergencia"
                                    value={formData.nivelEmergencia}
                                    onChange={handleChange}
                                    className={styles.input}
                                    required
                                    disabled={loading}
                                >
                                    <option value="CRITICA">Crítica</option>
                                    <option value="EMERGENCIA">Emergencia</option>
                                    <option value="URGENCIA">Urgencia</option>
                                    <option value="URGENCIA_MENOR">Urgencia Menor</option>
                                    <option value="SIN_URGENCIA">Sin Urgencia</option>
                                </select>
                            </div>
                        </div>

                        {/* Signos Vitales */}
                        <div className={styles.section}>
                            <h3 className={styles.sectionTitle}>Signos Vitales</h3>
                            <div className={styles.vitalsGrid}>
                                <div className={styles.vitalItem}>
                                    <label className={styles.label}>
                                        Temperatura (°C)
                                    </label>
                                    <input
                                        type="number"
                                        step="0.1"
                                        name="temperatura"
                                        value={formData.temperatura}
                                        onChange={handleChange}
                                        className={styles.input}
                                        required
                                        disabled={loading}
                                    />
                                </div>

                                <div className={styles.vitalItem}>
                                    <label className={styles.label}>
                                        Frec. Cardíaca
                                    </label>
                                    <input
                                        type="number"
                                        name="frecuenciaCardiaca"
                                        value={formData.frecuenciaCardiaca}
                                        onChange={handleChange}
                                        className={styles.input}
                                        required
                                        disabled={loading}
                                    />
                                </div>

                                <div className={styles.vitalItem}>
                                    <label className={styles.label}>
                                        Frec. Respiratoria
                                    </label>
                                    <input
                                        type="number"
                                        name="frecuenciaRespiratoria"
                                        value={formData.frecuenciaRespiratoria}
                                        onChange={handleChange}
                                        className={styles.input}
                                        required
                                        disabled={loading}
                                    />
                                </div>

                                <div className={styles.vitalItem}>
                                    <label className={styles.label}>
                                        Tensión Sistólica
                                    </label>
                                    <input
                                        type="number"
                                        name="tensionSistolica"
                                        value={formData.tensionSistolica}
                                        onChange={handleChange}
                                        className={styles.input}
                                        required
                                        disabled={loading}
                                    />
                                </div>

                                <div className={styles.vitalItem}>
                                    <label className={styles.label}>
                                        Tensión Diastólica
                                    </label>
                                    <input
                                        type="number"
                                        name="tensionDiastolica"
                                        value={formData.tensionDiastolica}
                                        onChange={handleChange}
                                        className={styles.input}
                                        required
                                        disabled={loading}
                                    />
                                </div>
                            </div>
                        </div>

                        <div className={styles.actions}>
                            <button
                                type="submit"
                                disabled={loading}
                                className={styles.submitButton}
                            >
                                {loading ? '⏳ Registrando...' : '✅ Registrar Urgencia'}
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

export default RegistrarUrgencia;