import axios from 'axios';

const API_BASE = 'http://localhost:8081/api';

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 10000, // 10 segundos timeout
});

// Interceptor para requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor para responses
api.interceptors.response.use(
    (response) => response,
    (error) => {
        let errorMessage = 'Error de conexión';

        if (error.response) {
            // El servidor respondió con un código de error
            const status = error.response.status;
            const data = error.response.data;

            switch (status) {
                case 400:
                    errorMessage = data.message || data.error || 'Solicitud incorrecta';
                    break;
                case 401:
                    errorMessage = 'No autorizado. Por favor, inicie sesión nuevamente.';
                    localStorage.removeItem('authToken');
                    localStorage.removeItem('user');
                    window.location.href = '/login';
                    break;
                case 404:
                    errorMessage = 'Recurso no encontrado';
                    break;
                case 409:
                    errorMessage = data.message || 'El recurso ya existe';
                    break;
                case 500:
                    errorMessage = 'Error interno del servidor';
                    break;
                default:
                    errorMessage = data.message || `Error ${status}`;
            }
        } else if (error.request) {
            // La solicitud fue hecha pero no se recibió respuesta
            errorMessage = 'No se pudo conectar con el servidor. Verifique su conexión.';
        } else {
            // Algo pasó en la configuración de la solicitud
            errorMessage = error.message;
        }

        error.userMessage = errorMessage;
        return Promise.reject(error);
    }
);

export default api;