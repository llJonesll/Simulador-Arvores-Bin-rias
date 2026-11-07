package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import simulador.modelo.Passo;
import simulador.modelo.TipoArvore;

/**
 * Barra superior: botão de voltar ao menu, nome da árvore e estatísticas
 * do passo exibido (quantidade de nós e altura).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class BarraTopo {
    
    public static final double ALTURA = 64;
    
    private final TipoArvore tipo;
    private final Color acento;
    private final Botao voltar;
    
    public BarraTopo( TipoArvore tipo, Color acento ) {
        this.tipo = tipo;
        this.acento = acento;
        this.voltar = new Botao( Botao.Icone.VOLTAR, acento ).pos( 16, 12, 112, 42 );
    }
    
    /** @return true se pediram para voltar ao menu. */
    public boolean atualizar( EngineFrame e, double delta, boolean aceitaEntrada ) {
        return voltar.atualizar( e, delta, aceitaEntrada );
    }
    
    public void desenhar( EngineFrame e, int largura, double tempo, Passo passo ) {
        
        voltar.desenhar( e, tempo );
        
        double cy = 33;
        double x = 148;
        double p = 0.5 + 0.5 * Math.sin( tempo * 3 );
        
        e.fillCircle( x + 8, cy, 8 + p, Tema.alfa( acento, 0.25 ) );
        e.fillCircle( x + 8, cy, 6, acento );
        Desenho.texto( e, tipo.sigla, x + 26, cy, 26, acento, true );
        int w = Desenho.largura( e, tipo.sigla, 26, true );
        Desenho.texto( e, tipo.nome, x + 26 + w + 12, cy + 2, 18, Tema.TEXTO_SUAVE, false );
        
        if ( passo != null ) {
            String s = "Nós: " + passo.qtdNos + "    Altura: " + passo.profundidade;
            double sw = Desenho.largura( e, s, 15, true ) + 32;
            double sx = largura - 16 - sw;
            Desenho.painel( e, sx, cy - 18, sw, 36, 18, Tema.PAINEL, Tema.BORDA );
            Desenho.textoCentro( e, s, sx + sw / 2, cy, 15, Tema.TEXTO, true );
        }
        
    }
    
}
