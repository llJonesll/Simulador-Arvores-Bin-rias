package simulador.ui;

/**
 * Ajuda global: o botão "?" no canto inferior direito e o tutorial que ele
 * abre. Funciona em qualquer tela (menu ou simulador).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Ajuda {
    
    private final BotaoAjuda botao = new BotaoAjuda();
    private final Tutorial tutorial = new Tutorial();
    private float tempo;
    
    /** @return true enquanto o tutorial está aberto (a tela de baixo fica parada). */
    public boolean ocupada() {
        return tutorial.visivel();
    }
    
    public void atualizar( float dt, float W, float H ) {
        tempo += dt;
        botao.atualizar( dt );
        tutorial.atualizar( dt, W, H );
    }
    
    /** @return true se a ajuda tratou o toque (a tela não deve recebê-lo). */
    public boolean toque( int acao, float x, float y, float W, float H, boolean botaoVisivel ) {
        
        if ( tutorial.visivel() ) {
            tutorial.toque( acao, x, y, W, H );
            return true;
        }
        
        if ( !botaoVisivel ) {
            return false;
        }
        
        if ( botao.toque( acao, x, y, W, H ) ) {
            tutorial.abrir();
            botao.parouDePulsar();
            return true;
        }
        
        return botao.pressionado();
        
    }
    
    /** Botão voltar do Android: fecha o tutorial se estiver aberto. */
    public boolean voltar() {
        if ( tutorial.visivel() ) {
            tutorial.fechar();
            return true;
        }
        return false;
    }
    
    public void desenhar( Pincel g, float W, float H, boolean botaoVisivel ) {
        if ( botaoVisivel ) {
            botao.desenhar( g, tempo, W, H );
        }
        tutorial.desenhar( g, W, H, tempo );
    }
    
}
