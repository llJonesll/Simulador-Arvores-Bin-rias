package simulador.modelo;

import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo.Tom;

/**
 * Árvore AVL: a ABB mais o fator de balanceamento
 * (fb = altura da esquerda - altura da direita) mantido entre -1 e +1
 * por rotações simples e duplas.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class AVL extends Arvore {

    @Override
    public String nome() {
        return "Árvore AVL";
    }
    
    //**************************************************************************
    // alturas e fator de balanceamento
    //**************************************************************************
    
    private int h( No n ) {
        return n == null ? 0 : n.altura;
    }
    
    private int fb( No n ) {
        return h( n.esq ) - h( n.dir );
    }
    
    private void atualizarAltura( No n ) {
        n.altura = 1 + Math.max( h( n.esq ), h( n.dir ) );
    }
    
    private String sinal( int fb ) {
        return fb > 0 ? "+" + fb : String.valueOf( fb );
    }
    
    @Override
    protected String selo( No n ) {
        return "h" + n.altura + "  fb " + sinal( fb( n ) );
    }
    
    @Override
    protected boolean seloAlerta( No n ) {
        return Math.abs( fb( n ) ) > 1;
    }
    
    @Override
    protected String posInsercao() {
        return " Agora subimos até a raiz recalculando alturas e verificando o balanceamento.";
    }
    
    @Override
    protected String posRemocao() {
        return " Agora subimos até a raiz recalculando alturas e verificando o balanceamento.";
    }
    
    @Override
    protected void aposRotacao( No t, No f ) {
        atualizarAltura( t );
        atualizarAltura( f );
    }
    
    @Override
    protected void aposInserir( No novo ) {
        rebalancear( novo.pai );
    }
    
    @Override
    protected void aposRemover( No pai ) {
        rebalancear( pai );
    }
    
    //**************************************************************************
    // rebalanceamento
    //**************************************************************************
    
    /** Sobe de {@code n} até a raiz, atualizando alturas e corrigindo desbalanceamentos. */
    private void rebalancear( No n ) {
        
        while ( n != null ) {
            
            int antiga = n.altura;
            atualizarAltura( n );
            int fb = fb( n );
            
            if ( Math.abs( fb ) <= 1 ) {
                
                boolean mudou = n.altura != antiga;
                
                if ( !mudou ) {
                    registrar( Tom.SUCESSO, "Verificando " + n.chave, 
                            "altura = " + n.altura + " (não mudou) e fb = " + sinal( fb ) 
                                    + ". Como a altura deste nó é a mesma de antes, nenhum ancestral é afetado: a árvore está balanceada!", 
                            m().d( n, Destaque.ENCONTRADO ) );
                    return;
                }
                
                registrar( Tom.INFO, "Verificando " + n.chave, 
                        "Recalculamos: altura = " + n.altura + " e fb = " + sinal( fb ) 
                                + ", dentro do intervalo [-1, +1]. Como a altura mudou, subimos para o pai.", 
                        m().d( n, Destaque.COMPARANDO ) );
                n = n.pai;
                
            } else {
                No topo = corrigir( n, fb );
                n = topo.pai;
            }
            
        }
        
        registrar( Tom.SUCESSO, "Árvore balanceada", 
                "Chegamos à raiz: todos os nós estão com fb entre -1 e +1.", m() );
        
    }
    
    /** Corrige o desbalanceamento em {@code z} e devolve a nova raiz da subárvore. */
    private No corrigir( No z, int fb ) {
        
        boolean esqPesada = fb > 0;
        No y = esqPesada ? z.esq : z.dir;
        int fy = fb( y );
        boolean dupla = esqPesada ? fy < 0 : fy > 0;
        
        String caso = esqPesada 
                ? ( dupla ? "Esquerda-Direita (LR)" : "Esquerda-Esquerda (LL)" ) 
                : ( dupla ? "Direita-Esquerda (RL)" : "Direita-Direita (RR)" );
        
        registrar( Tom.ALERTA, "Desbalanceado em " + z.chave, 
                "fb(" + z.chave + ") = " + sinal( fb ) + ": fora do intervalo [-1, +1]! O lado " + ( esqPesada ? "esquerdo" : "direito" ) 
                        + " está pesado demais. O filho " + y.chave + " tem fb = " + sinal( fy ) + ", então o caso é " + caso + ".", 
                m().d( z, Destaque.PROBLEMA ).p( z, "z" ).d( y, Destaque.PIVO ).p( y, "y" ) );
        
        if ( dupla ) {
            rotacionar( !esqPesada, y, "1ª rotação (" + ( esqPesada ? "esquerda" : "direita" ) + " em " + y.chave + ")" );
            return rotacionar( esqPesada, z, "2ª rotação (" + ( esqPesada ? "direita" : "esquerda" ) + " em " + z.chave + ")" );
        }
        
        return rotacionar( esqPesada, z, "Rotação simples à " + ( esqPesada ? "direita" : "esquerda" ) + " em " + z.chave );
        
    }
    
    @Override
    public String validar() {
        String e = super.validar();
        return e != null ? e : validarAVL( raiz );
    }
    
    private String validarAVL( No n ) {
        if ( n == null ) {
            return null;
        }
        String e = validarAVL( n.esq );
        if ( e != null ) return e;
        e = validarAVL( n.dir );
        if ( e != null ) return e;
        if ( n.altura != 1 + Math.max( h( n.esq ), h( n.dir ) ) ) {
            return "altura errada em " + n.chave;
        }
        if ( Math.abs( fb( n ) ) > 1 ) {
            return "desbalanceado em " + n.chave;
        }
        return null;
    }
    
}
