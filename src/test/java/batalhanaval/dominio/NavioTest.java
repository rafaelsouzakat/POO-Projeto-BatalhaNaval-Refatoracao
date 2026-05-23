package batalhanaval.dominio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NavioTest {

    @Test
    void deveCriarNavioComEstadoInicialCorreto() {
        Navio navio = new Navio(1, "Porta-Aviões", 5);
        
        // Verifica se o navio nasce intacto
        assertFalse(navio.isAfundado(), "Um navio recém-criado não pode estar afundado.");
    }

    @Test
    void deveAfundarNavioAposReceberTirosIguaisAoTamanho() {
        Navio submarino = new Navio(2, "Submarino", 2);

        submarino.registrarAcerto();
        assertFalse(submarino.isAfundado(), "O submarino de tamanho 2 não deve afundar com apenas 1 tiro.");

        submarino.registrarAcerto();
        assertTrue(submarino.isAfundado(), "O submarino deve afundar após receber 2 tiros.");
    }
}