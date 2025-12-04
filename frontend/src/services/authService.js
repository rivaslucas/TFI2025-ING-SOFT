import api from './api';

export const authService = {
    async login(email, password) {
        try {
            console.log('🔐 Intentando login con:', { email, password });

            const response = await api.post('/auth/login', {
                email,
                contrasena: password
            });

            console.log('✅ Login exitoso:', response.data);

            if (response.data) {
                // ✅ MEJORADO: Asegurar que los médicos tengan matrícula
                let userData = response.data;

                if (this.isMedico(userData) && !userData.matricula) {
                    userData = {
                        ...userData,
                        matricula: this.extraerMatricula(email)
                    };
                    console.log('🎯 Matrícula asignada al médico:', userData.matricula);
                }

                // Guardar en localStorage para persistencia
                localStorage.setItem('authToken', 'mock-token');
                localStorage.setItem('user', JSON.stringify(userData));
                localStorage.setItem('userEmail', email);

                return {
                    success: true,
                    data: userData,
                    message: 'Inicio de sesión exitoso'
                };
            }

            return {
                success: false,
                error: 'No se recibieron datos del servidor'
            };
        } catch (error) {
            console.error('❌ Error en login:', error);
            return {
                success: false,
                error: error.userMessage || 'Error al iniciar sesión',
                details: error.response?.data
            };
        }
    },

    // ✅ NUEVO: Determinar si es médico
    isMedico(userData) {
        const autoridad = userData.autoridad || userData.user?.autoridad || '';
        return autoridad.toLowerCase().includes('medico');
    },

    // ✅ NUEVO: Extraer matrícula del email
    extraerMatricula(email) {
        if (!email) return '67890'; // Matrícula por defecto

        // Intentar extraer del email (ej: medico67890@hospital.com)
        const match = email.match(/(\d+)/);
        if (match) {
            return match[1];
        }

        // Si no hay números, usar matrícula por defecto basada en el email
        return email.includes('medico') ? '67890' : '12345';
    },

    async register(email, password) {
        try {
            const response = await api.post('/auth/register', {
                email,
                contrasena: password
            });

            return {
                success: true,
                data: response.data,
                message: 'Usuario registrado exitosamente'
            };
        } catch (error) {
            return {
                success: false,
                error: error.userMessage || 'Error al registrar usuario',
                details: error.response?.data
            };
        }
    },

    async getCurrentUser() {
        try {
            const response = await api.get('/auth/current-user');
            let userData = response.data;

            // ✅ MEJORADO: Asegurar matrícula para médicos
            if (this.isMedico(userData) && !userData.matricula) {
                userData = {
                    ...userData,
                    matricula: this.extraerMatricula(userData.email)
                };
            }

            return {
                success: true,
                data: userData,
                message: 'Usuario obtenido correctamente'
            };
        } catch (error) {
            // Si falla, intentar con datos del localStorage
            const storedUser = this.getStoredUser();
            if (storedUser) {
                return {
                    success: true,
                    data: storedUser,
                    message: 'Usuario obtenido del almacenamiento local'
                };
            }
            return {
                success: false,
                error: error.userMessage || 'Error al obtener usuario actual',
                details: error.response?.data
            };
        }
    },

    logout() {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        localStorage.removeItem('userEmail');
    },

    getStoredUser() {
        try {
            const userStr = localStorage.getItem('user');
            const user = userStr ? JSON.parse(userStr) : null;

            // ✅ MEJORADO: Asegurar matrícula en usuario almacenado
            if (user && this.isMedico(user) && !user.matricula) {
                user.matricula = this.extraerMatricula(user.email);
                localStorage.setItem('user', JSON.stringify(user));
            }

            return user;
        } catch (error) {
            return null;
        }
    },

    // Para debug
    getConnectionStatus() {
        const user = this.getStoredUser();
        return {
            backend: 'http://localhost:8080',
            frontend: window.location.origin,
            userStored: !!user,
            userRole: user?.autoridad,
            userMatricula: user?.matricula
        };
    }
};