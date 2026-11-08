package simulador.ui;

import simulador.modelo.Passo;
import simulador.modelo.TipoArvore;

/**
 * Barra superior: botão de voltar ao menu, nome da árvore e estatísticas
 * do passo exibido (quantidade de nós e altura).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class BarraTopo {
    
    public static final float ALTURA = 52;
    
    private final TipoArvore tipo;
    private final int acento;
    private final GrupoBotoes grupo = new GrupoBotoes();
    private final Botao voltar;
    
    public BarraTopo( TipoArvore tipo, int acento ) {
        this.tipo = tipo;
        this.acento = acento;
        this.voltar = grupo.add( new Botao( Botao.Icone.VOLTAR, acento, "voltar" ) );
        voltar.pos( 6, 4, 44, 44 );
    }
    
    public void atualizar( float dt ) {
        grupo.atualizar( dt );
    }
    
    /** @return true se pediram para voltar ao menu. */
    public boolean toque( int acao, float x, float y ) {
        return grupo.toque( acao, x, y ) != null;
    }
    
    public void desenhar( Pincel g, float largura, float tempo, Passo passo ) {
        
        voltar.desenhar( g, tempo );
        
        float cy = ALTURA / 2;
        float x = 60;
        float pulso = 0.5f + 0.5f * (float) Math.sin( tempo * 3 );
        
        g.circulo( x + 5, cy, 6 + pulso, Tema.alfa( acento, 0.25 ) );
        g.circulo( x + 5, cy, 4.5f, acento );
        g.textoEsq( tipo.sigla, x + 18, cy, 22, acento, true );
        
        if ( passo != null ) {
            String s = passo.qtdNos + " nós · h " + passo.profundidade;
            float sw = g.largura( s, 12, true ) + 20;
            float sx = largura - 10 - sw;
            g.retanguloArred( sx, cy - 14, sw, 28, 14, Tema.PAINEL );
            g.bordaArred( sx, cy - 14, sw, 28, 14, Tema.BORDA, 1.2f );
            g.textoCentro( s, sx + sw / 2, cy, 12, Tema.TEXTO, true );
        }
        
    }
    
}
