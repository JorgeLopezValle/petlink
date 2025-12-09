console.log('modal-adopcion.js cargado');

let formActualAdopcion = null;
let nombreMascotaAdopcion = '';

function mostrarModalError(tipoEntidad) {
    console.log('Mostrando modal error:', tipoEntidad);
    const modal = document.getElementById('modalAdopcion');
    const titulo = document.getElementById('modalTitulo');
    const mensaje = document.getElementById('modalMensaje');
    const iconError = document.getElementById('iconError');
    const iconConfirm = document.getElementById('iconConfirm');
    const modalIcon = document.getElementById('modalIcon');
    const btnEntendido = document.getElementById('btnEntendido');
    const btnCancelar = document.getElementById('btnCancelar');
    const btnConfirmar = document.getElementById('btnConfirmar');

    titulo.textContent = 'No puedes adoptar';

    if (tipoEntidad === 'PROTECTORA') {
        mensaje.textContent = 'Las protectoras no pueden adoptar mascotas. Solo los usuarios registrados pueden realizar adopciones.';
    } else if (tipoEntidad === 'ADMIN') {
        mensaje.textContent = 'Los administradores no pueden adoptar mascotas. Solo los usuarios registrados pueden realizar adopciones.';
    } else {
        mensaje.textContent = 'No tienes permisos para adoptar mascotas.';
    }

    modalIcon.className = 'w-16 h-16 bg-red-100 rounded-full flex items-center justify-center';
    iconError.classList.remove('hidden');
    iconConfirm.classList.add('hidden');
    btnEntendido.classList.remove('hidden');
    btnCancelar.classList.add('hidden');
    btnConfirmar.classList.add('hidden');

    modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function mostrarModalConfirmacion(form, nombre) {
    console.log('Mostrando modal confirmación para:', nombre);
    const modal = document.getElementById('modalAdopcion');
    const titulo = document.getElementById('modalTitulo');
    const mensaje = document.getElementById('modalMensaje');
    const iconError = document.getElementById('iconError');
    const iconConfirm = document.getElementById('iconConfirm');
    const modalIcon = document.getElementById('modalIcon');
    const btnEntendido = document.getElementById('btnEntendido');
    const btnCancelar = document.getElementById('btnCancelar');
    const btnConfirmar = document.getElementById('btnConfirmar');

    formActualAdopcion = form;
    nombreMascotaAdopcion = nombre;

    titulo.textContent = '¿Confirmar adopción?';
    mensaje.textContent = `¿Estás seguro de que quieres adoptar a ${nombreMascotaAdopcion}? Esta decisión es un compromiso importante.`;

    modalIcon.className = 'w-16 h-16 bg-pink-100 rounded-full flex items-center justify-center';
    iconError.classList.add('hidden');
    iconConfirm.classList.remove('hidden');
    btnEntendido.classList.add('hidden');
    btnCancelar.classList.remove('hidden');
    btnConfirmar.classList.remove('hidden');

    modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function cerrarModalAdopcion() {
    console.log('Cerrando modal');
    const modal = document.getElementById('modalAdopcion');
    modal.classList.add('hidden');
    document.body.style.overflow = 'auto';
    formActualAdopcion = null;
    nombreMascotaAdopcion = '';
}

function confirmarAdopcion() {
    console.log('Confirmando adopción');
    if (formActualAdopcion) {
        formActualAdopcion.removeEventListener('submit', handleFormSubmit);
        formActualAdopcion.submit();
    }
    cerrarModalAdopcion();
}

function handleFormSubmit(event) {
    console.log('Form submit interceptado!');
    event.preventDefault();
    const form = event.target;
    const tipoEntidad = form.getAttribute('data-tipo-entidad');
    const rol = form.getAttribute('data-rol');

    console.log('Tipo entidad:', tipoEntidad, 'Rol:', rol);

    if (tipoEntidad === 'PROTECTORA') {
        mostrarModalError('PROTECTORA');
        return false;
    }

    if (tipoEntidad === 'USUARIO' && rol === 'ADMIN') {
        mostrarModalError('ADMIN');
        return false;
    }

    const mascotaCard = form.closest('.mascota-card');
    let nombre = mascotaCard ? mascotaCard.getAttribute('data-nombre') : null;

    if (!nombre && typeof window.nombreMascota !== 'undefined') {
        nombre = window.nombreMascota;
    }

    if (!nombre) {
        nombre = 'esta mascota';
    }

    console.log('Nombre de mascota obtenido:', nombre);
    mostrarModalConfirmacion(form, nombre);
    return false;
}

function inicializarFormulariosAdopcion() {
    const formsAdopcion = document.querySelectorAll('.form-adopcion');
    console.log('Formularios encontrados:', formsAdopcion.length);

    formsAdopcion.forEach(function(form, index) {
        console.log('Añadiendo listener al formulario', index);
        form.removeEventListener('submit', handleFormSubmit);
        form.addEventListener('submit', handleFormSubmit);
    });
}

console.log('Estado del DOM:', document.readyState);
if (document.readyState === 'loading') {
    console.log('Esperando DOMContentLoaded');
    document.addEventListener('DOMContentLoaded', inicializarFormulariosAdopcion);
} else {
    console.log('DOM ya listo, inicializando ahora');
    inicializarFormulariosAdopcion();
}

document.addEventListener('click', function(e) {
    const modal = document.getElementById('modalAdopcion');
    if (e.target === modal) {
        cerrarModalAdopcion();
    }
});
