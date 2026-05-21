package main.test.java.batalhanaval;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordenadaTest {

    @Test
    void deveCriarCoordenadaValida() {
        Coordenada coordenada = new Coordenada(2, 3);

        assertEquals(2, coordenada.getX());
        assertEquals(3, coordenada.getY());
    }

    @Test
    void deveLancarErroQuandoXForNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Coordenada(-1, 3);
        });
    }

    @Test
    void deveLancarErroQuandoYForNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Coordenada(2, -1);
        });
    }

    @Test
    void deveConverterTextoValidoEmCoordenada() {
        Coordenada coordenada = Coordenada.parse("A1", 10);

        assertEquals(0, coordenada.getX());
        assertEquals(0, coordenada.getY());
    }

    @Test
    void deveConverterTextoMinusculoEmCoordenada() {
        Coordenada coordenada = Coordenada.parse("b3", 10);

        assertEquals(1, coordenada.getX());
        assertEquals(2, coordenada.getY());
    }

    @Test
    void deveLancarErroQuandoTextoForNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            Coordenada.parse(null, 10);
        });
    }

    @Test
    void deveLancarErroQuandoTextoForMuitoCurto() {
        assertThrows(IllegalArgumentException.class, () -> {
            Coordenada.parse("A", 10);
        });
    }

    @Test
    void deveLancarErroQuandoNumeroForInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            Coordenada.parse("AB", 10);
        });
    }

    @Test
    void deveLancarErroQuandoLetraEstiverForaDoTabuleiro() {
        assertThrows(IllegalArgumentException.class, () -> {
            Coordenada.parse("K1", 10);
        });
    }

    @Test
    void deveLancarErroQuandoNumeroEstiverForaDoTabuleiro() {
        assertThrows(IllegalArgumentException.class, () -> {
            Coordenada.parse("A11", 10);
        });
    }

    @Test
    void deveLancarErroQuandoNumeroForZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            Coordenada.parse("A0", 10);
        });
    }
}