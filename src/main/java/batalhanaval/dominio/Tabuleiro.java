// O tabuleiro terá como atributo a matriz e recebe os objetos Coordenada e Navio.
// O tabuleiro "TEM" navios e "TEM" coordenadas, relação de composição.
package batalhanaval.dominio;
public class Tabuleiro{
    // Atributos da classe Tabuleiro
    private char[][] grid;
    private Navio[][] naviosPosicionados;  // Guarda qual navio está em qual célula
    private int size;

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

    public boolean adicionarNavio(Coordenada c, int tamanho, String direcao){
        //TODO
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
        int count = 0;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(naviosPosicionados[i][j] != null && !naviosPosicionados[i][j].isAfundado()){
                    count++;
                }
            }
        }
        return count;
    }

    public ResultadoTiro receberTiro(Coordenada c){
        int x = c.getX();
        int y = c.getY();

        if(grid[y][x] == 'S'){
            Navio navio = naviosPosicionados[y][x];
            navio.registrarAcerto();
            grid[y][x] = 'X'; // Marca o acerto no grid
            return navio.isAfundado() ? ResultadoTiro.AFUNDOU : ResultadoTiro.ACERTO;
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
