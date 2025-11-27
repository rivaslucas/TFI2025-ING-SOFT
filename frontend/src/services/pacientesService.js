import api from './api';

export const pacientesService = {
    async registrarPaciente(pacienteData) {
        try {
            console.log('📤 Enviando datos del paciente:', pacienteData);

            const response = await api.post('/pacientes', pacienteData);

            console.log('✅ Respuesta del backend:', response.data);

            // Manejar diferentes formatos de respuesta
            let message = 'Paciente registrado exitosamente';

            if (response.data.mensaje) {
                message = response.data.mensaje;
            } else if (response.data.message) {
                message = response.data.message;
            } else if (typeof response.data === 'string') {
                message = response.data;
            }

            return {
                success: true,
                data: response.data,
                message: message
            };
        } catch (error) {
            console.error('❌ Error al registrar paciente:', error);

            let errorMessage = 'Error al registrar paciente';

            if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.userMessage) {
                errorMessage = error.userMessage;
            }

            return {
                success: false,
                error: errorMessage,
                details: error.response?.data
            };
        }
    },

    async buscarPaciente(cuil) {
        try {
            const response = await api.get(`/pacientes/${cuil}`);
            return {
                success: true,
                data: response.data,
                message: 'Paciente encontrado exitosamente'
            };
        } catch (error) {
            let errorMessage = 'Error al buscar paciente';

            if (error.response?.status === 404) {
                errorMessage = 'Paciente no encontrado';
            } else if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            }

            return {
                success: false,
                error: errorMessage,
                details: error.response?.data
            };
        }
    },

    async listarPacientes() {
        try {
            const response = await api.get('/pacientes');
            return {
                success: true,
                data: response.data,
                message: 'Pacientes obtenidos correctamente'
            };
        } catch (error) {
            return {
                success: false,
                error: error.userMessage || 'Error al obtener pacientes',
                details: error.response?.data
            };
        }
    }
};