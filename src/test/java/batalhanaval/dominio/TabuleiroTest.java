package batalhanaval.dominio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TabuleiroTest {

    @Test
    void deveInicializarTabuleiroComTamanhoCorreto(){
        Tabuleiro tab = new Tabuleiro(10);
        assertEquals(10, tab.getTamanho(), "O tabuleiro deve ser inicializado com o tamanho especificado.");
    }

    @Test
    void devePermitirPosicionarNavioDentroDosLimites(){
        Tabuleiro tab = new Tabuleiro(10);
        Navio navio = new Navio(1, "Destroyer", 2);
        Coordenada coord = new Coordenada(0, 0); // Representa A1

        // Deve retornar true e nao lancar nenhuma exceção
        assertTrue(tab.podePosicionar(coord, navio, true), "Deve ser possível posicionar um navio dentro dos limites do tabuleiro.");
        assertDoesNotThrow(() -> tab.posicionarNavio(coord, navio, true));
    }

    @Test
    void deveImpedirPosicionamentoForaDosLimitesHorizontal(){
        Tabuleiro tab = new Tabuleiro(10);
        Navio portaAvioes = new Navio(1, "Porta-Aviões", 5);
        Coordenada coord = new Coordenada(8, 0); // Coluna 1 (indice 8) Só cabem 2 partes, o navio tem 5
        
        assertFalse(tab.podePosicionar(coord, portaAvioes, true), "Não deve ser possível posicionar um navio horizontalmente que ultrapasse os limites do tabuleiro.");
        assertThrows(IllegalArgumentException.class, () -> tab.posicionarNavio(coord, portaAvioes, true), "Deve lançar uma exceção ao tentar posicionar um navio para fora do grid.");
    }

    @Test
    void deveImpedirPosicionamentoForaDosLimitesVertical() {
        Tabuleiro tab = new Tabuleiro(10);
        Navio cruzador = new Navio(2, "Cruzador", 3);
        Coordenada coord = new Coordenada(0, 9); // Linha 10 (índice 9). Só cabe 1 parte, navio tem 3.

        assertFalse(tab.podePosicionar(coord, cruzador, false));
        assertThrows(IllegalArgumentException.class, () -> {
            tab.posicionarNavio(coord, cruzador, false);
        });
    }

    @Test
    void deveImpedirPosicionamentoSobreposto() {
        Tabuleiro tab = new Tabuleiro(10);
        Navio n1 = new Navio(1, "Submarino", 2);
        Navio n2 = new Navio(2, "Cruzador", 3);
        
        Coordenada c1 = new Coordenada(5, 5); // Coluna F, Linha 6
        tab.posicionarNavio(c1, n1, true); // Ocupa (5,5) e (6,5) na horizontal

        Coordenada c2 = new Coordenada(6, 4); // Coluna G, Linha 5
        
        // Tenta colocar o n2 na vertical, cruzando exatamente a célula (6,5) que já está ocupada
        assertFalse(tab.podePosicionar(c2, n2, false), "Não deve permitir sobreposição de navios.");
        assertThrows(IllegalArgumentException.class, () -> {
            tab.posicionarNavio(c2, n2, false);
        });
    }
}