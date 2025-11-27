import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { obrasSocialesService } from '../../services/obrasSocialesService';
import styles from './ObrasSociales.module.css';

const ObrasSociales = () => {
    const navigate = useNavigate();
    const [obrasSociales, setObrasSociales] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadingAction, setLoadingAction] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });
    const [showForm, setShowForm] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [editingObraSocial, setEditingObraSocial] = useState(null);

    const [formData, setFormData] = useState({
        nombre: '',
        identificador: ''
    });

    useEffect(() => {
        cargarObrasSociales();
    }, []);

    const cargarObrasSociales = async () => {
        try {
            setLoading(true);
            const data = await obrasSocialesService.obtenerObrasSociales();
            setObrasSociales(data || []);
        } catch (error) {
            console.error('Error cargando obras sociales:', error);
            setMessage({
                type: 'error',
                text: '❌ Error al cargar las obras sociales'
            });
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.nombre.trim() || !formData.identificador.trim()) {
            setMessage({
                type: 'error',
                text: '❌ Nombre e identificador son obligatorios'
            });
            return;
        }

        setLoadingAction(true);
        try {
            if (editingObraSocial) {
                await obrasSocialesService.actualizarObraSocial(editingObraSocial.nombre, formData);
                setMessage({
                    type: 'success',
                    text: '✅ Obra social actualizada exitosamente'
                });
            } else {
                await obrasSocialesService.registrarObraSocial(formData);
                setMessage({
                    type: 'success',
                    text: '✅ Obra social registrada exitosamente'
                });
            }

            setFormData({ nombre: '', identificador: '' });
            setEditingObraSocial(null);
            setShowForm(false);

            await cargarObrasSociales();

            setTimeout(() => setMessage({ type: '', text: '' }), 4000);
        } catch (error) {
            console.error('Error guardando obra social:', error);
            setMessage({
                type: 'error',
                text: error.message || '❌ Error al guardar la obra social'
            });
        } finally {
            setLoadingAction(false);
        }
    };

    const handleEdit = (obraSocial) => {
        setEditingObraSocial(obraSocial);
        setFormData({
            nombre: obraSocial.nombre || '',
            identificador: obraSocial.identificador || ''
        });
        setShowForm(true);
    };

    const handleDelete = async (obraSocial) => {
        if (!window.confirm(`¿Está seguro de eliminar la obra social "${obraSocial.nombre}"?`)) {
            return;
        }

        setLoadingAction(true);
        try {
            await obrasSocialesService.eliminarObraSocial(obraSocial.nombre);
            setMessage({
                type: 'success',
                text: '✅ Obra social eliminada exitosamente'
            });

            await cargarObrasSociales();
            setTimeout(() => setMessage({ type: '', text: '' }), 4000);
        } catch (error) {
            console.error('Error eliminando obra social:', error);
            setMessage({
                type: 'error',
                text: error.message || '❌ Error al eliminar la obra social'
            });
        } finally {
            setLoadingAction(false);
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingObraSocial(null);
        setFormData({ nombre: '', identificador: '' });
    };

    const filteredObrasSociales = obrasSociales.filter(obraSocial =>
        obraSocial.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        obraSocial.identificador?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className={styles.container}>
            <div className={styles.maxW7xl}>
                {/* Header */}
                <div className={styles.header}>
                    <div>
                        <h1 className={styles.title}>
                            🏢 Gestión de Obras Sociales
                        </h1>
                        <p className={styles.subtitle}>
                            Administre las obras sociales del sistema de salud
                        </p>
                    </div>
                    <button
                        onClick={() => navigate('/')}
                        className={styles.backButton}
                    >
                        ← Volver al Dashboard
                    </button>
                </div>

                {/* Mensajes */}
                {message.text && (
                    <div className={`${styles.message} ${
                        message.type === 'success'
                            ? styles.messageSuccess
                            : styles.messageError
                    }`}>
                        <div className={styles.messageContent}>
                            <span className={styles.messageIcon}>
                                {message.type === 'success' ? '✅' : '❌'}
                            </span>
                            <span>{message.text}</span>
                        </div>
                        <button
                            onClick={() => setMessage({ type: '', text: '' })}
                            className={styles.messageClose}
                        >
                            ✕
                        </button>
                    </div>
                )}

                <div className={styles.grid}>
                    {/* Panel lateral - Formulario */}
                    {showForm && (
                        <div className={styles.formPanel}>
                            <div className={`${styles.card} ${styles.formCard}`}>
                                <h2 className={styles.formTitle}>
                                    {editingObraSocial ? '✏️ Editar Obra Social' : '➕ Nueva Obra Social'}
                                </h2>

                                <form onSubmit={handleSubmit} className={styles.form}>
                                    <div>
                                        <label className={styles.label}>
                                            Nombre *
                                        </label>
                                        <input
                                            type="text"
                                            value={formData.nombre}
                                            onChange={(e) => setFormData(prev => ({ ...prev, nombre: e.target.value }))}
                                            className={styles.input}
                                            placeholder="Ej: OSDE, Swiss Medical"
                                            required
                                            disabled={loadingAction}
                                        />
                                    </div>

                                    <div>
                                        <label className={styles.label}>
                                            Identificador *
                                        </label>
                                        <input
                                            type="text"
                                            value={formData.identificador}
                                            onChange={(e) => setFormData(prev => ({ ...prev, identificador: e.target.value }))}
                                            className={styles.input}
                                            placeholder="Código único"
                                            required
                                            disabled={loadingAction}
                                        />
                                    </div>

                                    <div className={styles.formActions}>
                                        <button
                                            type="submit"
                                            disabled={loadingAction}
                                            className={styles.submitButton}
                                        >
                                            {loadingAction ? '⏳ Guardando...' : (editingObraSocial ? '💾 Actualizar' : '💾 Guardar')}
                                        </button>
                                        <button
                                            type="button"
                                            onClick={handleCancel}
                                            className={styles.cancelButton}
                                            disabled={loadingAction}
                                        >
                                            Cancelar
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    )}

                    {/* Panel principal - Lista */}
                    <div className={showForm ? styles.listPanelLarge : styles.listPanelFull}>
                        <div className={`${styles.card} ${styles.listCard}`}>
                            {/* Header de la lista */}
                            <div className={styles.listHeader}>
                                <div className={styles.listHeaderLeft}>
                                    <h2 className={styles.listTitle}>
                                        📋 Lista de Obras Sociales
                                    </h2>
                                    <p className={styles.listSubtitle}>
                                        {filteredObrasSociales.length} de {obrasSociales.length} obras sociales
                                    </p>
                                </div>

                                <div className={styles.listHeaderRight}>
                                    {/* Buscador */}
                                    <div className={styles.searchContainer}>
                                        <input
                                            type="text"
                                            value={searchTerm}
                                            onChange={(e) => setSearchTerm(e.target.value)}
                                            className={styles.searchInput}
                                            placeholder="Buscar obra social..."
                                        />
                                        <span className={styles.searchIcon}>🔍</span>
                                    </div>

                                    {/* Botón nuevo */}
                                    {!showForm && (
                                        <button
                                            onClick={() => setShowForm(true)}
                                            className={styles.newButton}
                                        >
                                            <span className={styles.newButtonIcon}>➕</span>
                                            Nueva Obra Social
                                        </button>
                                    )}
                                </div>
                            </div>

                            {/* Lista de obras sociales */}
                            {loading ? (
                                <div className={styles.loadingState}>
                                    <div className={styles.loadingSpinner}></div>
                                    <p className={styles.loadingText}>Cargando obras sociales...</p>
                                </div>
                            ) : filteredObrasSociales.length === 0 ? (
                                <div className={styles.emptyState}>
                                    <div className={styles.emptyIcon}>🏢</div>
                                    <h3 className={styles.emptyTitle}>
                                        {searchTerm ? 'No se encontraron resultados' : 'No hay obras sociales registradas'}
                                    </h3>
                                    <p className={styles.emptyDescription}>
                                        {searchTerm
                                            ? 'Intente con otros términos de búsqueda'
                                            : 'Comience agregando la primera obra social'
                                        }
                                    </p>
                                </div>
                            ) : (
                                <div className={styles.list}>
                                    {filteredObrasSociales.map((obraSocial, index) => (
                                        <div key={index} className={styles.listItem}>
                                            <div className={styles.itemContent}>
                                                <div className={styles.itemIcon}>
                                                    🏢
                                                </div>
                                                <div className={styles.itemInfo}>
                                                    <h3 className={styles.itemTitle}>
                                                        {obraSocial.nombre}
                                                    </h3>
                                                    <div className={styles.itemDetails}>
                                                        <span className={styles.itemId}>
                                                            ID: {obraSocial.identificador}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className={styles.itemActions}>
                                                <span className={styles.statusBadge}>
                                                    Activa
                                                </span>

                                                <div className={styles.actionButtons}>
                                                    <button
                                                        onClick={() => handleEdit(obraSocial)}
                                                        className={styles.editButton}
                                                        title="Editar obra social"
                                                    >
                                                        ✏️
                                                    </button>
                                                    <button
                                                        onClick={() => handleDelete(obraSocial)}
                                                        className={styles.deleteButton}
                                                        title="Eliminar obra social"
                                                    >
                                                        🗑️
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>

                        {/* Estadísticas */}
                        {!loading && obrasSociales.length > 0 && (
                            <div className={styles.stats}>
                                <div className={styles.statCard}>
                                    <div className={styles.statValue}>{obrasSociales.length}</div>
                                    <div className={styles.statLabel}>Total Obras Sociales</div>
                                </div>
                                <div className={styles.statCard}>
                                    <div className={styles.statValue}>{obrasSociales.length}</div>
                                    <div className={styles.statLabel}>Todas Activas</div>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ObrasSociales;