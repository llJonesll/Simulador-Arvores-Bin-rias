package simulador.ui;

import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo.Tom;
import simulador.modelo.TipoArvore;

/**
 * Paleta de cores do aplicativo (tema escuro, estilo "jogo").
 * Cores são inteiros ARGB, como no Android.
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Tema {
    
    private Tema() {
    }
    
    public static final int FUNDO_TOPO = 0xFF0C0F1F;
    public static final int FUNDO_BASE = 0xFF1C1F45;
    public static final int PAINEL = 0x0EFFFFFF;
    public static final int PAINEL_FORTE = 0xFF141734;
    public static final int PAINEL_ESCURO = 0xFF0F1230;
    public static final int FOLHA = 0xFF151839;
    public static final int BOTAO = 0xFF232957;
    public static final int BORDA = 0x24FFFFFF;
    public static final int TEXTO = 0xFFF2F3FF;
    public static final int TEXTO_SUAVE = 0xFFA4AAD8;
    public static final int LINHA = 0xFF8C95DA;
    public static final int GRADE = 0x09FFFFFF;
    
    public static final int NO_BASE = 0xFF1D2250;
    public static final int NO_VERMELHO = 0xFFD72A45;
    public static final int NO_VERMELHO_ANEL = 0xFFFF93A5;
    public static final int NO_PRETO = 0xFF0B0B13;
    public static final int NO_PRETO_ANEL = 0xFFAEB4DD;
    
    public static final int AMBAR = 0xFFFFC23D;
    public static final int CAMINHO = 0xFFFFE08A;
    public static final int VERDE = 0xFF3DDC8A;
    public static final int CIANO = 0xFF4DE1FF;
    public static final int ROXO = 0xFFB78CFF;
    public static final int ALERTA = 0xFFFF4D5E;
    public static final int LARANJA = 0xFFFF8A5C;
    public static final int TEAL = 0xFF2EC4B6;
    public static final int AJUDA = 0xFF7C9BFF;
    
    public static int acento( TipoArvore t ) {
        switch ( t ) {
            case AVL: return 0xFF35E0A1;
            case RN: return 0xFFFF5C7A;
            default: return 0xFF4DA3FF;
        }
    }
    
    public static int destaque( Destaque d ) {
        switch ( d ) {
            case CAMINHO: return CAMINHO;
            case COMPARANDO: return AMBAR;
            case ENCONTRADO: return VERDE;
            case NOVO: return CIANO;
            case PIVO: return ROXO;
            case PROBLEMA: return ALERTA;
            case VISITADO: return TEAL;
            case REMOVER: return LARANJA;
            default: return 0xFFFFFFFF;
        }
    }
    
    public static int tom( Tom t ) {
        switch ( t ) {
            case SUCESSO: return VERDE;
            case ALERTA: return AMBAR;
            case ERRO: return Tema.ALERTA;
            default: return 0xFF6F90FF;
        }
    }
    
    /** Troca a opacidade da cor. @param a de 0 a 1 */
    public static int alfa( int cor, double a ) {
        int v = (int) Math.round( Math.max( 0, Math.min( 1, a ) ) * 255 );
        return ( cor & 0x00FFFFFF ) | ( v << 24 );
    }
    
    /** Mistura duas cores (resultado opaco). */
    public static int misturar( int a, int b, double t ) {
        t = Math.max( 0, Math.min( 1, t ) );
        int r = (int) Math.round( ( ( a >> 16 ) & 255 ) + ( ( ( b >> 16 ) & 255 ) - ( ( a >> 16 ) & 255 ) ) * t );
        int g = (int) Math.round( ( ( a >> 8 ) & 255 ) + ( ( ( b >> 8 ) & 255 ) - ( ( a >> 8 ) & 255 ) ) * t );
        int bl = (int) Math.round( ( a & 255 ) + ( ( b & 255 ) - ( a & 255 ) ) * t );
        return 0xFF000000 | ( r << 16 ) | ( g << 8 ) | bl;
    }
    
}
