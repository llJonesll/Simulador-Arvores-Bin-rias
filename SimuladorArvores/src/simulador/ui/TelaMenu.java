package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import simulador.Main;
import simulador.modelo.TipoArvore;

/**
 * Menu principal: três cartões (ABB, AVL, Vermelho-Preto) sobre um fundo
 * animado. Escolha com o mouse, com as setas + Enter ou com as teclas 1, 2 e 3.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class TelaMenu implements Tela {
    
    private final Main m;
    private final FundoAnimado fundo;
    private final CartaoArvore[] cartoes = {
        new CartaoArvore( TipoArvore.ABB, 0, 
                "Menores à esquerda, maiores à direita", 
                "Simples e rápida em média", 
                "Sem balanceamento: pode virar uma lista" ),
        new CartaoArvore( TipoArvore.AVL, 1, 
                "Fator de balanceamento sempre entre -1 e +1", 
                "Quatro casos de rotação: LL, RR, LR e RL", 
                "Altura sempre proporcional a log n" ),
        new CartaoArvore( TipoArvore.RN, 2, 
                "Nós vermelhos e pretos com regras de cor", 
                "Recolore e rotaciona para se equilibrar", 
                "Poucas rotações, ótima para inserções" )
    };
    
    private int selecionado = 1;
    private double tempo;
    private int mouseAnteriorX = -1;
    
    public TelaMenu( Main m ) {
        this.m = m;
        this.fundo = new FundoAnimado( m.getScreenWidth(), m.getScreenHeight() );
    }
    
    @Override
    public void atualizar( double delta ) {
        
        tempo += delta;
        int largura = m.getScreenWidth();
        int altura = m.getScreenHeight();
        fundo.atualizar( delta, largura, altura );
        
        posicionar( largura, altura );
        
        // o mouse só muda a seleção quando se mexe, para não brigar com o teclado
        int mx = m.getMouseX();
        int my = m.getMouseY();
        boolean moveu = mx != mouseAnteriorX;
        mouseAnteriorX = mx;
        
        for ( int i = 0; i < cartoes.length; i++ ) {
            if ( cartoes[i].contem( mx, my ) ) {
                Botao.mao = true;
                if ( moveu ) {
                    selecionado = i;
                }
                if ( m.isMouseButtonPressed( EngineFrame.MOUSE_BUTTON_LEFT ) ) {
                    m.abrirSimulador( cartoes[i].tipo );
                    return;
                }
            }
        }
        
        if ( m.isKeyPressed( EngineFrame.KEY_LEFT ) ) {
            selecionado = ( selecionado + cartoes.length - 1 ) % cartoes.length;
        }
        if ( m.isKeyPressed( EngineFrame.KEY_RIGHT ) ) {
            selecionado = ( selecionado + 1 ) % cartoes.length;
        }
        if ( m.isKeyPressed( EngineFrame.KEY_ENTER ) ) {
            m.abrirSimulador( cartoes[selecionado].tipo );
            return;
        }
        for ( int i = 0; i < cartoes.length; i++ ) {
            if ( m.isKeyPressed( EngineFrame.KEY_ONE + i ) ) {
                m.abrirSimulador( cartoes[i].tipo );
                return;
            }
        }
        
        for ( int i = 0; i < cartoes.length; i++ ) {
            cartoes[i].atualizar( delta, i == selecionado );
        }
        
    }
    
    private void posicionar( int largura, int altura ) {
        double gap = 28;
        double w = Math.min( 340, ( largura - 80 - 2 * gap ) / 3 );
        double h = Math.min( 470, altura - 230 );
        double x0 = ( largura - ( 3 * w + 2 * gap ) ) / 2;
        double y0 = Math.max( 150, ( altura - h ) / 2 + 50 );
        for ( int i = 0; i < cartoes.length; i++ ) {
            cartoes[i].x = x0 + i * ( w + gap );
            cartoes[i].y = y0;
            cartoes[i].w = w;
            cartoes[i].h = h;
        }
    }
    
    @Override
    public void desenhar() {
        
        int largura = m.getScreenWidth();
        int altura = m.getScreenHeight();
        
        Fundo.gradiente( m, largura, altura );
        fundo.desenhar( m );
        
        double cx = largura / 2.0;
        Desenho.textoCentro( m, "SIMULADOR DE ÁRVORES", cx + 3, 63, 46, new java.awt.Color( 0, 0, 0, 110 ), true );
        Desenho.textoCentro( m, "SIMULADOR DE ÁRVORES", cx, 60, 46, Tema.TEXTO, true );
        Desenho.textoCentro( m, "Insira, busque e remova chaves e acompanhe cada passo do algoritmo", cx, 104, 18, Tema.TEXTO_SUAVE, false );
        
        for ( CartaoArvore c : cartoes ) {
            c.desenhar( m, tempo );
        }
        
        Desenho.textoCentro( m, "Setas + Enter, teclas 1 / 2 / 3 ou clique para escolher", cx, altura - 26, 14, Tema.alfa( Tema.TEXTO_SUAVE, 0.8 ), false );
        
    }
    
}
