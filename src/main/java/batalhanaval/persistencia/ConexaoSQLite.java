package batalhanaval.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoSQLite {
    private final String dbUrl;

    public ConexaoSQLite(String dbFilePath) {
        this.dbUrl = "jdbc:sqlite:" + dbFilePath;
    }

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public void criarTabelasSeNaoExistir() throws SQLException {
        String sqlPartida = "CREATE TABLE IF NOT EXISTS partida (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "inicio TEXT, fim TEXT, vencedor TEXT, seed TEXT)";

        String sqlJogador = "CREATE TABLE IF NOT EXISTS jogador (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT, tipo TEXT, id_partida INTEGER, " +
                "FOREIGN KEY(id_partida) REFERENCES partida(id))";

        String sqlJogada = "CREATE TABLE IF NOT EXISTS jogada (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_partida INTEGER, turno INTEGER, jogador TEXT, " +
                "coordenada TEXT, resultado TEXT, " +
                "FOREIGN KEY(id_partida) REFERENCES partida(id))";

        // O try-with-resources garante que a conexão será fechada automaticamente [2].
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlPartida);
            stmt.execute(sqlJogador);
            stmt.execute(sqlJogada);
        }
    }
}
