package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.dto.MascotaDTO;
import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.service.MascotaService;
import com.petlink.adopcion_mascotas.service.MinioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/mascota")
public class MascotaController {

  @Autowired
  private MascotaService mascotaService;

  @Autowired
  private MinioService minioService;

  @GetMapping("r")
  public String r(@RequestParam(required = false) String raza, ModelMap m, HttpSession session) {
    Rol rol = (Rol) session.getAttribute("rol");
    String emailProtectora = (String) session.getAttribute("emailProtectora");

    if (rol == null && emailProtectora == null) {
      return "redirect:/auth/login";
    }

    List<Mascota> mascotas;

    if (emailProtectora != null) {
      mascotas = mascotaService.buscarPorProtectora(emailProtectora);
    } else if (rol == Rol.ADMIN) {
      if (raza != null && !raza.trim().isEmpty()) {
        mascotas = mascotaService.buscarPorRaza(raza);
      } else {
        mascotas = mascotaService.r();
      }
      m.put("razaFiltro", raza != null ? raza : "");
    } else {
      return "redirect:/auth/login";
    }

    m.put("mascotas", mascotas);
    m.put("view", "mascota/r");
    return "_t/frame";
  }

  @GetMapping("c")
  public String c(ModelMap m, HttpSession session) {
    String emailProtectora = (String) session.getAttribute("emailProtectora");

    if (emailProtectora == null) {
      m.put("error", true);
      m.put("errorMessage", "Debe iniciar sesión como protectora para registrar mascotas");
      m.put("view", "auth/login");
      return "_t/frame";
    }

    m.put("mascotaDTO", new MascotaDTO());
    m.put("view", "mascota/c");
    return "_t/frame";
  }

  @PostMapping("c")
  public String cPost(@Valid MascotaDTO mascotaDTO, BindingResult bindingResult, @RequestParam(required = false)  MultipartFile[] imagenes, HttpSession session, ModelMap m) {

    String emailProtectora = (String) session.getAttribute("emailProtectora");

    if (imagenes == null || imagenes.length == 0 ||
        (imagenes.length == 1 && imagenes[0].isEmpty())) {
      bindingResult.rejectValue("imagenUrl", "error.imagenes", "Debe subir al menos una imagen de la mascota");
    }

    if (bindingResult.hasErrors()) {
      m.put("mascotaDTO", mascotaDTO);
      m.put("view", "mascota/c");
      return "_t/frame";
    }

    try {
      if (emailProtectora == null) {
        throw new Exception("Debe iniciar sesión como protectora");
      }

      try {
        String urlsImagenes = minioService.uploadMultipleImages(
            imagenes,
            emailProtectora,
            mascotaDTO.getNombre());
        mascotaDTO.setImagenUrl(urlsImagenes);
      } catch (Exception e) {
        bindingResult.rejectValue("imagenUrl", "error.imagenes", "Error al subir las imágenes: " + e.getMessage());
        m.put("mascotaDTO", mascotaDTO);
        m.put("view", "mascota/c");
        return "_t/frame";
      }

      mascotaService.c(mascotaDTO, emailProtectora);

      m.put("success", true);
      m.put("successMessage", "Mascota registrada correctamente");
      return "redirect:/mascota/r";

    } catch (Exception e) {
      m.put("error", true);
      m.put("errorMessage", "Error al registrar la mascota. Por favor, verifica los datos e inténtalo de nuevo.");
      m.put("mascotaDTO", mascotaDTO);
      m.put("view", "mascota/c");
      return "_t/frame";
    }
  }

  @GetMapping("u")
  public String u(@RequestParam Long id, ModelMap m, HttpSession session) {
    String emailProtectora = (String) session.getAttribute("emailProtectora");

    if (emailProtectora == null) {
      m.put("error", true);
      m.put("errorMessage", "Debe iniciar sesión como protectora");
      m.put("view", "auth/login");
      return "_t/frame";
    }

    try {
      Optional<Mascota> mascotaOpt = mascotaService.buscarPorId(id);

      if (mascotaOpt.isEmpty()) {
        m.put("error", true);
        m.put("errorMessage", "Mascota no encontrada");
        return "redirect:/mascota/r";
      }

      Mascota mascota = mascotaOpt.get();

      if (!mascota.getProtectora().getEmail().equals(emailProtectora)) {
        m.put("error", true);
        m.put("errorMessage", "No tiene permisos para editar esta mascota");
        return "redirect:/mascota/r";
      }

      if (mascota.isAdoptada()) {
        m.put("error", true);
        m.put("errorMessage", "No se puede editar una mascota que ya ha sido adoptada");
        return "redirect:/mascota/r";
      }

      MascotaDTO mascotaDTO = new MascotaDTO();
      mascotaDTO.setNombre(mascota.getNombre());
      mascotaDTO.setEspecie(mascota.getEspecie());
      mascotaDTO.setRaza(mascota.getRaza());
      mascotaDTO.setEdad(mascota.getEdad());
      mascotaDTO.setPeso(mascota.getPeso());
      mascotaDTO.setDescripcion(mascota.getDescripcion());
      mascotaDTO.setImagenUrl(mascota.getImagenUrl());

      m.put("mascotaDTO", mascotaDTO);
      m.put("mascotaId", id);
      m.put("view", "mascota/u");
      return "_t/frame";

    } catch (Exception e) {
      m.put("error", true);
      m.put("errorMessage", "Error al cargar la mascota");
      return "redirect:/mascota/r";
    }
  }

  @PostMapping("u")
  public String uPost(@RequestParam Long id, @Valid MascotaDTO mascotaDTO, BindingResult bindingResult, @RequestParam(required = false) MultipartFile[] imagenes, @RequestParam(required = false) String imagenesEliminadas, HttpSession session, ModelMap m) {
    String emailProtectora = (String) session.getAttribute("emailProtectora");

    if (emailProtectora == null) {
      return "redirect:/auth/login";
    }

    if (bindingResult.hasErrors()) {
      m.put("mascotaDTO", mascotaDTO);
      m.put("mascotaId", id);
      m.put("view", "mascota/u");
      return "_t/frame";
    }

    try {
      Optional<Mascota> mascotaOpt = mascotaService.buscarPorId(id);
      if (mascotaOpt.isEmpty()) {
        m.put("error", true);
        m.put("errorMessage", "Mascota no encontrada");
        return "redirect:/mascota/r";
      }

      Mascota mascota = mascotaOpt.get();
      if (mascota.isAdoptada()) {
        m.put("error", true);
        m.put("errorMessage", "No se puede editar una mascota que ya ha sido adoptada");
        return "redirect:/mascota/r";
      }

      String imagenesFinales = mascota.getImagenUrl();
      if (imagenesEliminadas != null && !imagenesEliminadas.trim().isEmpty() && imagenesFinales != null) {
        String[] urlsEliminadas = imagenesEliminadas.split(",");
        String[] urlsActuales = imagenesFinales.split(",");
        StringBuilder nuevasUrls = new StringBuilder();

        for (String urlActual : urlsActuales) {
          boolean eliminar = false;
          for (String urlEliminada : urlsEliminadas) {
            if (urlActual.trim().equals(urlEliminada.trim())) {
              eliminar = true;
              try {
                minioService.deleteImage(urlActual.trim());
              } catch (Exception e) {
                System.err.println("Error eliminando imagen de MinIO: " + e.getMessage());
              }
              break;
            }
          }
          if (!eliminar) {
            if (nuevasUrls.length() > 0) {
              nuevasUrls.append(",");
            }
            nuevasUrls.append(urlActual.trim());
          }
        }
        imagenesFinales = nuevasUrls.toString();
      }

      if (imagenes != null && imagenes.length > 0 && !imagenes[0].isEmpty()) {
        try {
          String urlsNuevasImagenes = minioService.uploadMultipleImages(imagenes, emailProtectora, mascota.getNombre());
          if (imagenesFinales != null && !imagenesFinales.isEmpty()) {
            imagenesFinales = imagenesFinales + "," + urlsNuevasImagenes;
          } else {
            imagenesFinales = urlsNuevasImagenes;
          }
        } catch (Exception e) {
          bindingResult.rejectValue("imagenUrl", "error.imagenes", "Error al subir las imágenes: " + e.getMessage());
          m.put("mascotaDTO", mascotaDTO);
          m.put("mascotaId", id);
          m.put("view", "mascota/u");
          return "_t/frame";
        }
      }

      if (imagenesFinales == null || imagenesFinales.trim().isEmpty()) {
        bindingResult.rejectValue("imagenUrl", "error.imagenes", "Debe mantener al menos una imagen de la mascota");
        mascotaDTO.setImagenUrl(mascota.getImagenUrl());
        m.put("mascotaDTO", mascotaDTO);
        m.put("mascotaId", id);
        m.put("view", "mascota/u");
        return "_t/frame";
      }

      mascotaDTO.setImagenUrl(imagenesFinales);
      mascotaService.u(id, mascotaDTO, emailProtectora);

      m.put("success", true);
      m.put("successMessage", "Mascota actualizada correctamente");
      return "redirect:/mascota/r";

    } catch (Exception e) {
      m.put("error", true);
      m.put("errorMessage", "Error al actualizar la mascota: " + e.getMessage());
      m.put("mascotaDTO", mascotaDTO);
      m.put("mascotaId", id);
      m.put("view", "mascota/u");
      return "_t/frame";
    }
  }

  @PostMapping("d")
  public String d(@RequestParam Long id, HttpSession session, ModelMap m) {
    String emailProtectora = (String) session.getAttribute("emailProtectora");
    Rol rol = (Rol) session.getAttribute("rol");

    if (emailProtectora == null && rol == null) {
      return "redirect:/auth/login";
    }

    try {
      Optional<Mascota> mascotaOpt = mascotaService.buscarPorId(id);
      if (mascotaOpt.isPresent() && mascotaOpt.get().isAdoptada()) {
        m.put("error", true);
        m.put("errorMessage", "No se puede eliminar una mascota que ya ha sido adoptada");
        return "redirect:/mascota/r";
      }

      boolean esAdmin = (rol != null && rol == Rol.ADMIN);
      mascotaService.d(id, emailProtectora, esAdmin);
      m.put("success", true);
      m.put("successMessage", "Mascota eliminada correctamente");
    } catch (Exception e) {
      m.put("error", true);
      m.put("errorMessage", "Error al eliminar la mascota: " + e.getMessage());
    }

    return "redirect:/mascota/r";
  }

  @PostMapping("adoptar/{id}")
  public String adoptar(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session, ModelMap m) {
    String emailUsuario = (String) session.getAttribute("emailUsuario");
    String emailProtectora = (String) session.getAttribute("emailProtectora");
    Rol rol = (Rol) session.getAttribute("rol");

    if (emailUsuario == null && emailProtectora == null && rol == null) {
      m.put("error", true);
      m.put("errorMessage", "Debes estar logueado para adoptar una mascota");
      m.put("view", "auth/login");
      return "_t/frame";
    }

    if (emailProtectora != null) {
      m.put("error", true);
      m.put("errorMessage", "Las protectoras no pueden adoptar mascotas");
      return "redirect:/";
    }

    if (rol != null && rol == Rol.ADMIN) {
      m.put("error", true);
      m.put("errorMessage", "Los administradores no pueden adoptar mascotas");
      return "redirect:/";
    }

    if (rol != null && rol != Rol.USUARIO) {
      m.put("error", true);
      m.put("errorMessage", "Solo los usuarios pueden adoptar mascotas");
      return "redirect:/";
    }

    try {
      mascotaService.adoptar(id, emailUsuario);
      m.put("success", true);
      m.put("successMessage", "¡Felicidades! Has adoptado la mascota correctamente");
    } catch (Exception e) {
      m.put("error", true);
      m.put("errorMessage", "Error al adoptar la mascota: " + e.getMessage());
    }

    return "redirect:/";
  }

  @GetMapping("mis-adopciones")
  public String misAdopciones(HttpSession session, ModelMap m) {
    String emailUsuario = (String) session.getAttribute("emailUsuario");
    Rol rol = (Rol) session.getAttribute("rol");

    if (emailUsuario == null || rol == null) {
      return "redirect:/auth/login";
    }

    if (rol == Rol.ADMIN) {
      m.put("error", true);
      m.put("errorMessage", "Los administradores no tienen adopciones");
      return "redirect:/";
    }

    List<Mascota> mascotasAdoptadas = mascotaService.buscarPorUsuario(emailUsuario);
    m.put("mascotas", mascotasAdoptadas);
    m.put("view", "mascota/mis-adopciones");
    return "_t/frame";
  }

  @GetMapping("detalle/{id}")
  public String detalle(@org.springframework.web.bind.annotation.PathVariable Long id, ModelMap m) {
    try {
      Optional<Mascota> mascotaOpt = mascotaService.buscarPorId(id);

      if (mascotaOpt.isEmpty()) {
        m.put("error", true);
        m.put("errorMessage", "Mascota no encontrada");
        return "redirect:/";
      }

      Mascota mascota = mascotaOpt.get();
      m.put("mascota", mascota);
      m.put("view", "mascota/detalle");
      return "_t/frame";

    } catch (Exception e) {
      m.put("error", true);
      m.put("errorMessage", "Error al cargar los detalles de la mascota");
      return "redirect:/";
    }
  }
}