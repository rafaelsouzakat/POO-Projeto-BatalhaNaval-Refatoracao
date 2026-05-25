package batalhanaval.persistencia;

public class JogadaDTO {
    private int turno;
    private String jogador, coordenada, resultado;

    public JogadaDTO(int turno, String jogador, String coordenada, String resultado) {
        this.turno = turno; this.jogador = jogador; this.coordenada = coordenada; this.resultado = resultado;
    }
    public String getJogador() { return jogador; }
    public String getCoordenada() { return coordenada; }
    public String getResultado() { return resultado; }
    public int getTurno() { return turno; }
}