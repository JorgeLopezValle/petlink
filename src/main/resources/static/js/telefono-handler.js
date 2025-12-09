document.addEventListener('DOMContentLoaded', function () {
    const variantes = [
        { prefijo: 'prefijo', numero: 'telefono-numero' },
        { prefijo: 'prefijo-usuario', numero: 'telefono-numero-usuario' },
        { prefijo: 'prefijo-protectora', numero: 'telefono-numero-protectora' }
    ];

    variantes.forEach(function (ids) {
        const prefijo = document.getElementById(ids.prefijo);
        const numero = document.getElementById(ids.numero);
        const telefonoHidden = document.getElementById('telefono');

        if (!prefijo || !numero || !telefonoHidden) {
            return;
        }

        function inicializarDesdeHidden() {
            const valorActual = telefonoHidden.value.trim();
            console.log('[Telefono] Valor inicial del campo hidden:', valorActual);

            if (valorActual && valorActual.length > 0) {
                const espacioIndex = valorActual.indexOf(' ');

                if (espacioIndex > 0) {
                    const prefijoValor = valorActual.substring(0, espacioIndex);
                    const numeroValor = valorActual.substring(espacioIndex + 1);

                    console.log('[Telefono] Formato con prefijo - Prefijo:', prefijoValor, 'Número:', numeroValor);

                    prefijo.value = prefijoValor;
                    numero.value = numeroValor;
                } else if (valorActual.startsWith('+')) {
                    const match = valorActual.match(/^(\+\d{1,3})(\d+)$/);
                    if (match) {
                        prefijo.value = match[1];
                        numero.value = match[2];
                        console.log('[Telefono] Formato sin espacio - Prefijo:', match[1], 'Número:', match[2]);
                    } else {
                        prefijo.value = '+34';
                        numero.value = valorActual.substring(1);
                        console.log('[Telefono] Formato inválido con +, usando +34');
                    }
                } else {
                    prefijo.value = '+34';
                    numero.value = valorActual;
                    console.log('[Telefono] Solo número sin prefijo, asumiendo +34. Número:', valorActual);
                }

                console.log('[Telefono] Campos finales - Prefijo:', prefijo.value, 'Número:', numero.value);

                actualizarTelefono();
            } else {
                console.log('[Telefono] Campo hidden vacío');
            }
        }

        function actualizarTelefono() {
            const numeroLimpio = numero.value.trim();
            if (numeroLimpio) {
                telefonoHidden.value = prefijo.value + ' ' + numeroLimpio;
            } else {
                telefonoHidden.value = '';
            }
        }

        inicializarDesdeHidden();

        prefijo.addEventListener('change', actualizarTelefono);
        numero.addEventListener('input', actualizarTelefono);
    });
});
