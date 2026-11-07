package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import simulador.modelo.TipoArvore;

/**
 * Cartão de seleção de uma árvore no menu principal: miniatura animada,
 * nome, características e atalho. Reage ao mouse com "levitação" e brilho.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class CartaoArvore {
    
    // posições relativas dos 7 nós da miniatura (raiz, 2 filhos, 4 netos)
    private static final double[][] POS = { 
        { 0, 0 }, { -56, 50 }, { 56, 50 }, { -84, 100 }, { -28, 100 }, { 28, 100 }, { 84, 100 } 
    };
    private static final int[] PAI = { -1, 0, 0, 1, 1, 2, 2 };
    private static final int[] CHAVES = { 8, 4, 12, 2, 6, 10, 14 };
    
    public final TipoArvore tipo;
    public double x, y, w, h;
    
    private final Color acento;
    private final int indice;
    private final String[] pontos;
    private double hover;
    
    public CartaoArvore( TipoArvore tipo, int indice, String... pontos ) {
        this.tipo = tipo;
        this.indice = indice;
        this.pontos = pontos;
        this.acento = Tema.acento( tipo );
    }
    
    public boolean contem( int mx, int my ) {
        return Desenho.dentro( mx, my, x, y, w, h );
    }
    
    public void atualizar( double delta, boolean selecionado ) {
        hover += ( ( selecionado ? 1 : 0 ) - hover ) * ( 1 - Math.exp( -delta * 12 ) );
    }
    
    public void desenhar( EngineFrame e, double tempo ) {
        
        double yy = y - 12 * hover;
        
        e.fillRoundRectangle( x + 4, yy + 10, w, h, 30, new Color( 0, 0, 0, 80 ) );
        if ( hover > 0.02 ) {
            e.fillRoundRectangle( x - 8, yy - 8, w + 16, h + 16, 40, Tema.alfa( acento, 0.10 * hover ) );
        }
        e.fillRoundRectangle( x, yy, w, h, 30, Tema.misturar( Tema.PAINEL_FORTE, acento, 0.06 + 0.12 * hover ) );
        e.setStrokeLineWidth( (float) ( 1.6 + 1.6 * hover ) );
        e.drawRoundRectangle( x, yy, w, h, 30, Tema.alfa( acento, 0.4 + 0.6 * hover ) );
        e.resetStrokeToDefault();
        
        desenharMiniatura( e, x + w / 2, yy + 56, tempo );
        
        Desenho.textoCentro( e, tipo.sigla, x + w / 2, yy + 214, 40, acento, true );
        Desenho.textoCentro( e, tipo.nome, x + w / 2, yy + 250, 17, Tema.TEXTO, true );
        
        double py = yy + 290;
        for ( String p : pontos ) {
            e.fillCircle( x + 30, py, 3.5, acento );
            py = Desenho.paragrafo( e, p, x + 44, py, w - 74, 14, 19, 2, Tema.TEXTO_SUAVE ) + 7;
        }
        
        double bw = 150;
        double by = yy + h - 54;
        e.fillRoundRectangle( x + w / 2 - bw / 2, by, bw, 34, 20, Tema.alfa( acento, 0.14 + 0.5 * hover ) );
        Desenho.textoCentro( e, "Entrar  [" + ( indice + 1 ) + "]", x + w / 2, by + 17, 15, Tema.TEXTO, true );
        
    }
    
    private void desenharMiniatura( EngineFrame e, double cx, double cy, double tempo ) {
        
        double[] px = new double[POS.length];
        double[] py = new double[POS.length];
        for ( int i = 0; i < POS.length; i++ ) {
            px[i] = cx + POS[i][0];
            py[i] = cy + POS[i][1] + Math.sin( tempo * 1.6 + i * 0.9 ) * 2.5;
        }
        
        e.setStrokeLineWidth( 2.4f );
        for ( int i = 1; i < POS.length; i++ ) {
            e.drawLine( px[PAI[i]], py[PAI[i]], px[i], py[i], Tema.alfa( Tema.LINHA, 0.8 ) );
        }
        e.resetStrokeToDefault();
        
        for ( int i = 0; i < POS.length; i++ ) {
            
            Color fundo;
            Color anel;
            if ( tipo == TipoArvore.RN ) {
                boolean vermelho = i == 1 || i == 2;
                fundo = vermelho ? Tema.NO_VERMELHO : Tema.NO_PRETO;
                anel = vermelho ? Tema.NO_VERMELHO_ANEL : Tema.NO_PRETO_ANEL;
            } else {
                fundo = Tema.misturar( Tema.NO_BASE, acento, 0.38 );
                anel = Tema.misturar( acento, Color.WHITE, 0.35 );
            }
            
            e.fillCircle( px[i], py[i], 17, fundo );
            e.setStrokeLineWidth( 2.4f );
            e.drawCircle( px[i], py[i], 17, anel );
            e.resetStrokeToDefault();
            Desenho.textoCentro( e, String.valueOf( CHAVES[i] ), px[i], py[i], 15, Tema.TEXTO, true );
            
            if ( tipo == TipoArvore.AVL ) {
                // selinho de fator de balanceamento
                e.fillRoundRectangle( px[i] - 15, py[i] + 20, 30, 12, 10, new Color( 0x10132E ) );
                Desenho.textoCentro( e, "fb 0", px[i], py[i] + 26, 9, Tema.VERDE, true );
            }
            
        }
        
    }
    
}
