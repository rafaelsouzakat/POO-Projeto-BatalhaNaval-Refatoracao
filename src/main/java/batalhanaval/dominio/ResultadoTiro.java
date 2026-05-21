// Classe enum para tipar as respostas dos disparos (água, acerto, afundou)
package batalhanaval.dominio;
public enum ResultadoTiro{
    // valores do enum
    AGUA("Água", false),
    ACERTO("Acerto", true),
    AFUNDOU("Afundou", true);

    // Atributos do enum
    private final String descricao;
    private final boolean atingiuNavio;

    // Construtor do enum (privado por padrão)
    ResultadoTiro(String descricao, boolean atingiuNavio) {
        this.descricao = descricao;
        this.atingiuNavio = atingiuNavio;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAcerto() {
        return atingiuNavio;
    }
}