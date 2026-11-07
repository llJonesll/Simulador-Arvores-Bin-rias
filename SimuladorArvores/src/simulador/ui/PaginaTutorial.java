package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;

/**
 * Uma página do tutorial: título, tópicos e uma ilustração desenhada
 * com os próprios componentes do simulador.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class PaginaTutorial {
    
    /** Desenha a ilustração centrada em (cx, cy). */
    public interface Ilustracao {
        void desenhar( EngineFrame e, double cx, double cy, double tempo );
    }
    
    public final String titulo;
    public final String[] topicos;
    public final Ilustracao ilustracao;
    
    public PaginaTutorial( String titulo, Ilustracao ilustracao, String... topicos ) {
        this.titulo = titulo;
        this.ilustracao = ilustracao;
        this.topicos = topicos;
    }
    
}
