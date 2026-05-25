package batalhanaval;

import batalhanaval.config.GameConfig;
import batalhanaval.config.ValidadorDeFrota;
import batalhanaval.ui.TerminalUI;
import batalhanaval.dominio.Jogo;
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

            // 4. Tudo certo! Exibe o Menu Principal do Membro 4
            int opcao = ui.exibirMenuPrincipal();

            if (opcao == 0) {
                ui.exibirMensagem("Encerrando Batalha Naval. Até logo!");
                return;
            }

            if (opcao == 1 || opcao == 2) {
                ui.exibirMensagem("\nIniciando o motor do jogo...");
                
                // Aqui é onde o jogo inicia de fato.
                Jogo jogo = new Jogo(config, ui);
                jogo.iniciarPartida();
            }

            if (opcao == 3) {
                // 1. Instancia o DAO do Membro 5 usando o caminho do game.properties
                PartidaDAO partidaDAO = new PartidaDAO(config.getDbSqliteFile());
                
                try {
                    // 2. Busca a lista bruta do banco de dados
                    // (Assumindo que o Membro 5 criou um método listarPartidas e um DTO próprio)
                    List<PartidaDTO> partidasBanco = partidaDAO.listarPartidas();
                    
                    // 3. Converte os dados do Banco para os DTOs visuais do Membro 4
                    List<TerminalUI.ResumoPartida> historicoUI = new ArrayList<>();
                    for (PartidaDTO p : partidasBanco) {
                        historicoUI.add(new TerminalUI.ResumoPartida(p.getId(), p.getInicio(), p.getVencedor()));
                    }
                    
                    // 4. Chama a UI para imprimir a tabela e pegar a resposta do usuário
                    int idEscolhido = ui.exibirHistoricoPartidas(historicoUI);
                    
                    // 5. Se o usuário escolheu uma partida válida para assistir
                    if (idEscolhido > 0) {
                        // Busca todas as jogadas daquela partida no banco
                        List<JogadaDTO> jogadasBanco = partidaDAO.buscarJogadasPorPartida(idEscolhido);
                        
                        // Constrói os quadros (frames) do Replay
                        List<TerminalUI.FrameReplay> frames = construirFramesDoReplay(jogadasBanco, config);
                        
                        // Manda dar o play na tela! (usando o delay configurado no properties)
                        ui.reproduzirReplay(frames, config.getReplayDelayMs());
                    }
                    
                } catch (SQLException e) {
                    ui.exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
                }
                
                continue; // Volta para o início do Menu Principal
            }           

        } catch (IOException e) {
            // Captura caso o arquivo game.properties não seja encontrado
            ui.exibirMensagem("Erro fatal ao abrir o arquivo de configurações: " + e.getMessage());
        } finally {
            // Garante que o Scanner será fechado ao encerrar o sistema
            ui.fecharScanner();
        }
    }
}
