package simulador.ui;

/**
 * Uma página do tutorial: título, tópicos e uma ilustração desenhada
 * com os próprios componentes do aplicativo.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class PaginaTutorial {
    
    /** Desenha a ilustração centrada em (cx, cy), em dp. */
    public interface Ilustracao {
        void desenhar( Pincel g, float cx, float cy, float tempo );
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
