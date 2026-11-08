package simulador.ui;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.List;

/**
 * Pequena camada sobre o {@link Canvas} do Android. Todo o desenho do
 * aplicativo passa por aqui, em unidades de <b>dp</b> (o canvas é escalado
 * pela densidade da tela), e o resto do código não conhece o Android.
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Pincel {
    
    public static final int ESQUERDA = 0;
    public static final int CENTRO = 1;
    public static final int DIREITA = 2;
    
    private final Paint p = new Paint( Paint.ANTI_ALIAS_FLAG );
    private final RectF r = new RectF();
    private final Path caminho = new Path();
    private Canvas c;
    
    /** Começa um quadro: passa a desenhar em dp. */
    public void iniciar( Canvas canvas, float densidade ) {
        c = canvas;
        c.save();
        c.scale( densidade, densidade );
    }
    
    public void terminar() {
        c.restore();
    }
    
    private void preencher( int cor ) {
        p.setStyle( Paint.Style.FILL );
        p.setShader( null );
        p.setColor( cor );
    }
    
    private void traco( int cor, float espessura ) {
        p.setStyle( Paint.Style.STROKE );
        p.setShader( null );
        p.setColor( cor );
        p.setStrokeWidth( espessura );
    }
    
    //**************************************************************************
    // formas
    //**************************************************************************
    
    public void circulo( float x, float y, float raio, int cor ) {
        preencher( cor );
        c.drawCircle( x, y, raio, p );
    }
    
    public void aro( float x, float y, float raio, int cor, float espessura ) {
        traco( cor, espessura );
        c.drawCircle( x, y, raio, p );
    }
    
    public void linha( float x1, float y1, float x2, float y2, int cor, float espessura ) {
        traco( cor, espessura );
        p.setStrokeCap( Paint.Cap.ROUND );
        c.drawLine( x1, y1, x2, y2, p );
        p.setStrokeCap( Paint.Cap.BUTT );
    }
    
    public void retangulo( float x, float y, float w, float h, int cor ) {
        preencher( cor );
        c.drawRect( x, y, x + w, y + h, p );
    }
    
    /** @param raio raio dos cantos */
    public void retanguloArred( float x, float y, float w, float h, float raio, int cor ) {
        preencher( cor );
        r.set( x, y, x + w, y + h );
        c.drawRoundRect( r, raio, raio, p );
    }
    
    public void bordaArred( float x, float y, float w, float h, float raio, int cor, float espessura ) {
        traco( cor, espessura );
        r.set( x, y, x + w, y + h );
        c.drawRoundRect( r, raio, raio, p );
    }
    
    public void triangulo( float x1, float y1, float x2, float y2, float x3, float y3, int cor ) {
        preencher( cor );
        caminho.reset();
        caminho.moveTo( x1, y1 );
        caminho.lineTo( x2, y2 );
        caminho.lineTo( x3, y3 );
        caminho.close();
        c.drawPath( caminho, p );
    }
    
    public void gradienteVertical( float x, float y, float w, float h, int cima, int baixo ) {
        p.setStyle( Paint.Style.FILL );
        p.setShader( new LinearGradient( x, y, x, y + h, cima, baixo, Shader.TileMode.CLAMP ) );
        c.drawRect( x, y, x + w, y + h, p );
        p.setShader( null );
    }
    
    /** Passa a recortar o desenho; termine com {@link #fimClipe()}. */
    public void clipe( float x, float y, float w, float h ) {
        c.save();
        c.clipRect( x, y, x + w, y + h );
    }
    
    public void fimClipe() {
        c.restore();
    }
    
    //**************************************************************************
    // texto
    //**************************************************************************
    
    private void estilo( float tam, boolean negrito ) {
        p.setTextSize( tam );
        p.setTypeface( negrito ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT );
    }
    
    public float largura( String s, float tam, boolean negrito ) {
        estilo( tam, negrito );
        return p.measureText( s );
    }
    
    /** Texto centralizado verticalmente em {@code cy}. */
    public void texto( String s, float x, float cy, float tam, int cor, boolean negrito, int alinhamento ) {
        estilo( tam, negrito );
        preencher( cor );
        p.setTextAlign( alinhamento == CENTRO ? Paint.Align.CENTER : alinhamento == DIREITA ? Paint.Align.RIGHT : Paint.Align.LEFT );
        Paint.FontMetrics fm = p.getFontMetrics();
        c.drawText( s, x, cy - ( fm.ascent + fm.descent ) / 2, p );
        p.setTextAlign( Paint.Align.LEFT );
    }
    
    public void textoEsq( String s, float x, float cy, float tam, int cor, boolean negrito ) {
        texto( s, x, cy, tam, cor, negrito, ESQUERDA );
    }
    
    public void textoCentro( String s, float cx, float cy, float tam, int cor, boolean negrito ) {
        texto( s, cx, cy, tam, cor, negrito, CENTRO );
    }
    
    public void textoDir( String s, float xd, float cy, float tam, int cor, boolean negrito ) {
        texto( s, xd, cy, tam, cor, negrito, DIREITA );
    }
    
    /** Quebra o texto em linhas que cabem em {@code larguraMax} (respeita "\n"). */
    public List<String> quebrar( String texto, float larguraMax, float tam, boolean negrito ) {
        
        estilo( tam, negrito );
        List<String> linhas = new ArrayList<>();
        
        for ( String par : texto.split( "\n", -1 ) ) {
            StringBuilder atual = new StringBuilder();
            for ( String palavra : par.split( " " ) ) {
                String teste = atual.length() == 0 ? palavra : atual + " " + palavra;
                if ( atual.length() > 0 && p.measureText( teste ) > larguraMax ) {
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
    
    /** Desenha um parágrafo; devolve o y (centro) logo após a última linha. */
    public float paragrafo( String texto, float x, float y, float larguraMax, float tam, 
            float alturaLinha, int maxLinhas, int cor ) {
        
        List<String> linhas = quebrar( texto, larguraMax, tam, false );
        float yy = y;
        
        for ( int i = 0; i < linhas.size() && i < maxLinhas; i++ ) {
            String l = linhas.get( i );
            if ( i == maxLinhas - 1 && linhas.size() > maxLinhas ) {
                l = l + "…";
            }
            textoEsq( l, x, yy, tam, cor, false );
            yy += alturaLinha;
        }
        
        return yy;
        
    }
    
    /** Corta o texto com reticências se não couber na largura. */
    public String ajustar( String s, float larg, float tam, boolean negrito ) {
        if ( largura( s, tam, negrito ) <= larg ) {
            return s;
        }
        while ( s.length() > 1 && largura( s + "…", tam, negrito ) > larg ) {
            s = s.substring( 0, s.length() - 1 );
        }
        return s + "…";
    }
    
    /** Halo suave em volta de um círculo. */
    public void brilho( float cx, float cy, float raio, int cor, double intensidade ) {
        if ( intensidade <= 0.01 ) {
            return;
        }
        for ( int i = 4; i >= 1; i-- ) {
            circulo( cx, cy, raio + i * 3.6f, Tema.alfa( cor, intensidade * 0.11 ) );
        }
    }
    
}
