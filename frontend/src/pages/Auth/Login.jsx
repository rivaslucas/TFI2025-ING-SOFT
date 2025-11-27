import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import styles from './Login.module.css';

const Login = () => {
    const [email, setEmail] = useState('medico@hospital.com');
    const [password, setPassword] = useState('password123');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const result = await login(email, password);

            if (result.success) {
                navigate('/');
            } else {
                setError(result.error || 'Error al iniciar sesión');
            }
        } catch (err) {
            setError(err.message || 'Error de conexión con el servidor');
        } finally {
            setLoading(false);
        }
    };

    const testCredentials = [
        { email: 'medico@hospital.com', password: 'password123', role: 'Médico' },
        { email: 'enfermero@hospital.com', password: 'password123', role: 'Enfermero' }
    ];

    const fillTestCredentials = (testEmail, testPassword) => {
        setEmail(testEmail);
        setPassword(testPassword);
    };

    return (
        <div className={styles.container}>
            <div className={styles.card}>
                <div className={styles.header}>
                    <h1 className={styles.title}>🏥 Clínica Emergencias</h1>
                    <p className={styles.subtitle}>Iniciar Sesión</p>
                </div>

                {error && (
                    <div className={styles.error}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.formGroup}>
                        <label className={styles.label}>
                            Email
                        </label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className={styles.input}
                            required
                            disabled={loading}
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>
                            Contraseña
                        </label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className={styles.input}
                            required
                            disabled={loading}
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className={styles.loginButton}
                    >
                        {loading ? '🔐 Conectando...' : 'Iniciar Sesión'}
                    </button>
                </form>

                <div className={styles.credentialsSection}>
                    <h3 className={styles.credentialsTitle}>Credenciales de prueba:</h3>
                    <div className={styles.credentialsList}>
                        {testCredentials.map((cred, index) => (
                            <button
                                key={index}
                                type="button"
                                onClick={() => fillTestCredentials(cred.email, cred.password)}
                                disabled={loading}
                                className={styles.credentialButton}
                            >
                                <div className={styles.credentialRole}>{cred.role}</div>
                                <div className={styles.credentialEmail}>{cred.email}</div>
                            </button>
                        ))}
                    </div>
                </div>

                <div className={styles.connectionStatus}>
                    <h4 className={styles.connectionTitle}>🔍 Estado de conexión</h4>
                    <p className={styles.connectionDetails}>
                        Conectando a: http://localhost:8080/api
                        <br />
                        {loading ? 'Verificando credenciales...' : 'Listo para autenticar'}
                    </p>
                </div>
            </div>

            {/* Efecto de partículas */}
            <div className={styles.particles}>
                <div className={styles.particle}></div>
                <div className={styles.particle}></div>
                <div className={styles.particle}></div>
                <div className={styles.particle}></div>
                <div className={styles.particle}></div>
            </div>
        </div>
    );
};

export default Login;