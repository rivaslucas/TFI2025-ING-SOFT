// Este archivo contendrá la lógica para comunicarse con la API del backend
// usando fetch()

const API_BASE_URL = '/api'; // Base de la URL de nuestra API

// Ejemplo de función para obtener todos los pacientes
export async function getPacientes() {
    try {
        const response = await fetch(`${API_BASE_URL}/pacientes`);
        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("No se pudieron obtener los pacientes:", error);
        return [];
    }
}
