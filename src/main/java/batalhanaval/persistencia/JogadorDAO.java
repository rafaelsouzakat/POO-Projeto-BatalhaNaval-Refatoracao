package batalhanaval.persistencia;

import java.sql.*;

public class JogadorDAO {
    private final ConexaoSQLite conexao;

    public JogadorDAO(ConexaoSQLite conexao) {
        this.conexao = conexao;
    }

    public void salvarJogador(long idPartida, String nome, String tipo) throws SQLException {
        String sql = "INSERT INTO jogador (id_partida, nome, tipo) VALUES (?, ?, ?)";
        try (Connection conn = conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPartida);
            ps.setString(2, nome);
            ps.setString(3, tipo);
            ps.executeUpdate();
        }
    }
}