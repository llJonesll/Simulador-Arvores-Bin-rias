package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.GradientPaint;

/**
 * Fundos reutilizados pelas telas.
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Fundo {
    
    private Fundo() {
    }
    
    /** Gradiente vertical que cobre a janela inteira. */
    public static void gradiente( EngineFrame e, int largura, int altura ) {
        e.fillRectangle( 0, 0, largura, altura, 
                new GradientPaint( 0, 0, Tema.FUNDO_TOPO, 0, altura, Tema.FUNDO_BASE ) );
    }
    
    /** Grade discreta dentro de uma área (clipada manualmente pelas linhas). */
    public static void grade( EngineFrame e, double x, double y, double w, double h, double passo, double deslocamento ) {
        double off = deslocamento % passo;
        for ( double gx = x + off; gx < x + w; gx += passo ) {
            e.drawLine( gx, y, gx, y + h, Tema.GRADE );
        }
        for ( double gy = y + off; gy < y + h; gy += passo ) {
            e.drawLine( x, gy, x + w, gy, Tema.GRADE );
        }
    }
    
}
