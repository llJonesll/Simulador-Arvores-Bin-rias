package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.List;
import simulador.modelo.Passo;
import simulador.modelo.TipoArvore;

/**
 * Painel lateral "passo a passo": linha do tempo clicável, título e
 * explicação do passo atual, saída dos percursos, legenda de cores e regras
 * da árvore.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class PainelPassos {
    
    private static final Object[][] LEGENDA = {
        { "Comparando", Tema.AMBAR },
        { "Caminho", new Color( 0xFFE08A ) },
        { "Novo", Tema.CIANO },
        { "Encontrado", Tema.VERDE },
        { "Pivô", Tema.ROXO },
        { "Problema", Tema.ALERTA }
    };
    
    private final TipoArvore tipo;
    private final Color acento;
    private double x, y, w, h;
    private double tlX, tlY, tlW;
    
    public PainelPassos( TipoArvore tipo, Color acento ) {
        this.tipo = tipo;
        this.acento = acento;
    }
    
    private String regras() {
        switch ( tipo ) {
            case AVL:
                return "Regra: em todo nó, fb = altura(esq) − altura(dir) fica entre −1 e +1. Fora disso, rotaciona.";
            case RN:
                return "Regras: raiz preta; vermelho não tem filho vermelho; todo caminho tem o mesmo número de pretos (null é preto).";
            default:
                return "Regra: à esquerda só chaves menores que o nó; à direita só chaves maiores.";
        }
    }
    
    //**************************************************************************
    // atualização
    //**************************************************************************
    
    public void atualizar( EngineFrame e, double x, double y, double w, double h, Reprodutor rep, boolean aceitaEntrada ) {
        
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        tlX = x + 26;
        tlY = y + 56;
        tlW = w - 52;
        
        int n = rep.total();
        if ( aceitaEntrada && n > 1 && Desenho.dentro( e.getMouseX(), e.getMouseY(), tlX - 10, tlY - 12, tlW + 20, 24 ) ) {
            Botao.mao = true;
            if ( e.isMouseButtonPressed( EngineFrame.MOUSE_BUTTON_LEFT ) ) {
                rep.saltarPara( (int) Math.round( ( e.getMouseX() - tlX ) / tlW * ( n - 1 ) ) );
            }
        }
        
    }
    
    //**************************************************************************
    // desenho
    //**************************************************************************
    
    public void desenhar( EngineFrame e, double tempo, Reprodutor rep ) {
        
        Desenho.painel( e, x, y, w, h, 18, Tema.PAINEL, Tema.BORDA );
        
        Passo p = rep.atual();
        if ( p == null ) {
            return;
        }
        
        Desenho.texto( e, "PASSO A PASSO", x + 20, y + 24, 13, Tema.TEXTO_SUAVE, true );
        String cont = ( rep.indice() + 1 ) + " / " + rep.total();
        double cw = Desenho.largura( e, cont, 13, true ) + 20;
        Desenho.painel( e, x + w - 18 - cw, y + 12, cw, 24, 12, Tema.alfa( acento, 0.18 ), Tema.alfa( acento, 0.6 ) );
        Desenho.textoCentro( e, cont, x + w - 18 - cw / 2, y + 24, 13, Tema.TEXTO, true );
        
        desenharLinhaDoTempo( e, rep );
        
        // faixa do título
        Color tom = Tema.tom( p.tom );
        double by = y + 82;
        e.fillRoundRectangle( x + 16, by, w - 32, 44, 14, Tema.alfa( tom, 0.16 ) );
        e.fillRoundRectangle( x + 16, by + 6, 5, 32, 4, tom );
        double larguraTitulo = w - 70;
        int tamTitulo = Desenho.largura( e, p.titulo, 17, true ) <= larguraTitulo ? 17 : 15;
        Desenho.texto( e, Desenho.ajustar( e, p.titulo, larguraTitulo, tamTitulo, true ), x + 32, by + 22, tamTitulo, Tema.TEXTO, true );
        
        // reserva a parte de baixo para legenda e regras
        List<String> regras = Desenho.quebrar( e, regras(), w - 40, 12, false );
        double regrasH = 30 + regras.size() * 16;
        double legendaH = 34 + 3 * 22;
        double baixo = y + h - regrasH - legendaH - 8;
        
        double ty = by + 66;
        double saidaH = 0;
        List<String> saida = null;
        if ( p.saida != null ) {
            saida = Desenho.quebrar( e, p.saida, w - 56, 15, true );
            saidaH = 44 + saida.size() * 20;
        }
        
        int maxLinhas = Math.max( 2, (int) ( ( baixo - ty - saidaH ) / 21 ) );
        double fim = Desenho.paragrafo( e, p.texto, x + 20, ty, w - 40, 15, 21, maxLinhas, Tema.TEXTO );
        
        if ( saida != null ) {
            double sy = Math.min( fim + 2, baixo - saidaH );
            Desenho.painel( e, x + 16, sy, w - 32, saidaH - 6, 12, new Color( 0x0F1230 ), Tema.alfa( Tema.TEAL, 0.7 ) );
            Desenho.texto( e, "SAÍDA", x + 30, sy + 16, 11, Tema.TEAL, true );
            for ( int i = 0; i < saida.size(); i++ ) {
                Desenho.texto( e, saida.get( i ), x + 30, sy + 38 + i * 20, 15, Tema.TEXTO, true );
            }
        }
        
        desenharLegenda( e, y + h - regrasH - legendaH, regras );
        
    }
    
    private void desenharLinhaDoTempo( EngineFrame e, Reprodutor rep ) {
        
        int n = rep.total();
        if ( n < 2 ) {
            return;
        }
        
        double passoX = tlW / ( n - 1 );
        double atualX = tlX + rep.indice() * passoX;
        
        e.setStrokeLineWidth( 3f );
        e.drawLine( tlX, tlY, tlX + tlW, tlY, new Color( 255, 255, 255, 40 ) );
        e.drawLine( tlX, tlY, atualX, tlY, Tema.alfa( acento, 0.9 ) );
        e.resetStrokeToDefault();
        
        double r = n <= 20 ? 5 : n <= 36 ? 3.5 : 2.5;
        List<Passo> ps = rep.passos();
        
        for ( int i = 0; i < n; i++ ) {
            double px = tlX + i * passoX;
            Color c = Tema.tom( ps.get( i ).tom );
            boolean feito = i <= rep.indice();
            e.fillCircle( px, tlY, r, feito ? c : Tema.alfa( c, 0.35 ) );
        }
        
        e.fillCircle( atualX, tlY, r + 4, Tema.alfa( Color.WHITE, 0.25 ) );
        e.fillCircle( atualX, tlY, r + 1.5, Tema.tom( ps.get( rep.indice() ).tom ) );
        e.setStrokeLineWidth( 2f );
        e.drawCircle( atualX, tlY, r + 3.5, Color.WHITE );
        e.resetStrokeToDefault();
        
    }
    
    private void desenharLegenda( EngineFrame e, double ly, List<String> regras ) {
        
        Desenho.texto( e, "LEGENDA", x + 20, ly + 8, 11, Tema.TEXTO_SUAVE, true );
        
        double colW = ( w - 40 ) / 2;
        for ( int i = 0; i < LEGENDA.length; i++ ) {
            double cx = x + 28 + ( i % 2 ) * colW;
            double cy = ly + 34 + ( i / 2 ) * 22;
            Color c = (Color) LEGENDA[i][1];
            e.fillCircle( cx, cy, 6, Tema.alfa( c, 0.25 ) );
            e.setStrokeLineWidth( 2f );
            e.drawCircle( cx, cy, 6, c );
            e.resetStrokeToDefault();
            Desenho.texto( e, (String) LEGENDA[i][0], cx + 14, cy, 13, Tema.TEXTO, false );
        }
        
        double ry = ly + 34 + 3 * 22 + 4;
        e.drawLine( x + 20, ry - 2, x + w - 20, ry - 2, Tema.BORDA );
        for ( int i = 0; i < regras.size(); i++ ) {
            Desenho.texto( e, regras.get( i ), x + 20, ry + 14 + i * 16, 12, Tema.alfa( Tema.TEXTO_SUAVE, 0.95 ), false );
        }
        
    }
    
}
