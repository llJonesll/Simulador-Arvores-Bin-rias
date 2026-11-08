package simulador.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Conjunto de botões que cuida do ciclo do toque: o clique só vale se o dedo
 * encosta e é levantado sobre o mesmo botão.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class GrupoBotoes {
    
    private final List<Botao> botoes = new ArrayList<>();
    private Botao pressionado;
    
    public <T extends Botao> T add( T b ) {
        botoes.add( b );
        return b;
    }
    
    public void atualizar( float dt ) {
        for ( Botao b : botoes ) {
            b.atualizar( dt );
        }
    }
    
    public void desenhar( Pincel g, float tempo ) {
        for ( Botao b : botoes ) {
            b.desenhar( g, tempo );
        }
    }
    
    /** @return o botão clicado neste evento, ou null. */
    public Botao toque( int acao, float x, float y ) {
        
        switch ( acao ) {
            
            case Toque.BAIXO:
                soltar();
                for ( Botao b : botoes ) {
                    if ( b.ativo && b.contem( x, y ) ) {
                        pressionado = b;
                        b.pressionado = true;
                        break;
                    }
                }
                return null;
                
            case Toque.MOVER:
                if ( pressionado != null && !pressionado.contem( x, y ) ) {
                    soltar();
                }
                return null;
                
            case Toque.CIMA:
                Botao b = pressionado;
                soltar();
                return b != null && b.contem( x, y ) ? b : null;
                
            default:
                soltar();
                return null;
                
        }
        
    }
    
    private void soltar() {
        if ( pressionado != null ) {
            pressionado.pressionado = false;
            pressionado = null;
        }
    }
    
}
