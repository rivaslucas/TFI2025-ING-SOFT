// services/obrasSocialesService.js - CORREGIDO
import api from './api';

export const obrasSocialesService = {
    obtenerObrasSociales: async () => {
        try {
            console.log('🏥 Solicitando obras sociales...');

            // ✅ CORREGIDO: URL sin duplicación
            const response = await api.get('/obras-sociales');

            console.log('✅ Obras sociales obtenidas:', response.data);

            // Asegurar que siempre retornamos un array
            const data = Array.isArray(response.data) ? response.data : [];
            console.log(`📊 Retornando ${data.length} obras sociales`);
            return data;

        } catch (error) {
            console.error('❌ Error obteniendo obras sociales:', error);

            // Log detallado para debugging
            if (error.response) {
                console.error('📊 Detalles del error:', {
                    status: error.response.status,
                    data: error.response.data,
                    headers: error.response.headers
                });
            }

            // Si es error 500, mostrar mensaje específico
            if (error.response?.status === 500) {
                throw new Error('Error interno del servidor. Por favor, contacte al administrador.');
            }

            // Si es error 404 (endpoint no existe), retornar array vacío
            if (error.response?.status === 404) {
                console.warn('⚠️ Endpoint no encontrado, retornando array vacío');
                return [];
            }

            throw new Error(error.response?.data?.error || 'Error al cargar las obras sociales');
        }
    },

    registrarObraSocial: async (obraSocialData) => {
        try {
            console.log('📤 Registrando obra social:', obraSocialData);

            // ✅ CORREGIDO: URL sin duplicación
            const response = await api.post('/obras-sociales', obraSocialData);

            console.log('✅ Obra social registrada:', response.data);
            return response.data;
        } catch (error) {
            console.error('❌ Error registrando obra social:', error);

            if (error.response?.data?.error) {
                throw new Error(error.response.data.error);
            }

            throw new Error('Error al registrar la obra social');
        }
    },

    actualizarObraSocial: async (nombreOriginal, obraSocialData) => {
        try {
            console.log('✏️ Actualizando obra social:', nombreOriginal, obraSocialData);

            // ✅ CORREGIDO: URL sin duplicación
            const response = await api.put(`/obras-sociales/${encodeURIComponent(nombreOriginal)}`, obraSocialData);

            console.log('✅ Obra social actualizada:', response.data);
            return response.data;
        } catch (error) {
            console.error('❌ Error actualizando obra social:', error);

            if (error.response?.data?.error) {
                throw new Error(error.response.data.error);
            }

            throw new Error('Error al actualizar la obra social');
        }
    },

    eliminarObraSocial: async (nombre) => {
        try {
            console.log('🗑️ Eliminando obra social:', nombre);

            // ✅ CORREGIDO: URL sin duplicación
            const response = await api.delete(`/obras-sociales/${encodeURIComponent(nombre)}`);

            console.log('✅ Obra social eliminada:', response.data);
            return response.data;

        } catch (error) {
            console.error('❌ Error eliminando obra social:', error);

            if (error.response?.data?.error) {
                throw new Error(error.response.data.error);
            }

            throw new Error('Error al eliminar la obra social');
        }
    },

    buscarObraSocial: async (nombre) => {
        try {
            console.log('🔍 Buscando obra social:', nombre);

            // ✅ CORREGIDO: URL sin duplicación
            const response = await api.get(`/obras-sociales/${encodeURIComponent(nombre)}`);

            console.log('✅ Obra social encontrada:', response.data);
            return response.data;
        } catch (error) {
            console.error('❌ Error buscando obra social:', error);
            throw new Error(error.response?.data?.error || 'Error al buscar la obra social');
        }
    }
};