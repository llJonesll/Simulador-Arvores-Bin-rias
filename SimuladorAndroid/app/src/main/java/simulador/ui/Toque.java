package simulador.ui;

/**
 * Tipos de evento de toque entregues às telas (já em coordenadas de dp).
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Toque {
    
    private Toque() {
    }
    
    public static final int BAIXO = 0;
    public static final int MOVER = 1;
    public static final int CIMA = 2;
    public static final int CANCELAR = 3;
    
}
