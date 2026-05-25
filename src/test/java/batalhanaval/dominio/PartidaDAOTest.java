package batalhanaval.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PartidaDAOTest {

    @TempDir
    Path tempDir;

    private ConexaoSQLite conexao;
    private PartidaDAO partidaDAO;

    @BeforeEach
    void prepararBancoDeTeste() throws SQLException {
        Path arquivoBanco = tempDir.resolve("batalha_naval_teste.db");

        conexao = new ConexaoSQLite(arquivoBanco.toString());
        conexao.criarTabelasSeNaoExistir();

        partidaDAO = new PartidaDAO(conexao);
    }

    @Test
    void deveSalvarPartidaERetornarIdGerado() throws SQLException {
        long id = partidaDAO.salvarPartida(
                "2026-05-25 10:00",
                "2026-05-25 10:30",
                "Jogador 1",
                "ABC123"
        );

        assertTrue(id > 0, "O ID da partida salva deve ser maior que zero.");
    }

    @Test
    void deveSalvarEListarUmaPartida() throws SQLException {
        long id = partidaDAO.salvarPartida(
                "2026-05-25 10:00",
                "2026-05-25 10:30",
                "Jogador 1",
                "ABC123"
        );

        List<PartidaDTO> partidas = partidaDAO.listarPartidas();

        assertEquals(1, partidas.size());

        PartidaDTO partida = partidas.get(0);

        assertEquals(id, partida.getId());
        assertEquals("2026-05-25 10:00", partida.getInicio());
        assertEquals("2026-05-25 10:30", partida.getFim());
        assertEquals("Jogador 1", partida.getVencedor());
        assertEquals("ABC123", partida.getSeed());
    }

    @Test
    void deveListarPartidasEmOrdemDecrescenteDeId() throws SQLException {
        long id1 = partidaDAO.salvarPartida(
                "2026-05-25 10:00",
                "2026-05-25 10:30",
                "Jogador 1",
                "SEED1"
        );

        long id2 = partidaDAO.salvarPartida(
                "2026-05-25 11:00",
                "2026-05-25 11:30",
                "Jogador 2",
                "SEED2"
        );

        List<PartidaDTO> partidas = partidaDAO.listarPartidas();

        assertEquals(2, partidas.size());

        assertEquals(id2, partidas.get(0).getId());
        assertEquals(id1, partidas.get(1).getId());

        assertEquals("Jogador 2", partidas.get(0).getVencedor());
        assertEquals("Jogador 1", partidas.get(1).getVencedor());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistemPartidas() throws SQLException {
        List<PartidaDTO> partidas = partidaDAO.listarPartidas();

        assertNotNull(partidas);
        assertTrue(partidas.isEmpty());
    }
}
