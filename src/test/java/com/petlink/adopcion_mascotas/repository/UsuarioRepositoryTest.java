package com.petlink.adopcion_mascotas.repository;

import com.petlink.adopcion_mascotas.enums.Rol;
import com.petlink.adopcion_mascotas.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests para UsuarioRepository")
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setNombre("Jorge");
        usuario.setApellido("Lopez");
        usuario.setPassword("password123");
        usuario.setTelefono("+34600000000");
        usuario.setRol(Rol.USUARIO);
    }

    @Test
    @DisplayName("Debe guardar usuario correctamente")
    void debeGuardarUsuario() {
        Usuario saved = usuarioRepository.save(usuario);

        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Debe encontrar usuario por email")
    void debeEncontrarPorEmail() {
        entityManager.persist(usuario);
        entityManager.flush();

        Optional<Usuario> found = usuarioRepository.findByEmail("test@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Debe retornar empty si usuario no existe")
    void debeRetornarEmptySiNoExiste() {
        Optional<Usuario> found = usuarioRepository.findByEmail("noexiste@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe contar usuarios correctamente")
    void debeContarUsuarios() {
        entityManager.persist(usuario);

        Usuario usuario2 = new Usuario();
        usuario2.setEmail("otro@test.com");
        usuario2.setNombre("Otro");
        usuario2.setRol(Rol.USUARIO);
        entityManager.persist(usuario2);
        entityManager.flush();

        long count = usuarioRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe eliminar usuario")
    void debeEliminarUsuario() {
        entityManager.persist(usuario);
        entityManager.flush();

        usuarioRepository.deleteById("test@test.com");

        Optional<Usuario> found = usuarioRepository.findByEmail("test@test.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void debeListarTodos() {
        entityManager.persist(usuario);

        Usuario usuario2 = new Usuario();
        usuario2.setEmail("otro@test.com");
        usuario2.setNombre("Otro");
        usuario2.setRol(Rol.USUARIO);
        entityManager.persist(usuario2);
        entityManager.flush();

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertThat(usuarios).hasSize(2);
    }

    @Test
    @DisplayName("Debe obtener últimos 5 usuarios")
    void debeObtenerUltimos5() {
        for (int i = 1; i <= 7; i++) {
            Usuario u = new Usuario();
            u.setEmail("usuario" + i + "@test.com");
            u.setNombre("Usuario" + i);
            u.setRol(Rol.USUARIO);
            entityManager.persist(u);
        }
        entityManager.flush();

        List<Usuario> ultimos = usuarioRepository.findTop5ByOrderByEmailDesc();

        assertThat(ultimos).hasSize(5);
    }

    @Test
    @DisplayName("Email debe ser único")
    void emailDebeSerUnico() {
        entityManager.persist(usuario);
        entityManager.flush();

        Usuario duplicado = new Usuario();
        duplicado.setEmail("test@test.com");
        duplicado.setNombre("Otro");
        duplicado.setRol(Rol.USUARIO);

        try {
            entityManager.persist(duplicado);
            entityManager.flush();
            assertThat(false).isTrue();
        } catch (Exception e) {
            assertThat(e).isNotNull();
        }
    }
}
