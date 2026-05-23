package batalhanaval;

import batalhanaval.config.GameConfig;
import batalhanaval.config.ValidadorDeFrota;
import batalhanaval.ui.TerminalUI;
import batalhanaval.dominio.Jogo;
import java.io.IOException;

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

        } catch (IOException e) {
            // Captura caso o arquivo game.properties não seja encontrado
            ui.exibirMensagem("Erro fatal ao abrir o arquivo de configurações: " + e.getMessage());
        } finally {
            // Garante que o Scanner será fechado ao encerrar o sistema
            ui.fecharScanner();
        }
    }
}
