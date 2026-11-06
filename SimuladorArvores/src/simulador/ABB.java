package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bv110309
 */
public class ABB {
    
    private No raiz;
    private int ranqueAtual;
    private List<No> nosDesenho;
    
    public void put( int c ) {
        raiz = put( raiz, c );
        nosDesenho = new ArrayList<>();
    }
    
    private No put( No no, int c ) {
        
        if ( no == null ) {
            no = new No( 20 );
            no.c = c;
        } else {
            
            if ( c < no.c ) {
                no.e = put( no.e, c );
            } else if ( c > no.c ) {
                no.d = put( no.d, c );
            } else { // c == no.c
                // não faz nada
            }
            
        }
        
        return no;
        
    }
    
    public void emOrdem() {
        emOrdem( raiz );
        System.out.println();
    }
    
    private void emOrdem( No no ) {
        if ( no != null ) {
            emOrdem( no.e );
            System.out.println( no );
            emOrdem( no.d );
        }
    }
    
    public void atualizarNos( double espHor, double espVert, int margemHor, int margemVert ) {
        ranqueAtual = 0;
        nosDesenho.clear();
        atualizarNos( raiz, 0, espHor, espVert, margemHor, margemVert );
    }
    
    private void atualizarNos( No no, int nivel, double espHor, double espVert, int margemHor, int margemVert ) {
        if ( no != null ) {
            atualizarNos( no.e, nivel + 1, espHor, espVert, margemHor, margemVert );
            no.nivel = nivel;
            no.ranque = ranqueAtual++;
            no.pos.x = margemHor + espHor * no.ranque;
            no.pos.y = margemVert + espVert * no.nivel;
            nosDesenho.add( no );
            atualizarNos( no.d, nivel + 1, espHor, espVert, margemHor, margemVert );
        }
    }
    
    public void desenhar( EngineFrame e ) {
        
        for ( No no : nosDesenho ) {
            if ( no.e != null ) {
                e.drawLine( no.pos, no.e.pos, EngineFrame.BLACK );
            }
            if ( no.d != null ) {
                e.drawLine( no.pos, no.d.pos, EngineFrame.BLACK );
            }
        }
        
        for ( No no : nosDesenho ) {
            no.desenhar( e );
        }
        
    }
    
    
}
