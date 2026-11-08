package simulador.ui;

import simulador.modelo.TipoArvore;

/**
 * Cartão de seleção de uma árvore no menu: miniatura animada à esquerda e
 * nome + características à direita. Afunda levemente quando tocado.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class CartaoArvore {
    
    // posições relativas dos 7 nós da miniatura (raiz, 2 filhos, 4 netos)
    private static final float[][] POS = { 
        { 0, 0 }, { -34, 32 }, { 34, 32 }, { -52, 64 }, { -17, 64 }, { 17, 64 }, { 52, 64 } 
    };
    private static final int[] PAI = { -1, 0, 0, 1, 1, 2, 2 };
    private static final int[] CHAVES = { 8, 4, 12, 2, 6, 10, 14 };
    
    public final TipoArvore tipo;
    public float x, y, w, h;
    public boolean pressionado;
    
    private final int acento;
    private final String[] pontos;
    private float pressao;
    
    public CartaoArvore( TipoArvore tipo, String... pontos ) {
        this.tipo = tipo;
        this.pontos = pontos;
        this.acento = Tema.acento( tipo );
    }
    
    public boolean contem( float px, float py ) {
        return Util.dentro( px, py, x, y, w, h );
    }
    
    public void atualizar( float dt ) {
        pressao = Util.suavizar( pressao, pressionado ? 1 : 0, dt, 18 );
    }
    
    public void desenhar( Pincel g, float tempo ) {
        
        float yy = y + 2 * pressao;
        
        g.retanguloArred( x + 2, yy + 5, w, h, 22, 0x50000000 );
        g.retanguloArred( x, yy, w, h, 22, Tema.misturar( Tema.PAINEL_FORTE, acento, 0.07 + 0.14 * pressao ) );
        g.bordaArred( x, yy, w, h, 22, Tema.alfa( acento, 0.5 + 0.5 * pressao ), 1.5f + 1.2f * pressao );
        
        desenharMiniatura( g, x + 72, yy + h / 2 - 34, tempo );
        
        float tx = x + 144;
        float tw = w - 144 - 14;
        g.textoEsq( tipo.sigla, tx, yy + 30, 30, acento, true );
        g.textoEsq( g.ajustar( tipo.nome, tw, 13, true ), tx, yy + 58, 13, Tema.TEXTO, true );
        
        float py = yy + 80;
        for ( String p : pontos ) {
            if ( py > yy + h - 22 ) {
                break;
            }
            g.circulo( tx + 3, py, 2.5f, acento );
            py = g.paragrafo( p, tx + 12, py, tw - 12, 11.5f, 15, 2, Tema.TEXTO_SUAVE ) + 5;
        }
        
        // seta "entrar"
        float ax = x + w - 26;
        float ay = yy + h - 22;
        g.triangulo( ax - 5, ay - 7, ax - 5, ay + 7, ax + 6, ay, Tema.alfa( acento, 0.6 + 0.4 * pressao ) );
        
    }
    
    private void desenharMiniatura( Pincel g, float cx, float cy, float tempo ) {
        
        float[] px = new float[POS.length];
        float[] py = new float[POS.length];
        for ( int i = 0; i < POS.length; i++ ) {
            px[i] = cx + POS[i][0];
            py[i] = cy + POS[i][1] + (float) Math.sin( tempo * 1.6 + i * 0.9 ) * 1.8f;
        }
        
        for ( int i = 1; i < POS.length; i++ ) {
            g.linha( px[PAI[i]], py[PAI[i]], px[i], py[i], Tema.alfa( Tema.LINHA, 0.8 ), 1.8f );
        }
        
        for ( int i = 0; i < POS.length; i++ ) {
            
            int fundo;
            int anel;
            if ( tipo == TipoArvore.RN ) {
                boolean vermelho = i == 1 || i == 2;
                fundo = vermelho ? Tema.NO_VERMELHO : Tema.NO_PRETO;
                anel = vermelho ? Tema.NO_VERMELHO_ANEL : Tema.NO_PRETO_ANEL;
            } else {
                fundo = Tema.misturar( Tema.NO_BASE, acento, 0.38 );
                anel = Tema.misturar( acento, 0xFFFFFFFF, 0.35 );
            }
            
            g.circulo( px[i], py[i], 11, fundo );
            g.aro( px[i], py[i], 11, anel, 1.8f );
            g.textoCentro( String.valueOf( CHAVES[i] ), px[i], py[i], 10, Tema.TEXTO, true );
            
            if ( tipo == TipoArvore.AVL ) {
                g.retanguloArred( px[i] - 10, py[i] + 13, 20, 9, 4.5f, Tema.PAINEL_ESCURO );
                g.textoCentro( "fb 0", px[i], py[i] + 17.5f, 6.5f, Tema.VERDE, true );
            }
            
        }
        
    }
    
}
