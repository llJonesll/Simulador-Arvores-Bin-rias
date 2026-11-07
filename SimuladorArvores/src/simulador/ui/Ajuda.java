package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;

/**
 * Ajuda global da janela: o botão "?" no canto inferior direito e o
 * tutorial que ele abre. Funciona em qualquer tela (menu ou simulador).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Ajuda {
    
    private final BotaoAjuda botao = new BotaoAjuda();
    private final Tutorial tutorial = new Tutorial();
    private double tempo;
    
    /** @return true se o tutorial está aberto (ou acabou de abrir/fechar) e a tela não deve tratar entrada. */
    public boolean atualizar( EngineFrame e, double delta, int largura, int altura ) {
        
        tempo += delta;
        boolean estavaAberto = tutorial.visivel();
        
        boolean abrir = botao.atualizar( e, delta, largura, altura ) && !estavaAberto;
        abrir |= !estavaAberto && ( e.isKeyPressed( EngineFrame.KEY_F1 ) || e.isKeyPressed( EngineFrame.KEY_H ) );
        
        tutorial.atualizar( e, delta, largura, altura );
        
        if ( abrir ) {
            tutorial.abrir();
            botao.parouDePulsar();
        }
        
        return estavaAberto || abrir;
        
    }
    
    public void desenhar( EngineFrame e, int largura, int altura ) {
        botao.desenhar( e, tempo );
        tutorial.desenhar( e, largura, altura, tempo );
    }
    
}
