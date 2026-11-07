package simulador.modelo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Os quatro percursos de uma árvore binária. Cada constante sabe explicar
 * a própria regra e gerar a ordem de visita dos nós.
 * 
 * @author Prof. Dr. David Buzatto
 */
public enum Percurso {
    
    EM_ORDEM( "Em ordem", 
            "Esquerda → Raiz → Direita. Numa árvore de busca isso visita as chaves em ordem crescente." ),
    PRE_ORDEM( "Pré-ordem", 
            "Raiz → Esquerda → Direita. Visita cada nó antes de seus filhos (útil para copiar a árvore)." ),
    POS_ORDEM( "Pós-ordem", 
            "Esquerda → Direita → Raiz. Visita cada nó depois de seus filhos (útil para apagar a árvore)." ),
    LARGURA( "Em largura", 
            "Nível por nível, da esquerda para a direita, usando uma fila." );
    
    public final String rotulo;
    public final String regra;
    
    private Percurso( String rotulo, String regra ) {
        this.rotulo = rotulo;
        this.regra = regra;
    }
    
    /** Nós da árvore na ordem em que este percurso os visita. */
    public List<No> ordem( No raiz ) {
        List<No> out = new ArrayList<>();
        switch ( this ) {
            case EM_ORDEM: emOrdem( raiz, out ); break;
            case PRE_ORDEM: preOrdem( raiz, out ); break;
            case POS_ORDEM: posOrdem( raiz, out ); break;
            default: largura( raiz, out ); break;
        }
        return out;
    }
    
    private static void emOrdem( No n, List<No> out ) {
        if ( n != null ) {
            emOrdem( n.esq, out );
            out.add( n );
            emOrdem( n.dir, out );
        }
    }
    
    private static void preOrdem( No n, List<No> out ) {
        if ( n != null ) {
            out.add( n );
            preOrdem( n.esq, out );
            preOrdem( n.dir, out );
        }
    }
    
    private static void posOrdem( No n, List<No> out ) {
        if ( n != null ) {
            posOrdem( n.esq, out );
            posOrdem( n.dir, out );
            out.add( n );
        }
    }
    
    private static void largura( No raiz, List<No> out ) {
        Queue<No> fila = new ArrayDeque<>();
        if ( raiz != null ) {
            fila.add( raiz );
        }
        while ( !fila.isEmpty() ) {
            No n = fila.remove();
            out.add( n );
            if ( n.esq != null ) fila.add( n.esq );
            if ( n.dir != null ) fila.add( n.dir );
        }
    }
    
}
