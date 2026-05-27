package batalhanaval.dominio;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class CpuPlayer extends Jogador{
    // Atributo que representa a estratégia de ataque da CPU (RANDOM, HUNT, PARITY)
    private String estrategia; // receberá o valor do game.properties
    private Random random;

    // Memória da CPU para a estratégia HUNT
    private List<Coordenada> alvosPendentes; // Lista de coordenadas vizinhas a um acerto para tentar depois

    //Construtor herdando os atributos da classe abstrata e recebendo a estratégia da CPU
    public CpuPlayer(String nome, Tabuleiro tabuleiro, String estrategia) {
        super(nome, tabuleiro);
        this.estrategia = estrategia;
        this.random = new Random();
        this.alvosPendentes = new ArrayList<>();
    }

    @Override
    public Coordenada prepararJogada(Coordenada coordenadaDaUI){
        // A CPU ignora completamente a "coordenadaDaUI"
        int tamanhoTabuleiro = this.getTabuleiro().getSize();

        if("RANDOM".equalsIgnoreCase(estrategia)){
            return gerarTiroAleatorioNaoAtirado(tamanhoTabuleiro);
        }
        else if("HUNT".equalsIgnoreCase(estrategia)){
            // Se tem alvos na fila (vizinhos de um acerto anterior), atira neles primeiro
            while (!alvosPendentes.isEmpty()) {
                Coordenada alvo = alvosPendentes.remove(0);
                // pula alvos onde já atirou anteriormente
                if (!jaAtirou(alvo)) {
                    return alvo;
                }
            }
            // Se a fila está vazia (não está caçando), volta a atirar aleatório
            return gerarTiroAleatorioNaoAtirado(tamanhoTabuleiro);
        }
        else if("PARITY".equalsIgnoreCase(estrategia)){
            Coordenada c;
            do {
                // Sorteia aleatório até achar uma coordenada onde a soma de X e Y seja Par
                c = gerarTiroAleatorioNaoAtirado(tamanhoTabuleiro);
            } while ((c.getX() + c.getY()) % 2 != 0);
            
            return c;
    }

    // Padrao de seguranca
    return gerarTiroAleatorioNaoAtirado(tamanhoTabuleiro);
    }

    /**
     * Novo método: A classe Jogo chamará isso quando a CPU acertar um tiro
     * para ela calcular os vizinhos (Cima, Baixo, Esquerda, Direita).
     */
    public void registrarAcertoHUNT(Coordenada acerto) {
        int x = acerto.getX();
        int y = acerto.getY();
        int tamanho = getTabuleiro().getSize();

        // Adiciona os vizinhos na fila respeitando as bordas do tabuleiro
        // Só adiciona vizinhos que ainda não foram alvos e que não estejam já na fila
        Coordenada c;
        if (x > 0) {
            c = new Coordenada(x - 1, y);
            if (!jaAtirou(c) && !alvosPendentes.contains(c)) alvosPendentes.add(c);
        }
        if (x < tamanho - 1) {
            c = new Coordenada(x + 1, y);
            if (!jaAtirou(c) && !alvosPendentes.contains(c)) alvosPendentes.add(c);
        }
        if (y > 0) {
            c = new Coordenada(x, y - 1);
            if (!jaAtirou(c) && !alvosPendentes.contains(c)) alvosPendentes.add(c);
        }
        if (y < tamanho - 1) {
            c = new Coordenada(x, y + 1);
            if (!jaAtirou(c) && !alvosPendentes.contains(c)) alvosPendentes.add(c);
        }
    }    

    // Metodo auxiliar isolando a logica aleatoria
    private Coordenada gerarTiroAleatorio(int limite){
        int x = random.nextInt(limite);
        int y = random.nextInt(limite);
        return new Coordenada(x, y);
    }

    // Gera um tiro aleatorio que ainda não tenha sido tentado. Evita repeticoes.
    private Coordenada gerarTiroAleatorioNaoAtirado(int limite){
        int maxAttempts = limite * limite * 2;
        Coordenada c;
        int attempts = 0;

        do {
            c = gerarTiroAleatorio(limite);
            attempts++;
            if (attempts > maxAttempts) break;
        } while (jaAtirou(c));

        // Se tentou muito e nao encontrou (caso extremo), varre linearmente para achar uma celula nao atirada
        if (jaAtirou(c)){
            for (int yy = 0; yy < limite; yy++){
                for (int xx = 0; xx < limite; xx++){
                    Coordenada cand = new Coordenada(xx, yy);
                    if (!jaAtirou(cand)) return cand;
                }
            }
        }

        return c;
    }

    // Verifica no grid de tiros do proprio tabuleiro se ja foi atirado nessa coordenada
    private boolean jaAtirou(Coordenada c){
        char[][] gridTiros = getTabuleiro().getGridDeTiros();
        int x = c.getX();
        int y = c.getY();
        return gridTiros[y][x] != '.';
    }
}