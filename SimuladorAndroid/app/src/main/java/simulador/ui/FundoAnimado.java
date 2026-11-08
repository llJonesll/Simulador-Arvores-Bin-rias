package simulador.ui;

import java.util.Random;
import simulador.modelo.TipoArvore;

/**
 * Fundo do menu: nós à deriva que se ligam quando ficam próximos,
 * como uma "árvore" se formando e se desfazendo.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class FundoAnimado {
    
    private static final int QTD = 26;
    private static final float ALCANCE = 120;
    
    private final float[] x = new float[QTD];
    private final float[] y = new float[QTD];
    private final float[] vx = new float[QTD];
    private final float[] vy = new float[QTD];
    private final float[] r = new float[QTD];
    private final int[] cor = new int[QTD];
    
    public FundoAnimado( float largura, float altura ) {
        Random rnd = new Random( 7 );
        int[] paleta = { Tema.acento( TipoArvore.ABB ), Tema.acento( TipoArvore.AVL ), Tema.acento( TipoArvore.RN ) };
        for ( int i = 0; i < QTD; i++ ) {
            x[i] = rnd.nextFloat() * largura;
            y[i] = rnd.nextFloat() * altura;
            vx[i] = ( rnd.nextFloat() - 0.5f ) * 18;
            vy[i] = ( rnd.nextFloat() - 0.5f ) * 18;
            r[i] = 3 + rnd.nextFloat() * 5;
            cor[i] = paleta[i % 3];
        }
    }
    
    public void atualizar( float dt, float largura, float altura ) {
        for ( int i = 0; i < QTD; i++ ) {
            x[i] += vx[i] * dt;
            y[i] += vy[i] * dt;
            if ( x[i] < -20 ) x[i] = largura + 20;
            if ( x[i] > largura + 20 ) x[i] = -20;
            if ( y[i] < -20 ) y[i] = altura + 20;
            if ( y[i] > altura + 20 ) y[i] = -20;
        }
    }
    
    public void desenhar( Pincel g ) {
        
        for ( int i = 0; i < QTD; i++ ) {
            for ( int j = i + 1; j < QTD; j++ ) {
                float d = (float) Math.hypot( x[i] - x[j], y[i] - y[j] );
                if ( d < ALCANCE ) {
                    g.linha( x[i], y[i], x[j], y[j], Tema.alfa( Tema.LINHA, ( 1 - d / ALCANCE ) * 0.28 ), 1.2f );
                }
            }
        }
        
        for ( int i = 0; i < QTD; i++ ) {
            g.circulo( x[i], y[i], r[i] * 2.2f, Tema.alfa( cor[i], 0.07 ) );
            g.circulo( x[i], y[i], r[i], Tema.alfa( cor[i], 0.55 ) );
        }
        
    }
    
}
