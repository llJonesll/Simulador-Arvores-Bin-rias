package simulador.ui;

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
        float x, y, vx, vy, vida, vidaMax, raio;
        int cor;
    }
    
    private final List<P> lista = new ArrayList<>();
    private final Random rnd = new Random();
    
    public void explodir( float x, float y, int cor, int qtd, float forca ) {
        for ( int i = 0; i < qtd; i++ ) {
            P p = new P();
            double ang = rnd.nextDouble() * Math.PI * 2;
            float vel = forca * ( 0.35f + rnd.nextFloat() * 0.65f );
            p.x = x;
            p.y = y;
            p.vx = (float) Math.cos( ang ) * vel;
            p.vy = (float) Math.sin( ang ) * vel - forca * 0.15f;
            p.vidaMax = 0.5f + rnd.nextFloat() * 0.5f;
            p.vida = p.vidaMax;
            p.raio = 1.5f + rnd.nextFloat() * 2.5f;
            p.cor = cor;
            lista.add( p );
        }
    }
    
    public void atualizar( float dt ) {
        for ( Iterator<P> it = lista.iterator(); it.hasNext(); ) {
            P p = it.next();
            p.vida -= dt;
            if ( p.vida <= 0 ) {
                it.remove();
                continue;
            }
            p.vy += 200 * dt;
            p.vx *= (float) Math.exp( -dt * 1.5 );
            p.x += p.vx * dt;
            p.y += p.vy * dt;
        }
    }
    
    public void desenhar( Pincel g ) {
        for ( P p : lista ) {
            float a = p.vida / p.vidaMax;
            g.circulo( p.x, p.y, p.raio * ( 0.4f + 0.6f * a ), Tema.alfa( p.cor, a ) );
        }
    }
    
}
