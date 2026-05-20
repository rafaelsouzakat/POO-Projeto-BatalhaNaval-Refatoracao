public class Coordenada{
    // Atributos da classe Coordenada
    private int x;
    private int y;

    // Construtor da classe Coordenada
    public Coordenada(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // O antigo parseCoord(String s, int N) vira um método de fábrica aqui:
    // Acho que deveria trocar por uma exceção depois? (mas por enquanto, para evitar complicações, vou deixar retornando null)
    public static Coordenada parse(String s, int boardSize){
        if (s == null) {
            return null; // Acho que deveria trocar por uma exceção depois?
        }

        String t = s.trim().toUpperCase();
        if(t.length() < 2 || t.length() > 3) {
            return null; // Acho que deveria trocar por uma exceção depois?
        }

        int x = t.charAt(0) - 'A';
        int y = Integer.parseInt(t.substring(1)) - 1;

        if(x < 0 || x >= boardSize || y < 0 || y >= boardSize){
            return null; // Acho que deveria trocar por uma exceção depois?
        }

        return new Coordenada(x, y);
    }

    // Getters para os atributos x e y
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}