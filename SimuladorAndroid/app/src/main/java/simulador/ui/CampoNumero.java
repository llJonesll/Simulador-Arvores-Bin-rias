package simulador.ui;

/**
 * Valor da chave digitada no teclado numérico (0 a 999).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class CampoNumero {
    
    public static final int MAX = 999;
    
    private final StringBuilder txt = new StringBuilder();
    private float tremor;
    
    /** @return a chave digitada ou null se o campo está vazio. */
    public Integer valor() {
        return txt.length() == 0 ? null : Integer.valueOf( txt.toString() );
    }
    
    public void definir( int v ) {
        txt.setLength( 0 );
        txt.append( v );
    }
    
    public void limpar() {
        txt.setLength( 0 );
    }
    
    public void tremer() {
        tremor = 1;
    }
    
    public void digitar( int d ) {
        if ( txt.length() >= 3 ) {
            tremor = 1;
            return;
        }
        if ( txt.length() == 1 && txt.charAt( 0 ) == '0' ) {
            txt.setLength( 0 );
        }
        txt.append( d );
    }
    
    public void apagar() {
        if ( txt.length() > 0 ) {
            txt.deleteCharAt( txt.length() - 1 );
        }
    }
    
    public void atualizar( float dt ) {
        tremor = Math.max( 0, tremor - dt * 3.5f );
    }
    
    public void desenhar( Pincel g, float x, float y, float w, float h, float tempo, int acento ) {
        
        float dx = (float) Math.sin( tremor * 28 ) * 5 * tremor;
        float xx = x + dx;
        
        g.retanguloArred( xx, y, w, h, 12, Tema.PAINEL_ESCURO );
        g.bordaArred( xx, y, w, h, 12, Tema.misturar( acento, Tema.ALERTA, tremor ), 1.8f );
        g.textoEsq( "CHAVE", xx + 9, y + 10, 8.5f, Tema.TEXTO_SUAVE, true );
        
        String s = txt.toString();
        float cy = y + h / 2 + 5;
        
        if ( s.isEmpty() ) {
            g.textoCentro( "--", xx + w / 2, cy, 22, Tema.alfa( Tema.TEXTO_SUAVE, 0.45 ), true );
        } else {
            g.textoCentro( s, xx + w / 2, cy, 22, Tema.TEXTO, true );
        }
        
        if ( ( (int) ( tempo * 2 ) ) % 2 == 0 ) {
            float lw = s.isEmpty() ? 0 : g.largura( s, 22, true );
            float cx = s.isEmpty() ? xx + w / 2 + 14 : xx + w / 2 + lw / 2 + 3;
            g.retangulo( cx, cy - 12, 2, 24, Tema.alfa( acento, 0.9 ) );
        }
        
    }
    
}
