package com.petlink.adopcion_mascotas.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para Capitalizar")
class CapitalizarTest {

    @Test
    @DisplayName("Debe capitalizar palabra en minúsculas")
    void debeCapitalizarPalabraMinusculas() {
        String resultado = Capitalizar.capitalizar("jorge");
        assertThat(resultado).isEqualTo("Jorge");
    }

    @Test
    @DisplayName("Debe capitalizar palabra en mayúsculas")
    void debeCapitalizarPalabraMayusculas() {
        String resultado = Capitalizar.capitalizar("JORGE");
        assertThat(resultado).isEqualTo("Jorge");
    }

    @Test
    @DisplayName("Debe capitalizar palabra con mayúsculas y minúsculas mezcladas")
    void debeCapitalizarPalabraMezclada() {
        String resultado = Capitalizar.capitalizar("jOrGe");
        assertThat(resultado).isEqualTo("Jorge");
    }

    @ParameterizedTest
    @CsvSource({
        "jorge, Jorge",
        "MARIA, Maria",
        "aNa, Ana",
        "pedro, Pedro",
        "garcía, García",
        "josé, José"
    })
    @DisplayName("Debe capitalizar correctamente múltiples casos")
    void debeCapitalizarMultiplesCasos(String entrada, String esperado) {
        assertThat(Capitalizar.capitalizar(entrada)).isEqualTo(esperado);
    }

    @Test
    @DisplayName("Debe retornar string vacío para string con solo espacios")
    void debeRetornarVacioParaEspacios() {
        String resultado = Capitalizar.capitalizar("   ");
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Debe capitalizar palabra de una sola letra")
    void debeCapitalizarUnaSolaLetra() {
        assertThat(Capitalizar.capitalizar("a")).isEqualTo("A");
        assertThat(Capitalizar.capitalizar("Z")).isEqualTo("Z");
    }

    @Test
    @DisplayName("Debe hacer trim de espacios antes y después")
    void debeHacerTrimEspacios() {
        String resultado = Capitalizar.capitalizar(" jorge ");
        assertThat(resultado).isEqualTo("Jorge");
        assertThat(resultado).doesNotStartWith(" ");
        assertThat(resultado).doesNotEndWith(" ");
    }
}
