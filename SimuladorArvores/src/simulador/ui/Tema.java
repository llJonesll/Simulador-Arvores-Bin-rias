package simulador.ui;

import java.awt.Color;
import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo.Tom;
import simulador.modelo.TipoArvore;

/**
 * Paleta de cores do simulador (tema escuro, estilo "jogo").
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class Tema {
    
    private Tema() {
    }
    
    public static final Color FUNDO_TOPO = new Color( 0x0C0F1F );
    public static final Color FUNDO_BASE = new Color( 0x1C1F45 );
    public static final Color PAINEL = new Color( 255, 255, 255, 14 );
    public static final Color PAINEL_FORTE = new Color( 20, 23, 52 );
    public static final Color BORDA = new Color( 255, 255, 255, 36 );
    public static final Color TEXTO = new Color( 0xF2F3FF );
    public static final Color TEXTO_SUAVE = new Color( 0xA4AAD8 );
    public static final Color LINHA = new Color( 0x8C95DA );
    public static final Color GRADE = new Color( 255, 255, 255, 9 );
    
    public static final Color NO_BASE = new Color( 0x1D2250 );
    public static final Color NO_VERMELHO = new Color( 0xD72A45 );
    public static final Color NO_VERMELHO_ANEL = new Color( 0xFF93A5 );
    public static final Color NO_PRETO = new Color( 0x0B0B13 );
    public static final Color NO_PRETO_ANEL = new Color( 0xAEB4DD );
    
    public static final Color AMBAR = new Color( 0xFFC23D );
    public static final Color VERDE = new Color( 0x3DDC8A );
    public static final Color CIANO = new Color( 0x4DE1FF );
    public static final Color ROXO = new Color( 0xB78CFF );
    public static final Color ALERTA = new Color( 0xFF4D5E );
    public static final Color LARANJA = new Color( 0xFF8A5C );
    public static final Color TEAL = new Color( 0x2EC4B6 );
    
    public static Color acento( TipoArvore t ) {
        switch ( t ) {
            case AVL: return new Color( 0x35E0A1 );
            case RN: return new Color( 0xFF5C7A );
            default: return new Color( 0x4DA3FF );
        }
    }
    
    public static Color destaque( Destaque d ) {
        switch ( d ) {
            case CAMINHO: return new Color( 0xFFE08A );
            case COMPARANDO: return AMBAR;
            case ENCONTRADO: return VERDE;
            case NOVO: return CIANO;
            case PIVO: return ROXO;
            case PROBLEMA: return ALERTA;
            case VISITADO: return TEAL;
            case REMOVER: return LARANJA;
            default: return Color.WHITE;
        }
    }
    
    public static Color tom( Tom t ) {
        switch ( t ) {
            case SUCESSO: return VERDE;
            case ALERTA: return AMBAR;
            case ERRO: return ALERTA;
            default: return new Color( 0x6F90FF );
        }
    }
    
    /** @param a opacidade de 0 a 1 */
    public static Color alfa( Color c, double a ) {
        int v = (int) Math.round( Math.max( 0, Math.min( 1, a ) ) * 255 );
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), v );
    }
    
    public static Color misturar( Color a, Color b, double t ) {
        t = Math.max( 0, Math.min( 1, t ) );
        return new Color( 
                (int) Math.round( a.getRed() + ( b.getRed() - a.getRed() ) * t ),
                (int) Math.round( a.getGreen() + ( b.getGreen() - a.getGreen() ) * t ),
                (int) Math.round( a.getBlue() + ( b.getBlue() - a.getBlue() ) * t ) );
    }
    
}
