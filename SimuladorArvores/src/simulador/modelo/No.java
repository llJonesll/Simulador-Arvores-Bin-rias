package simulador.modelo;

/**
 * Nó de uma árvore binária. Serve às três árvores do simulador:
 * {@code altura} é usada pela AVL e {@code vermelho} pela Vermelho-Preto.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class No {
    
    private static int contador = 0;
    
    /** Identificador único e estável: permite animar o "mesmo" nó entre passos. */
    public final int id = ++contador;
    
    public int chave;
    public No esq;
    public No dir;
    public No pai;
    
    /** Altura do nó (folha = 1). Usada apenas pela AVL. */
    public int altura = 1;
    
    /** Cor do nó. Usada apenas pela Vermelho-Preto. */
    public boolean vermelho;
    
    public No( int chave ) {
        this.chave = chave;
    }
    
    @Override
    public String toString() {
        return String.valueOf( chave );
    }
    
}
