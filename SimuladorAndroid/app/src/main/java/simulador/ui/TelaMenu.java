package simulador.ui;

import simulador.modelo.TipoArvore;

/**
 * Menu principal: três cartões (ABB, AVL, Vermelho-Preto) sobre um fundo
 * animado. Toque em um cartão para abrir o simulador.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class TelaMenu implements Tela {
    
    private final Aplicativo app;
    private final FundoAnimado fundo;
    private final CartaoArvore[] cartoes = {
        new CartaoArvore( TipoArvore.ABB, 
                "Menores à esquerda, maiores à direita", 
                "Simples e rápida em média", 
                "Sem balanceamento: pode virar uma lista" ),
        new CartaoArvore( TipoArvore.AVL, 
                "Fator de balanceamento entre -1 e +1", 
                "Rotações: LL, RR, LR e RL", 
                "Altura sempre proporcional a log n" ),
        new CartaoArvore( TipoArvore.RN, 
                "Nós vermelhos e pretos com regras de cor", 
                "Recolore e rotaciona para se equilibrar", 
                "Poucas rotações, ótima para inserções" )
    };
    
    private float tempo;
    private int pressionado = -1;
    
    public TelaMenu( Aplicativo app ) {
        this.app = app;
        this.fundo = new FundoAnimado( app.largura(), app.altura() );
    }
    
    private void posicionar() {
        float W = app.largura();
        float H = app.altura();
        float topo = 150;
        float rodape = 78;
        float gap = 14;
        float h = Util.limitar( ( H - topo - rodape - 2 * gap ) / 3, 128, 190 );
        float w = Math.min( W - 32, 480 );
        for ( int i = 0; i < cartoes.length; i++ ) {
            cartoes[i].x = ( W - w ) / 2;
            cartoes[i].y = topo + i * ( h + gap );
            cartoes[i].w = w;
            cartoes[i].h = h;
        }
    }
    
    @Override
    public void atualizar( float dt ) {
        tempo += dt;
        fundo.atualizar( dt, app.largura(), app.altura() );
        posicionar();
        for ( int i = 0; i < cartoes.length; i++ ) {
            cartoes[i].pressionado = i == pressionado;
            cartoes[i].atualizar( dt );
        }
    }
    
    @Override
    public void toque( int acao, float x, float y ) {
        
        int sobre = -1;
        for ( int i = 0; i < cartoes.length; i++ ) {
            if ( cartoes[i].contem( x, y ) ) {
                sobre = i;
            }
        }
        
        switch ( acao ) {
            case Toque.BAIXO:
                pressionado = sobre;
                break;
            case Toque.MOVER:
                if ( sobre != pressionado ) {
                    pressionado = -1;
                }
                break;
            case Toque.CIMA:
                if ( pressionado >= 0 && sobre == pressionado ) {
                    app.vibrar();
                    app.abrirSimulador( cartoes[pressionado].tipo );
                }
                pressionado = -1;
                break;
            default:
                pressionado = -1;
                break;
        }
        
    }
    
    @Override
    public boolean voltar() {
        return false; // deixa o Android fechar o aplicativo
    }
    
    @Override
    public void desenhar( Pincel g ) {
        
        float W = app.largura();
        float H = app.altura();
        
        Fundo.gradiente( g, W, H );
        fundo.desenhar( g );
        
        String titulo = "SIMULADOR DE ÁRVORES";
        float tam = 30;
        while ( tam > 16 && g.largura( titulo, tam, true ) > W - 28 ) {
            tam -= 1;
        }
        g.textoCentro( titulo, W / 2 + 1.5f, 61.5f, tam, 0x6E000000, true );
        g.textoCentro( titulo, W / 2, 60, tam, Tema.TEXTO, true );
        
        float y = 92;
        for ( String linha : g.quebrar( "Insira, busque e remova chaves e acompanhe cada passo do algoritmo", W - 56, 13, false ) ) {
            g.textoCentro( linha, W / 2, y, 13, Tema.TEXTO_SUAVE, false );
            y += 18;
        }
        
        for ( CartaoArvore c : cartoes ) {
            c.desenhar( g, tempo );
        }
        
        g.textoCentro( "Toque em uma árvore para começar", W / 2 - 22, H - 36, 12, Tema.alfa( Tema.TEXTO_SUAVE, 0.85 ), false );
        
    }
    
}
