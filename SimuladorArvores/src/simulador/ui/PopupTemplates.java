package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.List;
import simulador.modelo.Template;
import simulador.modelo.Template.Operacao;

/**
 * Janela modal com os templates (cenários prontos) da árvore atual.
 * Escolha com o mouse ou com as teclas 1 a 8; ESC fecha.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class PopupTemplates {
    
    private static final double CARTAO_ALTURA = 118;
    private static final double GAP = 12;
    
    private final Color acento;
    private List<Template> lista;
    private boolean visivel;
    private double anim;
    private double px, py, pw, ph, cardW;
    private int hover = -1;
    
    public PopupTemplates( Color acento ) {
        this.acento = acento;
    }
    
    public boolean visivel() {
        return visivel;
    }
    
    public void abrir( List<Template> lista ) {
        this.lista = lista;
        this.visivel = true;
    }
    
    public void fechar() {
        visivel = false;
    }
    
    private void calcular( int largura, int altura ) {
        pw = Math.min( 820, largura - 40 );
        cardW = ( pw - 48 - GAP ) / 2;
        int linhas = ( lista.size() + 1 ) / 2;
        ph = 70 + linhas * CARTAO_ALTURA + ( linhas - 1 ) * GAP + 46;
        px = ( largura - pw ) / 2;
        py = Math.max( 10, ( altura - ph ) / 2 );
    }
    
    private double cartaoX( int i ) {
        return px + 24 + ( i % 2 ) * ( cardW + GAP );
    }
    
    private double cartaoY( int i ) {
        return py + 70 + ( i / 2 ) * ( CARTAO_ALTURA + GAP );
    }
    
    /** @return o template escolhido neste quadro, ou null. */
    public Template atualizar( EngineFrame e, double delta, int largura, int altura ) {
        
        anim += ( ( visivel ? 1 : 0 ) - anim ) * ( 1 - Math.exp( -delta * 14 ) );
        
        if ( !visivel ) {
            return null;
        }
        
        calcular( largura, altura );
        
        if ( e.isKeyPressed( EngineFrame.KEY_ESCAPE ) ) {
            fechar();
            return null;
        }
        
        for ( int i = 0; i < lista.size() && i < 8; i++ ) {
            if ( e.isKeyPressed( EngineFrame.KEY_ZERO + 1 + i ) ) {
                fechar();
                return lista.get( i );
            }
        }
        
        if ( e.isMouseButtonPressed( EngineFrame.MOUSE_BUTTON_LEFT ) ) {
            for ( int i = 0; i < lista.size(); i++ ) {
                if ( Desenho.dentro( e.getMouseX(), e.getMouseY(), cartaoX( i ), cartaoY( i ), cardW, CARTAO_ALTURA ) ) {
                    fechar();
                    return lista.get( i );
                }
            }
            if ( !Desenho.dentro( e.getMouseX(), e.getMouseY(), px, py, pw, ph ) ) {
                fechar();
            }
        }
        
        hover = -1;
        for ( int i = 0; i < lista.size(); i++ ) {
            if ( Desenho.dentro( e.getMouseX(), e.getMouseY(), cartaoX( i ), cartaoY( i ), cardW, CARTAO_ALTURA ) ) {
                hover = i;
                Botao.mao = true;
            }
        }
        
        return null;
        
    }
    
    public void desenhar( EngineFrame e, int largura, int altura, double tempo ) {
        
        if ( anim < 0.02 || lista == null ) {
            return;
        }
        
        calcular( largura, altura );
        double a = anim;
        double desl = ( 1 - a ) * 24;
        
        e.fillRectangle( 0, 0, largura, altura, new Color( 4, 5, 16, (int) ( 190 * a ) ) );
        
        double y0 = py + desl;
        Desenho.painel( e, px, y0, pw, ph, 22, Tema.alfa( new Color( 0x151839 ), a ), Tema.alfa( acento, a * 0.8 ) );
        Desenho.texto( e, "Templates", px + 28, y0 + 34, 26, Tema.alfa( Tema.TEXTO, a ), true );
        Desenho.texto( e, "Escolha um cenário pronto (clique ou tecle 1-" + lista.size() + "). A árvore é montada e a próxima ação é sugerida.", 
                px + 28, y0 + 56, 13, Tema.alfa( Tema.TEXTO_SUAVE, a ), false );
        
        for ( int i = 0; i < lista.size(); i++ ) {
            desenharCartao( e, i, lista.get( i ), desl, a );
        }
        
        Desenho.textoCentro( e, "ESC para fechar", px + pw / 2, y0 + ph - 24, 13, Tema.alfa( Tema.TEXTO_SUAVE, a ), false );
        
    }
    
    private void desenharCartao( EngineFrame e, int i, Template t, double desl, double a ) {
        
        double cx = cartaoX( i );
        double cy = cartaoY( i ) + desl;
        boolean sobre = i == hover;
        
        Color cor = cor( t.operacao );
        Color fundo = Tema.misturar( new Color( 0x232957 ), cor, sobre ? 0.28 : 0.0 );
        e.fillRoundRectangle( cx, cy, cardW, CARTAO_ALTURA, 18, Tema.alfa( fundo, a ) );
        e.setStrokeLineWidth( sobre ? 2.6f : 1.6f );
        e.drawRoundRectangle( cx, cy, cardW, CARTAO_ALTURA, 18, Tema.alfa( cor, a * ( sobre ? 1 : 0.55 ) ) );
        e.resetStrokeToDefault();
        
        e.fillCircle( cx + 24, cy + 24, 13, Tema.alfa( cor, a * 0.25 ) );
        Desenho.textoCentro( e, String.valueOf( i + 1 ), cx + 24, cy + 24, 14, Tema.alfa( Tema.TEXTO, a ), true );
        
        double larguraBadge = 0;
        if ( t.operacao != Operacao.NENHUMA ) {
            String op = t.operacao.name() + " " + t.chave;
            larguraBadge = Desenho.largura( e, op, 11, true ) + 16;
            double bx = cx + cardW - larguraBadge - 12;
            e.fillRoundRectangle( bx, cy + 14, larguraBadge, 20, 12, Tema.alfa( cor, a * 0.22 ) );
            Desenho.textoCentro( e, op, bx + larguraBadge / 2, cy + 24, 11, Tema.alfa( cor, a ), true );
        }
        
        double larguraTitulo = cardW - 46 - larguraBadge - 22;
        Desenho.texto( e, Desenho.ajustar( e, t.nome, larguraTitulo, 16, true ), cx + 46, cy + 24, 16, Tema.alfa( Tema.TEXTO, a ), true );
        
        Desenho.paragrafo( e, t.descricao, cx + 16, cy + 52, cardW - 30, 13, 17, 4, Tema.alfa( Tema.TEXTO_SUAVE, a ) );
        
    }
    
    private Color cor( Operacao op ) {
        switch ( op ) {
            case INSERIR: return Tema.VERDE;
            case REMOVER: return Tema.LARANJA;
            case BUSCAR: return Tema.CIANO;
            default: return Tema.TEXTO_SUAVE;
        }
    }
    
}
