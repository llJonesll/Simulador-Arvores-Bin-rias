package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;

/**
 * Botão de modo imediato, com animação de hover, pulso de destaque e ícones
 * desenhados com formas (não dependem de fontes especiais).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Botao {
    
    public enum Icone { NENHUM, INICIO, ANTERIOR, PLAY, PAUSA, PROXIMO, FIM, VOLTAR, MAIS, MENOS }
    
    /** Ligado por qualquer botão sob o mouse; a janela usa para trocar o cursor. */
    public static boolean mao;
    
    public double x, y, w, h;
    public String texto;
    public String atalho;
    public Color cor;
    public Icone icone = Icone.NENHUM;
    public boolean ativo = true;
    public boolean pulsar;
    public boolean selecionado;
    
    private double hover;
    private double aperto;
    
    public Botao( String texto, Color cor ) {
        this.texto = texto;
        this.cor = cor;
    }
    
    public Botao( Icone icone, Color cor ) {
        this.icone = icone;
        this.cor = cor;
    }
    
    public Botao pos( double x, double y, double w, double h ) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }
    
    /** @return true se foi clicado neste quadro. */
    public boolean atualizar( EngineFrame e, double delta, boolean aceitaEntrada ) {
        
        boolean sobre = aceitaEntrada && ativo && Desenho.dentro( e.getMouseX(), e.getMouseY(), x, y, w, h );
        double k = 1 - Math.exp( -delta * 16 );
        hover += ( ( sobre ? 1 : 0 ) - hover ) * k;
        aperto += ( ( sobre && e.isMouseButtonDown( EngineFrame.MOUSE_BUTTON_LEFT ) ? 1 : 0 ) - aperto ) * k;
        
        if ( sobre ) {
            mao = true;
        }
        
        return sobre && e.isMouseButtonPressed( EngineFrame.MOUSE_BUTTON_LEFT );
        
    }
    
    public void desenhar( EngineFrame e, double tempo ) {
        
        double pulso = pulsar ? 0.5 + 0.5 * Math.sin( tempo * 6 ) : 0;
        double yy = y + aperto * 2;
        
        Color base = Tema.misturar( new Color( 0x232957 ), cor, 0.14 + 0.30 * hover + ( selecionado ? 0.25 : 0 ) + 0.25 * pulso );
        Color borda = Tema.alfa( cor, ativo ? 0.45 + 0.4 * hover + 0.5 * pulso : 0.2 );
        
        if ( pulsar ) {
            e.fillRoundRectangle( x - 4, yy - 4, w + 8, h + 8, 18, Tema.alfa( cor, 0.10 + 0.20 * pulso ) );
        }
        
        e.fillRoundRectangle( x, yy + 3, w, h, 14, new Color( 0, 0, 0, 70 ) );
        e.fillRoundRectangle( x, yy, w, h, 14, ativo ? base : Tema.alfa( base, 0.4 ) );
        e.setStrokeLineWidth( 1.6f );
        e.drawRoundRectangle( x, yy, w, h, 14, borda );
        e.resetStrokeToDefault();
        
        Color tc = ativo ? Tema.TEXTO : Tema.alfa( Tema.TEXTO, 0.4 );
        double cx = x + w / 2;
        double cy = yy + h / 2;
        
        if ( icone != Icone.NENHUM ) {
            desenharIcone( e, cx, cy, h * 0.17, tc );
        } else if ( atalho != null ) {
            Desenho.textoCentro( e, texto, cx, cy - 6, 16, tc, true );
            Desenho.textoCentro( e, atalho, cx, cy + 10, 11, Tema.alfa( tc, 0.6 ), false );
        } else {
            Desenho.textoCentro( e, texto, cx, cy, 16, tc, true );
        }
        
    }
    
    private void desenharIcone( EngineFrame e, double cx, double cy, double s, Color c ) {
        switch ( icone ) {
            case PLAY:
                e.fillTriangle( cx - s * 0.7, cy - s * 1.2, cx - s * 0.7, cy + s * 1.2, cx + s * 1.2, cy, c );
                break;
            case PAUSA:
                e.fillRectangle( cx - s * 0.95, cy - s * 1.1, s * 0.7, s * 2.2, c );
                e.fillRectangle( cx + s * 0.25, cy - s * 1.1, s * 0.7, s * 2.2, c );
                break;
            case PROXIMO:
                e.fillTriangle( cx - s * 1.1, cy - s, cx - s * 1.1, cy + s, cx + s * 0.6, cy, c );
                e.fillRectangle( cx + s * 0.75, cy - s, s * 0.45, s * 2, c );
                break;
            case ANTERIOR:
                e.fillTriangle( cx + s * 1.1, cy - s, cx + s * 1.1, cy + s, cx - s * 0.6, cy, c );
                e.fillRectangle( cx - s * 1.2, cy - s, s * 0.45, s * 2, c );
                break;
            case FIM:
                e.fillTriangle( cx - s * 1.3, cy - s, cx - s * 1.3, cy + s, cx, cy, c );
                e.fillTriangle( cx - s * 0.1, cy - s, cx - s * 0.1, cy + s, cx + s * 1.2, cy, c );
                break;
            case INICIO:
                e.fillTriangle( cx + s * 1.3, cy - s, cx + s * 1.3, cy + s, cx, cy, c );
                e.fillTriangle( cx + s * 0.1, cy - s, cx + s * 0.1, cy + s, cx - s * 1.2, cy, c );
                break;
            case VOLTAR:
                e.fillTriangle( cx - s * 3.9, cy, cx - s * 2.5, cy - s * 1.2, cx - s * 2.5, cy + s * 1.2, c );
                Desenho.texto( e, "Menu", cx - s * 1.9, cy, 15, c, true );
                break;
            case MAIS:
                e.fillRectangle( cx - s, cy - s * 0.2, s * 2, s * 0.4, c );
                e.fillRectangle( cx - s * 0.2, cy - s, s * 0.4, s * 2, c );
                break;
            case MENOS:
                e.fillRectangle( cx - s, cy - s * 0.2, s * 2, s * 0.4, c );
                break;
            default:
                break;
        }
    }
    
}
