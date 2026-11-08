package simulador.ui;

/**
 * Botão redondo "?" fixo no canto inferior direito da tela. Pulsa até a
 * primeira vez em que o tutorial é aberto.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class BotaoAjuda {
    
    public static final float TAMANHO = 44;
    public static final float MARGEM = 14;
    
    private boolean pressionado;
    private boolean pulsar = true;
    private float aperto;
    
    public boolean pressionado() {
        return pressionado;
    }
    
    public void parouDePulsar() {
        pulsar = false;
    }
    
    private boolean contem( float x, float y, float W, float H ) {
        float r = TAMANHO / 2 + 6;
        return Math.hypot( x - ( W - MARGEM - TAMANHO / 2 ), y - ( H - MARGEM - TAMANHO / 2 ) ) <= r;
    }
    
    /** @return true se foi tocado (dedo levantado sobre o botão). */
    public boolean toque( int acao, float x, float y, float W, float H ) {
        switch ( acao ) {
            case Toque.BAIXO:
                pressionado = contem( x, y, W, H );
                return false;
            case Toque.MOVER:
                if ( pressionado && !contem( x, y, W, H ) ) {
                    pressionado = false;
                }
                return false;
            case Toque.CIMA:
                boolean clicou = pressionado && contem( x, y, W, H );
                pressionado = false;
                return clicou;
            default:
                pressionado = false;
                return false;
        }
    }
    
    public void atualizar( float dt ) {
        aperto = Util.suavizar( aperto, pressionado ? 1 : 0, dt, 20 );
    }
    
    public void desenhar( Pincel g, float tempo, float W, float H ) {
        
        float r = TAMANHO / 2;
        float cx = W - MARGEM - r;
        float cy = H - MARGEM - r;
        float pulso = pulsar ? 0.5f + 0.5f * (float) Math.sin( tempo * 5 ) : 0;
        
        if ( pulsar ) {
            g.circulo( cx, cy, r + 4 + 5 * pulso, Tema.alfa( Tema.AJUDA, 0.12 + 0.18 * pulso ) );
        }
        
        g.circulo( cx + 1.5f, cy + 3, r, 0x5A000000 );
        g.circulo( cx, cy, r, Tema.misturar( Tema.BOTAO, Tema.AJUDA, 0.35 + 0.4 * aperto ) );
        g.aro( cx, cy, r, Tema.alfa( Tema.AJUDA, 0.75 + 0.25 * aperto ), 2f );
        g.textoCentro( "?", cx, cy, 24, Tema.TEXTO, true );
        
    }
    
}
