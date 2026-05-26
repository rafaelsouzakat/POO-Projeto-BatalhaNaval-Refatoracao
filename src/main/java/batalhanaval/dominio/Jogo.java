package batalhanaval.dominio;

import java.util.List;

import batalhanaval.config.GameConfig;
import batalhanaval.persistencia.PartidaDAO;
import batalhanaval.persistencia.JogadaDAO;
import batalhanaval.persistencia.JogadaDTO;
import batalhanaval.ui.TerminalUI;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Jogo {
    private GameConfig config;
    private TerminalUI ui;

    // DAOs para salvar no banco
    private PartidaDAO partidaDAO; // Para salvar o histórico de partidas
    private JogadaDAO jogadaDAO; // Para salvar o histórico de jogadas

    private Jogador humano;
    private Jogador cpu;
    private boolean turnoDoHumano; // Controle simples de quem é a vez

    // Lista para guardar o "filme" da partida
    private List<JogadaDTO> historicoJogadas;

    public Jogo(GameConfig config, TerminalUI ui, PartidaDAO partidaDAO, JogadaDAO jogadaDAO) {
        this.config = config;
        this.ui = ui;
        this.partidaDAO = partidaDAO;
        this.jogadaDAO = jogadaDAO;
        this.turnoDoHumano = true; // O jogador humano começa

        // Inicia a lista vazia
        this.historicoJogadas = new ArrayList<>();
        
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
    
    // 1. LÓGICA DE POSICIONAMENTO
    int[] tamanhosNavios = config.getFleetSizes();
    String[] nomesNavios = config.getFleetNames();
    
    // Posicionamento da CPU (Automático)
    cpu.posicionarFrota(tamanhosNavios);
    
    // Posicionamento do Humano (Pergunta se quer automático ou manual)
    boolean querManual = ui.lerConfirmacao("Deseja posicionar seus navios manualmente?");
    if (!querManual) {
        humano.posicionarFrota(tamanhosNavios);
        ui.exibirMensagem("Sua frota foi posicionada automaticamente!");
    } else {
        // Posicionamento Manual: Laço pedindo coordenada para cada navio
        for (int i = 0; i < tamanhosNavios.length; i++) {
            boolean posicaoValida = false;
            while (!posicaoValida) {
                ui.exibirMeuTabuleiro(humano.getTabuleiro().getGrid());
                ui.exibirMensagem("Posicionando: " + nomesNavios[i] + " (Tamanho: " + tamanhosNavios[i] + ")");
                
                String coord = ui.lerCoordenada("Digite a coordenada inicial");
                String direcao = ui.lerDirecao(); // H ou V
                
                // Tenta colocar no tabuleiro. O método adicionarNavio deve retornar true se couber e false se der erro de colisão/limite.
                try {
                    Coordenada c = Coordenada.parse(coord, config.getBoardSize());
                    posicaoValida = humano.getTabuleiro().adicionarNavio(c, tamanhosNavios[i], direcao);
                    
                    if (!posicaoValida) {
                        ui.exibirMensagem("[ERRO] Posição inválida! O navio colide com outro ou sai do tabuleiro. Tente novamente.");
                    }
                } catch (IllegalArgumentException e) {
                    ui.exibirMensagem("[ERRO] Coordenada fora do formato esperado.");
                }
            }
        }
    }

    boolean jogoAtivo = true;
    int turnoContador = 1;
    String dataInicio = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    // O GRANDE LAÇO DO JOGO
    while (jogoAtivo) {
        Jogador atacante = turnoDoHumano ? humano : cpu;
        Jogador defensor = turnoDoHumano ? cpu : humano;
        
        Coordenada coordAlvo = null;

        // --- 1. LÓGICA DO TURNO DO HUMANO (O que estava faltando!) ---
        if (turnoDoHumano) {
            ui.exibirTabuleirosLadoALado(atacante.getTabuleiro().getGrid(), atacante.getTabuleiro().getGridDeTiros());
            
            int opcao = ui.exibirMenuTurno(
                atacante.getTabuleiro().getNaviosRestantes(), 
                defensor.getTabuleiro().getNaviosRestantes()
            );
            
            if (opcao == 2) {
                ui.exibirLegenda();
                continue; 
            } else if (opcao == 3) {
                ui.exibirMeuTabuleiro(atacante.getTabuleiro().getGrid());
                continue; 
            } else if (opcao == 1) {
                try {
                    String entrada = ui.lerCoordenada("Digite a coordenada para atirar");
                    coordAlvo = Coordenada.parse(entrada, config.getBoardSize());
                } catch (IllegalArgumentException e) {
                    ui.exibirMensagem("Coordenada inválida: " + e.getMessage());
                    continue; // Pede de novo sem passar o turno
                }
            } else {
                // Opção inválida
                ui.exibirMensagem("[ERRO] Opção inválida! Tente novamente.");
                continue;
            }
        }

        // --- 2. PROCESSAMENTO DO TIRO ---
        // O atacante decide onde atirar (Humano usa a coordenada digitada, CPU sorteia a dela)
        Coordenada jogada = atacante.prepararJogada(coordAlvo);
        
        // Aplica o tiro no tabuleiro inimigo
        ResultadoTiro resultado = defensor.getTabuleiro().receberTiro(jogada);

        // Exibe a mensagem do que aconteceu na tela
        ui.exibirMensagem("\n>>> " + atacante.getNome() + " atirou em " + jogada.toString());
        ui.exibirResultadoTiro(resultado.getDescricao());

        // --- 3. INTEGRAÇÃO DA INTELIGÊNCIA DA CPU (HUNT) ---
        // Se foi a CPU que atirou e ela acertou um navio, avisa a memória dela!
        if (atacante instanceof CpuPlayer && resultado.isAcerto()) {
            ((CpuPlayer) atacante).registrarAcertoHUNT(jogada);
        }

        // --- 4. SALVANDO NO HISTÓRICO DO REPLAY ---
        historicoJogadas.add(new JogadaDTO(turnoContador, atacante.getNome(), jogada.toString(), resultado.getDescricao()));

        // --- 5. VERIFICA SE ALGUÉM VENCEU ---
        if (defensor.getTabuleiro().isFrotaTotalmenteAfundada()) {
            ui.exibirMensagem("\n🏆 FIM DE JOGO! A frota de " + defensor.getNome() + " foi destruída!");
            ui.exibirMensagem("🏆 VENCEDOR: " + atacante.getNome() + " 🏆");
            
            // Salva tudo no banco de dados
            salvarDadosNoBanco(dataInicio, atacante.getNome());
            
            jogoAtivo = false;
            break;
        }

        // --- 6. CONTROLE DE TROCA DE TURNO ---
        if (resultado.isAcerto() && config.isHitGrantsExtraShot()) {
            ui.exibirMensagem(atacante.getNome() + " acertou e ganhou um tiro extra!");
        } else {
            turnoDoHumano = !turnoDoHumano;
        }
        
        turnoContador++;}
    }

    // Método auxiliar privado para manter o iniciarPartida() limpo
    private void salvarDadosNoBanco(String dataInicio, String vencedor) {
        String dataFim = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String seed = config.getGameSeed(); // Puxa a seed do properties

        try {
            // Salva a Partida e pega o ID que o SQLite gerou
            long idPartida = partidaDAO.salvarPartida(dataInicio, dataFim, vencedor, seed);
            
            // Se salvou a partida com sucesso, salva todas as jogadas amarradas a esse ID
            if (idPartida > 0) {
                for (JogadaDTO jogada : historicoJogadas) {
                    jogadaDAO.salvarJogada(
                        idPartida, 
                        jogada.getTurno(), 
                        jogada.getJogador(), 
                        jogada.getCoordenada(), 
                        jogada.getResultado()
                    );
                }
                ui.exibirMensagem("[SISTEMA] Partida salva com sucesso no Histórico!");
            }
        } catch (SQLException e) {
            ui.exibirMensagem("[ERRO] Falha ao salvar partida no banco de dados: " + e.getMessage());
        }
    }    
}
