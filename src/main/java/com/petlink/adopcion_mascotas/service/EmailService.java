package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.model.Usuario;
import com.petlink.adopcion_mascotas.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarMensajeContacto(String nombre, String email, String asunto, String mensaje) {
        List<Usuario> admins = usuarioRepository.findByRol(Rol.ADMIN);

        if (admins.isEmpty()) {
            throw new RuntimeException("No hay administradores registrados para recibir mensajes");
        }

        String[] adminEmails = admins.stream()
            .map(Usuario::getEmail)
            .toArray(String[]::new);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("nombre", nombre);
            context.setVariable("email", email);
            context.setVariable("asunto", asunto);
            context.setVariable("mensaje", mensaje);
            context.setVariable("esProtectora", false);

            String htmlContent = templateEngine.process("contacto/template", context);

            helper.setFrom(fromEmail);
            helper.setTo(adminEmails);
            helper.setSubject("[PetLink Contacto] " + asunto);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage(), e);
        }
    }

    public void enviarMensajeProtectora(String nombre, String email, String asunto, String mensaje, String protectoraEmail, String protectoraNombre) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("destinatario", protectoraNombre);
            context.setVariable("nombre", nombre);
            context.setVariable("email", email);
            context.setVariable("asunto", asunto);
            context.setVariable("mensaje", mensaje);
            context.setVariable("esProtectora", true);

            String htmlContent = templateEngine.process("contacto/template", context);

            helper.setFrom(fromEmail);
            helper.setTo(protectoraEmail);
            helper.setReplyTo(email);
            helper.setSubject("[PetLink] " + asunto);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage(), e);
        }
    }
}
