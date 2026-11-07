package simulador.modelo;

/**
 * Cenário pronto: monta uma árvore e sugere a próxima operação, para
 * demonstrar um caso específico (ex.: rotação dupla da AVL).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Template {
    
    public enum Operacao { NENHUMA, INSERIR, BUSCAR, REMOVER }
    
    public final String nome;
    public final String descricao;
    public final int[] base;
    public final Operacao operacao;
    public final int chave;
    public final String dica;
    
    public Template( String nome, String descricao, int[] base, Operacao operacao, int chave, String dica ) {
        this.nome = nome;
        this.descricao = descricao;
        this.base = base;
        this.operacao = operacao;
        this.chave = chave;
        this.dica = dica;
    }
    
    /** Template sem operação sugerida (só monta a árvore). */
    public Template( String nome, String descricao, int[] base ) {
        this( nome, descricao, base, Operacao.NENHUMA, -1, "Experimente inserir, buscar e remover chaves." );
    }
    
}
