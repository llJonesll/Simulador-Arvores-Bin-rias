package simulador.modelo;

import java.util.ArrayList;
import java.util.List;
import simulador.modelo.NoVisual.Cor;
import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo.Tom;

/**
 * Parte da árvore responsável por gravar os passos: cada chamada a
 * {@link #registrar} tira uma "foto" completa da árvore (posição de cada nó
 * no layout, cor, destaques, etiquetas) junto com o texto explicativo.
 * 
 * O layout é o clássico: x = ranque do nó em ordem, y = nível.
 * 
 * @author Prof. Dr. David Buzatto
 */
public abstract class Gravador {
    
    protected No raiz;
    protected List<Passo> passos;
    
    //**************************************************************************
    // ganchos de aparência (AVL e Vermelho-Preto sobrescrevem)
    //**************************************************************************
    
    protected Cor cor( No n ) {
        return Cor.NEUTRO;
    }
    
    protected String selo( No n ) {
        return null;
    }
    
    protected boolean seloAlerta( No n ) {
        return false;
    }
    
    //**************************************************************************
    // estado atual
    //**************************************************************************
    
    /** Foto da árvore como está agora (sem operação em andamento). */
    public Passo estadoAtual( Tom tom, String titulo, String texto ) {
        passos = new ArrayList<>();
        registrar( tom, titulo, texto, m() );
        Passo p = passos.get( 0 );
        passos = null;
        return p;
    }
    
    protected Marcas m() {
        return new Marcas();
    }
    
    protected Passo registrar( Tom tom, String titulo, String texto, Marcas m ) {
        
        List<NoVisual> lista = new ArrayList<>();
        int[] ranque = { 0 };
        int[] prof = { 0 };
        montar( raiz, null, 0, ranque, prof, lista, m );
        
        if ( m.flut != null ) {
            NoVisual v = visual( m.flut, m );
            v.flutuante = true;
            v.alvoId = m.alvo == null ? -1 : m.alvo.id;
            v.lado = m.lado;
            v.destaque = Destaque.NOVO;
            lista.add( v );
        }
        
        Passo p = new Passo( tom, titulo, texto, lista, ranque[0], prof[0] );
        p.saida = m.saida;
        passos.add( p );
        return p;
        
    }
    
    private void montar( No n, No pai, int nivel, int[] ranque, int[] prof, List<NoVisual> out, Marcas m ) {
        if ( n == null ) {
            return;
        }
        montar( n.esq, n, nivel + 1, ranque, prof, out, m );
        NoVisual v = visual( n, m );
        v.paiId = pai == null ? -1 : pai.id;
        v.ehEsq = pai != null && pai.esq == n;
        v.nivel = nivel;
        v.ranque = ranque[0]++;
        v.arestaDestacada = pai != null && m.caminho.contains( n ) && m.caminho.contains( pai );
        prof[0] = Math.max( prof[0], nivel + 1 );
        out.add( v );
        montar( n.dir, n, nivel + 1, ranque, prof, out, m );
    }
    
    private NoVisual visual( No n, Marcas m ) {
        NoVisual v = new NoVisual();
        v.id = n.id;
        v.chave = n.chave;
        v.cor = cor( n );
        v.destaque = m.dest.getOrDefault( n, Destaque.NENHUM );
        v.papel = m.papeis.get( n );
        v.selo = selo( n );
        v.seloAlerta = seloAlerta( n );
        v.duploPreto = m.duplos.contains( n );
        return v;
    }
    
}
