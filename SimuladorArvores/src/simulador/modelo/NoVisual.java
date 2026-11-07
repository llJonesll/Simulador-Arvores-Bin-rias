package simulador.modelo;

/**
 * "Foto" de um nó em um determinado passo da simulação. O visualizador
 * compara fotos consecutivas (pelo {@code id}) para animar a transição.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class NoVisual {
    
    public enum Destaque { NENHUM, CAMINHO, COMPARANDO, ENCONTRADO, NOVO, PIVO, PROBLEMA, VISITADO, REMOVER }
    
    public enum Cor { NEUTRO, VERMELHO, PRETO }
    
    public int id;
    public int chave;
    public int paiId = -1;
    public int nivel;
    public int ranque;
    
    public boolean ehEsq;
    public boolean arestaDestacada;
    public boolean duploPreto;
    public boolean seloAlerta;
    
    /** Nó novo que ainda não foi ligado à árvore (paira perto de {@code alvoId}). */
    public boolean flutuante;
    public int alvoId = -1;
    public int lado;
    
    public Cor cor = Cor.NEUTRO;
    public Destaque destaque = Destaque.NENHUM;
    
    /** Etiqueta de papel: "z", "pai", "tio", "avô", "irmão"... */
    public String papel;
    
    /** Texto pequeno abaixo do nó (ex.: fator de balanceamento da AVL). */
    public String selo;
    
}
