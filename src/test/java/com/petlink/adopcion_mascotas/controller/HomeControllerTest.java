package com.petlink.adopcion_mascotas.controller;

import com.petlink.adopcion_mascotas.model.Mascota;
import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.service.MascotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
@DisplayName("Tests para HomeController")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MascotaService mascotaService;

    private List<Mascota> mascotas;

    @BeforeEach
    void setUp() {
        Protectora protectora = new Protectora();
        protectora.setEmail("protectora@test.com");
        protectora.setNombre("Protectora Test");
        protectora.setCiudad(com.petlink.adopcion_mascotas.enums.Ciudad.MADRID);

        Mascota perro1 = new Mascota();
        perro1.setId(1L);
        perro1.setNombre("Max");
        perro1.setEspecie("Perro");
        perro1.setProtectora(protectora);
        perro1.setAdoptada(false);

        Mascota perro2 = new Mascota();
        perro2.setId(2L);
        perro2.setNombre("Luna");
        perro2.setEspecie("Perro");
        perro2.setProtectora(protectora);
        perro2.setAdoptada(true);

        Mascota gato1 = new Mascota();
        gato1.setId(3L);
        gato1.setNombre("Michi");
        gato1.setEspecie("Gato");
        gato1.setProtectora(protectora);
        gato1.setAdoptada(false);

        mascotas = Arrays.asList(perro1, perro2, gato1);
    }

    @Test
    @WithMockUser
    @DisplayName("Debe mostrar página principal")
    void debeMostrarPaginaPrincipal() throws Exception {
        when(mascotaService.r()).thenReturn(mascotas);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("_t/frame"))
                .andExpect(model().attributeExists("mascotasPorEspecie"))
                .andExpect(model().attribute("view", "home/home"));
    }

    @Test
    @WithMockUser
    @DisplayName("Debe agrupar mascotas por especie")
    void debeAgruparMascotasPorEspecie() throws Exception {
        when(mascotaService.r()).thenReturn(mascotas);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("mascotasPorEspecie"));
    }

    @Test
    @WithMockUser
    @DisplayName("Debe manejar lista vacía de mascotas")
    void debeManjerarListaVacia() throws Exception {
        when(mascotaService.r()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("_t/frame"))
                .andExpect(model().attributeExists("mascotasPorEspecie"));
    }
}
