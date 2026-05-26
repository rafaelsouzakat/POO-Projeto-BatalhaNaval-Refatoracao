// O tabuleiro terá como atributo a matriz e recebe os objetos Coordenada e Navio.
// O tabuleiro "TEM" navios e "TEM" coordenadas, relação de composição.
package batalhanaval.dominio;
public class Tabuleiro{
    // Atributos da classe Tabuleiro
    private char[][] grid;
    private Navio[][] naviosPosicionados;  // Guarda qual navio está em qual célula
    private int size;
    private int naviosRestantes; // Contador de navios que ainda não foram afundados

    // Construtor da classe Tabuleiro
    public Tabuleiro(int size){
        this.size = size;
        this.grid = new char[size][size];
        this.naviosPosicionados = new Navio[size][size];

        // Inicializa o grid com água (representada por '.')
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                this.grid[i][j] = '.';
            }
        }
    }

    // Substitui o antigo canPlace(...)
    public boolean podePosicionar(Coordenada c, Navio navio, boolean horizontal){
        int x = c.getX();
        int y = c.getY();
        int len = navio.getTamanho();

        if(horizontal){
            if(x + len > size){
                return false; // O navio ultrapassaria os limites do tabuleiro
            }
            for(int i = 0; i < len; i++){
                if(grid[y][x + i] != '.'){
                    return false; // Já existe um navio nessa posição
                }
            }
        }
        else{
            if(y + len > size){
                return false;
            }
            for(int i = 0; i < len; i++){
                if(grid[y + i][x] != '.'){
                    return false;
                }
            }
        }
        return true; // Se passou por todas as checagens, é possível posicionar o navio
    }

    // Substitui o antigo placeShip(...)
    public void posicionarNavio(Coordenada c, Navio navio, boolean horizontal){
        if(!podePosicionar(c, navio, horizontal)){
            throw new IllegalArgumentException("Posição inválida para o navio.");
        }

        int x = c.getX();
        int y = c.getY();

        for(int i = 0; i < navio.getTamanho(); i++){
            if(horizontal){
                grid[y][x + i] = 'S'; // Marca a posição do navio no grid
                naviosPosicionados[y][x + i] = navio; // Associa o navio àquela célula
            }
            else{
                grid[y + i][x] = 'S'; // Marca a posição do navio no grid
                naviosPosicionados[y + i][x] = navio;
            }
        }
    }

    /**
     * Tenta adicionar um navio no tabuleiro.
     * Retorna true se foi possível, e false se a posição for inválida (saiu do mapa ou colidiu).
     */
    public boolean adicionarNavio(Coordenada c, int tamanho, String direcao) {
        int x = c.getX();
        int y = c.getY();
        int boardSize = grid.length; // Assume que 'grid' é a sua matriz char[][]

        // 1. Validação de Limites: Verifica se o navio sai para fora do tabuleiro
        if (direcao.equals("H")) {
            if (x + tamanho > boardSize) return false;
        } else if (direcao.equals("V")) {
            if (y + tamanho > boardSize) return false;
        } else {
            return false; // Direção inválida (não é H nem V)
        }

        // 2. Validação de Colisão: Verifica se já tem algum navio ('S') no caminho
        for (int i = 0; i < tamanho; i++) {
            int checkX = direcao.equals("H") ? x + i : x;
            int checkY = direcao.equals("V") ? y + i : y;

            if (grid[checkY][checkX] != '.') {
                return false; // Colidiu com outro navio!
            }
        }

        // 3. Cria um objeto Navio com um ID único baseado no naviosRestantes
        Navio navio = new Navio(naviosRestantes, "Navio-" + naviosRestantes, tamanho);

        // 4. Posicionamento: Se passou nos testes, grava o navio no grid e em naviosPosicionados
        for (int i = 0; i < tamanho; i++) {
            int placeX = direcao.equals("H") ? x + i : x;
            int placeY = direcao.equals("V") ? y + i : y;

            grid[placeY][placeX] = 'S'; // 'S' representa a parte do navio
            naviosPosicionados[placeY][placeX] = navio; // Armazena referência ao objeto Navio
        }

        this.naviosRestantes++; // Incrementa o contador de navios restantes
        return true;
    }

    // Getter para o tamanho do tabuleiro
    public int getSize(){
        return this.size;
    }

    public char[][] getGrid(){
        return this.grid;
    }

    public char[][] getGridDeTiros(){
        char[][] tiros = new char[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                tiros[i][j] = (grid[i][j] == 'S') ? '.' : grid[i][j]; // Esconde os navios
            }
        }
        return tiros;
    }

    public int getNaviosRestantes(){
        return naviosRestantes;
    }

    public ResultadoTiro receberTiro(Coordenada c){
        int x = c.getX();
        int y = c.getY();

        if(grid[y][x] == 'S'){
            Navio navio = naviosPosicionados[y][x];
            navio.registrarAcerto();
            grid[y][x] = 'X'; // Marca o acerto no grid

            if(navio.isAfundado()){
                naviosRestantes--; // Decrementa o contador de navios restantes
                return ResultadoTiro.AFUNDOU;
            }
            else{
                return ResultadoTiro.ACERTO;
            }
        }
        else if(grid[y][x] == '.' || grid[y][x] == 'O'){
            grid[y][x] = 'O'; // Marca o erro no grid
            return ResultadoTiro.AGUA;
        }
        else{
            return ResultadoTiro.ACERTO; // Já foi atingido antes
        }
    }

    public boolean isFrotaTotalmenteAfundada(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(naviosPosicionados[i][j] != null && !naviosPosicionados[i][j].isAfundado()){
                    return false; // Encontrou um navio que ainda não foi afundado
                }
            }
        }
        return true; // Todos os navios foram afundados
    }
}
