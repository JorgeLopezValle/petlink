function aplicarFiltros() {
    const nombre = document.getElementById('filtroNombreMascota').value.toLowerCase().trim();
    const raza = document.getElementById('filtroRazaMascota').value.toLowerCase().trim();
    const especie = document.getElementById('filtroEspecieMascota').value.toLowerCase();
    const edadMax = parseInt(document.getElementById('filtroEdadMascota').value);

    const mascotas = document.querySelectorAll('.mascota-card');
    let contador = 0;

    mascotas.forEach(mascota => {
        const nombreMascota = mascota.getAttribute('data-nombre').toLowerCase();
        const razaMascota = mascota.getAttribute('data-raza').toLowerCase();
        const especieMascota = mascota.getAttribute('data-especie').toLowerCase();
        const edadMascota = parseInt(mascota.getAttribute('data-edad'));

        const cumpleNombre = nombre === '' || nombreMascota.includes(nombre);
        const cumpleRaza = raza === '' || razaMascota.includes(raza);
        const cumpleEspecie = especie === '' || especieMascota.includes(especie);
        const cumpleEdad = edadMascota <= edadMax;

        if (cumpleNombre && cumpleRaza && cumpleEspecie && cumpleEdad) {
            mascota.style.display = '';
            contador++;
        } else {
            mascota.style.display = 'none';
        }
    });

    actualizarResultados(contador, mascotas.length);
}

function actualizarResultados(visibles, total) {
    const resultadosElement = document.getElementById('resultadosFiltro');
    const contadorElement = document.getElementById('contadorMascotas');

    if (visibles === total) {
        resultadosElement.innerHTML = `
            <p class="text-sm text-gray-500 text-center">Mostrando todas las mascotas</p>
        `;
        contadorElement.textContent = '';
    } else if (visibles === 0) {
        resultadosElement.innerHTML = `
            <div class="text-center">
                <svg class="w-12 h-12 mx-auto mb-2 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <p class="text-sm text-gray-600 font-medium">No se encontraron mascotas</p>
                <p class="text-xs text-gray-400 mt-1">Intenta con otros filtros</p>
            </div>
        `;
        contadorElement.textContent = '0 resultados';
    } else {
        resultadosElement.innerHTML = `
            <div class="flex items-center justify-center p-3 bg-blue-50 rounded-lg">
                <svg class="w-5 h-5 text-blue-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <p class="text-sm text-blue-700">
                    <strong>${visibles}</strong> de ${total} mascotas
                </p>
            </div>
        `;
        contadorElement.textContent = `${visibles} de ${total}`;
    }
}

function limpiarTodosFiltros() {
    document.getElementById('filtroNombreMascota').value = '';
    document.getElementById('filtroRazaMascota').value = '';
    document.getElementById('filtroEspecieMascota').value = '';
    document.getElementById('filtroEdadMascota').value = '20';
    document.getElementById('edadValor').textContent = '20';

    const mascotas = document.querySelectorAll('.mascota-card');
    mascotas.forEach(mascota => {
        mascota.style.display = '';
    });

    actualizarResultados(mascotas.length, mascotas.length);
}
