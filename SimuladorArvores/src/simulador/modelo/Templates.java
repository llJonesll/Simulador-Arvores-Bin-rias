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
        
        l.add( new Template( "LLRB Regra 1: Rotação à Esquerda", 
                "Na LLRB, não é permitido ter uma ligação vermelha pendendo para a direita. Inserir um valor maior cria esse filho direito vermelho, forçando uma rotação à esquerda.", 
                new int[] { 10 }, Operacao.INSERIR, 20, "Insira o 20 e observe a rotação à esquerda corrigir a inclinação." ) );
        
        l.add( new Template( "LLRB Regra 2: Rotação à Direita", 
                "Não é permitido ter dois nós vermelhos seguidos à esquerda. Inserir valores em ordem decrescente gera essa violação, forçando uma rotação à direita no pai.", 
                new int[] { 30, 20 }, Operacao.INSERIR, 10, "Insira o 10 e observe a rotação à direita resolver os vermelhos consecutivos." ) );
        
        l.add( new Template( "LLRB Regra 3: Inversão de Cores (Flip)", 
                "Quando um nó fica com ambos os filhos vermelhos, ele simula um nó estourado. A solução é inverter as cores: os filhos viram pretos e o pai vira vermelho.", 
                new int[] { 20, 10 }, Operacao.INSERIR, 30, "Insira o 30 e observe o Flip Colors (Inversão de Cores)." ) );
        
        l.add( new Template( "Efeito Cascata (Inserção Crescente)", 
                "Ao contrário da ABB que vira uma lista, inserir elementos em ordem crescente na LLRB engatilha rotações e inversões de cor contínuas, mantendo a árvore balanceada.", 
                new int[] { 10, 20, 30, 40, 50, 60 }, Operacao.INSERIR, 70, "Insira o 70 e veja as regras da LLRB se aplicando em cascata até a raiz." ) );
        
        l.add( new Template( "Remoção: Nó vermelho (Folha)", 
                "Remover um nó vermelho é a operação mais simples, pois não altera a altura negra (número de nós pretos) de nenhum caminho da árvore.", 
                new int[] { 50, 25, 75, 10, 30 }, Operacao.REMOVER, 30, "Remova o 30 (que é vermelho) sem violar regras." ) );
        
        l.add( new Template( "Remoção: Reequilíbrio e Inclinação", 
                "Ao remover um nó, a exclusão clássica pode deixar ligações vermelhas pendendo para a direita. A LLRB sobe pela árvore aplicando as 3 regras para restaurar a inclinação à esquerda.", 
                new int[] { 50, 20, 70, 10, 30, 60, 80 }, Operacao.REMOVER, 10, "Remova o 10 e acompanhe as correções subindo até a raiz." ) );
        
        return l;
        
    }
    
}