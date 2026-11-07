package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitários de desenho sobre a JSGE: texto alinhado/centralizado,
 * quebra de linhas, painéis arredondados e brilho.
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Desenho {
    
    private Desenho() {
    }
    
    // a JSGE desenha o texto com y no "topo" e a linha de base em y + altura/2;
    // este deslocamento centraliza verticalmente os glifos em torno de cy
    private static final double AJUSTE_Y = 0.22;
    
    private static void estilo( EngineFrame e, boolean negrito ) {
        e.setFontStyle( negrito ? EngineFrame.FONT_BOLD : EngineFrame.FONT_PLAIN );
    }
    
    public static int largura( EngineFrame e, String s, int tam, boolean negrito ) {
        estilo( e, negrito );
        return e.measureText( s, tam );
    }
    
    /** Texto alinhado à esquerda, centralizado verticalmente em cy. */
    public static void texto( EngineFrame e, String s, double x, double cy, int tam, Color cor, boolean negrito ) {
        estilo( e, negrito );
        e.drawText( s, x, cy - tam * AJUSTE_Y - tam * 0.5 + tam * 0.5, tam, cor );
    }
    
    public static void textoCentro( EngineFrame e, String s, double cx, double cy, int tam, Color cor, boolean negrito ) {
        estilo( e, negrito );
        int w = e.measureText( s, tam );
        e.drawText( s, cx - w / 2.0, cy - tam * AJUSTE_Y, tam, cor );
    }
    
    public static void textoDireita( EngineFrame e, String s, double xd, double cy, int tam, Color cor, boolean negrito ) {
        estilo( e, negrito );
        int w = e.measureText( s, tam );
        e.drawText( s, xd - w, cy - tam * AJUSTE_Y, tam, cor );
    }
    
    /** Quebra o texto em linhas que cabem em {@code larguraMax} (respeita "\n"). */
    public static List<String> quebrar( EngineFrame e, String texto, double larguraMax, int tam, boolean negrito ) {
        
        estilo( e, negrito );
        List<String> linhas = new ArrayList<>();
        
        for ( String par : texto.split( "\n", -1 ) ) {
            StringBuilder atual = new StringBuilder();
            for ( String palavra : par.split( " " ) ) {
                String teste = atual.length() == 0 ? palavra : atual + " " + palavra;
                if ( atual.length() > 0 && e.measureText( teste, tam ) > larguraMax ) {
                    linhas.add( atual.toString() );
                    atual = new StringBuilder( palavra );
                } else {
                    atual = new StringBuilder( teste );
                }
            }
            linhas.add( atual.toString() );
        }
        
        return linhas;
        
    }
    
    /** Desenha linhas quebradas; retorna o y (centro) logo após a última linha. */
    public static double paragrafo( EngineFrame e, String texto, double x, double y, double larguraMax, 
            int tam, double alturaLinha, int maxLinhas, Color cor ) {
        
        List<String> linhas = quebrar( e, texto, larguraMax, tam, false );
        double yy = y;
        
        for ( int i = 0; i < linhas.size(); i++ ) {
            String l = linhas.get( i );
            if ( i == maxLinhas - 1 && linhas.size() > maxLinhas ) {
                l = l + "…";
            }
            if ( i >= maxLinhas ) {
                break;
            }
            texto( e, l, x, yy, tam, cor, false );
            yy += alturaLinha;
        }
        
        return yy;
        
    }
    
    /** Corta o texto com reticências se não couber na largura. */
    public static String ajustar( EngineFrame e, String s, double larg, int tam, boolean negrito ) {
        if ( largura( e, s, tam, negrito ) <= larg ) {
            return s;
        }
        while ( s.length() > 1 && largura( e, s + "…", tam, negrito ) > larg ) {
            s = s.substring( 0, s.length() - 1 );
        }
        return s + "…";
    }
    
    public static void painel( EngineFrame e, double x, double y, double w, double h, double raio, Color fundo, Color borda ) {
        if ( fundo != null ) {
            e.fillRoundRectangle( x, y, w, h, raio * 2, fundo );
        }
        if ( borda != null ) {
            e.setStrokeLineWidth( 1.5f );
            e.drawRoundRectangle( x, y, w, h, raio * 2, borda );
            e.resetStrokeToDefault();
        }
    }
    
    /** Halo suave em volta de um círculo. */
    public static void brilho( EngineFrame e, double cx, double cy, double r, Color cor, double intensidade ) {
        if ( intensidade <= 0.01 ) {
            return;
        }
        for ( int i = 4; i >= 1; i-- ) {
            e.fillCircle( cx, cy, r + i * 4.5, Tema.alfa( cor, intensidade * 0.11 ) );
        }
    }
    
    public static boolean dentro( double px, double py, double x, double y, double w, double h ) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }
    
    public static double limitar( double v, double min, double max ) {
        return Math.max( min, Math.min( max, v ) );
    }
    
}
