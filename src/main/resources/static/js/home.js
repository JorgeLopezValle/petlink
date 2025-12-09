function scrollSlider(id, direction) {
    const slider = document.getElementById(id);
    const firstCard = slider.querySelector('.mascota-card');
    if (!firstCard) return;

    const gap = window.innerWidth < 640 ? 12 : 16;
    const cardWidth = firstCard.offsetWidth + gap;
    const cardsToScroll = window.innerWidth < 640 ? 1 : 3;

    slider.scrollBy({
        left: direction * cardWidth * cardsToScroll,
        behavior: 'smooth'
    });
}

function actualizarBotonesSlider() {
    const sliders = document.querySelectorAll('[id^="slider-"]');
    sliders.forEach(slider => {
        const seccion = slider.closest('[class*="mb-"]');
        if (!seccion) return;

        const botones = seccion.querySelector('.flex.space-x-2');
        if (!botones) return;

        const necesitaScroll = slider.scrollWidth > slider.clientWidth;
        botones.style.display = necesitaScroll ? 'flex' : 'none';
    });
}

window.addEventListener('resize', actualizarBotonesSlider);
window.addEventListener('load', actualizarBotonesSlider);

function aplicarFiltros() {
    const nombre = document.getElementById('filtroNombreMascota').value.toLowerCase().trim();
    const raza = document.getElementById('filtroRazaMascota').value.toLowerCase().trim();
    const edadMax = parseInt(document.getElementById('filtroEdadMascota').value) || 20;

    const cards = document.querySelectorAll('.mascota-card');
    let visibles = 0;

    cards.forEach(card => {
        const cardNombre = card.querySelector('h3')?.textContent.toLowerCase() || '';
        const razaElement = card.querySelector('.bg-blue-50 .text-sm');
        const cardRaza = razaElement?.textContent.toLowerCase() || '';
        const edadElement = card.querySelector('.bg-orange-50 .text-sm');
        const cardEdadText = edadElement?.textContent || '0';
        const cardEdad = parseInt(cardEdadText) || 0;

        const cumpleNombre = !nombre || cardNombre.includes(nombre);
        const cumpleRaza = !raza || cardRaza.includes(raza);
        const cumpleEdad = edadMax >= 20 || cardEdad <= edadMax;

        if (cumpleNombre && cumpleRaza && cumpleEdad) {
            card.style.display = '';
            visibles++;
        } else {
            card.style.display = 'none';
        }
    });

    const resultado = document.getElementById('resultadosFiltro');
    if (nombre || raza || edadMax < 20) {
        resultado.innerHTML = `<p class="text-xs text-blue-600 text-center font-medium">${visibles} encontrada${visibles !== 1 ? 's' : ''}</p>`;
    } else {
        resultado.innerHTML = '<p class="text-xs text-gray-500 text-center">Todas las mascotas</p>';
    }

    actualizarBotonesSlider();
}

function limpiarTodosFiltros() {
    document.getElementById('filtroNombreMascota').value = '';
    document.getElementById('filtroRazaMascota').value = '';
    document.getElementById('filtroEdadMascota').value = '20';
    document.getElementById('edadValor').textContent = '20';
    aplicarFiltros();
}
