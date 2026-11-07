package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Pequenas faíscas para dar "sabor de jogo" aos eventos (nó novo, achado,
 * desbalanceamento...).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Particulas {
    
    private static class P {
        double x, y, vx, vy, vida, vidaMax, raio;
        Color cor;
    }
    
    private final List<P> lista = new ArrayList<>();
    private final Random rnd = new Random();
    
    public void explodir( double x, double y, Color cor, int qtd, double forca ) {
        for ( int i = 0; i < qtd; i++ ) {
            P p = new P();
            double ang = rnd.nextDouble() * Math.PI * 2;
            double vel = forca * ( 0.35 + rnd.nextDouble() * 0.65 );
            p.x = x;
            p.y = y;
            p.vx = Math.cos( ang ) * vel;
            p.vy = Math.sin( ang ) * vel - forca * 0.15;
            p.vidaMax = 0.5 + rnd.nextDouble() * 0.5;
            p.vida = p.vidaMax;
            p.raio = 2 + rnd.nextDouble() * 3;
            p.cor = cor;
            lista.add( p );
        }
    }
    
    public void atualizar( double delta ) {
        for ( Iterator<P> it = lista.iterator(); it.hasNext(); ) {
            P p = it.next();
            p.vida -= delta;
            if ( p.vida <= 0 ) {
                it.remove();
                continue;
            }
            p.vy += 260 * delta;
            p.vx *= Math.exp( -delta * 1.5 );
            p.x += p.vx * delta;
            p.y += p.vy * delta;
        }
    }
    
    public void desenhar( EngineFrame e ) {
        for ( P p : lista ) {
            double a = p.vida / p.vidaMax;
            e.fillCircle( p.x, p.y, p.raio * ( 0.4 + 0.6 * a ), Tema.alfa( p.cor, a ) );
        }
    }
    
}
