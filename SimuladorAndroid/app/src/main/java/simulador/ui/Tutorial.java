package simulador.ui;

import java.util.List;

/**
 * Janela modal do tutorial, em páginas. Navegue com os botões, deslizando
 * o dedo para os lados ou tocando nos pontos; o botão voltar do Android
 * ou um toque fora da janela fecham.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Tutorial {
    
    private static final String ANTERIOR = "anterior";
    private static final String PROXIMO = "proximo";
    
    private final List<PaginaTutorial> paginas = PaginasTutorial.todas();
    private final GrupoBotoes grupo = new GrupoBotoes();
    private final Botao anterior = grupo.add( new Botao( "Anterior", Tema.AJUDA, ANTERIOR ) );
    private final Botao proximo = grupo.add( new Botao( "Próximo", Tema.AJUDA, PROXIMO ) );
    
    private boolean visivel;
    private float anim;
    private int pagina;
    private float px, py, pw, ph;
    
    // toque
    private boolean tocando;
    private float inicioX, inicioY;
    
    public boolean visivel() {
        return visivel;
    }
    
    public void abrir() {
        visivel = true;
        pagina = 0;
    }
    
    public void fechar() {
        visivel = false;
    }
    
    private void calcular( float W, float H ) {
        pw = Math.min( 520, W - 24 );
        ph = Math.min( 520, H - 48 );
        px = ( W - pw ) / 2;
        py = ( H - ph ) / 2;
        float by = py + ph - 56;
        anterior.pos( px + 14, by, 104, 42 );
        proximo.pos( px + pw - 118, by, 104, 42 );
        anterior.ativo = pagina > 0;
        proximo.texto = pagina == paginas.size() - 1 ? "Concluir" : "Próximo";
    }
    
    private float pontoX( int i ) {
        float passo = 18;
        return px + pw / 2 - ( paginas.size() - 1 ) * passo / 2 + i * passo;
    }
    
    public void atualizar( float dt, float W, float H ) {
        anim = Util.suavizar( anim, visivel ? 1 : 0, dt, 14 );
        if ( visivel ) {
            calcular( W, H );
            grupo.atualizar( dt );
        }
    }
    
    private void avancar() {
        if ( pagina == paginas.size() - 1 ) {
            fechar();
        } else {
            pagina++;
        }
    }
    
    private void voltarPagina() {
        pagina = Math.max( 0, pagina - 1 );
    }
    
    public void toque( int acao, float x, float y, float W, float H ) {
        
        if ( !visivel ) {
            return;
        }
        
        calcular( W, H );
        
        Botao b = grupo.toque( acao, x, y );
        if ( b != null ) {
            if ( b.id == ANTERIOR ) {
                voltarPagina();
            } else {
                avancar();
            }
            tocando = false;
            return;
        }
        
        switch ( acao ) {
            
            case Toque.BAIXO:
                tocando = true;
                inicioX = x;
                inicioY = y;
                break;
                
            case Toque.CIMA:
                if ( !tocando ) {
                    break;
                }
                tocando = false;
                float dx = x - inicioX;
                float dy = y - inicioY;
                if ( Math.abs( dx ) > 50 && Math.abs( dx ) > Math.abs( dy ) * 1.5f ) {
                    if ( dx < 0 ) {
                        avancar();
                    } else {
                        voltarPagina();
                    }
                } else if ( Math.abs( dx ) < 12 && Math.abs( dy ) < 12 ) {
                    tocar( x, y );
                }
                break;
                
            case Toque.CANCELAR:
                tocando = false;
                break;
                
            default:
                break;
                
        }
        
    }
    
    private void tocar( float x, float y ) {
        
        if ( Math.hypot( x - ( px + pw - 26 ), y - ( py + 26 ) ) < 22 ) {
            fechar();
            return;
        }
        
        for ( int i = 0; i < paginas.size(); i++ ) {
            if ( Math.hypot( x - pontoX( i ), y - ( py + ph - 35 ) ) < 12 ) {
                pagina = i;
                return;
            }
        }
        
        if ( !Util.dentro( x, y, px, py, pw, ph ) ) {
            fechar();
        }
        
    }
    
    public void desenhar( Pincel g, float W, float H, float tempo ) {
        
        if ( anim < 0.02f ) {
            return;
        }
        
        calcular( W, H );
        float a = anim;
        
        g.retangulo( 0, 0, W, H, Tema.alfa( 0xFF040510, 0.8 * a ) );
        g.retanguloArred( px, py, pw, ph, 22, Tema.alfa( Tema.FOLHA, a ) );
        g.bordaArred( px, py, pw, ph, 22, Tema.alfa( Tema.AJUDA, 0.8 * a ), 1.6f );
        
        if ( a < 0.6f ) {
            return;
        }
        
        PaginaTutorial p = paginas.get( pagina );
        
        g.textoEsq( "TUTORIAL  " + ( pagina + 1 ) + " / " + paginas.size(), px + 20, py + 24, 10.5f, Tema.TEXTO_SUAVE, true );
        g.textoEsq( g.ajustar( p.titulo, pw - 80, 21, true ), px + 20, py + 50, 21, Tema.TEXTO, true );
        
        float cx = px + pw - 26;
        float cy = py + 26;
        g.linha( cx - 6, cy - 6, cx + 6, cy + 6, Tema.TEXTO_SUAVE, 2.2f );
        g.linha( cx - 6, cy + 6, cx + 6, cy - 6, Tema.TEXTO_SUAVE, 2.2f );
        
        // moldura da ilustração
        float iy = py + 72;
        float ih = 150;
        g.retanguloArred( px + 14, iy, pw - 28, ih, 16, 0x0AFFFFFF );
        g.bordaArred( px + 14, iy, pw - 28, ih, 16, Tema.BORDA, 1.2f );
        g.clipe( px + 14, iy, pw - 28, ih );
        p.ilustracao.desenhar( g, px + pw / 2, iy + ih / 2, tempo );
        g.fimClipe();
        
        float y = iy + ih + 24;
        for ( String t : p.topicos ) {
            g.circulo( px + 26, y, 3.5f, Tema.AJUDA );
            y = g.paragrafo( t, px + 40, y, pw - 66, 13, 18, 4, Tema.TEXTO ) + 8;
        }
        
        grupo.desenhar( g, tempo );
        
        float dy = py + ph - 35;
        for ( int i = 0; i < paginas.size(); i++ ) {
            boolean atual = i == pagina;
            g.circulo( pontoX( i ), dy, atual ? 5 : 3.8f, atual ? Tema.AJUDA : Tema.alfa( Tema.TEXTO_SUAVE, 0.6 ) );
        }
        
    }
    
}
