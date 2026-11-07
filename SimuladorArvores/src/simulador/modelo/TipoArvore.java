package simulador.modelo;

/**
 * Os três tipos de árvore do simulador.
 * 
 * @author Prof. Dr. David Buzatto
 */
public enum TipoArvore {
    
    ABB( "ABB", "Árvore Binária de Busca" ),
    AVL( "AVL", "Árvore AVL" ),
    RN( "RN", "Árvore Vermelho-Preto" );
    
    public final String sigla;
    public final String nome;
    
    private TipoArvore( String sigla, String nome ) {
        this.sigla = sigla;
        this.nome = nome;
    }
    
    public Arvore criar() {
        switch ( this ) {
            case AVL: return new AVL();
            case RN: return new ArvoreRN();
            default: return new ABB();
        }
    }
    
}
