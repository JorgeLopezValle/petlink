package com.petlink.adopcion_mascotas.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class IntentosLoginService {

    private static final int MAX_INTENTOS = 5;

    private static final int DURACION_BLOQUEO_MINUTOS = 15;

    private static final int DURACION_RESET_MINUTOS = 30;

    private final Map<String, InfoIntentoLogin> intentosLogin = new ConcurrentHashMap<>();

   
    public void loginFallido(String direccionIp) {
        InfoIntentoLogin infoIntento = intentosLogin.getOrDefault(direccionIp, new InfoIntentoLogin());
        infoIntento.incrementarIntentos();
        intentosLogin.put(direccionIp, infoIntento);
    }

    
    public void loginExitoso(String direccionIp) {
        intentosLogin.remove(direccionIp);
    }

    public boolean estaBloqueado(String direccionIp) {
        InfoIntentoLogin infoIntento = intentosLogin.get(direccionIp);

        if (infoIntento == null) {
            return false;
        }

        if (infoIntento.debeResetear()) {
            intentosLogin.remove(direccionIp);
            return false;
        }

        return infoIntento.estaBloqueado();
    }

    public int getIntentosRestantes(String direccionIp) {
        InfoIntentoLogin infoIntento = intentosLogin.get(direccionIp);
        if (infoIntento == null) {
            return MAX_INTENTOS;
        }
        return Math.max(0, MAX_INTENTOS - infoIntento.getIntentos());
    }

    public long getMinutosBloqueoRestantes(String direccionIp) {
        InfoIntentoLogin infoIntento = intentosLogin.get(direccionIp);
        if (infoIntento == null || !infoIntento.estaBloqueado()) {
            return 0;
        }
        return infoIntento.getMinutosBloqueoRestantes();
    }

    private static class InfoIntentoLogin {
        private int intentos = 0;
        private LocalDateTime tiempoUltimoIntento = LocalDateTime.now();
        private LocalDateTime tiempoInicioBloqueo;

        public void incrementarIntentos() {
            this.intentos++;
            this.tiempoUltimoIntento = LocalDateTime.now();

            if (this.intentos >= MAX_INTENTOS && this.tiempoInicioBloqueo == null) {
                this.tiempoInicioBloqueo = LocalDateTime.now();
            }
        }

        public int getIntentos() {
            return intentos;
        }

        public boolean estaBloqueado() {
            if (intentos < MAX_INTENTOS) {
                return false;
            }

            if (tiempoInicioBloqueo != null) {
                LocalDateTime tiempoFinBloqueo = tiempoInicioBloqueo.plusMinutes(DURACION_BLOQUEO_MINUTOS);
                if (LocalDateTime.now().isAfter(tiempoFinBloqueo)) {
                    this.intentos = 0;
                    this.tiempoInicioBloqueo = null;
                    return false;
                }
            }

            return true;
        }

        public boolean debeResetear() {
            return LocalDateTime.now().isAfter(tiempoUltimoIntento.plusMinutes(DURACION_RESET_MINUTOS));
        }

        public long getMinutosBloqueoRestantes() {
            if (tiempoInicioBloqueo == null) {
                return 0;
            }
            LocalDateTime tiempoFinBloqueo = tiempoInicioBloqueo.plusMinutes(DURACION_BLOQUEO_MINUTOS);
            LocalDateTime ahora = LocalDateTime.now();

            if (ahora.isAfter(tiempoFinBloqueo)) {
                return 0;
            }

            return java.time.Duration.between(ahora, tiempoFinBloqueo).toMinutes();
        }
    }
}
