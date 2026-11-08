package simulador.ui;

/**
 * Fundos reutilizados pelas telas.
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Fundo {
    
    private Fundo() {
    }
    
    /** Gradiente vertical que cobre a tela inteira. */
    public static void gradiente( Pincel g, float largura, float altura ) {
        g.gradienteVertical( 0, 0, largura, altura, Tema.FUNDO_TOPO, Tema.FUNDO_BASE );
    }
    
    /** Grade discreta dentro de uma área. */
    public static void grade( Pincel g, float x, float y, float w, float h, float passo, float deslocamento ) {
        float off = deslocamento % passo;
        for ( float gx = x + off; gx < x + w; gx += passo ) {
            g.linha( gx, y, gx, y + h, Tema.GRADE, 1 );
        }
        for ( float gy = y + off; gy < y + h; gy += passo ) {
            g.linha( x, gy, x + w, gy, Tema.GRADE, 1 );
        }
    }
    
}
