let formToSubmit = null;

function abrirModalRechazar(form) {
    formToSubmit = form;
    const nombreProtectora = form.getAttribute('data-nombre');
    document.getElementById('nombreProtectoraRechazar').textContent = nombreProtectora;
    document.getElementById('modalRechazarProtectora').classList.remove('hidden');
}

function cerrarModalRechazar() {
    const modal = document.getElementById('modalRechazarProtectora');
    if (modal) {
        modal.classList.add('hidden');
    }
    formToSubmit = null;
}

function confirmarRechazo() {
    if (formToSubmit) {
        formToSubmit.submit();
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('modalRechazarProtectora');

    if (modal) {
        modal.addEventListener('click', function(event) {
            if (event.target === this) {
                cerrarModalRechazar();
            }
        });
    }
});

document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modal = document.getElementById('modalRechazarProtectora');
        if (modal && !modal.classList.contains('hidden')) {
            cerrarModalRechazar();
        }
    }
});
