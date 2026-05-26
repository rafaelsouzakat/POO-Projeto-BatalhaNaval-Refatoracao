package batalhanaval;

import batalhanaval.config.GameConfig;
import batalhanaval.config.ValidadorDeFrota;
import batalhanaval.dominio.Coordenada;
import batalhanaval.ui.TerminalUI;
import batalhanaval.dominio.Jogo;
import batalhanaval.persistencia.PartidaDAO;
import batalhanaval.persistencia.PartidaDTO;
import batalhanaval.persistencia.JogadaDTO;
import batalhanaval.persistencia.JogadaDAO;
import batalhanaval.persistencia.ConexaoSQLite;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Instancia a interface de terminal construída pelo Membro 4
        TerminalUI ui = new TerminalUI();

        try {
            // 1. O Membro 3 carrega o arquivo de configuração
            GameConfig config = GameConfig.carregar("game.properties");

            // 2. O Membro 3 valida se a frota e as regras do professor estão corretas
            ValidadorDeFrota.ResultadoValidacao validacao = ValidadorDeFrota.validar(config);

            // 3. Se houver erro de configuração, o jogo aborta e a UI imprime os erros
            if (!validacao.isOk()) {
                ui.exibirMensagem("\n[ERRO CRÍTICO] O jogo não pode iniciar devido a regras inválidas:");
                ui.exibirMensagem(validacao.resumo());
                return;
            }

            // 4. Instancia os DAOs para persistência
            ConexaoSQLite conexao = new ConexaoSQLite(config.getDbSqliteFile());
            conexao.criarTabelasSeNaoExistir(); // Garante que as tabelas existam

            PartidaDAO partidaDAO = new PartidaDAO(conexao);
            JogadaDAO jogadaDAO = new JogadaDAO(conexao);

            // 5. Loop do Menu Principal
            boolean continuarNoMenu = true;
            while (continuarNoMenu) {
                int opcao = ui.exibirMenuPrincipal();

                if (opcao == 0) {
                    ui.exibirMensagem("Encerrando Batalha Naval. Até logo!");
                    continuarNoMenu = false;
                } else if (opcao == 1 || opcao == 2) {
                    ui.exibirMensagem("\nIniciando o motor do jogo...");
                    
                    // Aqui é onde o jogo inicia de fato, com DAOs para salvar no banco
                    Jogo jogo = new Jogo(config, ui, partidaDAO, jogadaDAO);
                    jogo.iniciarPartida();
                } else if (opcao == 3) {
                    // Replay de partidas anteriores
                    exibirReplay(ui, config, partidaDAO);
                }
            }           

        } catch (IOException e) {
            // Captura caso o arquivo game.properties não seja encontrado
            ui.exibirMensagem("Erro fatal ao abrir o arquivo de configurações: " + e.getMessage());
        } 
        catch (SQLException e) {
            // Captura erros relacionados ao banco de dados
            ui.exibirMensagem("Erro fatal ao acessar o banco de dados: " + e.getMessage());
        } 
        finally {
            // Garante que o Scanner será fechado ao encerrar o sistema
            ui.fecharScanner();
        }
    }

    /**
     * Exibe o replay de uma partida anterior
     */
    private static void exibirReplay(TerminalUI ui, GameConfig config, PartidaDAO partidaDAO) {
        try {
            // 1. Busca a lista bruta do banco de dados
            List<PartidaDTO> partidasBanco = partidaDAO.listarPartidas();
            
            if (partidasBanco.isEmpty()) {
                ui.exibirMensagem("Nenhuma partida anterior encontrada no banco de dados.");
                return;
            }
            
            // 2. Converte os dados do Banco para os DTOs visuais do Membro 4
            List<TerminalUI.ResumoPartida> historicoUI = new ArrayList<>();
            for (PartidaDTO p : partidasBanco) {
                historicoUI.add(new TerminalUI.ResumoPartida(p.getId(), p.getInicio(), p.getVencedor()));
            }
            
            // 3. Chama a UI para imprimir a tabela e pegar a resposta do usuário
            int idEscolhido = ui.exibirHistoricoPartidas(historicoUI);
            
            // 4. Se o usuário escolheu uma partida válida para assistir
            if (idEscolhido > 0) {
                try {
                    // Busca todas as jogadas daquela partida no banco
                    JogadaDAO jogadaDAO = new JogadaDAO(new ConexaoSQLite(config.getDbSqliteFile()));
                    List<JogadaDTO> jogadasBanco = jogadaDAO.buscarJogadasPorPartida(idEscolhido);
                    
                    // Constrói os quadros (frames) do Replay
                    List<TerminalUI.FrameReplay> frames = construirFramesDoReplay(jogadasBanco, config);
                    
                    // Manda dar o play na tela! (usando o delay configurado no properties)
                    ui.reproduzirReplay(frames, config.getReplayDelayMs());
                } catch (SQLException e) {
                    ui.exibirMensagem("Erro ao buscar jogadas: " + e.getMessage());
                }
            }
            
        } catch (SQLException e) {
            ui.exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    /**
     * Constrói os frames do replay a partir das jogadas armazenadas
     */
    private static List<TerminalUI.FrameReplay> construirFramesDoReplay(List<JogadaDTO> jogadas, GameConfig config) {
        List<TerminalUI.FrameReplay> frames = new ArrayList<>();
        int boardSize = config.getBoardSize();
        
        // Tabuleiros iniciais vazios com tamanho dinâmico
        char[][] tabuleiroJogador = new char[boardSize][boardSize];
        char[][] tabuleiroTiros = new char[boardSize][boardSize];
        
        // Inicializa com . (desconhecido) para o replay
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                tabuleiroJogador[i][j] = '.';
                tabuleiroTiros[i][j] = '.';
            }
        }
        
        for (JogadaDTO jogada : jogadas) {
            String descricao = String.format("Turno %d: %s atirou em %s (%s)", 
                jogada.getTurno(), 
                jogada.getJogador(), 
                jogada.getCoordenada(),
                jogada.getResultado());
            Coordenada coordenada = Coordenada.parse(jogada.getCoordenada(), boardSize);
            char marcador = (jogada.getResultado().equalsIgnoreCase("Água") || jogada.getResultado().equalsIgnoreCase("AGUA")) ? 'o' : 'X';

            // Se a jogada foi feita pela CPU, mostra no tabuleiro do jogador (lado esquerdo).
            // Caso contrário, mostra no tabuleiro de tiros (lado direito).
            if (jogada.getJogador() != null && jogada.getJogador().equalsIgnoreCase("CPU")) {
                tabuleiroJogador[coordenada.getY()][coordenada.getX()] = marcador;
            } else {
                tabuleiroTiros[coordenada.getY()][coordenada.getX()] = marcador;
            }

            frames.add(new TerminalUI.FrameReplay(
                copiarMatriz(tabuleiroJogador),
                copiarMatriz(tabuleiroTiros),
                descricao
            ));
        }
        
        return frames;
    }

    private static char[][] copiarMatriz(char[][] original) {
        int size = original.length;
        char[][] copia = new char[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(original[i], 0, copia[i], 0, size);
        }
        return copia;
    }
}
