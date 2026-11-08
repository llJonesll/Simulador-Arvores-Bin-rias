package simulador;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Tela única do aplicativo. Só hospeda a {@link ArvoreView}, que faz todo
 * o desenho, e repassa o ciclo de vida e o botão voltar.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class MainActivity extends Activity {
    
    private ArvoreView view;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
        view = new ArvoreView( this );
        setContentView( view );
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        view.retomar();
    }
    
    @Override
    protected void onPause() {
        view.pausar();
        super.onPause();
    }
    
    @Override
    @SuppressWarnings( "deprecation" )
    public void onBackPressed() {
        if ( !view.aoVoltar() ) {
            super.onBackPressed();
        }
    }
    
}
