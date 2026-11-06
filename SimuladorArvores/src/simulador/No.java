package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.math.Vector2;

/**
 *
 * @author bv110309
 */
public class No {
    
    int c;
    No e;
    No d;
    
    Vector2 pos;
    int nivel;
    int ranque;
    int raio;
    
    public No( int raio ) {
        this.pos = new Vector2();
        this.raio = raio;
    }

    public void desenhar( EngineFrame e ) {
        
        e.fillCircle( pos, raio, EngineFrame.WHITE );
        e.drawCircle( pos, raio, EngineFrame.BLACK );
        
        String label = String.valueOf( c );
        int larg = e.measureText( label );
        e.drawText( label, pos.x - larg / 2, pos.y - 5, EngineFrame.BLACK );
                
    }
    
    @Override
    public String toString() {
        return String.format( "c: %d, n: %d, r: %d", c, nivel, ranque );
    }
    
}
