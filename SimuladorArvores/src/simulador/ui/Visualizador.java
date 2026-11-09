package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import simulador.modelo.NoVisual;
import simulador.modelo.NoVisual.Destaque;
import simulador.modelo.Passo;

/**
 * Desenha a árvore de um {@link Passo} e anima a transição para o próximo.
 * Atualizado para exibir conexões vermelhas de forma horizontal (estilo LLRB / 2-3).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Visualizador {
    
    private static class Anim {
        int id;
        double x, y, tx, ty;
        double esc, alfa, glow;
        double[] fill = new double[3];
        double[] anel = new double[3];
        boolean novo = true;
        boolean saindo;
        NoVisual v;
        Destaque faisca;
    }
    
    private final Map<Integer, Anim> nos = new LinkedHashMap<>();
    private final Particulas particulas = new Particulas();
    private final Color acento;
    private Passo passo;
    private double raio = 24;
    private double tempo;
    
    public Visualizador( Color acento ) {
        this.acento = acento;
    }
    
    public Passo passo() {
        return passo;
    }
    
    public void mostrar( Passo novo ) {
        
        Map<Integer, NoVisual> antes = new HashMap<>();
        if ( passo != null ) {
            for ( NoVisual v : passo.nos ) {
                antes.put( v.id, v );
            }
        }
        
        Set<Integer> presentes = new HashSet<>();
        
        for ( NoVisual v : novo.nos ) {
            presentes.add( v.id );
            Anim a = nos.get( v.id );
            if ( a == null ) {
                a = new Anim();
                a.id = v.id;
                nos.put( v.id, a );
            }
            a.saindo = false;
            NoVisual p = antes.get( v.id );
            boolean mudou = p == null || p.destaque != v.destaque;
            if ( mudou && ( v.destaque == Destaque.ENCONTRADO || v.destaque == Destaque.NOVO || v.destaque == Destaque.PROBLEMA ) ) {
                a.faisca = v.destaque;
            }
            a.v = v;
        }
        
        for ( Anim a : nos.values() ) {
            if ( !presentes.contains( a.id ) ) {
                a.saindo = true;
            }
        }
        
        passo = novo;
        
    }
    
    //**************************************************************************
    // atualização
    //**************************************************************************
    
    public void atualizar( double delta, double ax, double ay, double aw, double ah ) {
        
        tempo += delta;
        particulas.atualizar( delta );
        
        if ( passo == null ) {
            return;
        }
        
        calcularAlvos( ax, ay, aw, ah );
        
        double k = 1 - Math.exp( -delta * 9 );
        double kCor = 1 - Math.exp( -delta * 12 );
        
        for ( Iterator<Anim> it = nos.values().iterator(); it.hasNext(); ) {
            
            Anim a = it.next();
            
            if ( a.novo ) {
                iniciar( a );
            }
            
            if ( a.faisca != null ) {
                int qtd = a.faisca == Destaque.PROBLEMA ? 10 : 16;
                particulas.explodir( a.x, a.y, Tema.destaque( a.faisca ), qtd, 150 );
                a.faisca = null;
            }
            
            a.x += ( a.tx - a.x ) * k;
            a.y += ( a.ty - a.y ) * k;
            a.esc += ( ( a.saindo ? 0 : 1 ) - a.esc ) * ( 1 - Math.exp( -delta * 11 ) );
            a.alfa += ( ( a.saindo ? 0 : 1 ) - a.alfa ) * ( 1 - Math.exp( -delta * 8 ) );
            
            double[] f = rgb( corFundo( a.v ) );
            double[] r = rgb( corAnel( a.v ) );
            for ( int i = 0; i < 3; i++ ) {
                a.fill[i] += ( f[i] - a.fill[i] ) * kCor;
                a.anel[i] += ( r[i] - a.anel[i] ) * kCor;
            }
            a.glow += ( brilhoAlvo( a.v ) - a.glow ) * kCor;
            
            if ( a.saindo && a.alfa < 0.03 ) {
                it.remove();
            }
            
        }
        
    }
    
    private void iniciar( Anim a ) {
        
        a.novo = false;
        a.x = a.tx;
        a.y = a.ty;
        a.esc = 0;
        a.alfa = 0;
        
        if ( a.v != null && !a.v.flutuante && a.v.paiId >= 0 ) {
            Anim pai = nos.get( a.v.paiId );
            if ( pai != null && !pai.novo ) {
                a.x = pai.x;
                a.y = pai.y;
            }
        }
        
        a.fill = rgb( corFundo( a.v ) );
        a.anel = rgb( corAnel( a.v ) );
        
    }

    // Calcula a Profundidade Negra para achatar os nós vermelhos na visualização 2-3
    private int getNivelNegro( NoVisual v, Map<Integer, NoVisual> mapaNos, Map<Integer, Integer> cache ) {
        if ( v.paiId < 0 ) return 0;
        if ( cache.containsKey( v.id ) ) return cache.get( v.id );
        
        NoVisual pai = mapaNos.get( v.paiId );
        int nnPai = pai != null ? getNivelNegro( pai, mapaNos, cache ) : 0;
        int nn = nnPai + ( v.cor == NoVisual.Cor.VERMELHO ? 0 : 1 ); // Não desce o Y se for vermelho
        cache.put( v.id, nn );
        return nn;
    }
    
    private void calcularAlvos( double ax, double ay, double aw, double ah ) {
        
        int n = Math.max( passo.qtdNos, 1 );
        int prof = Math.max( passo.profundidade, 1 );
        double margemX = 46;
        double topo = 92;
        double base = 56;
        
        double dx = Math.min( 78, ( aw - 2 * margemX ) / Math.max( n - 1, 1 ) );
        double dy = Math.min( 92, ( ah - topo - base ) / Math.max( prof - 1, 1 ) );
        raio = Desenho.limitar( Math.min( Math.min( dx * 0.8, dy * 0.4 ), 26 ), 8, 26 );
        
        double largArvore = ( n - 1 ) * dx;
        double x0 = ax + aw / 2 - largArvore / 2;
        double y0 = ay + topo;

        Map<Integer, NoVisual> mapaNos = new HashMap<>();
        for ( NoVisual v : passo.nos ) {
            mapaNos.put( v.id, v );
        }
        Map<Integer, Integer> nivelNegro = new HashMap<>();
        
        for ( NoVisual v : passo.nos ) {
            if ( !v.flutuante ) {
                Anim a = nos.get( v.id );
                a.tx = x0 + v.ranque * dx;
                
                // Utiliza nível negro para nivelar 2-3 na LLRB (em ABB/AVL ele agirá como profundidade normal)
                int nn = getNivelNegro( v, mapaNos, nivelNegro );
                a.ty = y0 + nn * dy;
            }
        }
        
        for ( NoVisual v : passo.nos ) {
            if ( v.flutuante ) {
                Anim a = nos.get( v.id );
                Anim alvo = v.alvoId >= 0 ? nos.get( v.alvoId ) : null;
                if ( alvo != null ) {
                    a.tx = alvo.tx + v.lado * raio * 2.1;
                    a.ty = alvo.ty - raio * 2.3;
                } else {
                    a.tx = ax + aw / 2;
                    a.ty = y0 - raio * 2.3;
                }
            }
        }
        
    }
    
    //**************************************************************************
    // cores
    //**************************************************************************
    
    private Color corFundo( NoVisual v ) {
        // Para simular a 2-3 visualmente, todos os nós base da LLRB (Vermelho e Preto) ficam com fundo neutro escuro.
        if ( v.cor == NoVisual.Cor.VERMELHO || v.cor == NoVisual.Cor.PRETO ) {
            return Tema.NO_PRETO;
        }
        return Tema.misturar( Tema.NO_BASE, acento, 0.38 );
    }
    
    private Color corAnel( NoVisual v ) {
        if ( v.destaque != Destaque.NENHUM ) {
            return Tema.destaque( v.destaque );
        }
        if ( v.cor == NoVisual.Cor.VERMELHO || v.cor == NoVisual.Cor.PRETO ) {
            return Tema.NO_PRETO_ANEL;
        }
        return Tema.misturar( acento, Color.WHITE, 0.35 );
    }
    
    private double brilhoAlvo( NoVisual v ) {
        switch ( v.destaque ) {
            case NENHUM: return 0;
            case CAMINHO: return 0.3;
            case VISITADO: return 0.5;
            default: return 1;
        }
    }
    
    private static double[] rgb( Color c ) {
        return new double[] { c.getRed(), c.getGreen(), c.getBlue() };
    }
    
    private static Color cor( double[] c, double alfa ) {
        return new Color( 
                (int) Desenho.limitar( c[0], 0, 255 ), 
                (int) Desenho.limitar( c[1], 0, 255 ), 
                (int) Desenho.limitar( c[2], 0, 255 ), 
                (int) Desenho.limitar( alfa * 255, 0, 255 ) );
    }
    
    //**************************************************************************
    // desenho
    //**************************************************************************
    
    public void desenhar( EngineFrame e ) {
        
        if ( passo == null ) {
            return;
        }
        
        desenharArestas( e );
        
        for ( Anim a : nos.values() ) {
            if ( !a.v.flutuante || a.saindo ) {
                desenharNo( e, a );
            }
        }
        
        for ( Anim a : nos.values() ) {
            if ( !a.saindo ) {
                desenharEtiquetas( e, a );
            }
        }
        
        for ( Anim a : nos.values() ) {
            if ( a.v.flutuante && !a.saindo ) {
                desenharNo( e, a );
                desenharEtiquetas( e, a );
            }
        }
        
        particulas.desenhar( e );
        
    }
    
    private void desenharArestas( EngineFrame e ) {
        
        for ( NoVisual v : passo.nos ) {
            
            if ( v.flutuante || v.paiId < 0 ) {
                continue;
            }
            
            Anim filho = nos.get( v.id );
            Anim pai = nos.get( v.paiId );
            if ( filho == null || pai == null ) {
                continue;
            }
            
            double a = Math.min( filho.alfa, pai.alfa );
            
            // Foca a cor vermelha na LINHA, desenhando mais grossa e colorida
            if ( v.cor == NoVisual.Cor.VERMELHO ) {
                e.setStrokeLineWidth( 5.0f );
                e.drawLine( pai.x, pai.y, filho.x, filho.y, Tema.alfa( Tema.NO_VERMELHO, a ) );
            } else if ( v.arestaDestacada ) {
                e.setStrokeLineWidth( 4.5f );
                e.drawLine( pai.x, pai.y, filho.x, filho.y, Tema.alfa( Tema.AMBAR, a ) );
            } else {
                e.setStrokeLineWidth( 2.6f );
                e.drawLine( pai.x, pai.y, filho.x, filho.y, Tema.alfa( Tema.LINHA, a * 0.75 ) );
            }
            
        }
        
        e.resetStrokeToDefault();
        
    }
    
    private void desenharNo( EngineFrame e, Anim a ) {
        
        double r = raio * a.esc;
        if ( r < 1 ) {
            return;
        }
        
        double pulso = 0.7 + 0.3 * Math.sin( tempo * 6 );
        Color anel = cor( a.anel, a.alfa );
        
        Desenho.brilho( e, a.x, a.y, r, cor( a.anel, 1 ), a.glow * pulso * a.alfa );
        
        e.fillCircle( a.x + 2, a.y + 4, r, new Color( 0, 0, 0, (int) ( 80 * a.alfa ) ) );
        e.fillCircle( a.x, a.y, r, cor( a.fill, a.alfa ) );
        e.fillCircle( a.x - r * 0.28, a.y - r * 0.34, r * 0.5, new Color( 255, 255, 255, (int) ( 26 * a.alfa ) ) );
        
        e.setStrokeLineWidth( (float) Math.max( 2, r * 0.13 ) );
        e.drawCircle( a.x, a.y, r, anel );
        
        if ( a.v.duploPreto ) {
            double p = 0.5 + 0.5 * Math.sin( tempo * 7 );
            e.setStrokeLineWidth( 2.5f );
            e.drawCircle( a.x, a.y, r + 5, new Color( 255, 255, 255, (int) ( 230 * a.alfa ) ) );
            e.drawCircle( a.x, a.y, r + 10 + 2 * p, new Color( 255, 255, 255, (int) ( 120 * a.alfa ) ) );
        }
        
        e.resetStrokeToDefault();
        
        String s = String.valueOf( a.v.chave );
        int tam = (int) Math.max( 8, s.length() <= 2 ? r * 0.95 : r * 0.72 );
        Desenho.textoCentro( e, s, a.x, a.y, tam, Tema.alfa( Tema.TEXTO, a.alfa ), true );
        
    }
    
    private void desenharEtiquetas( EngineFrame e, Anim a ) {
        
        if ( a.esc < 0.6 ) {
            return;
        }
        
        double r = raio * a.esc;
        int tam = (int) Desenho.limitar( raio * 0.52, 10, 13 );
        
        if ( a.v.papel != null ) {
            double w = Desenho.largura( e, a.v.papel, tam, true ) + 14;
            double lado = a.v.paiId < 0 ? 0 : ( a.v.ehEsq ? -1 : 1 );
            double cx = a.x + lado * ( w / 2 + r * 0.25 );
            double cy = a.y - r - 12;
            Color c = a.v.destaque == Destaque.NENHUM ? Tema.ROXO : Tema.destaque( a.v.destaque );
            e.fillRoundRectangle( cx - w / 2, cy - 10, w, 20, 14, new Color( 0x14173A ) );
            e.setStrokeLineWidth( 1.6f );
            e.drawRoundRectangle( cx - w / 2, cy - 10, w, 20, 14, c );
            e.resetStrokeToDefault();
            Desenho.textoCentro( e, a.v.papel, cx, cy, tam, c, true );
        }
        
        if ( a.v.selo != null ) {
            double w = Desenho.largura( e, a.v.selo, tam, true ) + 12;
            double cy = a.y + r + 13;
            Color c = a.v.seloAlerta ? Tema.ALERTA : Tema.VERDE;
            e.fillRoundRectangle( a.x - w / 2, cy - 9, w, 18, 12, new Color( 0x10132E ) );
            e.setStrokeLineWidth( 1.4f );
            e.drawRoundRectangle( a.x - w / 2, cy - 9, w, 18, 12, Tema.alfa( c, 0.8 ) );
            e.resetStrokeToDefault();
            Desenho.textoCentro( e, a.v.selo, a.x, cy, tam, c, true );
        }
        
    }
    
}