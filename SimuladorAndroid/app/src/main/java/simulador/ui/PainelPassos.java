package simulador.ui;

import java.util.List;
import simulador.modelo.Passo;

/**
 * Painel "passo a passo": título e explicação do passo atual, saída dos
 * percursos e linha do tempo (toque ou arraste para saltar entre passos).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class PainelPassos {
    
    private final int acento;
    private float x, y, w, h;
    private float tlX, tlY, tlW;
    private boolean arrastando;
    
    public PainelPassos( int acento ) {
        this.acento = acento;
    }
    
    public void posicionar( float x, float y, float w, float h ) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        tlX = x + 18;
        tlY = y + 56;
        tlW = w - 36;
    }
    
    /** @return true se o toque pertence à linha do tempo. */
    public boolean toque( int acao, float px, float py, Reprodutor rep ) {
        switch ( acao ) {
            case Toque.BAIXO:
                if ( rep.total() > 1 && Util.dentro( px, py, x, tlY - 18, w, 36 ) ) {
                    arrastando = true;
                    saltar( px, rep );
                    return true;
                }
                return false;
            case Toque.MOVER:
                if ( arrastando ) {
                    saltar( px, rep );
                }
                return arrastando;
            default:
                boolean era = arrastando;
                arrastando = false;
                return era;
        }
    }
    
    private void saltar( float px, Reprodutor rep ) {
        int n = rep.total();
        rep.saltarPara( Math.round( ( px - tlX ) / tlW * ( n - 1 ) ) );
    }
    
    public void desenhar( Pincel g, float tempo, Reprodutor rep ) {
        
        g.retanguloArred( x, y, w, h, 18, Tema.PAINEL );
        g.bordaArred( x, y, w, h, 18, Tema.BORDA, 1.2f );
        
        Passo p = rep.atual();
        if ( p == null ) {
            return;
        }
        
        // faixa do título + contador
        int tom = Tema.tom( p.tom );
        float by = y + 8;
        g.retanguloArred( x + 8, by, w - 16, 30, 10, Tema.alfa( tom, 0.16 ) );
        g.retanguloArred( x + 8, by + 5, 4, 20, 2, tom );
        
        String cont = ( rep.indice() + 1 ) + "/" + rep.total();
        float cw = g.largura( cont, 11, true ) + 14;
        g.retanguloArred( x + w - 14 - cw, by + 6, cw, 18, 9, Tema.alfa( acento, 0.22 ) );
        g.textoCentro( cont, x + w - 14 - cw / 2, by + 15, 11, Tema.TEXTO, true );
        
        g.textoEsq( g.ajustar( p.titulo, w - 40 - cw - 16, 14.5f, true ), x + 20, by + 15, 14.5f, Tema.TEXTO, true );
        
        desenharLinhaDoTempo( g, rep );
        
        // saída dos percursos (mostra o final se for longa)
        float rodape = 6;
        if ( p.saida != null ) {
            String s = "Saída: " + p.saida;
            while ( s.length() > 12 && g.largura( s, 12.5f, true ) > w - 32 ) {
                s = "…" + s.substring( s.indexOf( ',', 12 ) >= 0 ? s.indexOf( ',', 12 ) + 1 : 12 );
            }
            g.retanguloArred( x + 8, y + h - 30, w - 16, 22, 8, Tema.PAINEL_ESCURO );
            g.bordaArred( x + 8, y + h - 30, w - 16, 22, 8, Tema.alfa( Tema.TEAL, 0.7 ), 1.2f );
            g.textoEsq( s, x + 16, y + h - 19, 12.5f, Tema.TEXTO, true );
            rodape = 34;
        }
        
        // explicação (reduz a fonte se não couber)
        float inicio = rep.total() > 1 ? 78 : 56; // sem linha do tempo o texto sobe
        float ty = y + inicio;
        float disponivel = h - inicio - rodape;
        float tam = 13;
        float lh = 17;
        List<String> linhas = g.quebrar( p.texto, w - 32, tam, false );
        if ( linhas.size() * lh > disponivel ) {
            tam = 12;
            lh = 15.5f;
        }
        int maxLinhas = Math.max( 1, (int) ( disponivel / lh ) );
        g.paragrafo( p.texto, x + 16, ty, w - 32, tam, lh, maxLinhas, Tema.TEXTO );
        
    }
    
    private void desenharLinhaDoTempo( Pincel g, Reprodutor rep ) {
        
        int n = rep.total();
        if ( n < 2 ) {
            return;
        }
        
        float passoX = tlW / ( n - 1 );
        float atualX = tlX + rep.indice() * passoX;
        
        g.linha( tlX, tlY, tlX + tlW, tlY, 0x28FFFFFF, 2.6f );
        g.linha( tlX, tlY, atualX, tlY, Tema.alfa( acento, 0.9 ), 2.6f );
        
        float r = n <= 20 ? 4.2f : n <= 36 ? 3f : 2.2f;
        List<Passo> ps = rep.passos();
        
        for ( int i = 0; i < n; i++ ) {
            int c = Tema.tom( ps.get( i ).tom );
            g.circulo( tlX + i * passoX, tlY, r, i <= rep.indice() ? c : Tema.alfa( c, 0.35 ) );
        }
        
        g.circulo( atualX, tlY, r + 4.5f, Tema.alfa( 0xFFFFFFFF, 0.25 ) );
        g.circulo( atualX, tlY, r + 1.5f, Tema.tom( ps.get( rep.indice() ).tom ) );
        g.aro( atualX, tlY, r + 3.5f, 0xFFFFFFFF, 1.8f );
        
    }
    
}
