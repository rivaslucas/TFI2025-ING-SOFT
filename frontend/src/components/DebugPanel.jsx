import React, { useState, useEffect } from 'react';
import { authService } from '../services/authService';

const DebugPanel = () => {
    const [connectionStatus, setConnectionStatus] = useState('checking');
    const [backendInfo, setBackendInfo] = useState({});

    const testBackendConnection = async () => {
        try {
            setConnectionStatus('testing');

            const tests = [
                { name: 'Auth Current User', endpoint: '/auth/current-user', method: 'GET' },
                { name: 'Pacientes List', endpoint: '/pacientes', method: 'GET' },
                { name: 'Urgencias List', endpoint: '/urgencias/lista-espera', method: 'GET' }
            ];

            const results = {};

            for (const test of tests) {
                try {
                    const startTime = Date.now();
                    const response = await fetch(`http://localhost:8081/api${test.endpoint}`, {
                        method: test.method,
                        headers: { 'Content-Type': 'application/json' }
                    });
                    const endTime = Date.now();

                    results[test.name] = {
                        success: response.ok,
                        status: response.status,
                        time: endTime - startTime,
                        data: await response.json().catch(() => ({ error: 'No JSON' }))
                    };
                } catch (error) {
                    results[test.name] = {
                        success: false,
                        error: error.message,
                        time: 0
                    };
                }
            }

            setBackendInfo(results);
            setConnectionStatus('completed');
        } catch (error) {
            setConnectionStatus('error');
        }
    };

    useEffect(() => {
        testBackendConnection();
    }, []);

    return (
        <div className="fixed bottom-4 right-4 z-50 max-w-md">
            <div className="bg-gray-800 text-white rounded-lg shadow-lg p-4">
                <div className="flex justify-between items-center mb-3">
                    <h3 className="font-bold">🔧 Debug Panel</h3>
                    <button
                        onClick={testBackendConnection}
                        className="text-xs bg-blue-500 px-2 py-1 rounded hover:bg-blue-600"
                    >
                        Test
                    </button>
                </div>

                <div className="space-y-2 text-xs">
                    <div>
                        <strong>Backend:</strong> localhost:8081
                    </div>
                    <div>
                        <strong>Frontend:</strong> {window.location.origin}
                    </div>
                    <div>
                        <strong>Estado:</strong> {connectionStatus}
                    </div>

                    {Object.entries(backendInfo).map(([testName, result]) => (
                        <div key={testName} className={`p-2 rounded ${
                            result.success ? 'bg-green-900' : 'bg-red-900'
                        }`}>
                            <div className="font-medium">{testName}</div>
                            <div>Status: {result.status || 'N/A'}</div>
                            <div>Time: {result.time}ms</div>
                            {result.error && <div>Error: {result.error}</div>}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default DebugPanel;