import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Dashboard from './pages/Dashboard';
import Login from './pages/Auth/Login';
import RegistrarPaciente from './pages/Pacientes/Registrar';
import RegistrarUrgencia from './pages/Urgencias/Registrar';
import ReclamarPaciente from './pages/Atencion/Reclamar';
import AtenderPaciente from './pages/Atencion/Atender';
import ListaEspera from './pages/Urgencias/ListaEspera';
import ObrasSociales from './pages/ObrasSociales/ObrasSociales';
import DebugPanel from './components/DebugPanel';

const ProtectedRoute = ({ children }) => {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-600 to-purple-700">
                <div className="bg-white rounded-2xl shadow-2xl p-8 text-center">
                    <div className="text-2xl font-bold text-gray-800 mb-4">🏥 Clínica Emergencias</div>
                    <div className="text-gray-600">Conectando con el servidor...</div>
                </div>
            </div>
        );
    }

    return isAuthenticated ? children : <Navigate to="/login" />;
};

function App() {
    return (
        <Router>
            <AuthProvider>

                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
                    <Route path="/pacientes/registrar" element={<ProtectedRoute><RegistrarPaciente /></ProtectedRoute>} />
                    <Route path="/urgencias/registrar" element={<ProtectedRoute><RegistrarUrgencia /></ProtectedRoute>} />
                    <Route path="/obras-sociales" element={<ProtectedRoute><ObrasSociales /></ProtectedRoute>} />
                    <Route path="/atencion/reclamar" element={<ProtectedRoute><ReclamarPaciente /></ProtectedRoute>} />
                    <Route path="/atencion/atender" element={<ProtectedRoute><AtenderPaciente /></ProtectedRoute>} />
                    <Route path="/urgencias/lista-espera" element={<ProtectedRoute><ListaEspera /></ProtectedRoute>} />
                    <Route path="*" element={<Navigate to="/" />} />
                </Routes>
            </AuthProvider>
        </Router>
    );
}

export default App;