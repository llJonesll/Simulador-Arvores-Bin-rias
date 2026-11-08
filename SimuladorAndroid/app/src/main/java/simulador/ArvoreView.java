package simulador;

import android.content.Context;
import android.graphics.Canvas;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import simulador.ui.Aplicativo;
import simulador.ui.Pincel;
import simulador.ui.Toque;

/**
 * Única {@link View} do aplicativo: um "motor" de jogo mínimo. A cada
 * quadro mede o tempo, atualiza o {@link Aplicativo}, desenha tudo no
 * Canvas e pede o quadro seguinte. Os toques são convertidos para dp.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class ArvoreView extends View {
    
    private final Aplicativo app;
    private final Pincel pincel = new Pincel();
    private final float densidade;
    
    private long ultimoQuadro;
    private boolean rodando;
    
    public ArvoreView( Context contexto ) {
        super( contexto );
        densidade = contexto.getResources().getDisplayMetrics().density;
        app = new Aplicativo( () -> performHapticFeedback( HapticFeedbackConstants.KEYBOARD_TAP ) );
        setFocusable( true );
    }
    
    /** Retoma o laço de animação (chamado em onResume). */
    public void retomar() {
        rodando = true;
        ultimoQuadro = 0;
        postInvalidateOnAnimation();
    }
    
    /** Pausa o laço de animação (chamado em onPause). */
    public void pausar() {
        rodando = false;
    }
    
    /** @return true se o aplicativo tratou o botão voltar. */
    public boolean aoVoltar() {
        return app.voltar();
    }
    
    @Override
    protected void onSizeChanged( int w, int h, int antigoW, int antigoH ) {
        app.tamanho( w / densidade, h / densidade );
    }
    
    @Override
    protected void onDraw( Canvas canvas ) {
        
        long agora = System.nanoTime();
        float dt = ultimoQuadro == 0 ? 0 : ( agora - ultimoQuadro ) / 1_000_000_000f;
        ultimoQuadro = agora;
        
        // evita saltos grandes (ex.: ao voltar de outro aplicativo)
        app.atualizar( Math.min( dt, 0.05f ) );
        
        pincel.iniciar( canvas, densidade );
        app.desenhar( pincel );
        pincel.terminar();
        
        if ( rodando ) {
            postInvalidateOnAnimation();
        }
        
    }
    
    @Override
    public boolean onTouchEvent( MotionEvent e ) {
        
        float x = e.getX() / densidade;
        float y = e.getY() / densidade;
        
        switch ( e.getActionMasked() ) {
            case MotionEvent.ACTION_DOWN:
                app.toque( Toque.BAIXO, x, y );
                break;
            case MotionEvent.ACTION_MOVE:
                app.toque( Toque.MOVER, x, y );
                break;
            case MotionEvent.ACTION_UP:
                app.toque( Toque.CIMA, x, y );
                break;
            case MotionEvent.ACTION_CANCEL:
                app.toque( Toque.CANCELAR, x, y );
                break;
            default:
                break;
        }
        
        return true;
        
    }
    
}
