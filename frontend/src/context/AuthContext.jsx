import React, { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth debe ser usado dentro de un AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Se elimina la llamada a checkAuth() para forzar el inicio de sesión en cada carga.
        // Simplemente se establece loading a false para que la aplicación pueda continuar.
        setLoading(false);
    }, []);

    const checkAuth = async () => {
        try {
            const result = await authService.getCurrentUser();

            if (result.success) {
                setUser(result.data);
            } else {
                // Fallback: intentar con usuario almacenado
                const storedUser = authService.getStoredUser();
                if (storedUser) {
                    setUser(storedUser);
                }
            }
        } catch (error) {
            console.log('Error verificando autenticación:', error);
            // Fallback: usuario almacenado
            const storedUser = authService.getStoredUser();
            if (storedUser) {
                setUser(storedUser);
            }
        } finally {
            setLoading(false);
        }
    };

    const login = async (email, password) => {
        const result = await authService.login(email, password);

        if (result.success) {
            setUser(result.data);
            return result;
        } else {
            throw new Error(result.error);
        }
    };

    const logout = () => {
        authService.logout();
        setUser(null);
    };

    // Determinar rol basado en la respuesta del backend
    const getUserRole = () => {
        if (!user) return null;

        // Si el backend devuelve la autoridad
        if (user.autoridad) {
            return user.autoridad.toLowerCase(); // 'medico' o 'enfermero'
        }

        // Si el backend devuelve un objeto de usuario con autoridad
        if (user.user?.autoridad) {
            return user.user.autoridad.toLowerCase();
        }

        // Fallback basado en email (para testing)
        if (user.email && user.email.includes('medico')) {
            return 'medico';
        } else if (user.email && user.email.includes('enfermero')) {
            return 'enfermero';
        }

        return 'enfermero'; // default
    };

    const userRole = getUserRole();

    const value = {
        user,
        login,
        logout,
        loading,
        isAuthenticated: !!user,
        userRole,
        isMedico: userRole === 'medico',
        isEnfermero: userRole === 'enfermero'
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};