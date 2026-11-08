package simulador.ui;

import simulador.modelo.Passo.Tom;

/**
 * Mensagem rápida que aparece e some sozinha ("toast").
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Aviso {
    
    private String texto;
    private Tom tom = Tom.INFO;
    private float tempo;
    
    public void mostrar( String texto, Tom tom ) {
        this.texto = texto;
        this.tom = tom;
        this.tempo = 2.6f;
    }
    
    public void atualizar( float dt ) {
        tempo = Math.max( 0, tempo - dt );
    }
    
    public void desenhar( Pincel g, float cx, float cy ) {
        
        if ( texto == null || tempo <= 0 ) {
            return;
        }
        
        float a = Math.min( 1, Math.min( tempo * 3, ( 2.6f - tempo ) * 6 ) );
        float w = g.largura( texto, 14, true ) + 32;
        float y = cy + ( 1 - a ) * 12;
        
        g.retanguloArred( cx - w / 2, y - 18, w, 36, 18, Tema.alfa( Tema.FOLHA, a * 0.96 ) );
        g.bordaArred( cx - w / 2, y - 18, w, 36, 18, Tema.alfa( Tema.tom( tom ), a ), 1.6f );
        g.textoCentro( texto, cx, y, 14, Tema.alfa( Tema.TEXTO, a ), true );
        
    }
    
}
