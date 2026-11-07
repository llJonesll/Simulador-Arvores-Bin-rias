package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.Random;

/**
 * Fundo do menu: nós à deriva que se ligam quando ficam próximos,
 * como uma "árvore" se formando e se desfazendo.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class FundoAnimado {
    
    private static final int QTD = 34;
    private static final double ALCANCE = 190;
    
    private final double[] x = new double[QTD];
    private final double[] y = new double[QTD];
    private final double[] vx = new double[QTD];
    private final double[] vy = new double[QTD];
    private final double[] r = new double[QTD];
    private final Color[] cor = new Color[QTD];
    
    public FundoAnimado( int largura, int altura ) {
        Random rnd = new Random( 7 );
        Color[] paleta = { Tema.acento( simulador.modelo.TipoArvore.ABB ), 
            Tema.acento( simulador.modelo.TipoArvore.AVL ), Tema.acento( simulador.modelo.TipoArvore.RN ) };
        for ( int i = 0; i < QTD; i++ ) {
            x[i] = rnd.nextDouble() * largura;
            y[i] = rnd.nextDouble() * altura;
            vx[i] = ( rnd.nextDouble() - 0.5 ) * 26;
            vy[i] = ( rnd.nextDouble() - 0.5 ) * 26;
            r[i] = 4 + rnd.nextDouble() * 7;
            cor[i] = paleta[i % 3];
        }
    }
    
    public void atualizar( double delta, int largura, int altura ) {
        for ( int i = 0; i < QTD; i++ ) {
            x[i] += vx[i] * delta;
            y[i] += vy[i] * delta;
            if ( x[i] < -20 ) x[i] = largura + 20;
            if ( x[i] > largura + 20 ) x[i] = -20;
            if ( y[i] < -20 ) y[i] = altura + 20;
            if ( y[i] > altura + 20 ) y[i] = -20;
        }
    }
    
    public void desenhar( EngineFrame e ) {
        
        e.setStrokeLineWidth( 1.5f );
        for ( int i = 0; i < QTD; i++ ) {
            for ( int j = i + 1; j < QTD; j++ ) {
                double d = Math.hypot( x[i] - x[j], y[i] - y[j] );
                if ( d < ALCANCE ) {
                    e.drawLine( x[i], y[i], x[j], y[j], Tema.alfa( Tema.LINHA, ( 1 - d / ALCANCE ) * 0.28 ) );
                }
            }
        }
        e.resetStrokeToDefault();
        
        for ( int i = 0; i < QTD; i++ ) {
            e.fillCircle( x[i], y[i], r[i] * 2.2, Tema.alfa( cor[i], 0.07 ) );
            e.fillCircle( x[i], y[i], r[i], Tema.alfa( cor[i], 0.55 ) );
        }
        
    }
    
}
