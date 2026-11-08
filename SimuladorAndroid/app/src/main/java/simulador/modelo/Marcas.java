package simulador.modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import simulador.modelo.NoVisual.Destaque;

/**
 * Marcações de um passo da simulação: destaques, etiquetas de papel
 * ("z", "pai", "tio"...), caminho percorrido, duplo-preto e nó flutuante.
 * Usa interface fluente: {@code m().d( no, Destaque.NOVO ).p( no, "z" )}.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Marcas {
    
    final Map<No, Destaque> dest = new HashMap<>();
    final Map<No, String> papeis = new HashMap<>();
    final Set<No> caminho = new HashSet<>();
    final Set<No> duplos = new HashSet<>();
    No flut;
    No alvo;
    int lado;
    String saida;
    
    /** Destaca um nó. */
    public Marcas d( No n, Destaque d ) {
        if ( n != null ) {
            dest.put( n, d );
        }
        return this;
    }
    
    /** Etiqueta de papel sobre um nó. */
    public Marcas p( No n, String papel ) {
        if ( n != null ) {
            papeis.put( n, papel );
        }
        return this;
    }
    
    /** Marca o caminho percorrido (nós e arestas entre eles). */
    public Marcas caminho( Collection<No> c ) {
        for ( No n : c ) {
            caminho.add( n );
            dest.putIfAbsent( n, Destaque.CAMINHO );
        }
        return this;
    }
    
    /** Marca um nó como "duplo-preto" (remoção na Vermelho-Preto). */
    public Marcas duplo( No n ) {
        if ( n != null ) {
            duplos.add( n );
        }
        return this;
    }
    
    /** Nó novo ainda fora da árvore, pairando perto de {@code alvo}. */
    public Marcas flutuante( No n, No alvo, int lado ) {
        this.flut = n;
        this.alvo = alvo;
        this.lado = lado;
        return this;
    }
    
}
