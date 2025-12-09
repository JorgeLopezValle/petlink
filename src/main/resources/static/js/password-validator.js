function iniciarValidadorPassword() {
    const inputPassword = document.getElementById('password');
    const inputConfirmar = document.getElementById('confirmarPassword');
    const contenedorFuerza = document.getElementById('password-strength-container');

    if (!inputPassword) return;

    if (contenedorFuerza) {
        contenedorFuerza.innerHTML = `
            <div class="mt-2">
                <div class="h-2 bg-gray-200 rounded-full overflow-hidden">
                    <div id="barra-fuerza" class="h-full transition-all duration-300" style="width: 0%; background-color: #ef4444;"></div>
                </div>
                <p id="texto-fuerza" class="text-xs mt-1 font-medium"></p>
                <ul id="lista-requisitos" class="text-xs mt-2 space-y-1 text-gray-600">
                    <li id="req-longitud"><span class="icono-req">○</span> Mínimo 8 caracteres</li>
                    <li id="req-mayuscula"><span class="icono-req">○</span> Al menos una mayúscula</li>
                    <li id="req-minuscula"><span class="icono-req">○</span> Al menos una minúscula</li>
                    <li id="req-numero"><span class="icono-req">○</span> Al menos un número</li>
                    <li id="req-especial"><span class="icono-req">○</span> Al menos un carácter especial</li>
                </ul>
            </div>
        `;
    }

    inputPassword.addEventListener('input', validarPassword);
    if (inputConfirmar) {
        inputConfirmar.addEventListener('input', validarCoincidencia);
    }

    function validarPassword() {
        const password = inputPassword.value;

        if (!contenedorFuerza) return;
        contenedorFuerza.style.display = 'block';

        const tieneLogitud = password.length >= 8;
        const tieneMayuscula = /[A-Z]/.test(password);
        const tieneMinuscula = /[a-z]/.test(password);
        const tieneNumero = /\d/.test(password);
        const tieneEspecial = /[^A-Za-z0-9]/.test(password);

        let puntuacion = 0;
        if (tieneLogitud) puntuacion++;
        if (tieneMayuscula) puntuacion++;
        if (tieneMinuscula) puntuacion++;
        if (tieneNumero) puntuacion++;
        if (tieneEspecial) puntuacion++;

        const barra = document.getElementById('barra-fuerza');
        const texto = document.getElementById('texto-fuerza');

        const anchos = ['0%', '20%', '40%', '60%', '80%', '100%'];
        const colores = ['#ef4444', '#ef4444', '#f97316', '#eab308', '#84cc16', '#22c55e'];
        const textos = ['', 'Muy débil', 'Débil', 'Media', 'Fuerte', 'Muy fuerte'];

        barra.style.width = anchos[puntuacion];
        barra.style.backgroundColor = colores[puntuacion];
        texto.style.color = colores[puntuacion];
        texto.textContent = textos[puntuacion] ? 'Seguridad: ' + textos[puntuacion] : '';

        actualizarRequisito('req-longitud', tieneLogitud);
        actualizarRequisito('req-mayuscula', tieneMayuscula);
        actualizarRequisito('req-minuscula', tieneMinuscula);
        actualizarRequisito('req-numero', tieneNumero);
        actualizarRequisito('req-especial', tieneEspecial);

        if (inputConfirmar?.value) {
            validarCoincidencia();
        }
    }

    function actualizarRequisito(id, cumplido) {
        const item = document.getElementById(id);
        if (item) {
            const icono = item.querySelector('.icono-req');
            if (cumplido) {
                icono.textContent = '●';
                icono.style.color = '#22c55e';
                item.style.color = '#22c55e';
            } else {
                icono.textContent = '○';
                icono.style.color = '#6b7280';
                item.style.color = '#6b7280';
            }
        }
    }

    function validarCoincidencia() {
        if (!inputConfirmar) return;

        const password = inputPassword.value;
        const confirmar = inputConfirmar.value;

        const feedbackAnterior = document.getElementById('feedback-coincidencia');
        if (feedbackAnterior) feedbackAnterior.remove();

        if (confirmar.length === 0) {
            inputConfirmar.classList.remove(
                'border-red-500', 'border-green-500',
                'focus:border-red-500', 'focus:border-green-500',
                'focus:ring-red-500', 'focus:ring-green-500'
            );
            return;
        }

        const coinciden = password === confirmar;

        if (coinciden) {
            inputConfirmar.classList.remove('border-red-500', 'focus:border-red-500', 'focus:ring-red-500');
            inputConfirmar.classList.add('border-green-500', 'focus:border-green-500', 'focus:ring-green-500');
        } else {
            inputConfirmar.classList.remove('border-green-500', 'focus:border-green-500', 'focus:ring-green-500');
            inputConfirmar.classList.add('border-red-500', 'focus:border-red-500', 'focus:ring-red-500');
        }

        const feedback = document.createElement('p');
        feedback.id = 'feedback-coincidencia';
        feedback.className = 'text-sm mt-2 ' + (coinciden ? 'text-green-600' : 'text-red-600');
        feedback.textContent = coinciden ? '✓ Las contraseñas coinciden' : '✗ Las contraseñas no coinciden';
        inputConfirmar.parentElement.parentElement.appendChild(feedback);
    }
}
