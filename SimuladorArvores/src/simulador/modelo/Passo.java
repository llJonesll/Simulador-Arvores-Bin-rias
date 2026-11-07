package simulador.modelo;

import java.util.List;

/**
 * Um passo da simulação: o estado completo da árvore + a explicação.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Passo {
    
    public enum Tom { INFO, SUCESSO, ALERTA, ERRO }
    
    public final Tom tom;
    public final String titulo;
    public final String texto;
    public final List<NoVisual> nos;
    public final int qtdNos;
    public final int profundidade;
    
    /** Saída acumulada dos percursos (ex.: "10, 20, 30"). */
    public String saida;
    
    public Passo( Tom tom, String titulo, String texto, List<NoVisual> nos, int qtdNos, int profundidade ) {
        this.tom = tom;
        this.titulo = titulo;
        this.texto = texto;
        this.nos = nos;
        this.qtdNos = qtdNos;
        this.profundidade = profundidade;
    }
    
}
