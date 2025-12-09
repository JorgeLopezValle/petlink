package com.petlink.adopcion_mascotas.service;

import com.petlink.adopcion_mascotas.dto.ProtectoraDTO;
import com.petlink.adopcion_mascotas.enums.Ciudad;
import com.petlink.adopcion_mascotas.model.Protectora;
import com.petlink.adopcion_mascotas.repository.MascotaRepository;
import com.petlink.adopcion_mascotas.repository.ProtectoraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para ProtectoraService")
class ProtectoraServiceTest {

    @Mock
    private ProtectoraRepository protectoraRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private ProtectoraService protectoraService;

    private ProtectoraDTO protectoraDTO;
    private Protectora protectora;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        protectoraDTO = new ProtectoraDTO();
        protectoraDTO.setEmail("protectora@test.com");
        protectoraDTO.setNombre("protectora madrid");
        protectoraDTO.setDireccion("Calle Test 123");
        protectoraDTO.setCiudad(Ciudad.MADRID);
        protectoraDTO.setPassword("password123");
        protectoraDTO.setTelefono("+34600000000");

        protectora = new Protectora();
        protectora.setEmail("protectora@test.com");
        protectora.setNombre("Protectora Madrid");
        protectora.setDireccion("Calle Test 123");
        protectora.setCiudad(Ciudad.MADRID);
        protectora.setPassword(passwordEncoder.encode("password123"));
        protectora.setTelefono("+34600000000");
        protectora.setAprobado(true);
    }

    @Test
    @DisplayName("Debe crear protectora correctamente")
    void debeCrearProtectora() {
        when(protectoraRepository.save(any(Protectora.class))).thenReturn(protectora);

        protectoraService.c(protectoraDTO);

        verify(protectoraRepository, times(1)).save(any(Protectora.class));
    }

    @Test
    @DisplayName("Debe crear protectora con aprobado en false")
    void debeCrearProtectoraSinAprobar() {
        when(protectoraRepository.save(any(Protectora.class))).thenAnswer(invocation -> {
            Protectora p = invocation.getArgument(0);
            assertThat(p.getAprobado()).isFalse();
            return p;
        });

        protectoraService.c(protectoraDTO);

        verify(protectoraRepository).save(any(Protectora.class));
    }

    @Test
    @DisplayName("Debe capitalizar nombre al crear")
    void debeCapitalizarNombre() {
        when(protectoraRepository.save(any(Protectora.class))).thenAnswer(invocation -> {
            Protectora p = invocation.getArgument(0);
            assertThat(p.getNombre()).isEqualTo("Protectora madrid");
            return p;
        });

        protectoraService.c(protectoraDTO);

        verify(protectoraRepository).save(any(Protectora.class));
    }

    @Test
    @DisplayName("Debe encriptar contraseña al crear")
    void debeEncriptarPassword() {
        when(protectoraRepository.save(any(Protectora.class))).thenAnswer(invocation -> {
            Protectora p = invocation.getArgument(0);
            assertThat(p.getPassword()).isNotEqualTo("password123");
            assertThat(p.getPassword()).startsWith("$2a$");
            return p;
        });

        protectoraService.c(protectoraDTO);

        verify(protectoraRepository).save(any(Protectora.class));
    }

    @Test
    @DisplayName("Debe listar todas las protectoras")
    void debeListarTodasProtectoras() {
        Protectora protectora2 = new Protectora();
        protectora2.setEmail("otra@test.com");
        List<Protectora> protectoras = Arrays.asList(protectora, protectora2);

        when(protectoraRepository.findAll()).thenReturn(protectoras);

        List<Protectora> resultado = protectoraService.r();

        assertThat(resultado).hasSize(2);
        verify(protectoraRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe validar login con credenciales correctas")
    void debeValidarLoginCorrecto() throws Exception {
        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));

        Protectora resultado = protectoraService.validarLogin("protectora@test.com", "password123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("protectora@test.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción si protectora no existe")
    void debeLanzarExcepcionProtectoraNoExiste() {
        when(protectoraRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> protectoraService.validarLogin("noexiste@test.com", "password"))
                .isInstanceOf(Exception.class)
                .hasMessage("Protectora no encontrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción si contraseña es incorrecta")
    void debeLanzarExcepcionPasswordIncorrecta() {
        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));

        assertThatThrownBy(() -> protectoraService.validarLogin("protectora@test.com", "passwordIncorrecta"))
                .isInstanceOf(Exception.class)
                .hasMessage("Contraseña incorrecta");
    }

    @Test
    @DisplayName("Debe lanzar excepción si protectora no está aprobada")
    void debeLanzarExcepcionProtectoraNoAprobada() {
        protectora.setAprobado(false);

        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));

        assertThatThrownBy(() -> protectoraService.validarLogin("protectora@test.com", "password123"))
                .isInstanceOf(Exception.class)
                .hasMessage("Su cuenta está pendiente de aprobación por un administrador");
    }

    @Test
    @DisplayName("Debe obtener protectoras pendientes de aprobación")
    void debeObtenerPendientesAprobacion() {
        Protectora pendiente1 = new Protectora();
        pendiente1.setAprobado(false);
        Protectora pendiente2 = new Protectora();
        pendiente2.setAprobado(false);

        List<Protectora> pendientes = Arrays.asList(pendiente1, pendiente2);

        when(protectoraRepository.findByAprobado(false)).thenReturn(pendientes);

        List<Protectora> resultado = protectoraService.obtenerPendientesAprobacion();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(p -> !p.getAprobado());
    }

    @Test
    @DisplayName("Debe aprobar protectora")
    void debeAprobarProtectora() throws Exception {
        protectora.setAprobado(false);

        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));
        when(protectoraRepository.save(any(Protectora.class))).thenAnswer(invocation -> {
            Protectora p = invocation.getArgument(0);
            assertThat(p.getAprobado()).isTrue();
            return p;
        });

        protectoraService.aprobarProtectora("protectora@test.com");

        verify(protectoraRepository).save(any(Protectora.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al aprobar protectora que no existe")
    void debeLanzarExcepcionAprobarProtectoraNoExiste() {
        when(protectoraRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> protectoraService.aprobarProtectora("noexiste@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("Protectora no encontrada");
    }

    @Test
    @DisplayName("Debe rechazar protectora")
    void debeRechazarProtectora() throws Exception {
        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));

        protectoraService.rechazarProtectora("protectora@test.com");

        verify(protectoraRepository).delete(protectora);
    }

    @Test
    @DisplayName("Debe eliminar protectora")
    void debeEliminarProtectora() throws Exception {
        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));

        protectoraService.d("protectora@test.com");

        verify(protectoraRepository).delete(protectora);
    }

    @Test
    @DisplayName("Debe encontrar protectora por email")
    void debeEncontrarProtectoraPorEmail() throws Exception {
        when(protectoraRepository.findByEmail("protectora@test.com")).thenReturn(Optional.of(protectora));

        Protectora resultado = protectoraService.findByEmail("protectora@test.com");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("protectora@test.com");
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar protectora que no existe")
    void debeLanzarExcepcionBuscarProtectoraNoExiste() {
        when(protectoraRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> protectoraService.findByEmail("noexiste@test.com"))
                .isInstanceOf(Exception.class)
                .hasMessage("Protectora no encontrada");
    }
}
