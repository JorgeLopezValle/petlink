let formToSubmit = null;

function abrirModalEliminar(form) {
    formToSubmit = form;
    const nombreProtectora = form.getAttribute('data-nombre');
    document.getElementById('nombreProtectora').textContent = nombreProtectora;
    document.getElementById('modalEliminarProtectora').classList.remove('hidden');
}

function cerrarModalEliminar() {
    const modal = document.getElementById('modalEliminarProtectora');
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
    const modal = document.getElementById('modalEliminarProtectora');

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
        const modal = document.getElementById('modalEliminarProtectora');
        if (modal && !modal.classList.contains('hidden')) {
            cerrarModalEliminar();
        }
    }
});
