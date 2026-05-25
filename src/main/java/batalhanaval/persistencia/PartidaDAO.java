package batalhanaval.persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {
    private final ConexaoSQLite conexao;

    public PartidaDAO(ConexaoSQLite conexao) {
        this.conexao = conexao;
    }

    public long salvarPartida(String inicio, String fim, String vencedor, String seed) throws SQLException {
        String sql = "INSERT INTO partida (inicio, fim, vencedor, seed) VALUES (?, ?, ?, ?)";
        
        // Usamos PreparedStatement com RETURN_GENERATED_KEYS para capturar o ID da partida criada [3].
        try (Connection conn = conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, inicio);
            ps.setString(2, fim);
            ps.setString(3, vencedor);
            ps.setString(4, seed);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1L;
    }

    public List<PartidaDTO> listarPartidas() throws SQLException {
        List<PartidaDTO> lista = new ArrayList<>();
        String sql = "SELECT id, inicio, fim, vencedor, seed FROM partida ORDER BY id DESC";
        
        try (Connection conn = conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new PartidaDTO(
                    rs.getInt("id"), rs.getString("inicio"), rs.getString("fim"),
                    rs.getString("vencedor"), rs.getString("seed")
                ));
            }
        }
        return lista;
    }
}
