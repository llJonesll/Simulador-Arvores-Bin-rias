package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.List;

/**
 * Janela modal do tutorial, em páginas. Navegue com os botões, com as
 * setas (ou Enter) ou clicando nos pontos; Esc fecha.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Tutorial {
    
    private static final Color COR = new Color( 0x7C9BFF );
    
    private final List<PaginaTutorial> paginas = PaginasTutorial.todas();
    private final Botao anterior = new Botao( "Anterior", COR );
    private final Botao proximo = new Botao( "Próximo", COR );
    
    private boolean visivel;
    private double anim;
    private int pagina;
    private double px, py, pw, ph;
    
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
    
    private void calcular( int largura, int altura ) {
        pw = Math.min( 800, largura - 40 );
        ph = Math.min( 490, altura - 30 );
        px = ( largura - pw ) / 2;
        py = ( altura - ph ) / 2;
    }
    
    private double pontoX( int i ) {
        double passo = 22;
        return px + pw / 2 - ( paginas.size() - 1 ) * passo / 2 + i * passo;
    }
    
    public void atualizar( EngineFrame e, double delta, int largura, int altura ) {
        
        anim += ( ( visivel ? 1 : 0 ) - anim ) * ( 1 - Math.exp( -delta * 14 ) );
        
        if ( !visivel ) {
            return;
        }
        
        calcular( largura, altura );
        double by = py + ph - 62;
        anterior.pos( px + 28, by, 130, 42 );
        proximo.pos( px + pw - 158, by, 130, 42 );
        anterior.ativo = pagina > 0;
        boolean ultima = pagina == paginas.size() - 1;
        proximo.texto = ultima ? "Concluir" : "Próximo";
        
        if ( e.isKeyPressed( EngineFrame.KEY_ESCAPE ) ) {
            fechar();
            return;
        }
        
        boolean voltar = anterior.atualizar( e, delta, true ) || e.isKeyPressed( EngineFrame.KEY_LEFT );
        boolean avancar = proximo.atualizar( e, delta, true ) 
                || e.isKeyPressed( EngineFrame.KEY_RIGHT ) || e.isKeyPressed( EngineFrame.KEY_ENTER );
        
        if ( voltar && pagina > 0 ) {
            pagina--;
        } else if ( avancar ) {
            if ( ultima ) {
                fechar();
            } else {
                pagina++;
            }
        }
        
        boolean clique = e.isMouseButtonPressed( EngineFrame.MOUSE_BUTTON_LEFT );
        
        for ( int i = 0; i < paginas.size(); i++ ) {
            if ( Math.hypot( e.getMouseX() - pontoX( i ), e.getMouseY() - ( by + 21 ) ) < 10 ) {
                Botao.mao = true;
                if ( clique ) {
                    pagina = i;
                }
            }
        }
        
        if ( Desenho.dentro( e.getMouseX(), e.getMouseY(), px + pw - 48, py + 14, 34, 34 ) ) {
            Botao.mao = true;
            if ( clique ) {
                fechar();
            }
        } else if ( clique && !Desenho.dentro( e.getMouseX(), e.getMouseY(), px, py, pw, ph ) ) {
            fechar();
        }
        
    }
    
    public void desenhar( EngineFrame e, int largura, int altura, double tempo ) {
        
        if ( anim < 0.02 ) {
            return;
        }
        
        calcular( largura, altura );
        double a = anim;
        
        e.fillRectangle( 0, 0, largura, altura, new Color( 4, 5, 16, (int) ( 200 * a ) ) );
        Desenho.painel( e, px, py, pw, ph, 24, Tema.alfa( new Color( 0x151839 ), a ), Tema.alfa( COR, a * 0.8 ) );
        
        if ( a < 0.6 ) {
            return;
        }
        
        PaginaTutorial p = paginas.get( pagina );
        
        Desenho.texto( e, "TUTORIAL  " + ( pagina + 1 ) + " / " + paginas.size(), px + 30, py + 30, 12, Tema.TEXTO_SUAVE, true );
        Desenho.texto( e, p.titulo, px + 30, py + 62, 26, Tema.TEXTO, true );
        desenharFechar( e );
        
        // moldura da ilustração
        double iy = py + 92;
        double ih = 150;
        Desenho.painel( e, px + 28, iy, pw - 56, ih, 18, new Color( 255, 255, 255, 10 ), Tema.BORDA );
        p.ilustracao.desenhar( e, px + pw / 2, iy + ih / 2, tempo );
        
        double y = iy + ih + 30;
        
        // Injeção dos Créditos
        if ( pagina == 0 ) {
            String[] creditos = {
                "Desenvolvedores: João Carlos Silveira e José Pedro Franco",
                "Engine Base: JSGE (Prof. David Buzatto)",
                "IA Auxiliar: Claude e Gemini"
            };
            for ( String c : creditos ) {
                Desenho.texto( e, c, px + 40, y, 14, COR, true );
                y += 20;
            }
            y += 10;
        }
        
        for ( String t : p.topicos ) {
            e.fillCircle( px + 40, y, 4, COR );
            y = Desenho.paragrafo( e, t, px + 56, y, pw - 112, 15, 21, 3, Tema.TEXTO ) + 9;
        }
        
        anterior.desenhar( e, tempo );
        proximo.desenhar( e, tempo );
        
        double cy = py + ph - 62 + 21;
        for ( int i = 0; i < paginas.size(); i++ ) {
            boolean atual = i == pagina;
            e.fillCircle( pontoX( i ), cy, atual ? 6 : 4.5, atual ? COR : Tema.alfa( Tema.TEXTO_SUAVE, 0.6 ) );
        }
        
    }
    
    private void desenharFechar( EngineFrame e ) {
        double cx = px + pw - 31;
        double cy = py + 31;
        e.setStrokeLineWidth( 2.5f );
        e.drawLine( cx - 7, cy - 7, cx + 7, cy + 7, Tema.TEXTO_SUAVE );
        e.drawLine( cx - 7, cy + 7, cx + 7, cy - 7, Tema.TEXTO_SUAVE );
        e.resetStrokeToDefault();
    }
    
}