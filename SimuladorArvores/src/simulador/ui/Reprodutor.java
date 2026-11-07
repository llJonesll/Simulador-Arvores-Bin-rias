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
    
    private static final double[] VELOCIDADES = { 0.5, 1, 1.5, 2, 3 };
    
    private final Visualizador viz;
    private List<Passo> passos = new ArrayList<>();
    private int indice;
    private boolean tocando;
    private boolean automatico = true;
    private int velocidade = 1;
    private double timer;
    
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
    public String velocidadeTexto() { return VELOCIDADES[velocidade] + "x"; }
    
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
    
    public void proximo() {
        tocando = false;
        ir( indice + 1 );
    }
    
    /** Salta para um passo (usado ao clicar na linha do tempo). */
    public void saltarPara( int novo ) {
        tocando = false;
        ir( novo );
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
    
    public void maisRapido() {
        velocidade = Math.min( VELOCIDADES.length - 1, velocidade + 1 );
    }
    
    public void maisLento() {
        velocidade = Math.max( 0, velocidade - 1 );
    }
    
    public void atualizar( double delta ) {
        
        if ( !tocando || passos.isEmpty() ) {
            return;
        }
        
        // passos com texto longo ficam um pouco mais na tela
        double intervalo = 0.7 + 0.008 * passos.get( indice ).texto.length();
        timer += delta * VELOCIDADES[velocidade];
        
        if ( timer >= intervalo ) {
            ir( indice + 1 );
            if ( noFim() ) {
                tocando = false;
            }
        }
        
    }
    
}
