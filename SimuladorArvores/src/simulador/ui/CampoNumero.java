package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;

/**
 * Campo para digitar a chave (0 a 999). Sempre recebe o teclado, para que
 * se possa digitar e apertar Enter sem usar o mouse.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class CampoNumero {
    
    public static final int MAX = 999;
    
    public double x, y, w, h;
    private final StringBuilder txt = new StringBuilder();
    private double tremor;
    
    public CampoNumero pos( double x, double y, double w, double h ) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }
    
    /** @return a chave digitada ou null se o campo está vazio. */
    public Integer valor() {
        return txt.length() == 0 ? null : Integer.valueOf( txt.toString() );
    }
    
    public void definir( int v ) {
        txt.setLength( 0 );
        txt.append( v );
    }
    
    public void limpar() {
        txt.setLength( 0 );
    }
    
    public void tremer() {
        tremor = 1;
    }
    
    public void atualizar( EngineFrame e, double delta ) {
        
        for ( int i = 0; i < 10; i++ ) {
            if ( e.isKeyPressed( EngineFrame.KEY_ZERO + i ) || e.isKeyPressed( EngineFrame.KEY_KP_0 + i ) ) {
                if ( txt.length() < 3 ) {
                    if ( txt.length() == 1 && txt.charAt( 0 ) == '0' ) {
                        txt.setLength( 0 );
                    }
                    txt.append( i );
                } else {
                    tremor = 1;
                }
            }
        }
        
        if ( e.isKeyPressed( EngineFrame.KEY_BACKSPACE ) && txt.length() > 0 ) {
            txt.deleteCharAt( txt.length() - 1 );
        }
        
        if ( e.isKeyPressed( EngineFrame.KEY_UP ) ) {
            int v = valor() == null ? -1 : valor();
            definir( Math.min( MAX, v + 1 ) );
        }
        
        if ( e.isKeyPressed( EngineFrame.KEY_DOWN ) ) {
            int v = valor() == null ? 1 : valor();
            definir( Math.max( 0, v - 1 ) );
        }
        
        tremor = Math.max( 0, tremor - delta * 3.5 );
        
    }
    
    public void desenhar( EngineFrame e, double tempo, Color acento ) {
        
        double dx = Math.sin( tremor * 28 ) * 5 * tremor;
        double xx = x + dx;
        
        e.fillRoundRectangle( xx, y, w, h, 14, new Color( 0x0F1230 ) );
        e.setStrokeLineWidth( 2f );
        e.drawRoundRectangle( xx, y, w, h, 14, Tema.misturar( acento, Tema.ALERTA, tremor ) );
        e.resetStrokeToDefault();
        
        Desenho.texto( e, "CHAVE", xx + 10, y + 11, 9, Tema.alfa( Tema.TEXTO_SUAVE, 0.9 ), true );
        
        String s = txt.toString();
        if ( s.isEmpty() ) {
            Desenho.textoCentro( e, "--", xx + w / 2, y + h / 2 + 5, 22, Tema.alfa( Tema.TEXTO_SUAVE, 0.45 ), true );
        } else {
            Desenho.textoCentro( e, s, xx + w / 2, y + h / 2 + 5, 22, Tema.TEXTO, true );
        }
        
        if ( ( (int) ( tempo * 2 ) ) % 2 == 0 ) {
            int lw = s.isEmpty() ? 0 : Desenho.largura( e, s, 22, true );
            double cx = s.isEmpty() ? xx + w / 2 + 16 : xx + w / 2 + lw / 2.0 + 3;
            e.fillRectangle( cx, y + h / 2 - 5, 2, 22, Tema.alfa( acento, 0.9 ) );
        }
        
    }
    
}
