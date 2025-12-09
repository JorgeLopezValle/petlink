let imagenItemToDelete = null;

function abrirModalAdvertencia() {
    const modal = document.getElementById('modalAdvertenciaImagen');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

function cerrarModalAdvertencia() {
    const modal = document.getElementById('modalAdvertenciaImagen');
    if (modal) {
        modal.classList.add('hidden');
    }
}

function abrirModalEliminarImagen(imagenItem) {
    imagenItemToDelete = imagenItem;
    const modal = document.getElementById('modalEliminarImagen');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

function cerrarModalEliminarImagen() {
    const modal = document.getElementById('modalEliminarImagen');
    if (modal) {
        modal.classList.add('hidden');
    }
    imagenItemToDelete = null;
}

function confirmarEliminacionImagen() {
    if (imagenItemToDelete) {
        const imagenesEliminadas = document.getElementById('imagenesEliminadas').value
            ? document.getElementById('imagenesEliminadas').value.split(',')
            : [];

        const url = imagenItemToDelete.dataset.url;
        imagenesEliminadas.push(url);
        document.getElementById('imagenesEliminadas').value = imagenesEliminadas.join(',');
        imagenItemToDelete.remove();

        cerrarModalEliminarImagen();
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const imagenesGrid = document.getElementById('imagenes-grid');

    function contarImagenesVisibles() {
        if (!imagenesGrid) return 0;
        return imagenesGrid.querySelectorAll('.imagen-item').length;
    }

    if (imagenesGrid) {
        imagenesGrid.addEventListener('click', function (e) {
            const btnEliminar = e.target.closest('.btn-eliminar-imagen');
            if (btnEliminar) {
                const imagenItem = btnEliminar.closest('.imagen-item');
                const imagenesRestantes = contarImagenesVisibles();

                if (imagenesRestantes <= 1) {
                    abrirModalAdvertencia();
                    return;
                }

                abrirModalEliminarImagen(imagenItem);
            }
        });
    }

    const modalAdvertencia = document.getElementById('modalAdvertenciaImagen');
    if (modalAdvertencia) {
        modalAdvertencia.addEventListener('click', function(event) {
            if (event.target === this) {
                cerrarModalAdvertencia();
            }
        });
    }

    const modalEliminar = document.getElementById('modalEliminarImagen');
    if (modalEliminar) {
        modalEliminar.addEventListener('click', function(event) {
            if (event.target === this) {
                cerrarModalEliminarImagen();
            }
        });
    }
});

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modalAdvertencia = document.getElementById('modalAdvertenciaImagen');
        if (modalAdvertencia && !modalAdvertencia.classList.contains('hidden')) {
            cerrarModalAdvertencia();
        }

        const modalEliminar = document.getElementById('modalEliminarImagen');
        if (modalEliminar && !modalEliminar.classList.contains('hidden')) {
            cerrarModalEliminarImagen();
        }
    }
});
