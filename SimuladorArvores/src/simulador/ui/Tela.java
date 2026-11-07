package simulador.ui;

/**
 * Uma "tela" do simulador (menu principal ou um simulador de árvore).
 * 
 * @author Prof. Dr. David Buzatto
 */
public interface Tela {
    
    void atualizar( double delta );
    
    void desenhar();
    
}
