package batalhanaval.persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class JogadorDAOTest {

    @TempDir
    Path tempDir;

    private ConexaoSQLite conexao;
    private JogadorDAO jogadorDAO;

    @BeforeEach
    void prepararBancoDeTeste() throws SQLException {
        Path arquivoBanco = tempDir.resolve("batalha_naval_teste.db");

        conexao = new ConexaoSQLite(arquivoBanco.toString());
        conexao.criarTabelasSeNaoExistir();

        jogadorDAO = new JogadorDAO(conexao);
    }

    @Test
    void deveSalvarJogadorNoBanco() throws SQLException {
        jogadorDAO.salvarJogador(1L, "João", "HUMANO");

        String sql = "SELECT id_partida, nome, tipo FROM jogador";

        try (Connection conn = conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            assertTrue(rs.next());

            assertEquals(1L, rs.getLong("id_partida"));
            assertEquals("João", rs.getString("nome"));
            assertEquals("HUMANO", rs.getString("tipo"));

            assertFalse(rs.next());
        }
    }

    @Test
    void deveSalvarMaisDeUmJogador() throws SQLException {
        jogadorDAO.salvarJogador(1L, "João", "HUMANO");
        jogadorDAO.salvarJogador(1L, "Computador", "IA");

        String sql = "SELECT id_partida, nome, tipo FROM jogador ORDER BY id ASC";

        try (Connection conn = conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            assertTrue(rs.next());
            assertEquals(1L, rs.getLong("id_partida"));
            assertEquals("João", rs.getString("nome"));
            assertEquals("HUMANO", rs.getString("tipo"));

            assertTrue(rs.next());
            assertEquals(1L, rs.getLong("id_partida"));
            assertEquals("Computador", rs.getString("nome"));
            assertEquals("IA", rs.getString("tipo"));

            assertFalse(rs.next());
        }
    }
}
