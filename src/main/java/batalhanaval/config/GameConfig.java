package batalhanaval.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GameConfig {

    private final Properties props;
    private GameConfig(Properties props) {
        this.props = props;
    }

    /**
     * Fábrica estática. Carrega o arquivo pelo classpath (src/main/resources/).
     *
     * @param caminho  caminho relativo dentro do classpath, ex: "game.properties"
     * @throws IOException se o arquivo não for encontrado ou não puder ser lido
     */
    public static GameConfig carregar(String caminho) throws IOException {
        Properties props = new Properties();

        try (InputStream is = GameConfig.class.getClassLoader().getResourceAsStream(caminho)) {
            if (is == null) {
                throw new IOException(
                    "Arquivo de configuração não encontrado no classpath: " + caminho
                );
            }
            props.load(is);
        }

        return new GameConfig(props);
    }


    public String getGroupId() {
        return props.getProperty("group.id", "");
    }

    public String getGroupName() {
        return props.getProperty("group.name", "");
    }

    public String getProjectVersion() {
        return props.getProperty("project.version", "1.0.0");
    }


    public int getBoardSize() {
        return parseInt("board.size", 10);
    }

    public char getBoardColumnsStart() {
        String val = props.getProperty("board.columns.start", "A").trim();
        return val.isEmpty() ? 'A' : val.charAt(0);
    }

    public char getBoardColumnsEnd() {
        String val = props.getProperty("board.columns.end", "J").trim();
        return val.isEmpty() ? 'J' : val.charAt(0);
    }

    public int[] getFleetSizes() {
        String raw = props.getProperty("fleet.sizes", "5,4,3,3,2");
        String[] parts = raw.split(",");
        int[] sizes = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                sizes[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException e) {
                sizes[i] = 0;
            }
        }
        return sizes;
    }

    public String[] getFleetNames() {
        String raw = props.getProperty("fleet.names", "");
        String[] names = raw.split(",");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].trim();
        }
        return names;
    }
    public String getFleetAdjacencyRule() {
        return props.getProperty("fleet.adjacency_rule", "ORTHO_DIAG").trim().toUpperCase();
    }

    public boolean isHitGrantsExtraShot() {
        return parseBoolean("rules.hit_grants_extra_shot", false);
    }

    public int getMaxExtraShots() {
        return parseInt("rules.max_extra_shots", 3);
    }

    public String getCpuStrategy() {
        return props.getProperty("cpu.strategy", "HUNT").trim().toUpperCase();
    }

    public boolean isCpuUseParityPreference() {
        return parseBoolean("cpu.use_parity_preference", true);
    }


    public boolean isShowOwnShips() {
        return parseBoolean("ui.show_own_ships", true);
    }

    public boolean isShowLegend() {
        return parseBoolean("ui.show_legend", true);
    }

    public int getReplayDelayMs() {
        return parseInt("ui.replay_delay_ms", 250);
    }

    public boolean isDbEnabled() {
        return parseBoolean("db.enabled", true);
    }

    public String getDbType() {
        return props.getProperty("db.type", "sqlite").trim().toLowerCase();
    }

    public String getDbSqliteFile() {
        return props.getProperty("db.sqlite.file", "data/batalha_naval.db").trim();
    }

    public boolean isDbAutoMigrate() {
        return parseBoolean("db.auto_migrate", true);
    }

    public boolean isDbSaveInitialFleet() {
        return parseBoolean("db.save_initial_fleet", true);
    }

    public String getGameSeed() {
        return props.getProperty("game.seed", "").trim();
    }

    public String getGameMode() {
        return props.getProperty("game.mode", "PLAY").trim().toUpperCase();
    }

    private int parseInt(String key, int defaultValue) {
        String val = props.getProperty(key);
        if (val == null || val.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean parseBoolean(String key, boolean defaultValue) {
        String val = props.getProperty(key);
        if (val == null || val.isBlank()) return defaultValue;
        return Boolean.parseBoolean(val.trim());
    }
}