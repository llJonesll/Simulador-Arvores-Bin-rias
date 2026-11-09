package simulador.modelo;

import simulador.modelo.NoVisual.Cor;
import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo.Tom;

/**
 * Árvore LLRB (Left-Leaning Red-Black Tree - Vermelho-Preto Caída à Esquerda). 
 * Regras adicionais à RB clássica:
 * 1) Ligações vermelhas são sempre à esquerda (nenhum nó tem filho direito vermelho e esquerdo preto).
 * 2) Não há dois links vermelhos seguidos à esquerda.
 * 
 * @author Prof. Dr. David Buzatto (adaptado para LLRB)
 */
public class ArvoreRN extends Arvore {

    @Override
    public String nome() {
        return "Árvore LLRB (Caída à Esquerda)";
    }
    
    @Override
    protected No criarNo( int chave ) {
        No n = new No( chave );
        n.vermelho = true; // todo nó novo nasce vermelho
        return n;
    }
    
    @Override
    protected Cor cor( No n ) {
        return n.vermelho ? Cor.VERMELHO : Cor.PRETO;
    }
    
    private boolean preto( No n ) {
        return n == null || !n.vermelho;
    }
    
    private boolean vermelho( No n ) {
        return n != null && n.vermelho;
    }
    
    private String nomeCor( No n ) {
        return preto( n ) ? "preto" : "vermelho";
    }
    
    @Override
    protected String posInsercao() {
        return " Todo nó novo nasce vermelho; agora subimos na árvore aplicando as 3 regras LLRB (Rotação à Esquerda, Rotação à Direita e Inversão de Cores).";
    }
    
    //**************************************************************************
    // inserção
    //**************************************************************************
    
    @Override
    protected void aposInserir( No z ) {
        if ( z == raiz ) {
            z.vermelho = false;
            registrar( Tom.SUCESSO, "A raiz é sempre preta", 
                    "Regra: a raiz é preta. Então " + z.chave + " é pintado de preto e terminamos.", 
                    m().d( z, Destaque.NOVO ) );
            return;
        }
        
        balancearLLRB( z.pai );
    }

    /**
     * Aplica as operações fundamentais da LLRB de forma iterativa subindo para a raiz.
     */
    private void balancearLLRB(No inicio) {
        No atual = inicio;
        
        while ( atual != null ) {
            
            // Regra 1: Filho direito vermelho e esquerdo preto -> Rotação à esquerda
            if ( vermelho( atual.dir ) && !vermelho( atual.esq ) ) {
                registrar( Tom.ALERTA, "Link vermelho à direita", 
                        "Nó " + atual.chave + " possui link vermelho à direita (" + atual.dir.chave + "). " 
                                + "Na LLRB, links vermelhos devem inclinar para a esquerda.", 
                        m().d( atual, Destaque.PROBLEMA ).d( atual.dir, Destaque.PROBLEMA ) );
                
                No x = atual.dir;
                rotacionar( false, atual, "Rotação à esquerda em " + atual.chave );
                x.vermelho = atual.vermelho;
                atual.vermelho = true;
                atual = x; // Atualiza para a nova raiz da subárvore e continua as regras nela
            }
            
            // Regra 2: Dois links vermelhos seguidos à esquerda -> Rotação à direita
            if ( vermelho( atual.esq ) && vermelho( atual.esq.esq ) ) {
                registrar( Tom.ALERTA, "Dois vermelhos à esquerda", 
                        "Nó " + atual.chave + " possui filho esquerdo e neto esquerdo vermelhos. " 
                                + "Rotacionamos à direita para balancear.", 
                        m().d( atual, Destaque.PROBLEMA ).d( atual.esq, Destaque.PROBLEMA ).d( atual.esq.esq, Destaque.PROBLEMA ) );
                
                No x = atual.esq;
                rotacionar( true, atual, "Rotação à direita em " + atual.chave );
                x.vermelho = atual.vermelho;
                atual.vermelho = true;
                atual = x;
            }
            
            // Regra 3: Ambos os filhos vermelhos (simula 4-nó) -> Flip Colors
            if ( vermelho( atual.esq ) && vermelho( atual.dir ) ) {
                registrar( Tom.INFO, "Inversão de cores (Flip Colors)", 
                        "Nó " + atual.chave + " tem ambos os filhos vermelhos. " 
                                + "Invertemos a cor do pai para vermelho e dos filhos para preto.", 
                        m().d( atual, Destaque.PIVO ).d( atual.esq, Destaque.PIVO ).d( atual.dir, Destaque.PIVO ) );
                
                atual.vermelho = !atual.vermelho;
                atual.esq.vermelho = !atual.esq.vermelho;
                atual.dir.vermelho = !atual.dir.vermelho;
            }
            
            atual = atual.pai; // Sobe para o pai da subárvore recém-ajustada
        }
        
        if ( raiz != null && raiz.vermelho ) {
            raiz.vermelho = false;
            registrar( Tom.SUCESSO, "Raiz volta a ser preta", 
                    "A recoloração chegou à raiz " + raiz.chave + ". Pela regra ela é pintada de preto. Árvore válida!", m() );
        } else {
            registrar( Tom.SUCESSO, "Regras LLRB aplicadas", 
                    "Nenhum nó tem vermelho à direita e não há vermelhos consecutivos. Árvore válida!", m() );
        }
    }
    
    //**************************************************************************
    // remoção
    //**************************************************************************
    
    @Override
    protected void removerNo( No z, No filho ) {
        
        No pai = z.pai;
        
        if ( filho != null ) {
            transplantar( z, filho );
            tamanho--;
            registrar( Tom.INFO, "Nó removido", 
                    "O nó foi desligado e o filho " + filho.chave + " ocupou seu lugar.", 
                    m().d( filho, Destaque.NOVO ) );
            if ( !z.vermelho ) {
                corrigirRemocao( filho );
            }
        } else if ( pai == null ) {
            raiz = null;
            tamanho--;
            registrar( Tom.SUCESSO, "Árvore vazia", "Era o único nó: a árvore ficou vazia.", m() );
        } else if ( z.vermelho ) {
            transplantar( z, null );
            tamanho--;
            registrar( Tom.SUCESSO, "Nó vermelho removido", 
                    "Remover nó vermelho não muda a altura negra.", m() );
        } else {
            registrar( Tom.ALERTA, "Remover um nó preto", 
                    "Tratamos " + z.chave + " como \"duplo-preto\" e corrigimos as cores.", 
                    m().d( z, Destaque.PROBLEMA ).duplo( z ).p( z, "x" ) );
            
            corrigirRemocao( z );
            
            transplantar( z, null );
            tamanho--;
            registrar( Tom.SUCESSO, "Nó removido", 
                    "Com o \"preto extra\" resolvido, o nó é desligado da árvore.", m() );
        }
        
        // Na LLRB, operações clássicas de remoção RB podem deixar links vermelhos pendendo 
        // para a direita. Subimos validando as 3 regras LLRB para consertar a estrutura.
        if (raiz != null) {
            No inicioBalanceamento = (filho != null) ? filho : pai;
            if (inicioBalanceamento != null) {
                balancearLLRB(inicioBalanceamento);
            }
        }
    }
    
    /** Mantido igual à RB Clássica: Resolve o "duplo-preto" reparando a altura negra */
    private void corrigirRemocao( No x ) {
        while ( x != raiz && preto( x ) ) {
            No pai = x.pai;
            boolean xEsq = x == pai.esq;
            No irmao = xEsq ? pai.dir : pai.esq;
            
            registrar( Tom.ALERTA, "x é duplo-preto", 
                    "x (" + x.chave + ") carrega um \"preto extra\".", base( x, pai, irmao ) );
            
            if ( !preto( irmao ) ) {
                irmao.vermelho = false;
                pai.vermelho = true;
                registrar( Tom.INFO, "Caso 1: irmão vermelho", "Rotaciona pai e troca cores.", base( x, pai, irmao ) );
                rotacionar( !xEsq, pai, "Rotação à " + ( xEsq ? "esquerda" : "direita" ) + " em " + pai.chave, m().duplo( x ).p( x, "x" ).p( pai, "pai" ) );
                irmao = xEsq ? pai.dir : pai.esq;
            }
            
            No perto = xEsq ? irmao.esq : irmao.dir;
            No longe = xEsq ? irmao.dir : irmao.esq;
            
            if ( preto( perto ) && preto( longe ) ) {
                irmao.vermelho = true;
                registrar( Tom.INFO, "Caso 2: irmão e sobrinhos pretos", "O preto extra sobe para o pai.", m().duplo( x ).p( x, "x" ).d( irmao, Destaque.PIVO ).p( irmao, "irmão" ).p( pai, "pai" ) );
                x = pai;
            } else {
                if ( preto( longe ) ) {
                    perto.vermelho = false;
                    irmao.vermelho = true;
                    registrar( Tom.INFO, "Caso 3: sobrinho de fora preto", "Rotaciona irmão.", base( x, pai, irmao ).d( perto, Destaque.PIVO ).p( perto, "sobrinho" ) );
                    rotacionar( xEsq, irmao, "Rotação à " + ( xEsq ? "direita" : "esquerda" ) + " em " + irmao.chave, m().duplo( x ).p( x, "x" ).p( pai, "pai" ) );
                    irmao = xEsq ? pai.dir : pai.esq;
                    longe = xEsq ? irmao.dir : irmao.esq;
                }
                irmao.vermelho = pai.vermelho;
                pai.vermelho = false;
                longe.vermelho = false;
                registrar( Tom.INFO, "Caso 4: sobrinho de fora vermelho", "Rotaciona pai em direção a x absorvendo o preto extra.", base( x, pai, irmao ).d( longe, Destaque.PIVO ).p( longe, "sobrinho" ) );
                rotacionar( !xEsq, pai, "Rotação à " + ( xEsq ? "esquerda" : "direita" ) + " em " + pai.chave, m().duplo( x ).p( x, "x" ) );
                x = raiz;
            }
        }
        if ( x.vermelho ) {
            x.vermelho = false;
            registrar( Tom.INFO, "Pintando " + x.chave + " de preto", x.chave + " absorve o preto extra.", m().d( x, Destaque.NOVO ) );
        }
    }
    
    private Marcas base( No x, No pai, No irmao ) {
        return m().d( x, Destaque.PROBLEMA ).duplo( x ).p( x, "x" ).d( irmao, Destaque.COMPARANDO ).p( irmao, "irmão" ).p( pai, "pai" );
    }
    
    //**************************************************************************
    // validação
    //**************************************************************************
    
    @Override
    public String validar() {
        String e = super.validar();
        if ( e != null ) return e;
        if ( raiz != null && raiz.vermelho ) return "raiz vermelha";
        
        String erroLLRB = validarLLRB(raiz);
        if ( erroLLRB != null ) return erroLLRB;

        return alturaNegra( raiz ) < 0 ? "regras de cor violadas" : null;
    }
    
    private String validarLLRB(No n) {
        if (n == null) return null;
        if (vermelho(n.dir) && !vermelho(n.esq)) return "nó vermelho à direita não permitido na LLRB (" + n.dir.chave + ")";
        if (vermelho(n.esq) && vermelho(n.esq.esq)) return "dois links vermelhos consecutivos à esquerda";
        
        String erroEsq = validarLLRB(n.esq);
        if (erroEsq != null) return erroEsq;
        return validarLLRB(n.dir);
    }
    
    private int alturaNegra( No n ) {
        if ( n == null ) return 1;
        if ( n.vermelho && ( !preto( n.esq ) || !preto( n.dir ) ) ) return -1;
        int e = alturaNegra( n.esq );
        int d = alturaNegra( n.dir );
        if ( e < 0 || d < 0 || e != d ) return -1;
        return e + ( n.vermelho ? 0 : 1 );
    }
}