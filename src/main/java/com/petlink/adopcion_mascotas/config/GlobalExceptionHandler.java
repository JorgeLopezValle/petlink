package com.petlink.adopcion_mascotas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {

        logger.error("Error en la aplicación: {}", sanitizeMessage(e.getMessage()), e);

        model.addAttribute("error", true);
        model.addAttribute("errorMessage", "Ha ocurrido un error. Por favor, inténtalo de nuevo más tarde.");
        model.addAttribute("view", "home/home");
        return "_t/frame";
    }

    
    private String sanitizeMessage(String message) {
        if (message == null) {
            return "Error desconocido";
        }

        String sanitized = message
            .replaceAll("password=[^&\\s]+", "password=***")
            .replaceAll("token=[^&\\s]+", "token=***")
            .replaceAll("apikey=[^&\\s]+", "apikey=***")
            .replaceAll("secret=[^&\\s]+", "secret=***")
            .replaceAll("authorization:\\s*[^\\s]+", "authorization: ***")
            .replaceAll("bearer\\s+[^\\s]+", "bearer ***")
            .replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", "***@***.***");

        return sanitized;
    }
}
