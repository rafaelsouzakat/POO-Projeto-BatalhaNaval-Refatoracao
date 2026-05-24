package batalhanaval.ui;

import java.util.Scanner;
import java.util.InputMismatchException;

/*
 * Classe responsável por toda a interação de terminal com o usuário.
 * Não possui regras de negócio ou de domínio, atuando apenas como camada de visualização (View).
 */
public class TerminalUI {
    private final Scanner scanner;

    public TerminalUI() {
        this.scanner = new Scanner(System.in);
    }

    // MENUS

    public int exibirMenuPrincipal() {
        System.out.println("\n=== BATALHA NAVAL ===");
        System.out.println("1. Nova Partida (Posicionamento Manual)");
        System.out.println("2. Nova Partida (Posicionamento Automático)");
        System.out.println("3. Histórico de Partidas / Replay");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
        return lerInteiroSeguro();
    }

    public int exibirMenuTurno(int naviosJogador, int naviosCpu) {
        System.out.println("\n--- SEU TURNO ---");
        System.out.printf("Navios vivos -> Você: %d | CPU: %d%n", naviosJogador, naviosCpu); 
        System.out.println("1. Atirar");
        System.out.println("2. Ver Legenda / Ajuda");
        System.out.println("3. Mostrar meu tabuleiro");
        System.out.print("> ");
        return lerInteiroSeguro();
    }

    public void exibirLegenda() {
        System.out.println("\n--- LEGENDA ---");
        System.out.println(" S : Navio intacto");
        System.out.println(" X : Acerto (Navio atingido)");
        System.out.println(" o : Água (Tiro na água)");
        System.out.println(" . : Desconhecido / Vazio");
        System.out.println("---------------");
    }

    // RENDERIZAÇÃO DE TABULEIROS
    // Renderiza dois tabuleiros lado a lado com cabeçalhos alinhados.
    public void exibirTabuleirosLadoALado(char[][] meuTabuleiro, char[][] meusTiros) {
        System.out.println();
        System.out.printf("%-30s | %s%n", "SEU TABULEIRO", "SEUS TIROS NO INIMIGO");
        System.out.println("    A B C D E F G H I J     |     A B C D E F G H I J");

        for (int y = 0; y < 10; y++) {
            // Usa %2d para garantir que o '10' fique alinhado com '1' a '9'
            StringBuilder linhaEsquerda = new StringBuilder(String.format("%2d  ", (y + 1)));
            StringBuilder linhaDireita = new StringBuilder(String.format("%2d  ", (y + 1)));

            // Constrói linha do próprio tabuleiro
            for (int x = 0; x < 10; x++) {
                linhaEsquerda.append(meuTabuleiro[y][x]).append(" ");
            }

            // Constrói linha do tabuleiro de tiros
            for (int x = 0; x < 10; x++) {
                linhaDireita.append(meusTiros[y][x]).append(" ");
            }

            // Imprime as duas linhas separadas pelo divisor
            System.out.println(linhaEsquerda.toString() + "  |  " + linhaDireita.toString());
        }
    }

    public void exibirMeuTabuleiro(char[][] meuTabuleiro) {
        System.out.println("\nSEU TABULEIRO");
        System.out.println("    A B C D E F G H I J");
        for (int y = 0; y < 10; y++) {
            StringBuilder linha = new StringBuilder(String.format("%2d  ", (y + 1)));
            for (int x = 0; x < 10; x++) {
                linha.append(meuTabuleiro[y][x]).append(" ");
            }
            System.out.println(linha.toString());
        }
    }

    // ENTRADA E SAÍDA DE DADOS

    public String lerCoordenada(String mensagem) {
        System.out.print(mensagem + " (ex: A1, J10): ");
        return scanner.nextLine().trim().toUpperCase();
    }

    public String lerDirecao() {
        System.out.print("Direção (H para horizontal, V para vertical): ");
        return scanner.nextLine().trim().toUpperCase();
    }

    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public void exibirResultadoTiro(String padrao) {
        System.out.println("\n>>> RESULTADO: " + padrao.toUpperCase() + " <<<");
    }

    public void fecharScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
    
    // MÉTODOS DE SUPORTE 
    // Garante que o programa não quebre (InputMismatchException) se o usuário digitar texto no lugar de números.
    private int lerInteiroSeguro() {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, digite um número: ");
            }
        }
    }

    /*
     * Garante que o usuário digite apenas uma das opções fornecidas.
     * Útil para menus de Sim/Não, direções (H/V), etc.
     */
    private String lerLetraSegura(String mensagem, String... opcoesValidas) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim().toUpperCase();

            for (String opcao : opcoesValidas) {
                if (entrada.equals(opcao.toUpperCase())) {
                    return entrada;
                }
            }
            
            // Se chegou aqui, a entrada não bateu com nenhuma opção válida
            System.out.print("Entrada inválida. Opções aceitas: ");
            for (int i = 0; i < opcoesValidas.length; i++) {
                System.out.print(opcoesValidas[i].toUpperCase() + (i < opcoesValidas.length - 1 ? ", " : "\n"));
            }
        }
    }

    /**
     * Método útil para perguntas de Sim ou Não durante o fluxo do jogo (ex: posicionamento manual).
     */
    public boolean lerConfirmacao(String mensagem) {
        String resposta = lerLetraSegura(mensagem + " (S/N): ", "S", "N");
        return resposta.equals("S");
    }

    // Estrutura para o Histórico
    public static class ResumoPartida {
        public final int id;
        public final String data;
        public final String vencedor;

        public ResumoPartida(int id, String data, String vencedor) {
            this.id = id;
            this.data = data;
            this.vencedor = vencedor;
        }
    }

    // Estrutura para cada passo do Replay
    public static class FrameReplay {
        public final char[][] tabuleiroJogador;
        public final char[][] tabuleiroTiros;
        public final String descricaoAcao; // Ex: "Turno 4: CPU atirou em B5 (Água)"

        public FrameReplay(char[][] tabuleiroJogador, char[][] tabuleiroTiros, String descricaoAcao) {
            this.tabuleiroJogador = tabuleiroJogador;
            this.tabuleiroTiros = tabuleiroTiros;
            this.descricaoAcao = descricaoAcao;
        }
    }

    // --- HISTÓRICO E REPLAY ---

    /*
     * Lista as partidas salvas e pede ao usuário qual ID ele quer assistir.
     * Recebe os dados já formatados do motor/banco de dados.
     */
    public int exibirHistoricoPartidas(java.util.List<ResumoPartida> historico) {
        System.out.println("\n=== HISTÓRICO DE PARTIDAS ===");
        
        if (historico == null || historico.isEmpty()) {
            System.out.println("Nenhuma partida encontrada no banco de dados.");
            return 0; // 0 pode indicar retorno ao menu principal no seu motor
        }

        System.out.printf("%-5s | %-20s | %-15s%n", "ID", "DATA", "VENCEDOR");
        System.out.println("--------------------------------------------------");
        
        for (ResumoPartida partida : historico) {
            System.out.printf("%-5d | %-20s | %-15s%n", partida.id, partida.data, partida.vencedor);
        }
        
        System.out.println("--------------------------------------------------");
        System.out.print("Digite o ID da partida que deseja assistir (ou 0 para voltar): ");
        
        return lerInteiroSeguro(); // Repassa o ID escolhido para o backend processar
    }

    //Executa o replay passo a passo baseado na configuração de delay.
    public void reproduzirReplay(java.util.List<FrameReplay> jogadas, int delayMs) {
        System.out.println("\n=== INICIANDO REPLAY ===");
        
        if (jogadas == null || jogadas.isEmpty()) {
            System.out.println("Nenhuma jogada encontrada para este replay.");
            return;
        }

        for (int i = 0; i < jogadas.size(); i++) {
            FrameReplay frame = jogadas.get(i);
            
            System.out.println("\nPasso " + (i + 1) + " de " + jogadas.size());
            if (frame.descricaoAcao != null && !frame.descricaoAcao.isEmpty()) {
                System.out.println(">>> " + frame.descricaoAcao);
            }
            
            exibirTabuleirosLadoALado(frame.tabuleiroJogador, frame.tabuleiroTiros);

            // Controle de Pausa do Replay
            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Replay interrompido pelo sistema.");
                    break;
                }
            } else {
                System.out.print("\nPressione [ENTER] para a próxima jogada...");
                scanner.nextLine();
            }
        }
        
        System.out.println("\n=== FIM DO REPLAY ===");
        System.out.print("Pressione [ENTER] para voltar ao menu principal...");
        scanner.nextLine();
    }
}

