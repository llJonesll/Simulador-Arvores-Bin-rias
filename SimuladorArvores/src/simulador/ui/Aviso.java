package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import simulador.modelo.Passo.Tom;

/**
 * Mensagem rápida que aparece e some sozinha ("toast").
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Aviso {
    
    private String texto;
    private Tom tom = Tom.INFO;
    private double tempo;
    
    public void mostrar( String texto, Tom tom ) {
        this.texto = texto;
        this.tom = tom;
        this.tempo = 2.6;
    }
    
    public void atualizar( double delta ) {
        tempo = Math.max( 0, tempo - delta );
    }
    
    public void desenhar( EngineFrame e, double cx, double cy ) {
        
        if ( texto == null || tempo <= 0 ) {
            return;
        }
        
        double a = Math.min( 1, Math.min( tempo * 3, ( 2.6 - tempo ) * 6 ) );
        double w = Desenho.largura( e, texto, 16, true ) + 40;
        double y = cy - ( 1 - a ) * -12;
        
        e.fillRoundRectangle( cx - w / 2, y - 20, w, 40, 24, Tema.alfa( new java.awt.Color( 0x151839 ), a * 0.96 ) );
        e.setStrokeLineWidth( 2f );
        e.drawRoundRectangle( cx - w / 2, y - 20, w, 40, 24, Tema.alfa( Tema.tom( tom ), a ) );
        e.resetStrokeToDefault();
        Desenho.textoCentro( e, texto, cx, y, 16, Tema.alfa( Tema.TEXTO, a ), true );
        
    }
    
}
