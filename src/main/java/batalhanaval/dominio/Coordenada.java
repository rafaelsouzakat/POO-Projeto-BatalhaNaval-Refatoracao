package batalhanaval.dominio;

public class Coordenada {
    // Atributos da classe Coordenada
    private int x;
    private int y;

    // Construtor da classe Coordenada
    public Coordenada(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordenada não pode ser negativa.");
        }

        this.x = x;
        this.y = y;
    }

    // O antigo parseCoord(String s, int N) vira um método de fábrica aqui:
    public static Coordenada parse(String s, int boardSize) {
        if (s == null) {
            throw new IllegalArgumentException("Coordenada não pode ser nula.");
        }

        String t = s.trim().toUpperCase();

        if (t.length() < 2 || t.length() > 3) {
            throw new IllegalArgumentException("Formato de coordenada inválido.");
        }

        char letra = t.charAt(0);
        int x = letra - 'A';

        int y;
        try {
            y = Integer.parseInt(t.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número da coordenada inválido.");
        }

        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            throw new IllegalArgumentException("Coordenada fora dos limites do tabuleiro.");
        }

        return new Coordenada(x, y);
    }

    // Getters para os atributos x e y
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        // Converte o X (0, 1, 2) de volta para Letra (A, B, C) e o Y para número
        char letra = (char) ('A' + this.getX());
        return "" + letra + (this.getY() + 1);
    }
}
