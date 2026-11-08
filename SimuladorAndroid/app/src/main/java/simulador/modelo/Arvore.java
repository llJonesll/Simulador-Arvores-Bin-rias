package simulador.modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo.Tom;

/**
 * Base das três árvores. Cada operação (inserir, buscar, remover, percorrer)
 * executa o algoritmo de verdade sobre a estrutura e, a cada etapa relevante,
 * grava um {@link Passo} (uma "foto" da árvore + explicação). A interface
 * apenas reproduz esses passos, animando as diferenças entre eles.
 * 
 * Os algoritmos comuns (busca, inserção e remoção de ABB, rotações e
 * percursos) ficam aqui; AVL e Vermelho-Preto sobrescrevem os "ganchos".
 * 
 * @author Prof. Dr. David Buzatto
 */
public abstract class Arvore extends Gravador {
    
    public static final int MAX_NOS = 63;
    
    protected int tamanho;
    
    //**************************************************************************
    // informações
    //**************************************************************************
    
    public abstract String nome();
    
    public int tamanho() {
        return tamanho;
    }
    
    public boolean contem( int chave ) {
        No n = raiz;
        while ( n != null && n.chave != chave ) {
            n = chave < n.chave ? n.esq : n.dir;
        }
        return n != null;
    }
    
    public int altura() {
        return alturaReal( raiz );
    }
    
    private int alturaReal( No n ) {
        return n == null ? 0 : 1 + Math.max( alturaReal( n.esq ), alturaReal( n.dir ) );
    }
    
    public void limpar() {
        raiz = null;
        tamanho = 0;
    }
    
    /** Monta a árvore inserindo as chaves sem guardar os passos. */
    public void carregar( int... chaves ) {
        for ( int c : chaves ) {
            inserir( c );
        }
        passos = null;
    }
    
    /** @return null se a estrutura é válida; senão, a descrição do problema. */
    public String validar() {
        return validarBST( raiz, null, Integer.MIN_VALUE, Integer.MAX_VALUE );
    }
    
    private String validarBST( No n, No pai, int min, int max ) {
        if ( n == null ) {
            return null;
        }
        if ( n.pai != pai ) {
            return "ponteiro pai errado em " + n.chave;
        }
        if ( n.chave < min || n.chave > max ) {
            return "ordem BST violada em " + n.chave;
        }
        String e = validarBST( n.esq, n, min, n.chave - 1 );
        return e != null ? e : validarBST( n.dir, n, n.chave + 1, max );
    }
    
    //**************************************************************************
    // ganchos (AVL e Vermelho-Preto sobrescrevem)
    //**************************************************************************
    
    protected No criarNo( int chave ) {
        return new No( chave );
    }
    
    /** Complemento do texto do passo "inserido". */
    protected String posInsercao() {
        return "";
    }
    
    /** Complemento do texto do passo "removido". */
    protected String posRemocao() {
        return "";
    }
    
    /** Chamado depois que o nó novo foi ligado à árvore. */
    protected void aposInserir( No novo ) {
    }
    
    /** Chamado depois que um nó foi desligado (recebe o pai dele). */
    protected void aposRemover( No pai ) {
    }
    
    /** Chamado depois de uma rotação: {@code t} desceu, {@code f} subiu. */
    protected void aposRotacao( No t, No f ) {
    }
    
    //**************************************************************************
    // operações
    //**************************************************************************
    
    public List<Passo> inserir( int chave ) {
        
        passos = new ArrayList<>();
        
        if ( tamanho >= MAX_NOS ) {
            registrar( Tom.ERRO, "Limite atingido", 
                    "O simulador aceita no máximo " + MAX_NOS + " nós para manter o desenho legível.", m() );
            return passos;
        }
        
        No novo = criarNo( chave );
        
        registrar( Tom.INFO, "Novo nó", 
                "Vamos inserir " + chave + ". Ele começa fora da árvore e será comparado, a partir da raiz, até achar seu lugar.", 
                m().flutuante( novo, null, 0 ) );
        
        if ( raiz == null ) {
            raiz = novo;
            tamanho++;
            registrar( Tom.SUCESSO, "Árvore vazia", 
                    "Como a árvore está vazia, " + chave + " vira a raiz.", m().d( novo, Destaque.NOVO ) );
            aposInserir( novo );
            return passos;
        }
        
        No atual = raiz;
        List<No> caminho = new ArrayList<>();
        
        while ( true ) {
            
            caminho.add( atual );
            
            if ( chave == atual.chave ) {
                registrar( Tom.ALERTA, "Chave repetida", 
                        chave + " == " + atual.chave + ". A chave já existe e estas árvores não aceitam duplicatas, então nada é inserido.", 
                        m().caminho( caminho ).d( atual, Destaque.ENCONTRADO ) );
                return passos;
            }
            
            boolean esq = chave < atual.chave;
            registrar( Tom.INFO, "Comparando com " + atual.chave, 
                    chave + ( esq ? " < " : " > " ) + atual.chave + ": " + ( esq ? "menores ficam à esquerda" : "maiores ficam à direita" ) 
                            + ", então descemos para a " + ( esq ? "esquerda" : "direita" ) + ".", 
                    m().caminho( caminho ).d( atual, Destaque.COMPARANDO ).flutuante( novo, atual, esq ? -1 : 1 ) );
            
            No prox = esq ? atual.esq : atual.dir;
            
            if ( prox == null ) {
                if ( esq ) {
                    atual.esq = novo;
                } else {
                    atual.dir = novo;
                }
                novo.pai = atual;
                tamanho++;
                registrar( Tom.SUCESSO, "Nó inserido", 
                        "Achamos um espaço vazio (null): " + chave + " vira filho " + ( esq ? "esquerdo" : "direito" ) 
                                + " de " + atual.chave + "." + posInsercao(), 
                        m().caminho( caminho ).d( novo, Destaque.NOVO ) );
                break;
            }
            
            atual = prox;
            
        }
        
        aposInserir( novo );
        return passos;
        
    }
    
    public List<Passo> buscar( int chave ) {
        
        passos = new ArrayList<>();
        
        registrar( Tom.INFO, "Busca", "Procurando " + chave + ", começando pela raiz.", m() );
        
        if ( raiz == null ) {
            registrar( Tom.ERRO, "Árvore vazia", "Não há nenhum nó: " + chave + " não está na árvore.", m() );
            return passos;
        }
        
        No atual = raiz;
        List<No> caminho = new ArrayList<>();
        
        while ( atual != null ) {
            
            caminho.add( atual );
            
            if ( chave == atual.chave ) {
                registrar( Tom.SUCESSO, "Encontrado!", 
                        chave + " == " + atual.chave + ". Achamos a chave depois de " + caminho.size() 
                                + ( caminho.size() == 1 ? " comparação." : " comparações." ), 
                        m().caminho( caminho ).d( atual, Destaque.ENCONTRADO ) );
                return passos;
            }
            
            boolean esq = chave < atual.chave;
            registrar( Tom.INFO, "Comparando com " + atual.chave, 
                    chave + ( esq ? " < " : " > " ) + atual.chave + ", então a busca continua pela " + ( esq ? "esquerda" : "direita" ) + ".", 
                    m().caminho( caminho ).d( atual, Destaque.COMPARANDO ) );
            
            atual = esq ? atual.esq : atual.dir;
            
        }
        
        registrar( Tom.ERRO, "Não encontrado", 
                "Chegamos a um espaço vazio (null) depois de " + caminho.size() + " comparações: " + chave + " não está na árvore.", 
                m().caminho( caminho ) );
        return passos;
        
    }
    
    public List<Passo> remover( int chave ) {
        
        passos = new ArrayList<>();
        
        registrar( Tom.INFO, "Remoção", "Primeiro localizamos o nó " + chave + ", como em uma busca.", m() );
        
        No alvo = raiz;
        List<No> caminho = new ArrayList<>();
        
        while ( alvo != null && alvo.chave != chave ) {
            caminho.add( alvo );
            boolean esq = chave < alvo.chave;
            registrar( Tom.INFO, "Comparando com " + alvo.chave, 
                    chave + ( esq ? " < " : " > " ) + alvo.chave + ": descemos para a " + ( esq ? "esquerda" : "direita" ) + ".", 
                    m().caminho( caminho ).d( alvo, Destaque.COMPARANDO ) );
            alvo = esq ? alvo.esq : alvo.dir;
        }
        
        if ( alvo == null ) {
            registrar( Tom.ERRO, "Não encontrado", 
                    "Chegamos a um espaço vazio (null): " + chave + " não está na árvore, então não há o que remover.", 
                    m().caminho( caminho ) );
            return passos;
        }
        
        caminho.add( alvo );
        registrar( Tom.SUCESSO, "Nó encontrado", 
                "Achamos " + chave + ". O que fazer agora depende de quantos filhos ele tem.", 
                m().caminho( caminho ).d( alvo, Destaque.ENCONTRADO ) );
        
        No remover = alvo;
        
        if ( alvo.esq != null && alvo.dir != null ) {
            
            registrar( Tom.INFO, "Dois filhos", 
                    chave + " tem dois filhos. Vamos trocá-lo pelo sucessor: o menor nó da subárvore direita (o próximo na ordem crescente).", 
                    m().d( alvo, Destaque.ENCONTRADO ) );
            
            No s = alvo.dir;
            registrar( Tom.INFO, "Procurando o sucessor", 
                    "Vamos uma vez para a direita e depois sempre para a esquerda, até não haver mais filho esquerdo.", 
                    m().d( alvo, Destaque.ENCONTRADO ).d( s, Destaque.COMPARANDO ) );
            
            while ( s.esq != null ) {
                s = s.esq;
                registrar( Tom.INFO, "Descendo à esquerda", 
                        "Ainda há filho esquerdo, então continuamos descendo.", 
                        m().d( alvo, Destaque.ENCONTRADO ).d( s, Destaque.COMPARANDO ) );
            }
            
            registrar( Tom.SUCESSO, "Sucessor: " + s.chave, 
                    s.chave + " não tem filho esquerdo, logo é o menor da subárvore direita.", 
                    m().d( alvo, Destaque.ENCONTRADO ).d( s, Destaque.PIVO ) );
            
            int antiga = alvo.chave;
            alvo.chave = s.chave;
            registrar( Tom.INFO, "Copiando o sucessor", 
                    "A chave " + s.chave + " é copiada para o nó que guardava " + antiga 
                            + ". Falta só remover o nó original do sucessor, que tem no máximo um filho.", 
                    m().d( alvo, Destaque.NOVO ).d( s, Destaque.REMOVER ) );
            
            remover = s;
            
        } else {
            
            boolean folha = alvo.esq == null && alvo.dir == null;
            registrar( Tom.INFO, folha ? "Nó folha" : "Um filho", 
                    folha ? chave + " não tem filhos: basta desligá-lo do pai." 
                            : chave + " tem um só filho: ele vai ocupar o lugar do pai.", 
                    m().d( alvo, Destaque.REMOVER ) );
            
        }
        
        removerNo( remover, remover.esq != null ? remover.esq : remover.dir );
        return passos;
        
    }
    
    /** Remove {@code z}, que tem no máximo um filho (o {@code filho}). */
    protected void removerNo( No z, No filho ) {
        No pai = z.pai;
        transplantar( z, filho );
        tamanho--;
        registrar( Tom.SUCESSO, "Nó removido", 
                "O nó foi desligado da árvore." + ( filho != null ? " O filho " + filho.chave + " ocupa o lugar dele." : "" ) + posRemocao(), 
                m().d( filho, Destaque.NOVO ) );
        aposRemover( pai );
    }
    
    /** Coloca {@code filho} no lugar de {@code z} (que sai da árvore). */
    protected void transplantar( No z, No filho ) {
        No pai = z.pai;
        if ( pai == null ) {
            raiz = filho;
        } else if ( pai.esq == z ) {
            pai.esq = filho;
        } else {
            pai.dir = filho;
        }
        if ( filho != null ) {
            filho.pai = pai;
        }
    }
    
    //**************************************************************************
    // rotações
    //**************************************************************************
    
    /**
     * Rotaciona em torno de {@code t}: o filho do lado oposto sobe e {@code t} desce.
     * Grava o passo explicando o que aconteceu e devolve o nó que subiu.
     */
    protected No rotacionar( boolean direita, No t, String titulo ) {
        return rotacionar( direita, t, titulo, m() );
    }
    
    /** Igual à outra versão, mas aproveita marcações extras (papéis, duplo-preto...) no passo gravado. */
    protected No rotacionar( boolean direita, No t, String titulo, Marcas extra ) {
        
        No f = direita ? t.esq : t.dir;
        No neto = direita ? f.dir : f.esq;
        
        if ( direita ) {
            t.esq = neto;
            f.dir = t;
        } else {
            t.dir = neto;
            f.esq = t;
        }
        
        if ( neto != null ) {
            neto.pai = t;
        }
        
        f.pai = t.pai;
        if ( t.pai == null ) {
            raiz = f;
        } else if ( t.pai.esq == t ) {
            t.pai.esq = f;
        } else {
            t.pai.dir = f;
        }
        t.pai = f;
        
        aposRotacao( t, f );
        
        String lado = direita ? "direita" : "esquerda";
        String oposto = direita ? "esquerda" : "direita";
        registrar( Tom.INFO, titulo, 
                f.chave + " sobe e ocupa o lugar de " + t.chave + "; " + t.chave + " desce para a " + lado + " de " + f.chave + "." 
                        + ( neto != null ? " A subárvore de " + neto.chave + " troca de pai e passa a ser filha " + oposto + " de " + t.chave + "." : "" ) 
                        + " A ordem das chaves continua válida.", 
                extra.d( f, Destaque.PIVO ).d( t, Destaque.PIVO ) );
        
        return f;
        
    }
    
    //**************************************************************************
    // percursos
    //**************************************************************************
    
    public List<Passo> percorrer( Percurso tipo ) {
        
        passos = new ArrayList<>();
        
        if ( raiz == null ) {
            registrar( Tom.ERRO, "Árvore vazia", "Não há nós para percorrer.", m() );
            return passos;
        }
        
        List<No> ordem = tipo.ordem( raiz );
        registrar( Tom.INFO, "Percurso " + tipo.rotulo.toLowerCase(), tipo.regra, m() );
        
        Set<No> visitados = new HashSet<>();
        StringBuilder saida = new StringBuilder();
        
        for ( No n : ordem ) {
            visitados.add( n );
            if ( saida.length() > 0 ) {
                saida.append( ", " );
            }
            saida.append( n.chave );
            Marcas m = m();
            for ( No v : visitados ) {
                m.d( v, Destaque.VISITADO );
            }
            m.d( n, Destaque.COMPARANDO );
            m.saida = saida.toString();
            registrar( Tom.INFO, "Visitando " + n.chave, "Visita " + n.chave + " e acrescenta à saída.", m );
        }
        
        Marcas fim = m();
        for ( No v : visitados ) {
            fim.d( v, Destaque.VISITADO );
        }
        fim.saida = saida.toString();
        registrar( Tom.SUCESSO, "Percurso concluído", "Todos os " + ordem.size() + " nós foram visitados.", fim );
        
        return passos;
        
    }
    
}
