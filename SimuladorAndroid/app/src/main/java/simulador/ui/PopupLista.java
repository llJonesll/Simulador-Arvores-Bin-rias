package simulador.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Painel que sobe da parte de baixo com uma lista rolável de cartões
 * (usado para os templates e para os percursos). Role arrastando; toque
 * em um cartão para escolher; toque fora ou use "voltar" para fechar.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class PopupLista {
    
    public static class Item {
        public final String titulo;
        public final String texto;
        public final String selo;
        public final int corSelo;
        public final Object dado;
        float h;
        
        public Item( String titulo, String texto, String selo, int corSelo, Object dado ) {
            this.titulo = titulo;
            this.texto = texto;
            this.selo = selo;
            this.corSelo = corSelo;
            this.dado = dado;
        }
    }
    
    private static final float GAP = 10;
    
    private List<Item> itens = new ArrayList<>();
    private String titulo = "";
    private String subtitulo = "";
    private int acento;
    private boolean visivel;
    private float anim;
    private float rolagem;
    private float rolMax;
    private float velocidade;
    private float deltaFrame;
    
    // geometria (preenchida em desenhar)
    private float sheetY, listaY, listaH, largura;
    
    // estado do toque
    private boolean tocando;
    private boolean arrastou;
    private float inicioX, inicioY, ultimoY;
    private int pressionado = -1;
    
    public boolean visivel() {
        return visivel;
    }
    
    public void abrir( String titulo, String subtitulo, List<Item> itens, int acento ) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.itens = itens;
        this.acento = acento;
        this.rolagem = 0;
        this.velocidade = 0;
        this.visivel = true;
    }
    
    public void fechar() {
        visivel = false;
    }
    
    public void atualizar( float dt ) {
        
        anim = Util.suavizar( anim, visivel ? 1 : 0, dt, 14 );
        
        if ( tocando ) {
            velocidade = dt > 0 ? deltaFrame / dt : 0;
        } else {
            rolagem += velocidade * dt;
            velocidade *= (float) Math.exp( -dt * 4 );
            if ( Math.abs( velocidade ) < 4 ) {
                velocidade = 0;
            }
        }
        deltaFrame = 0;
        rolagem = Util.limitar( rolagem, 0, rolMax );
        
    }
    
    //**************************************************************************
    // toque
    //**************************************************************************
    
    private int itemEm( float x, float y ) {
        if ( y < listaY || y > listaY + listaH ) {
            return -1;
        }
        float cy = listaY + 6 - rolagem;
        for ( int i = 0; i < itens.size(); i++ ) {
            float h = itens.get( i ).h;
            if ( y >= cy && y <= cy + h && x >= 12 && x <= largura - 12 ) {
                return i;
            }
            cy += h + GAP;
        }
        return -1;
    }
    
    /** @return o item escolhido neste toque, ou null. */
    public Item toque( int acao, float x, float y ) {
        
        if ( !visivel ) {
            return null;
        }
        
        switch ( acao ) {
            
            case Toque.BAIXO:
                tocando = true;
                arrastou = false;
                inicioX = x;
                inicioY = y;
                ultimoY = y;
                velocidade = 0;
                pressionado = itemEm( x, y );
                return null;
                
            case Toque.MOVER:
                if ( !tocando ) {
                    return null;
                }
                if ( Math.abs( y - inicioY ) > 10 ) {
                    arrastou = true;
                    pressionado = -1;
                }
                if ( arrastou ) {
                    float antes = rolagem;
                    rolagem = Util.limitar( rolagem - ( y - ultimoY ), 0, rolMax );
                    deltaFrame += rolagem - antes;
                }
                ultimoY = y;
                return null;
                
            case Toque.CIMA:
                boolean eraToque = tocando && !arrastou;
                tocando = false;
                int i = pressionado;
                pressionado = -1;
                if ( eraToque ) {
                    if ( i >= 0 && itemEm( x, y ) == i ) {
                        Item it = itens.get( i );
                        fechar();
                        return it;
                    }
                    boolean fora = y < sheetY;
                    boolean noX = Math.hypot( x - ( largura - 32 ), y - ( sheetY + 30 ) ) < 22;
                    if ( fora || noX ) {
                        fechar();
                    }
                }
                return null;
                
            default:
                tocando = false;
                pressionado = -1;
                return null;
                
        }
        
    }
    
    //**************************************************************************
    // desenho
    //**************************************************************************
    
    public void desenhar( Pincel g, float W, float H, float tempo ) {
        
        if ( anim < 0.02f ) {
            return;
        }
        
        largura = W;
        float a = anim;
        float topo = Math.max( 60, H * 0.1f );
        float desl = ( 1 - a ) * ( H - topo ) * 0.35f;
        sheetY = topo + desl;
        
        g.retangulo( 0, 0, W, H, Tema.alfa( 0xFF040510, 0.78 * a ) );
        g.retanguloArred( 0, sheetY, W, H - sheetY + 30, 24, Tema.alfa( Tema.FOLHA, a ) );
        g.bordaArred( 0, sheetY, W, H - sheetY + 30, 24, Tema.alfa( acento, 0.8 * a ), 1.6f );
        
        // alça e cabeçalho
        g.retanguloArred( W / 2 - 20, sheetY + 8, 40, 4, 2, Tema.alfa( Tema.TEXTO_SUAVE, 0.5 * a ) );
        g.textoEsq( titulo, 18, sheetY + 36, 22, Tema.alfa( Tema.TEXTO, a ), true );
        g.textoEsq( subtitulo, 18, sheetY + 58, 11.5f, Tema.alfa( Tema.TEXTO_SUAVE, a ), false );
        g.linha( W - 38, sheetY + 24, W - 26, sheetY + 36, Tema.alfa( Tema.TEXTO_SUAVE, a ), 2.4f );
        g.linha( W - 38, sheetY + 36, W - 26, sheetY + 24, Tema.alfa( Tema.TEXTO_SUAVE, a ), 2.4f );
        
        listaY = sheetY + 72;
        listaH = H - listaY;
        
        // mede os cartões
        float total = 6;
        for ( Item it : itens ) {
            int linhas = g.quebrar( it.texto, W - 24 - 28, 12.5f, false ).size();
            it.h = 14 + 22 + 6 + linhas * 17 + 12;
            total += it.h + GAP;
        }
        rolMax = Math.max( 0, total - listaH + 8 );
        
        g.clipe( 0, listaY, W, listaH );
        float cy = listaY + 6 - rolagem;
        for ( int i = 0; i < itens.size(); i++ ) {
            Item it = itens.get( i );
            if ( cy + it.h > listaY && cy < listaY + listaH ) {
                desenharCartao( g, it, 12, cy, W - 24, i == pressionado, a );
            }
            cy += it.h + GAP;
        }
        g.fimClipe();
        
        // barra de rolagem
        if ( rolMax > 0 ) {
            float bh = Math.max( 30, listaH * listaH / ( listaH + rolMax ) );
            float by = listaY + ( listaH - bh ) * ( rolagem / rolMax );
            g.retanguloArred( W - 5, by, 3, bh, 1.5f, Tema.alfa( Tema.TEXTO_SUAVE, 0.5 * a ) );
        }
        
    }
    
    private void desenharCartao( Pincel g, Item it, float x, float y, float w, boolean pressionado, float a ) {
        
        int fundo = Tema.misturar( Tema.BOTAO, it.corSelo, pressionado ? 0.3 : 0.0 );
        g.retanguloArred( x, y, w, it.h, 14, Tema.alfa( fundo, a ) );
        g.bordaArred( x, y, w, it.h, 14, Tema.alfa( it.corSelo, a * ( pressionado ? 1 : 0.55 ) ), pressionado ? 2.2f : 1.3f );
        
        float seloW = 0;
        if ( it.selo != null ) {
            seloW = g.largura( it.selo, 10, true ) + 14;
            g.retanguloArred( x + w - seloW - 10, y + 12, seloW, 18, 9, Tema.alfa( it.corSelo, 0.22 * a ) );
            g.textoCentro( it.selo, x + w - seloW / 2 - 10, y + 21, 10, Tema.alfa( it.corSelo, a ), true );
        }
        
        g.textoEsq( g.ajustar( it.titulo, w - 28 - seloW - 8, 15, true ), x + 14, y + 21, 15, Tema.alfa( Tema.TEXTO, a ), true );
        g.paragrafo( it.texto, x + 14, y + 49, w - 28, 12.5f, 17, 12, Tema.alfa( Tema.TEXTO_SUAVE, a ) );
        
    }
    
}
