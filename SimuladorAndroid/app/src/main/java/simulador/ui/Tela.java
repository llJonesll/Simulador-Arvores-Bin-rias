package simulador.ui;

/**
 * Uma "tela" do aplicativo (menu principal ou um simulador de árvore).
 * 
 * @author Prof. Dr. David Buzatto
 */
public interface Tela {
    
    void atualizar( float dt );
    
    void desenhar( Pincel g );
    
    /** @param acao um dos valores de {@link Toque} */
    void toque( int acao, float x, float y );
    
    /** @return true se a tela quer esconder o botão "?" (ex.: com um popup aberto). */
    default boolean ocultaAjuda() {
        return false;
    }
    
    /** Botão "voltar" do Android. @return true se a tela tratou o evento. */
    boolean voltar();
    
}
