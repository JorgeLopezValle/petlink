let indiceActual = 0;

function mostrarImagen(indice) {
    const items = document.querySelectorAll('.carousel-item');
    const indicadores = document.querySelectorAll('.indicator-dot');

    if (items.length === 0) return;

    if (indice >= items.length) {
        indiceActual = 0;
    } else if (indice < 0) {
        indiceActual = items.length - 1;
    } else {
        indiceActual = indice;
    }

    items.forEach((item, i) => {
        if (i === indiceActual) {
            item.classList.remove('hidden');
        } else {
            item.classList.add('hidden');
        }
    });

    if (indicadores.length > 0) {
        indicadores.forEach((dot, i) => {
            if (i === indiceActual) {
                dot.classList.remove('bg-white/50');
                dot.classList.add('bg-white');
            } else {
                dot.classList.remove('bg-white');
                dot.classList.add('bg-white/50');
            }
        });
    }
}

function cambiarImagen(direccion) {
    mostrarImagen(indiceActual + direccion);
}

function irAImagen(indice) {
    mostrarImagen(indice);
}

document.addEventListener('DOMContentLoaded', function() {
    mostrarImagen(0);

    document.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowLeft') {
            cambiarImagen(-1);
        } else if (e.key === 'ArrowRight') {
            cambiarImagen(1);
        }
    });
});
