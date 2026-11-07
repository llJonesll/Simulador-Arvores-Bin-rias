package simulador.modelo;

import simulador.modelo.NoVisual.Cor;
import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo.Tom;

/**
 * Árvore Vermelho-Preto (rubro-negra). Regras:
 * 1) todo nó é vermelho ou preto; 2) a raiz é preta; 3) nós vermelhos não
 * têm filhos vermelhos; 4) todo caminho de um nó até suas folhas nulas tem o
 * mesmo número de nós pretos. Null conta como preto.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class ArvoreRN extends Arvore {

    @Override
    public String nome() {
        return "Árvore Vermelho-Preto";
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
    
    private String nomeCor( No n ) {
        return preto( n ) ? "preto" : "vermelho";
    }
    
    @Override
    protected String posInsercao() {
        return " Todo nó novo nasce vermelho; agora conferimos se alguma regra das cores foi violada.";
    }
    
    //**************************************************************************
    // inserção
    //**************************************************************************
    
    @Override
    protected void aposInserir( No z ) {
        
        if ( z == raiz ) {
            z.vermelho = false;
            registrar( Tom.SUCESSO, "A raiz é sempre preta", 
                    "Regra 2: a raiz é preta. Então " + z.chave + " é pintado de preto e terminamos.", 
                    m().d( z, Destaque.NOVO ) );
            return;
        }
        
        if ( !z.pai.vermelho ) {
            registrar( Tom.SUCESSO, "Pai preto: tudo certo", 
                    "O pai (" + z.pai.chave + ") é preto, então um filho vermelho não quebra nenhuma regra. Nada a corrigir!", 
                    m().d( z, Destaque.NOVO ) );
            return;
        }
        
        while ( z != raiz && z.pai.vermelho ) {
            
            No pai = z.pai;
            No avo = pai.pai; // pai vermelho nunca é a raiz, então o avô existe
            boolean paiEsq = pai == avo.esq;
            No tio = paiEsq ? avo.dir : avo.esq;
            boolean tioVermelho = tio != null && tio.vermelho;
            
            registrar( Tom.ALERTA, "Dois vermelhos seguidos", 
                    "O pai " + pai.chave + " e o nó " + z.chave + " são vermelhos, violando a regra 3. Olhamos o tio (" 
                            + ( tio == null ? "null, que conta como preto" : tio.chave + ", " + nomeCor( tio ) ) + ") para decidir: " 
                            + ( tioVermelho ? "tio vermelho, basta recolorir." : "tio preto, é preciso rotacionar." ), 
                    m().d( z, Destaque.PROBLEMA ).p( z, "z" ).d( pai, Destaque.PROBLEMA ).p( pai, "pai" )
                            .d( tio, Destaque.PIVO ).p( tio, "tio" ).p( avo, "avô" ) );
            
            if ( tioVermelho ) {
                
                pai.vermelho = false;
                tio.vermelho = false;
                avo.vermelho = true;
                registrar( Tom.INFO, "Caso 1: tio vermelho", 
                        "Pai e tio viram pretos e o avô " + avo.chave + " vira vermelho. O número de pretos em cada caminho não mudou, " 
                                + "mas o avô pode ter agora um pai vermelho: repetimos a verificação a partir dele.", 
                        m().d( pai, Destaque.PIVO ).d( tio, Destaque.PIVO ).d( avo, Destaque.PIVO ).p( avo, "novo z" ) );
                z = avo;
                
            } else {
                
                boolean triangulo = z == ( paiEsq ? pai.dir : pai.esq );
                
                if ( triangulo ) {
                    registrar( Tom.INFO, "Caso 2: tio preto, em triângulo", 
                            z.chave + ", " + pai.chave + " e " + avo.chave + " formam um \"joelho\" (z está do lado de dentro). " 
                                    + "Uma rotação em torno do pai " + pai.chave + " transforma o joelho em uma linha reta.", 
                            m().d( z, Destaque.PROBLEMA ).p( z, "z" ).d( pai, Destaque.PIVO ).p( pai, "pai" ).p( tio, "tio" ).p( avo, "avô" ) );
                    No antigoPai = pai;
                    rotacionar( !paiEsq, pai, "Rotação à " + ( paiEsq ? "esquerda" : "direita" ) + " em " + pai.chave );
                    z = antigoPai;
                    pai = z.pai;
                }
                
                pai.vermelho = false;
                avo.vermelho = true;
                registrar( Tom.INFO, "Caso 3: tio preto, em linha", 
                        "Agora z, pai e avô estão em linha. O pai " + pai.chave + " vira preto e o avô " + avo.chave 
                                + " vira vermelho; depois rotacionamos o avô para baixo, no sentido oposto ao do pai.", 
                        m().d( pai, Destaque.PIVO ).p( pai, "pai" ).d( avo, Destaque.PIVO ).p( avo, "avô" ).d( z, Destaque.PROBLEMA ).p( z, "z" ) );
                rotacionar( paiEsq, avo, "Rotação à " + ( paiEsq ? "direita" : "esquerda" ) + " em " + avo.chave );
                
            }
            
        }
        
        if ( raiz.vermelho ) {
            raiz.vermelho = false;
            registrar( Tom.SUCESSO, "Raiz volta a ser preta", 
                    "A recoloração chegou à raiz " + raiz.chave + ". Pela regra 2 ela é pintada de preto. Árvore válida!", m() );
        } else {
            registrar( Tom.SUCESSO, "Regras restauradas", 
                    "Nenhum nó vermelho tem filho vermelho e todos os caminhos têm o mesmo número de pretos. Árvore válida!", m() );
        }
        
    }
    
    //**************************************************************************
    // remoção
    //**************************************************************************
    
    @Override
    protected void removerNo( No z, No filho ) {
        
        No pai = z.pai;
        
        if ( filho != null ) {
            
            // z só pode ser preto com um filho vermelho
            transplantar( z, filho );
            tamanho--;
            registrar( Tom.INFO, "Nó removido", 
                    "O nó foi desligado e o filho " + filho.chave + " ocupou seu lugar." 
                            + ( z.vermelho ? "" : " O nó removido era preto, então esse caminho perdeu um preto: o filho precisa compensar." ), 
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
                    "Remover um nó vermelho não muda o número de pretos de nenhum caminho. Nenhuma regra foi violada!", m() );
            
        } else {
            
            registrar( Tom.ALERTA, "Remover um nó preto", 
                    "Desligar um nó preto tiraria um preto de todos os caminhos que passam por ele. Para compensar, tratamos " 
                            + z.chave + " como \"duplo-preto\" e corrigimos as cores antes de desligá-lo.", 
                    m().d( z, Destaque.PROBLEMA ).duplo( z ).p( z, "x" ) );
            
            corrigirRemocao( z );
            
            transplantar( z, null );
            tamanho--;
            registrar( Tom.SUCESSO, "Nó removido", 
                    "Com o \"preto extra\" resolvido, o nó " + z.chave + " finalmente é desligado da árvore. Árvore válida!", m() );
            
        }
        
    }
    
    /** Resolve o "duplo-preto" em {@code x} (equivalente ao fixAfterDeletion clássico). */
    private void corrigirRemocao( No x ) {
        
        while ( x != raiz && preto( x ) ) {
            
            No pai = x.pai;
            boolean xEsq = x == pai.esq;
            No irmao = xEsq ? pai.dir : pai.esq;
            
            registrar( Tom.ALERTA, "x é duplo-preto", 
                    "x (" + x.chave + ") carrega um \"preto extra\". Olhamos o irmão (" + irmao.chave + ", " + nomeCor( irmao ) 
                            + ") e os sobrinhos para decidir como resolver.", 
                    base( x, pai, irmao ) );
            
            if ( !preto( irmao ) ) {
                
                irmao.vermelho = false;
                pai.vermelho = true;
                registrar( Tom.INFO, "Caso 1: irmão vermelho", 
                        "O irmão " + irmao.chave + " é vermelho. Pintamos o irmão de preto e o pai " + pai.chave 
                                + " de vermelho e rotacionamos o pai em direção a x. Assim x ganha um irmão preto e caímos nos casos seguintes.", 
                        base( x, pai, irmao ) );
                rotacionar( !xEsq, pai, "Rotação à " + ( xEsq ? "esquerda" : "direita" ) + " em " + pai.chave, 
                        m().duplo( x ).p( x, "x" ).p( pai, "pai" ) );
                irmao = xEsq ? pai.dir : pai.esq;
                
            }
            
            No perto = xEsq ? irmao.esq : irmao.dir;
            No longe = xEsq ? irmao.dir : irmao.esq;
            
            if ( preto( perto ) && preto( longe ) ) {
                
                irmao.vermelho = true;
                registrar( Tom.INFO, "Caso 2: irmão e sobrinhos pretos", 
                        "O irmão " + irmao.chave + " e seus dois filhos são pretos. Pintamos o irmão de vermelho e o \"preto extra\" sobe para o pai " 
                                + pai.chave + "." 
                                + ( pai.vermelho ? " Como o pai é vermelho, basta pintá-lo de preto para terminar." 
                                        : pai == raiz ? " Como o pai é a raiz, o preto extra simplesmente desaparece." 
                                                : " Repetimos a análise a partir dele." ), 
                        m().duplo( x ).p( x, "x" ).d( irmao, Destaque.PIVO ).p( irmao, "irmão" ).p( pai, "pai" ) );
                x = pai;
                
            } else {
                
                if ( preto( longe ) ) {
                    
                    perto.vermelho = false;
                    irmao.vermelho = true;
                    registrar( Tom.INFO, "Caso 3: sobrinho de fora preto", 
                            "O irmão " + irmao.chave + " é preto, o sobrinho de fora é preto e o de dentro (" + perto.chave 
                                    + ") é vermelho. Trocamos as cores do irmão e do sobrinho de dentro e rotacionamos o irmão para fora, " 
                                    + "deixando um sobrinho vermelho \"em linha\" para o próximo caso.", 
                            base( x, pai, irmao ).d( perto, Destaque.PIVO ).p( perto, "sobrinho" ) );
                    rotacionar( xEsq, irmao, "Rotação à " + ( xEsq ? "direita" : "esquerda" ) + " em " + irmao.chave, 
                            m().duplo( x ).p( x, "x" ).p( pai, "pai" ) );
                    irmao = xEsq ? pai.dir : pai.esq;
                    longe = xEsq ? irmao.dir : irmao.esq;
                    
                }
                
                irmao.vermelho = pai.vermelho;
                pai.vermelho = false;
                longe.vermelho = false;
                registrar( Tom.INFO, "Caso 4: sobrinho de fora vermelho", 
                        "O irmão " + irmao.chave + " é preto e o sobrinho de fora " + longe.chave 
                                + " é vermelho. O irmão assume a cor do pai, e pai e sobrinho viram pretos. Depois rotacionamos o pai em direção a x: " 
                                + "o \"preto extra\" é absorvido e a correção termina.", 
                        base( x, pai, irmao ).d( longe, Destaque.PIVO ).p( longe, "sobrinho" ) );
                rotacionar( !xEsq, pai, "Rotação à " + ( xEsq ? "esquerda" : "direita" ) + " em " + pai.chave, 
                        m().duplo( x ).p( x, "x" ) );
                x = raiz;
                
            }
            
        }
        
        if ( x.vermelho ) {
            x.vermelho = false;
            registrar( Tom.INFO, "Pintando " + x.chave + " de preto", 
                    x.chave + " é vermelho e absorve o \"preto extra\": basta pintá-lo de preto.", m().d( x, Destaque.NOVO ) );
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
        if ( e != null ) {
            return e;
        }
        if ( raiz != null && raiz.vermelho ) {
            return "raiz vermelha";
        }
        return alturaNegra( raiz ) < 0 ? "regras de cor violadas" : null;
    }
    
    /** @return altura negra, ou -1 se houver violação. */
    private int alturaNegra( No n ) {
        if ( n == null ) {
            return 1;
        }
        if ( n.vermelho && ( !preto( n.esq ) || !preto( n.dir ) ) ) {
            return -1;
        }
        int e = alturaNegra( n.esq );
        int d = alturaNegra( n.dir );
        if ( e < 0 || d < 0 || e != d ) {
            return -1;
        }
        return e + ( n.vermelho ? 0 : 1 );
    }
    
}
