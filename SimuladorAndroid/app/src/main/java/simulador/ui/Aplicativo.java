package simulador.ui;

import simulador.modelo.TipoArvore;

/**
 * Núcleo do aplicativo, independente do Android: cuida da troca de telas
 * (com fade), da ajuda e do tamanho da área de desenho (em dp). A
 * {@code ArvoreView} apenas repassa tempo, desenho e toques para cá.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Aplicativo {
    
    /** Recursos da plataforma de que o aplicativo precisa. */
    public interface Plataforma {
        /** Vibração curta de confirmação. */
        void vibrar();
    }
    
    private final Plataforma plataforma;
    private final Ajuda ajuda = new Ajuda();
    private Tela tela;
    private Tela proxima;
    private float fade;
    private float largura = 360;
    private float altura = 720;
    
    public Aplicativo( Plataforma plataforma ) {
        this.plataforma = plataforma;
        this.tela = new TelaMenu( this );
    }
    
    /** Tamanho da área de desenho em dp. */
    public void tamanho( float larguraDp, float alturaDp ) {
        this.largura = larguraDp;
        this.altura = alturaDp;
    }
    
    public float largura() {
        return largura;
    }
    
    public float altura() {
        return altura;
    }
    
    public void vibrar() {
        plataforma.vibrar();
    }
    
    public void abrirSimulador( TipoArvore tipo ) {
        if ( proxima == null ) {
            proxima = new TelaSimulador( this, tipo );
        }
    }
    
    public void voltarAoMenu() {
        if ( proxima == null ) {
            proxima = new TelaMenu( this );
        }
    }
    
    //**************************************************************************
    // ciclo
    //**************************************************************************
    
    public void atualizar( float dt ) {
        
        ajuda.atualizar( dt, largura, altura );
        if ( !ajuda.ocupada() ) {
            tela.atualizar( dt );
        }
        
        // escurece, troca de tela quando está toda escura e clareia de novo
        if ( proxima != null ) {
            fade = Math.min( 1, fade + dt * 6 );
            if ( fade >= 1 ) {
                tela = proxima;
                proxima = null;
            }
        } else {
            fade = Math.max( 0, fade - dt * 4 );
        }
        
    }
    
    public void desenhar( Pincel g ) {
        tela.desenhar( g );
        ajuda.desenhar( g, largura, altura, !tela.ocultaAjuda() );
        if ( fade > 0 ) {
            g.retangulo( 0, 0, largura, altura, Tema.alfa( 0xFF050612, fade ) );
        }
    }
    
    /** @param acao um dos valores de {@link Toque}; x e y em dp */
    public void toque( int acao, float x, float y ) {
        if ( proxima != null ) {
            return;
        }
        if ( !ajuda.toque( acao, x, y, largura, altura, !tela.ocultaAjuda() ) ) {
            tela.toque( acao, x, y );
        }
    }
    
    /** Botão voltar do Android. @return true se o aplicativo tratou o evento. */
    public boolean voltar() {
        return ajuda.voltar() || tela.voltar();
    }
    
}
