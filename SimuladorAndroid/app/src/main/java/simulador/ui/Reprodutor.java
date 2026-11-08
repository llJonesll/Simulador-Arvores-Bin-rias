package simulador.ui;

import java.util.ArrayList;
import java.util.List;
import simulador.modelo.Passo;

/**
 * Controla a reprodução dos passos de uma operação: automática (com
 * velocidade ajustável) ou manual (passo a passo), com avanço e retrocesso.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Reprodutor {
    
    private static final float[] VELOCIDADES = { 0.5f, 1f, 1.5f, 2f, 3f };
    
    private final Visualizador viz;
    private List<Passo> passos = new ArrayList<>();
    private int indice;
    private boolean tocando;
    private boolean automatico = true;
    private int velocidade = 1;
    private float timer;
    
    public Reprodutor( Visualizador viz ) {
        this.viz = viz;
    }
    
    //**************************************************************************
    // consultas
    //**************************************************************************
    
    public List<Passo> passos() { return passos; }
    public int indice() { return indice; }
    public int total() { return passos.size(); }
    public boolean tocando() { return tocando; }
    public boolean automatico() { return automatico; }
    public boolean noInicio() { return indice <= 0; }
    public boolean noFim() { return indice >= passos.size() - 1; }
    
    public String velocidadeTexto() {
        float v = VELOCIDADES[velocidade];
        return ( v == (int) v ? String.valueOf( (int) v ) : String.valueOf( v ) ) + "x";
    }
    
    public Passo atual() {
        return passos.isEmpty() ? null : passos.get( indice );
    }
    
    //**************************************************************************
    // comandos
    //**************************************************************************
    
    /** Começa a reproduzir uma nova sequência de passos. */
    public void iniciar( List<Passo> novos ) {
        passos = novos;
        indice = 0;
        timer = 0;
        viz.mostrar( passos.get( 0 ) );
        tocando = automatico && passos.size() > 1;
    }
    
    /** Pula para o último passo da sequência atual (estado final da operação). */
    public void finalizar() {
        if ( !passos.isEmpty() ) {
            ir( passos.size() - 1 );
        }
        tocando = false;
    }
    
    public void ir( int novo ) {
        novo = Math.max( 0, Math.min( passos.size() - 1, novo ) );
        timer = 0;
        if ( novo != indice ) {
            indice = novo;
            viz.mostrar( passos.get( indice ) );
        }
    }
    
    /** Salta para um passo (usado ao arrastar na linha do tempo). */
    public void saltarPara( int novo ) {
        tocando = false;
        ir( novo );
    }
    
    public void proximo() {
        tocando = false;
        ir( indice + 1 );
    }
    
    public void anterior() {
        tocando = false;
        ir( indice - 1 );
    }
    
    public void inicio() {
        tocando = false;
        ir( 0 );
    }
    
    public void fim() {
        tocando = false;
        ir( passos.size() - 1 );
    }
    
    public void alternarPlay() {
        if ( passos.size() < 2 ) {
            return;
        }
        if ( tocando ) {
            tocando = false;
        } else {
            if ( noFim() ) {
                ir( 0 );
            }
            tocando = true;
        }
    }
    
    public void alternarModo() {
        automatico = !automatico;
        if ( !automatico ) {
            tocando = false;
        }
    }
    
    /** Passa para a próxima velocidade (volta à mais lenta depois da mais rápida). */
    public void ciclarVelocidade() {
        velocidade = ( velocidade + 1 ) % VELOCIDADES.length;
    }
    
    public void atualizar( float dt ) {
        
        if ( !tocando || passos.isEmpty() ) {
            return;
        }
        
        // passos com texto longo ficam um pouco mais na tela
        float intervalo = 0.7f + 0.008f * passos.get( indice ).texto.length();
        timer += dt * VELOCIDADES[velocidade];
        
        if ( timer >= intervalo ) {
            ir( indice + 1 );
            if ( noFim() ) {
                tocando = false;
            }
        }
        
    }
    
}
