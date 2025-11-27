import api from './api';

export const urgenciasService = {
    async registrarUrgencia(urgenciaData) {
        try {
            console.log('📤 Enviando datos de urgencia:', urgenciaData);

            const response = await api.post('/urgencias/ingresos', urgenciaData);

            console.log('✅ Respuesta del backend:', response.data);

            let message = 'Urgencia registrada exitosamente. Paciente agregado a la lista de espera.';

            if (response.data.mensaje) {
                message = response.data.mensaje;
            } else if (response.data.message) {
                message = response.data.message;
            }

            return {
                success: true,
                data: response.data,
                message: message
            };
        } catch (error) {
            console.error('❌ Error al registrar urgencia:', error);

            let errorMessage = 'Error al registrar urgencia';

            if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            }

            return {
                success: false,
                error: errorMessage,
                details: error.response?.data
            };
        }
    },

    async obtenerListaEspera() {
        try {
            console.log('📋 Solicitando lista de espera...');

            const response = await api.get('/urgencias/lista-espera');

            console.log('✅ Respuesta completa:', response);
            console.log('📦 Response data:', response.data);
            console.log('🔍 Tipo de data:', typeof response.data);
            console.log('📊 Es array?:', Array.isArray(response.data));

            if (Array.isArray(response.data)) {
                console.log('👥 Primer paciente:', response.data[0]);
                console.log('🎯 Keys del primer paciente:', response.data[0] ? Object.keys(response.data[0]) : 'No hay pacientes');
            }

            return {
                success: true,
                data: response.data,
                message: `Se encontraron ${response.data?.length || 0} pacientes en espera`
            };
        } catch (error) {
            console.error('❌ Error al obtener lista de espera:', error);
            console.error('❌ Error response:', error.response);

            let errorMessage = 'Error al obtener la lista de espera';

            if (error.response?.status === 404) {
                errorMessage = 'No hay pacientes en la lista de espera';
            } else if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            }

            return {
                success: false,
                error: errorMessage,
                details: error.response?.data
            };
        }
    }
};