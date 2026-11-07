package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;

/**
 * Botão redondo "?" fixo no canto inferior direito da janela.
 * Pulsa até a primeira vez em que o tutorial é aberto.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class BotaoAjuda {
    
    public static final double TAMANHO = 52;
    public static final double MARGEM = 16;
    
    private static final Color COR = new Color( 0x7C9BFF );
    
    private double x, y;
    private double hover;
    private boolean pulsar = true;
    
    public void parouDePulsar() {
        pulsar = false;
    }
    
    /** @return true se foi clicado neste quadro. */
    public boolean atualizar( EngineFrame e, double delta, int largura, int altura ) {
        
        x = largura - MARGEM - TAMANHO;
        y = altura - MARGEM - TAMANHO;
        
        double r = TAMANHO / 2;
        boolean sobre = Math.hypot( e.getMouseX() - ( x + r ), e.getMouseY() - ( y + r ) ) <= r;
        hover += ( ( sobre ? 1 : 0 ) - hover ) * ( 1 - Math.exp( -delta * 14 ) );
        
        if ( sobre ) {
            Botao.mao = true;
        }
        
        return sobre && e.isMouseButtonPressed( EngineFrame.MOUSE_BUTTON_LEFT );
        
    }
    
    public void desenhar( EngineFrame e, double tempo ) {
        
        double r = TAMANHO / 2;
        double cx = x + r;
        double cy = y + r;
        double pulso = pulsar ? 0.5 + 0.5 * Math.sin( tempo * 5 ) : 0;
        
        if ( pulsar ) {
            e.fillCircle( cx, cy, r + 6 + 6 * pulso, Tema.alfa( COR, 0.12 + 0.18 * pulso ) );
        }
        
        e.fillCircle( cx + 2, cy + 4, r, new Color( 0, 0, 0, 90 ) );
        e.fillCircle( cx, cy, r, Tema.misturar( new Color( 0x232957 ), COR, 0.35 + 0.4 * hover ) );
        e.setStrokeLineWidth( 2.4f );
        e.drawCircle( cx, cy, r, Tema.alfa( COR, 0.7 + 0.3 * hover ) );
        e.resetStrokeToDefault();
        Desenho.textoCentro( e, "?", cx, cy, 28, Tema.TEXTO, true );
        
        if ( hover > 0.05 ) {
            String s = "Tutorial (F1)";
            double w = Desenho.largura( e, s, 14, true ) + 24;
            double bx = x - 10 - w;
            e.fillRoundRectangle( bx, cy - 16, w, 32, 18, Tema.alfa( new Color( 0x151839 ), hover ) );
            Desenho.textoCentro( e, s, bx + w / 2, cy, 14, Tema.alfa( Tema.TEXTO, hover ), true );
        }
        
    }
    
}
