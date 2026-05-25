package batalhanaval.persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JogadaDAO {
    private final ConexaoSQLite conexao;

    public JogadaDAO(ConexaoSQLite conexao) {
        this.conexao = conexao;
    }

    public void salvarJogada(long idPartida, int turno, String jogador, String coordenada, String resultado) throws SQLException {
        String sql = "INSERT INTO jogada (id_partida, turno, jogador, coordenada, resultado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idPartida);
            ps.setInt(2, turno);
            ps.setString(3, jogador);
            ps.setString(4, coordenada);
            ps.setString(5, resultado);
            ps.executeUpdate();
        }
    }

    public List<JogadaDTO> buscarJogadasPorPartida(int idPartida) throws SQLException {
        List<JogadaDTO> lista = new ArrayList<>();
        String sql = "SELECT turno, jogador, coordenada, resultado FROM jogada WHERE id_partida = ? ORDER BY id ASC";
        
        try (Connection conn = conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPartida);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new JogadaDTO(
                        rs.getInt("turno"), rs.getString("jogador"),
                        rs.getString("coordenada"), rs.getString("resultado")
                    ));
                }
            }
        }
        return lista;
    }
}
