package batalhanaval.dominio;

import batalhanaval.config.GameConfig;
import batalhanaval.ui.TerminalUI;

public class Jogo {
    private GameConfig config;
    private TerminalUI ui;
    private Jogador humano;
    private Jogador cpu;
    private boolean turnoDoHumano; // Controle simples de quem é a vez

    public Jogo(GameConfig config, TerminalUI ui) {
        this.config = config;
        this.ui = ui;
        this.turnoDoHumano = true; // O jogador humano começa
        
        // 1. Instancia os tabuleiros lendo o tamanho do arquivo properties
        int tamanho = config.getBoardSize();
        Tabuleiro tabHumano = new Tabuleiro(tamanho);
        Tabuleiro tabCpu = new Tabuleiro(tamanho);
        
        // 2. Instancia os jogadores que você (Membro 2) já criou
        this.humano = new HumanPlayer("Jogador 1", tabHumano);
        this.cpu = new CpuPlayer("CPU", tabCpu, config.getCpuStrategy());
    }

    public void iniciarPartida() {
        ui.exibirMensagem("\n[SISTEMA] Posicionando frotas...");
        // TODO: Aqui vocês chamarão a lógica de posicionamento (manual/automático)
        // usando o ValidadorDeFrota do Membro 3.
        
        boolean jogoAtivo = true;

        // O GRANDE LAÇO DO JOGO
        while (jogoAtivo) {
            Jogador atacante = turnoDoHumano ? humano : cpu;
            Jogador defensor = turnoDoHumano ? cpu : humano;

            // Variável para armazenar onde o tiro vai acertar
            Coordenada coordAlvo = null;

            // --- LÓGICA DO TURNO DO HUMANO ---
            if (turnoDoHumano) {
                // Pede para o Membro 4 desenhar a tela
                // (Nota: assumindo que o seu Tabuleiro possui os métodos getGrid() e getGridDeTiros())
                ui.exibirTabuleirosLadoALado(atacante.getTabuleiro().getGrid(), atacante.getTabuleiro().getGridDeTiros());
                
                // Exibe o menu informando quantos navios restam (assumindo que você criou getNaviosRestantes())
                int opcao = ui.exibirMenuTurno(
                    atacante.getTabuleiro().getNaviosRestantes(), 
                    defensor.getTabuleiro().getNaviosRestantes()
                );
                
                if (opcao == 2) {
                    ui.exibirLegenda();
                    continue; // Reinicia o laço sem passar o turno
                } else if (opcao == 3) {
                    ui.exibirMeuTabuleiro(atacante.getTabuleiro().getGrid());
                    continue; // Reinicia o laço sem passar o turno
                } else if (opcao == 1) {
                    // Opção de atirar: A UI lê a string e nós tentamos converter
                    try {
                        String entrada = ui.lerCoordenada("Digite a coordenada para atirar");
                        coordAlvo = Coordenada.parse(entrada, config.getBoardSize());
                    } catch (IllegalArgumentException e) {
                        ui.exibirMensagem("Coordenada inválida: " + e.getMessage());
                        continue; // Pede de novo sem passar o turno
                    }
                }
            }

            // --- PROCESSAMENTO DO TIRO (Serve tanto para Humano quanto CPU) ---
            
            // O jogador prepara a jogada (Humano retorna a coordAlvo, CPU ignora e sorteia a dela)
            Coordenada jogada = atacante.prepararJogada(coordAlvo);
            
            // Aplica o tiro no tabuleiro inimigo
            // (Nota: você precisará criar o método receberTiro(Coordenada) no Tabuleiro)
            ResultadoTiro resultado = defensor.getTabuleiro().receberTiro(jogada);

            // Exibe o que aconteceu na UI
            ui.exibirMensagem("\n>>> " + atacante.getNome() + " atirou em (" + jogada.getX() + "," + jogada.getY() + ")");
            ui.exibirResultadoTiro(resultado.getDescricao());

            // --- VERIFICAR CONDIÇÃO DE VITÓRIA ---
            if (defensor.getTabuleiro().isFrotaTotalmenteAfundada()) {
                ui.exibirMensagem("\n🏆 FIM DE JOGO! A frota de " + defensor.getNome() + " foi destruída!");
                ui.exibirMensagem("🏆 VENCEDOR: " + atacante.getNome() + " 🏆");
                jogoAtivo = false;
                break;
            }

            // --- CONTROLE DE TROCA DE TURNO ---
            // Verifica a regra do game.properties se ganha tiro extra ao acertar [3, 4]
            if (resultado.isAcerto() && config.isHitGrantsExtraShot()) {
                ui.exibirMensagem(atacante.getNome() + " acertou e ganhou um tiro extra!");
            } else {
                // Passa a vez para o outro jogador
                turnoDoHumano = !turnoDoHumano;
            }
        }
    }
}
