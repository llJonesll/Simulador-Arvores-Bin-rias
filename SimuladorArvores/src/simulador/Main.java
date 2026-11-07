package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import simulador.modelo.TipoArvore;
import simulador.ui.Botao;
import simulador.ui.Tela;
import simulador.ui.TelaMenu;
import simulador.ui.TelaSimulador;

/**
 * Simulador de Árvores: ABB, AVL e Vermelho-Preto.
 * 
 * A janela só cuida do ciclo de vida e da troca de telas (com fade);
 * o menu e os simuladores ficam em {@code simulador.ui} e os algoritmos
 * em {@code simulador.modelo}.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Main extends EngineFrame {
    
    private Tela tela;
    private Tela proxima;
    private double fade;
    private boolean maoAnterior;
    
    public Main() {
        super( 1280, 720, "Simulador de Árvores", 60, true );
    }
    
    @Override
    public void create() {
        setDefaultFontName( FONT_SANS_SERIF );
        setDefaultFontStyle( FONT_PLAIN );
        tela = new TelaMenu( this );
    }
    
    @Override
    public void update( double delta ) {
        
        Botao.mao = false;
        tela.atualizar( delta );
        
        boolean mao = Botao.mao;
        if ( mao != maoAnterior ) {
            setMouseCursor( mao ? MOUSE_CURSOR_POINTING_HAND : MOUSE_CURSOR_DEFAULT );
            maoAnterior = mao;
        }
        
        atualizarFade( delta );
        
    }
    
    /** Escurece a tela, troca de tela quando está toda escura e clareia de novo. */
    private void atualizarFade( double delta ) {
        if ( proxima != null ) {
            fade = Math.min( 1, fade + delta * 6 );
            if ( fade >= 1 ) {
                tela = proxima;
                proxima = null;
            }
        } else {
            fade = Math.max( 0, fade - delta * 4 );
        }
    }
    
    @Override
    public void draw() {
        tela.desenhar();
        if ( fade > 0 ) {
            fillRectangle( 0, 0, getScreenWidth(), getScreenHeight(), new java.awt.Color( 5, 6, 18, (int) ( 255 * fade ) ) );
        }
    }
    
    public void abrirSimulador( TipoArvore tipo ) {
        if ( proxima == null ) {
            proxima = new TelaSimulador( this, tipo );
        }
    }
    
    public void voltarAoMenu() {
        if ( proxima == null ) {
            proxima = new TelaMenu( this );
        }
    }
    
    public static void main( String[] args ) {
        // melhora a suavização do texto no Swing
        System.setProperty( "awt.useSystemAAFontSettings", "on" );
        System.setProperty( "swing.aatext", "true" );
        new Main();
    }
    
}
