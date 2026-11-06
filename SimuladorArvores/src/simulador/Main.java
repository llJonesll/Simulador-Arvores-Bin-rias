package simulador;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import javax.swing.JOptionPane;

/**
 * Simulador de Árvores.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Main extends EngineFrame {
    
    private ABB abb;
    
    private int espHor;
    private int espVert;
    private int margemHor;
    private int margemVert;
        
    public Main() {
        super( 800, 450, "Simulador Árvores", 60, true );
    }
    
    @Override
    public void create() {
        
        abb = new ABB();
        
        abb.put( 20 );
        abb.put( 10 );
        abb.put( 15 );
        abb.put( 5 );
        abb.put( 30 );

        espHor = 50;
        espVert = 50;
        margemHor = 50;
        margemVert = 50;
        
        abb.atualizarNos( espHor, espVert, margemHor, margemVert );
        
        setDefaultFontSize( 20 );
        
    }
    
    @Override
    public void update( double delta ) {
        if ( isMouseButtonPressed( MOUSE_BUTTON_LEFT ) ) {
            int chave = Integer.parseInt( JOptionPane.showInputDialog( "Chave" ) );
            abb.put( chave );
            abb.atualizarNos( espHor, espVert, margemHor, margemVert );
        }        
    }
    
    @Override
    public void draw() {
        clearBackground( WHITE );
        abb.desenhar( this );
    }
    
    public static void main( String[] args ) {
        new Main();
    }
    
}
