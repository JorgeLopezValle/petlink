let formToSubmit = null;

function abrirModalEliminar(form) {
    formToSubmit = form;
    const nombreUsuario = form.getAttribute('data-nombre');
    document.getElementById('nombreUsuario').textContent = nombreUsuario;
    document.getElementById('modalEliminarUsuario').classList.remove('hidden');
}

function cerrarModalEliminar() {
    const modal = document.getElementById('modalEliminarUsuario');
    if (modal) {
        modal.classList.add('hidden');
    }
    formToSubmit = null;
}

function confirmarEliminacion() {
    if (formToSubmit) {
        formToSubmit.submit();
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('modalEliminarUsuario');

    if (modal) {
        modal.addEventListener('click', function(event) {
            if (event.target === this) {
                cerrarModalEliminar();
            }
        });
    }
});

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modal = document.getElementById('modalEliminarUsuario');
        if (modal && !modal.classList.contains('hidden')) {
            cerrarModalEliminar();
        }
    }
});
