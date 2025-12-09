package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.dto.ContactoDTO;
import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.repository.ProtectoraRepository;
import com.petlink.adopcion_mascotas.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/contacto")
public class ContactoController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ProtectoraRepository protectoraRepository;

    @GetMapping
    public String mostrarFormulario(
            @RequestParam(required = false) String protectora,
            ModelMap m) {

        ContactoDTO contactoDTO = new ContactoDTO();

        if (protectora != null && !protectora.isBlank()) {
            Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(protectora);
            if (protectoraOpt.isPresent()) {
                m.put("protectora", protectoraOpt.get());
                contactoDTO.setProtectoraEmail(protectora);
            }
        }

        m.put("contactoDTO", contactoDTO);
        m.put("view", "contacto/contacto");
        return "_t/frame";
    }

    @PostMapping
    public String enviarMensaje(
            @Valid @ModelAttribute ContactoDTO contactoDTO,
            BindingResult result,
            ModelMap m) {

        if (result.hasErrors()) {
            if (contactoDTO.getProtectoraEmail() != null && !contactoDTO.getProtectoraEmail().isBlank()) {
                Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(contactoDTO.getProtectoraEmail());
                protectoraOpt.ifPresent(protectora -> m.put("protectora", protectora));
            }
            m.put("view", "contacto/contacto");
            return "_t/frame";
        }

        try {
            if (contactoDTO.getProtectoraEmail() != null && !contactoDTO.getProtectoraEmail().isBlank()) {
                Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(contactoDTO.getProtectoraEmail());
                if (protectoraOpt.isPresent()) {
                    Protectora protectora = protectoraOpt.get();
                    emailService.enviarMensajeProtectora(
                            contactoDTO.getNombre(),
                            contactoDTO.getEmail(),
                            contactoDTO.getAsunto(),
                            contactoDTO.getMensaje(),
                            protectora.getEmail(),
                            protectora.getNombre());
                    m.put("success", true);
                    m.put("successMessage",
                            "Mensaje enviado correctamente a " + protectora.getNombre() + ". Te responderán pronto.");
                    m.put("protectora", protectora);
                } else {
                    m.put("error", true);
                    m.put("errorMessage", "Protectora no encontrada");
                }
            } else {
                emailService.enviarMensajeContacto(
                        contactoDTO.getNombre(),
                        contactoDTO.getEmail(),
                        contactoDTO.getAsunto(),
                        contactoDTO.getMensaje());
                m.put("success", true);
                m.put("successMessage", "Mensaje enviado correctamente. Nos pondremos en contacto contigo pronto.");
            }
        } catch (Exception e) {
            if (contactoDTO.getProtectoraEmail() != null && !contactoDTO.getProtectoraEmail().isBlank()) {
                Optional<Protectora> protectoraOpt = protectoraRepository.findByEmail(contactoDTO.getProtectoraEmail());
                protectoraOpt.ifPresent(protectora -> m.put("protectora", protectora));
            }
            m.put("error", true);
            m.put("errorMessage", "Error al enviar el mensaje. Por favor, inténtalo de nuevo más tarde.");
        }

        m.put("contactoDTO", new ContactoDTO());
        m.put("view", "contacto/contacto");
        return "_t/frame";
    }
}
