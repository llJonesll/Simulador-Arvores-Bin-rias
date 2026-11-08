package simulador.ui;

/**
 * Botão de toque: arredondado, com animação de pressão, pulso de destaque
 * e ícones desenhados com formas (não dependem de fontes).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Botao {
    
    public enum Icone { NENHUM, INICIO, ANTERIOR, PLAY, PAUSA, PROXIMO, FIM, VOLTAR, APAGAR }
    
    public float x, y, w, h;
    public String texto;
    public int cor;
    public Icone icone = Icone.NENHUM;
    public float tamTexto = 14;
    public float raio = 12;
    public boolean ativo = true;
    public boolean pulsar;
    public boolean selecionado;
    public boolean pressionado;
    
    /** Identificação livre, usada por quem recebe o clique. */
    public Object id;
    
    private float aperto;
    
    public Botao( String texto, int cor, Object id ) {
        this.texto = texto;
        this.cor = cor;
        this.id = id;
    }
    
    public Botao( Icone icone, int cor, Object id ) {
        this.icone = icone;
        this.cor = cor;
        this.id = id;
    }
    
    public Botao pos( float x, float y, float w, float h ) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }
    
    public boolean contem( float px, float py ) {
        return Util.dentro( px, py, x, y, w, h );
    }
    
    public void atualizar( float dt ) {
        aperto = Util.suavizar( aperto, pressionado ? 1 : 0, dt, 22 );
    }
    
    public void desenhar( Pincel g, float tempo ) {
        
        float pulso = pulsar ? 0.5f + 0.5f * (float) Math.sin( tempo * 6 ) : 0;
        float yy = y + aperto * 2;
        
        int base = Tema.misturar( Tema.BOTAO, cor, 0.14 + 0.38 * aperto + ( selecionado ? 0.25 : 0 ) + 0.25 * pulso );
        int borda = Tema.alfa( cor, ativo ? 0.5 + 0.4 * aperto + 0.5 * pulso : 0.2 );
        
        if ( pulsar ) {
            g.retanguloArred( x - 3, yy - 3, w + 6, h + 6, raio + 3, Tema.alfa( cor, 0.10 + 0.20 * pulso ) );
        }
        
        g.retanguloArred( x, yy + 2, w, h, raio, 0x46000000 );
        g.retanguloArred( x, yy, w, h, raio, ativo ? base : Tema.alfa( base, 0.4 ) );
        g.bordaArred( x, yy, w, h, raio, borda, 1.3f );
        
        int tc = ativo ? Tema.TEXTO : Tema.alfa( Tema.TEXTO, 0.4 );
        float cx = x + w / 2;
        float cy = yy + h / 2;
        
        if ( icone != Icone.NENHUM ) {
            desenharIcone( g, cx, cy, Math.min( h, 48 ) * 0.16f, tc );
        } else if ( texto != null ) {
            g.textoCentro( texto, cx, cy, tamTexto, tc, true );
        }
        
    }
    
    private void desenharIcone( Pincel g, float cx, float cy, float s, int c ) {
        switch ( icone ) {
            case PLAY:
                g.triangulo( cx - s * 0.7f, cy - s * 1.2f, cx - s * 0.7f, cy + s * 1.2f, cx + s * 1.2f, cy, c );
                break;
            case PAUSA:
                g.retangulo( cx - s * 0.95f, cy - s * 1.1f, s * 0.7f, s * 2.2f, c );
                g.retangulo( cx + s * 0.25f, cy - s * 1.1f, s * 0.7f, s * 2.2f, c );
                break;
            case PROXIMO:
                g.triangulo( cx - s * 1.1f, cy - s, cx - s * 1.1f, cy + s, cx + s * 0.6f, cy, c );
                g.retangulo( cx + s * 0.75f, cy - s, s * 0.45f, s * 2, c );
                break;
            case ANTERIOR:
                g.triangulo( cx + s * 1.1f, cy - s, cx + s * 1.1f, cy + s, cx - s * 0.6f, cy, c );
                g.retangulo( cx - s * 1.2f, cy - s, s * 0.45f, s * 2, c );
                break;
            case FIM:
                g.triangulo( cx - s * 1.3f, cy - s, cx - s * 1.3f, cy + s, cx, cy, c );
                g.triangulo( cx - s * 0.1f, cy - s, cx - s * 0.1f, cy + s, cx + s * 1.2f, cy, c );
                break;
            case INICIO:
                g.triangulo( cx + s * 1.3f, cy - s, cx + s * 1.3f, cy + s, cx, cy, c );
                g.triangulo( cx + s * 0.1f, cy - s, cx + s * 0.1f, cy + s, cx - s * 1.2f, cy, c );
                break;
            case VOLTAR:
                g.linha( cx + s * 0.8f, cy - s * 1.5f, cx - s * 0.9f, cy, c, 2.6f );
                g.linha( cx - s * 0.9f, cy, cx + s * 0.8f, cy + s * 1.5f, c, 2.6f );
                break;
            case APAGAR:
                g.triangulo( cx - s * 2.2f, cy, cx - s * 0.9f, cy - s * 1.3f, cx - s * 0.9f, cy + s * 1.3f, c );
                g.retangulo( cx - s * 0.9f, cy - s * 1.3f, s * 3.1f, s * 2.6f, c );
                g.linha( cx - s * 0.1f, cy - s * 0.6f, cx + s * 1.2f, cy + s * 0.6f, Tema.BOTAO, 1.8f );
                g.linha( cx - s * 0.1f, cy + s * 0.6f, cx + s * 1.2f, cy - s * 0.6f, Tema.BOTAO, 1.8f );
                break;
            default:
                break;
        }
    }
    
}
