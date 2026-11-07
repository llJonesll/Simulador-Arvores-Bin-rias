package simulador.modelo;

import java.util.ArrayList;
import java.util.List;
import simulador.modelo.Template.Operacao;

/**
 * Catálogo de templates (cenários prontos) de cada tipo de árvore.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Templates {
    
    private Templates() {
    }
    
    public static List<Template> de( TipoArvore tipo ) {
        switch ( tipo ) {
            case AVL: return avl();
            case RN: return rn();
            default: return abb();
        }
    }
    
    private static List<Template> abb() {
        
        List<Template> l = new ArrayList<>();
        int[] equilibrada = { 50, 30, 70, 20, 40, 60, 80 };
        
        l.add( new Template( "Árvore equilibrada", 
                "Uma ABB \"bonita\": todo nó tem dois filhos e a altura é mínima. Qualquer chave é encontrada em no máximo 3 comparações.", 
                equilibrada, Operacao.BUSCAR, 60, "Busque o 60 e conte as comparações." ) );
        
        l.add( new Template( "Pior caso: vira uma lista", 
                "Inserir chaves já ordenadas faz a ABB degenerar numa lista encadeada: a altura vira n e a busca vira O(n).", 
                new int[] { 10, 20, 30, 40, 50, 60 }, Operacao.BUSCAR, 60, 
                "Busque o 60: são 6 comparações. É esse problema que a AVL e a Vermelho-Preto resolvem." ) );
        
        l.add( new Template( "Zigue-zague", 
                "Alternar chaves pequenas e grandes cria um zigue-zague: a árvore fica estreita e comprida.", 
                new int[] { 10, 50, 20, 40, 30 }, Operacao.BUSCAR, 30, "Busque o 30 e observe o caminho percorrido." ) );
        
        l.add( new Template( "Remover uma folha", 
                "Caso mais simples de remoção: o nó não tem filhos e é apenas desligado do pai.", 
                equilibrada, Operacao.REMOVER, 20, "Remova o 20." ) );
        
        l.add( new Template( "Remover nó com um filho", 
                "O nó 40 tem um único filho (35). Na remoção, o filho sobe para ocupar o lugar do pai.", 
                new int[] { 50, 30, 70, 20, 40, 35 }, Operacao.REMOVER, 40, "Remova o 40 e veja o 35 subir." ) );
        
        l.add( new Template( "Remover nó com dois filhos", 
                "Caso mais interessante: o nó é substituído pelo seu sucessor (o menor da subárvore direita), que é copiado para o lugar dele.", 
                equilibrada, Operacao.REMOVER, 50, "Remova a raiz 50: ela será trocada pelo sucessor 60." ) );
        
        l.add( new Template( "Percursos", 
                "Uma árvore para testar os quatro percursos: em ordem, pré-ordem, pós-ordem e em largura.", 
                equilibrada ) );
        
        return l;
        
    }
    
    private static List<Template> avl() {
        
        List<Template> l = new ArrayList<>();
        
        l.add( new Template( "Caso LL: esquerda-esquerda", 
                "Inserir 10 deixa a raiz 50 pesada demais no lado esquerdo, e o filho 30 também pende para a esquerda. Solução: uma rotação simples à direita.", 
                new int[] { 50, 30, 70, 20, 40 }, Operacao.INSERIR, 10, "Insira o 10 e acompanhe a rotação à direita." ) );
        
        l.add( new Template( "Caso RR: direita-direita", 
                "Espelho do caso LL: inserir 90 deixa a raiz pesada à direita. Solução: rotação simples à esquerda.", 
                new int[] { 50, 30, 70, 60, 80 }, Operacao.INSERIR, 90, "Insira o 90 e acompanhe a rotação à esquerda." ) );
        
        l.add( new Template( "Caso LR: esquerda-direita", 
                "Inserir 35 pesa o lado esquerdo, mas o filho 30 pende para a direita (um \"joelho\"). Solução: rotação dupla, esquerda em 30 e depois direita em 50.", 
                new int[] { 50, 30, 70, 20, 40 }, Operacao.INSERIR, 35, "Insira o 35 e veja as duas rotações." ) );
        
        l.add( new Template( "Caso RL: direita-esquerda", 
                "Espelho do caso LR: inserir 65 cria um joelho no lado direito. Solução: rotação dupla, direita em 70 e depois esquerda em 50.", 
                new int[] { 50, 30, 70, 60, 80 }, Operacao.INSERIR, 65, "Insira o 65 e veja as duas rotações." ) );
        
        l.add( new Template( "Remoção: rotação simples", 
                "Remover chaves também pode desbalancear. Ao remover 40, o nó 30 fica com fb = +2 e o filho 20 pende para a esquerda.", 
                new int[] { 30, 20, 40, 10 }, Operacao.REMOVER, 40, "Remova o 40 e veja a rotação corrigir a árvore." ) );
        
        l.add( new Template( "Remoção: rotação dupla", 
                "Ao remover 40, o nó 30 fica com fb = +2, mas o filho 20 pende para a direita: é preciso uma rotação dupla.", 
                new int[] { 30, 20, 40, 25 }, Operacao.REMOVER, 40, "Remova o 40 e veja as duas rotações." ) );
        
        l.add( new Template( "Sequência crescente", 
                "Na ABB, chaves em ordem crescente formam uma lista. Na AVL, a mesma sequência continua equilibrada graças às rotações.", 
                new int[] { 10, 20, 30, 40, 50, 60 }, Operacao.INSERIR, 70, "Insira o 70 e compare com a ABB." ) );
        
        return l;
        
    }
    
    private static List<Template> rn() {
        
        List<Template> l = new ArrayList<>();
        
        l.add( new Template( "Inserção: tio vermelho", 
                "Inserir 20 cria dois vermelhos seguidos (30 e 20) e o tio 70 é vermelho. Solução: só recolorir, sem rotações.", 
                new int[] { 50, 30, 70 }, Operacao.INSERIR, 20, "Insira o 20 e veja a recoloração." ) );
        
        l.add( new Template( "Inserção: tio preto (linha)", 
                "Inserir 20 cria dois vermelhos seguidos, mas o tio é preto (null). Como 20, 30 e 50 estão em linha, basta uma rotação simples com recoloração.", 
                new int[] { 50, 30 }, Operacao.INSERIR, 20, "Insira o 20 e veja a rotação à direita." ) );
        
        l.add( new Template( "Inserção: tio preto (joelho)", 
                "Inserir 40 cria dois vermelhos seguidos, o tio é preto e os três nós formam um \"joelho\". Solução: duas rotações.", 
                new int[] { 50, 30 }, Operacao.INSERIR, 40, "Insira o 40 e veja a rotação dupla." ) );
        
        l.add( new Template( "Remover nó vermelho", 
                "Remover nó vermelho nunca quebra as regras: nenhum caminho perde nós pretos.", 
                new int[] { 50, 30, 70 }, Operacao.REMOVER, 30, "Remova o 30." ) );
        
        l.add( new Template( "Remoção: sobrinho de fora", 
                "Remover o 20 (preto) deixa um \"preto extra\". O irmão 90 é preto e seu filho de fora, 120, é vermelho: uma rotação resolve tudo (caso 4).", 
                new int[] { 30, 20, 90, 120 }, Operacao.REMOVER, 20, "Remova o 20 e acompanhe o duplo-preto." ) );
        
        l.add( new Template( "Remoção: sobrinho de dentro", 
                "Remover o 50 (preto) deixa um \"preto extra\". O sobrinho vermelho está do lado de dentro: primeiro uma rotação no irmão (caso 3), depois outra no pai (caso 4).", 
                new int[] { 50, 100, 60, 90 }, Operacao.REMOVER, 50, "Remova o 50 e veja as duas rotações." ) );
        
        l.add( new Template( "Remoção: irmão vermelho", 
                "Ao remover 150, o irmão 40 é vermelho (caso 1). Uma rotação o troca por um irmão preto e cai em outro caso.", 
                new int[] { 50, 150, 80, 40, 10, 30 }, Operacao.REMOVER, 150, "Remova o 150 e siga os casos em cadeia." ) );
        
        l.add( new Template( "Remoção: sem sobrinhos", 
                "Remover 110 (preto): o irmão e os sobrinhos são pretos, então o irmão fica vermelho e o \"preto extra\" sobe para o pai (caso 2).", 
                new int[] { 110, 150, 130, 100, 90, 50, 30, 70 }, Operacao.REMOVER, 110, "Remova o 110 e veja o preto extra subir." ) );
        
        return l;
        
    }
    
}
