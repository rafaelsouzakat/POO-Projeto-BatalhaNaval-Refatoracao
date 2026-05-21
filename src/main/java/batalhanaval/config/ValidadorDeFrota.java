package batalhanaval.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ValidadorDeFrota {

    private static final int QUANTIDADE_NAVIOS = 5;

    private static final Set<String> REGRAS_ADJACENCIA_VALIDAS =
        Set.of("NONE", "ORTHO", "ORTHO_DIAG");

    private static final Set<String> ESTRATEGIAS_CPU_VALIDAS =
        Set.of("RANDOM", "HUNT", "PARITY");

    private static final Set<String> MODOS_JOGO_VALIDOS =
        Set.of("PLAY", "LIST", "REPLAY");

    private ValidadorDeFrota() {}

    /**
     * Executa todas as validações sobre o GameConfig fornecido.
     *
     * @param config  configuração já carregada pelo GameConfig
     * @return ResultadoValidacao com ok=true e lista vazia, ou ok=false e lista de erros
     */
    public static ResultadoValidacao validar(GameConfig config) {
        List<String> erros = new ArrayList<>();

        int[]    tamanhos = config.getFleetSizes();
        String[] nomes    = config.getFleetNames();
        int      boardSize = config.getBoardSize();

        //1-Quantidade de tamanhos
        if (tamanhos.length != QUANTIDADE_NAVIOS) {
            erros.add(String.format(
                "fleet.sizes deve ter exatamente %d navios, mas encontrou %d.",
                QUANTIDADE_NAVIOS, tamanhos.length
            ));
        }

        //2-Quantidade de nomes
        if (nomes.length != QUANTIDADE_NAVIOS) {
            erros.add(String.format(
                "fleet.names deve ter exatamente %d navios, mas encontrou %d.",
                QUANTIDADE_NAVIOS, nomes.length
            ));
        }

        //3-Os dois arrays devem ter o mesmo comprimento
        if (tamanhos.length != nomes.length
                && tamanhos.length == QUANTIDADE_NAVIOS
                && nomes.length == QUANTIDADE_NAVIOS) {
            erros.add("fleet.sizes e fleet.names possuem quantidades diferentes de entradas.");
        }

        //4-Nenhum tamanho pode ser ≤ 0
        for (int i = 0; i < tamanhos.length; i++) {
            if (tamanhos[i] <= 0) {
                erros.add(String.format(
                    "fleet.sizes[%d]: tamanho inválido (%d). Todo navio deve ter tamanho ≥ 1.",
                    i, tamanhos[i]
                ));
            }
        }

        //5-Nenhum nome pode ser vazio ou em branco
        for (int i = 0; i < nomes.length; i++) {
            if (nomes[i] == null || nomes[i].isBlank()) {
                erros.add(String.format(
                    "fleet.names[%d]: nome ausente ou em branco. Todo navio precisa de um nome.",
                    i
                ));
            }
        }

        //6-Tamanho do tabuleiro
        if (boardSize < 5 || boardSize > 26) {
            erros.add(String.format(
                "board.size inválido: %d. Deve ser entre 5 e 26.",
                boardSize
            ));
        }

        //7-Todos os navios devem caber no tabuleiro
        if (boardSize >= 5 && boardSize <= 26) {
            for (int i = 0; i < tamanhos.length; i++) {
                if (tamanhos[i] > boardSize) {
                    String nomeSufixo = (i < nomes.length && !nomes[i].isBlank())
                        ? " (" + nomes[i] + ")"
                        : "";
                    erros.add(String.format(
                        "fleet.sizes[%d]%s: tamanho %d é maior que o tabuleiro (%d). Navio não cabe.",
                        i, nomeSufixo, tamanhos[i], boardSize
                    ));
                }
            }
        }

        //8-Regra de adjacência
        String regra = config.getFleetAdjacencyRule();
        if (!REGRAS_ADJACENCIA_VALIDAS.contains(regra)) {
            erros.add(String.format(
                "fleet.adjacency_rule inválido: '%s'. Valores aceitos: NONE, ORTHO, ORTHO_DIAG.",
                regra
            ));
        }

        //9-Estratégia da CPU
        String estrategia = config.getCpuStrategy();
        if (!ESTRATEGIAS_CPU_VALIDAS.contains(estrategia)) {
            erros.add(String.format(
                "cpu.strategy inválido: '%s'. Valores aceitos: RANDOM, HUNT, PARITY.",
                estrategia
            ));
        }

        //10-Modo de jogo
        String modo = config.getGameMode();
        if (!MODOS_JOGO_VALIDOS.contains(modo)) {
            erros.add(String.format(
                "game.mode inválido: '%s'. Valores aceitos: PLAY, LIST, REPLAY.",
                modo
            ));
        }

        return new ResultadoValidacao(erros.isEmpty(), erros);
    }

    public static final class ResultadoValidacao {

        private final boolean      ok;
        private final List<String> erros;

        ResultadoValidacao(boolean ok, List<String> erros) {
            this.ok    = ok;
            this.erros = Collections.unmodifiableList(new ArrayList<>(erros));
        }

        /** @return true se a configuração passou em todas as regras */
        public boolean isOk() {
            return ok;
        }

        /**
         * @return lista imutável de mensagens de erro; vazia quando isOk() == true
         */
        public List<String> getErros() {
            return erros;
        }

        public String resumo() {
            if (ok) return "Configuração válida.";
            StringBuilder sb = new StringBuilder("Erros de configuração encontrados:\n");
            for (int i = 0; i < erros.size(); i++) {
                sb.append(String.format("  %d) %s%n", i + 1, erros.get(i)));
            }
            return sb.toString();
        }
    }
}