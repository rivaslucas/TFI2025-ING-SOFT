import api from './api';

export const atencionService = {
    // ✅ MEJORADO: Liberar paciente con mejor manejo de errores
    async liberarPaciente(idIngreso, liberacionData) {
        try {
            console.log('🔓 Liberando paciente:', { idIngreso, liberacionData });

            const response = await api.post(`/atenciones/${idIngreso}/liberar`, liberacionData);

            console.log('✅ Respuesta de liberación:', response.data);

            return {
                success: true,
                data: response.data,
                message: response.data.mensaje || 'Paciente liberado exitosamente'
            };
        } catch (error) {
            console.error('❌ Error al liberar paciente:', error);

            let errorMessage = 'Error al liberar paciente';

            if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.message) {
                errorMessage = error.message;
            }

            return {
                success: false,
                error: errorMessage,
                details: error.response?.data
            };
        }
    },

    // ✅ MEJORADO: Verificación más detallada del estado del médico
    async verificarPuedeReclamar(medicoMatricula) {
        try {
            console.log('🔍 Verificando estado completo del médico:', medicoMatricula);

            const response = await api.get(`/atenciones/medico/${medicoMatricula}/puede-reclamar`);

            console.log('✅ Resultado verificación:', response.data);

            return {
                success: true,
                data: response.data,
                puedeReclamar: response.data.puedeReclamarPaciente,
                mensaje: response.data.mensaje,
                estadoDetallado: response.data.estadoDetallado
            };
        } catch (error) {
            console.error('❌ Error verificando estado médico:', error);

            let errorMessage = 'Error al verificar estado del médico';

            if (error.response?.data?.error) {
                errorMessage = error.response.data.error;
            } else if (error.response?.data?.message) {
                errorMessage = error.response.data.message;
            }

            return {
                success: false,
                error: errorMessage,
                puedeReclamar: false,
                details: error.response?.data
            };
        }
    },

    // ✅ CORREGIDO: Reclamar paciente con validación mejorada
    async reclamarPaciente(medicoMatricula) {
        try {
            console.log('👨‍⚕️ Reclamando paciente con matrícula:', medicoMatricula);

            // ✅ PRIMERO: Verificación simple del backend
            const verificacion = await this.verificarPuedeReclamar(medicoMatricula);

            if (!verificacion.success || !verificacion.data.puedeReclamarPaciente) {
                throw new Error(verificacion.data?.mensaje || 'No puede reclamar otro paciente');
            }

            // ✅ SEGUNDO: Reclamar paciente
            const response = await api.post(`/atenciones/reclamar?medicoMatricula=${medicoMatricula}`);

            console.log('✅ Respuesta de reclamar paciente:', response.data);

            return {
                success: true,
                data: response.data,
                message: 'Paciente reclamado exitosamente'
            };
        } catch (error) {
            console.error('❌ Error al reclamar paciente:', error);

            let errorMessage = 'Error al reclamar paciente';

            if (error.message) {
                errorMessage = error.message;
            } else if (error.response?.data?.error) {
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

    // ✅ MEJORADO: Registrar atención con mejor manejo de errores
    async registrarAtencion(idIngreso, atencionData) {
        try {
            console.log('📝 Registrando atención para ingreso:', idIngreso);

            const response = await api.post(`/atenciones/${idIngreso}/atender`, atencionData);

            console.log('✅ Atención registrada exitosamente:', response.data);

            return {
                success: true,
                data: response.data,
                message: 'Atención registrada exitosamente'
            };
        } catch (error) {
            console.error('❌ Error al registrar atención:', error);

            let errorMessage = 'Error al registrar atención';

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

    // ✅ Obtener pacientes pendientes
    async obtenerPendientes() {
        try {
            console.log('📋 Obteniendo pacientes pendientes...');

            const response = await api.get('/atenciones/pendientes');

            console.log('✅ Pacientes pendientes obtenidos:', response.data);

            return {
                success: true,
                data: response.data,
                message: 'Pacientes pendientes obtenidos correctamente'
            };
        } catch (error) {
            console.error('❌ Error al obtener pacientes pendientes:', error);

            return {
                success: false,
                error: error.userMessage || 'Error al obtener pacientes pendientes',
                details: error.response?.data
            };
        }
    },

    // ✅ MEJORADO: Obtener estado completo del médico con mejor estructura
    async obtenerEstadoMedico(medicoMatricula) {
        try {
            console.log('🏥 Obteniendo estado del médico:', medicoMatricula);

            const response = await api.get(`/atenciones/medico/${medicoMatricula}/estado`);

            console.log('✅ Estado del médico obtenido:', response.data);

            return {
                success: true,
                data: response.data,
                message: 'Estado del médico obtenido correctamente'
            };
        } catch (error) {
            console.error('❌ Error al obtener estado del médico:', error);

            let errorMessage = 'Error al obtener estado del médico';

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

    // ✅ NUEVO: Obtener historial de atenciones del médico
    async obtenerHistorialAtenciones(medicoMatricula, pagina = 0, tamaño = 10) {
        try {
            console.log('📊 Obteniendo historial de atenciones para médico:', medicoMatricula);

            const response = await api.get(`/atenciones/medico/${medicoMatricula}/historial`, {
                params: { pagina, tamaño }
            });

            console.log('✅ Historial obtenido:', response.data);

            return {
                success: true,
                data: response.data,
                message: 'Historial de atenciones obtenido correctamente'
            };
        } catch (error) {
            console.error('❌ Error al obtener historial de atenciones:', error);

            return {
                success: false,
                error: 'Error al obtener historial de atenciones',
                details: error.response?.data
            };
        }
    },

    // ✅ NUEVO: Cancelar reclamo de paciente
    async cancelarReclamo(idIngreso, medicoMatricula, motivo) {
        try {
            console.log('🚫 Cancelando reclamo:', { idIngreso, medicoMatricula, motivo });

            const response = await api.post(`/atenciones/${idIngreso}/cancelar-reclamo`, {
                medicoMatricula,
                motivo
            });

            console.log('✅ Reclamo cancelado:', response.data);

            return {
                success: true,
                data: response.data,
                message: response.data.mensaje || 'Reclamo cancelado exitosamente'
            };
        } catch (error) {
            console.error('❌ Error al cancelar reclamo:', error);

            let errorMessage = 'Error al cancelar reclamo';

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

    // ✅ NUEVO: Verificar disponibilidad del sistema
    async verificarDisponibilidadSistema() {
        try {
            console.log('🔧 Verificando disponibilidad del sistema...');

            const response = await api.get('/atenciones/sistema/disponibilidad');

            console.log('✅ Disponibilidad del sistema:', response.data);

            return {
                success: true,
                data: response.data,
                message: 'Sistema disponible'
            };
        } catch (error) {
            console.error('❌ Error de disponibilidad del sistema:', error);

            return {
                success: false,
                error: 'Sistema no disponible',
                details: error.response?.data
            };
        }
    },

    // ✅ NUEVO: Obtener paciente actual del médico
    async obtenerPacienteActual(medicoMatricula) {
        try {
            console.log('🔍 Obteniendo paciente actual del médico:', medicoMatricula);

            const response = await api.get(`/atenciones/medico/${medicoMatricula}/paciente-actual`);

            console.log('✅ Paciente actual obtenido:', response.data);

            return {
                success: true,
                data: response.data,
                message: 'Paciente actual obtenido correctamente'
            };
        } catch (error) {
            console.error('❌ Error al obtener paciente actual:', error);

            // Si no hay paciente actual, no es un error
            if (error.response?.status === 404) {
                return {
                    success: true,
                    data: null,
                    message: 'No hay paciente actual'
                };
            }

            return {
                success: false,
                error: 'Error al obtener paciente actual',
                details: error.response?.data
            };
        }
    },

    // ✅ NUEVO: Liberar todos los pacientes del médico (para casos de error)
    async liberarTodosPacientes(medicoMatricula) {
        try {
            console.log('🔄 Liberando todos los pacientes del médico:', medicoMatricula);

            const response = await api.post(`/atenciones/medico/${medicoMatricula}/liberar-todos`);

            console.log('✅ Todos los pacientes liberados:', response.data);

            return {
                success: true,
                data: response.data,
                message: response.data.mensaje || 'Todos los pacientes liberados exitosamente'
            };
        } catch (error) {
            console.error('❌ Error al liberar todos los pacientes:', error);

            return {
                success: false,
                error: 'Error al liberar todos los pacientes',
                details: error.response?.data
            };
        }
    },

    // ✅ NUEVO: Sincronizar estado completo (método combinado)
    async sincronizarEstadoCompleto(medicoMatricula) {
        try {
            console.log('🔄 Sincronizando estado completo del médico:', medicoMatricula);

            // Obtener estado del médico (que ahora incluye pacienteActual)
            const estadoResponse = await this.obtenerEstadoMedico(medicoMatricula);

            if (!estadoResponse.success) {
                throw new Error('No se pudo obtener el estado del médico');
            }

            const resultado = {
                estadoMedico: estadoResponse.data,
                pacienteActual: null
            };

            // Si hay paciente actual en el estado, usarlo
            if (estadoResponse.data.pacienteActual) {
                resultado.pacienteActual = estadoResponse.data.pacienteActual;
                console.log('✅ Paciente actual encontrado en estado:', resultado.pacienteActual);
            } else {
                // Si no hay paciente actual en el estado, verificar explícitamente
                const pacienteResponse = await this.obtenerPacienteActual(medicoMatricula);
                if (pacienteResponse.success && pacienteResponse.data) {
                    resultado.pacienteActual = pacienteResponse.data;
                    console.log('✅ Paciente actual obtenido explícitamente:', resultado.pacienteActual);
                }
            }

            console.log('✅ Sincronización completada:', {
                puedeReclamar: resultado.estadoMedico.puedeReclamarPaciente,
                tienePacienteActual: !!resultado.pacienteActual
            });

            return {
                success: true,
                data: resultado,
                message: 'Estado sincronizado correctamente'
            };
        } catch (error) {
            console.error('❌ Error sincronizando estado:', error);

            return {
                success: false,
                error: 'Error al sincronizar estado',
                details: error.message
            };
        }
    }
};