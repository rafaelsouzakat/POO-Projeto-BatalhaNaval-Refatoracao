package batalhanaval.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JogadaDAOTest {

    @TempDir
    Path tempDir;

    private ConexaoSQLite conexao;
    private JogadaDAO jogadaDAO;

    @BeforeEach
    void prepararBancoDeTeste() throws SQLException {
        Path arquivoBanco = tempDir.resolve("batalha_naval_teste.db");

        conexao = new ConexaoSQLite(arquivoBanco.toString());
        conexao.criarTabelasSeNaoExistir();

        jogadaDAO = new JogadaDAO(conexao);
    }

    @Test
    void deveSalvarEBuscarUmaJogada() throws SQLException {
        jogadaDAO.salvarJogada(
                1L,
                1,
                "Jogador 1",
                "A1",
                "AGUA"
        );

        List<JogadaDTO> jogadas = jogadaDAO.buscarJogadasPorPartida(1);

        assertEquals(1, jogadas.size());

        JogadaDTO jogada = jogadas.get(0);

        assertEquals(1, jogada.getTurno());
        assertEquals("Jogador 1", jogada.getJogador());
        assertEquals("A1", jogada.getCoordenada());
        assertEquals("AGUA", jogada.getResultado());
    }

    @Test
    void deveBuscarVariasJogadasDaMesmaPartida() throws SQLException {
        jogadaDAO.salvarJogada(1L, 1, "Jogador 1", "A1", "AGUA");
        jogadaDAO.salvarJogada(1L, 2, "Jogador 2", "B2", "ACERTO");

        List<JogadaDTO> jogadas = jogadaDAO.buscarJogadasPorPartida(1);

        assertEquals(2, jogadas.size());

        assertEquals(1, jogadas.get(0).getTurno());
        assertEquals("Jogador 1", jogadas.get(0).getJogador());
        assertEquals("A1", jogadas.get(0).getCoordenada());
        assertEquals("AGUA", jogadas.get(0).getResultado());

        assertEquals(2, jogadas.get(1).getTurno());
        assertEquals("Jogador 2", jogadas.get(1).getJogador());
        assertEquals("B2", jogadas.get(1).getCoordenada());
        assertEquals("ACERTO", jogadas.get(1).getResultado());
    }

    @Test
    void deveBuscarSomenteJogadasDaPartidaInformada() throws SQLException {
        jogadaDAO.salvarJogada(1L, 1, "Jogador 1", "A1", "AGUA");
        jogadaDAO.salvarJogada(2L, 1, "Jogador 2", "C3", "ACERTO");

        List<JogadaDTO> jogadas = jogadaDAO.buscarJogadasPorPartida(1);

        assertEquals(1, jogadas.size());

        assertEquals("Jogador 1", jogadas.get(0).getJogador());
        assertEquals("A1", jogadas.get(0).getCoordenada());
        assertEquals("AGUA", jogadas.get(0).getResultado());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistemJogadas() throws SQLException {
        List<JogadaDTO> jogadas = jogadaDAO.buscarJogadasPorPartida(999);

        assertNotNull(jogadas);
        assertTrue(jogadas.isEmpty());
    }
}
