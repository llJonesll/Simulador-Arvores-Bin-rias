package simulador.ui;

/**
 * Funções pequenas de uso geral.
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Util {
    
    private Util() {
    }
    
    public static float limitar( float v, float min, float max ) {
        return Math.max( min, Math.min( max, v ) );
    }
    
    public static boolean dentro( float px, float py, float x, float y, float w, float h ) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }
    
    /** Aproxima {@code v} de {@code alvo} (suavização exponencial independente do FPS). */
    public static float suavizar( float v, float alvo, float dt, float velocidade ) {
        return v + ( alvo - v ) * ( 1 - (float) Math.exp( -dt * velocidade ) );
    }
    
}
