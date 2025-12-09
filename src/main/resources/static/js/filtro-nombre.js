function filtrarPorNombre() {
    const inputFiltro = document.getElementById('filtroNombre');
    const filtro = inputFiltro.value.toLowerCase().trim();
    const btnLimpiar = document.getElementById('btnLimpiar');
    let contador = 0;

    if (filtro !== '') {
        btnLimpiar.classList.remove('hidden');
    } else {
        btnLimpiar.classList.add('hidden');
    }

    const filas = document.querySelectorAll('.usuario-row');
    filas.forEach(fila => {
        const nombre = (fila.getAttribute('data-nombre') || '').toLowerCase();
        const apellido = (fila.getAttribute('data-apellido') || '').toLowerCase();
        const email = (fila.getAttribute('data-email') || '').toLowerCase();

        if (nombre.includes(filtro) || apellido.includes(filtro) || email.includes(filtro)) {
            fila.style.display = '';
            contador++;
        } else {
            fila.style.display = 'none';
        }
    });

    const contadorElement = document.getElementById('resultadosContador');
    if (filtro === '') {
        contadorElement.innerHTML = '';
    } else {
        if (contador > 0) {
            contadorElement.innerHTML = `
                    <div class="flex items-center p-3 bg-blue-50 border border-blue-200 rounded-lg">
                        <svg class="w-5 h-5 text-blue-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                        </svg>
                        <p class="text-sm text-blue-700">
                            Mostrando <strong>${contador}</strong> usuario${contador !== 1 ? 's' : ''} que coinciden con <strong>"${inputFiltro.value}"</strong>
                        </p>
                    </div>
                `;
        } else {
            contadorElement.innerHTML = `
                    <div class="flex items-center p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                        <svg class="w-5 h-5 text-yellow-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
                        </svg>
                        <p class="text-sm text-yellow-700">
                            No se encontraron usuarios que coincidan con <strong>"${inputFiltro.value}"</strong>
                        </p>
                    </div>
                `;
        }
    }
}

function limpiarFiltro() {
    const inputFiltro = document.getElementById('filtroNombre');
    inputFiltro.value = '';
    inputFiltro.focus();
    filtrarPorNombre();
}
