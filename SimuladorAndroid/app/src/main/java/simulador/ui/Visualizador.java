package simulador.ui;

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
 * Desenha a árvore de um {@link Passo} e anima a transição para o próximo:
 * nós deslizam para a nova posição (rotações), mudam de cor (recoloração),
 * nascem do pai (inserção) e desaparecem (remoção).
 * 
 * O layout é o clássico: x = ranque em ordem, y = nível.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class Visualizador {
    
    private static class Anim {
        int id;
        float x, y, tx, ty;
        float esc, alfa, glow;
        float[] fill = new float[3];
        float[] anel = new float[3];
        boolean novo = true;
        boolean saindo;
        NoVisual v;
        Destaque faisca;
    }
    
    private final Map<Integer, Anim> nos = new LinkedHashMap<>();
    private final Particulas particulas = new Particulas();
    private final int acento;
    private Passo passo;
    private float raio = 20;
    private float tempo;
    
    public Visualizador( int acento ) {
        this.acento = acento;
    }
    
    public Passo passo() {
        return passo;
    }
    
    /** Passa a exibir o passo; as diferenças em relação ao anterior viram animação. */
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
    
    public void atualizar( float dt, float ax, float ay, float aw, float ah ) {
        
        tempo += dt;
        particulas.atualizar( dt );
        
        if ( passo == null ) {
            return;
        }
        
        calcularAlvos( ax, ay, aw, ah );
        
        for ( Iterator<Anim> it = nos.values().iterator(); it.hasNext(); ) {
            
            Anim a = it.next();
            
            if ( a.novo ) {
                iniciar( a );
            }
            
            if ( a.faisca != null ) {
                particulas.explodir( a.x, a.y, Tema.destaque( a.faisca ), a.faisca == Destaque.PROBLEMA ? 10 : 16, 110 );
                a.faisca = null;
            }
            
            a.x = Util.suavizar( a.x, a.tx, dt, 9 );
            a.y = Util.suavizar( a.y, a.ty, dt, 9 );
            a.esc = Util.suavizar( a.esc, a.saindo ? 0 : 1, dt, 11 );
            a.alfa = Util.suavizar( a.alfa, a.saindo ? 0 : 1, dt, 8 );
            
            float[] f = rgb( corFundo( a.v ) );
            float[] r = rgb( corAnel( a.v ) );
            for ( int i = 0; i < 3; i++ ) {
                a.fill[i] = Util.suavizar( a.fill[i], f[i], dt, 12 );
                a.anel[i] = Util.suavizar( a.anel[i], r[i], dt, 12 );
            }
            a.glow = Util.suavizar( a.glow, brilhoAlvo( a.v ), dt, 12 );
            
            if ( a.saindo && a.alfa < 0.03f ) {
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
        
        // nós ligados à árvore nascem de dentro do pai
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
    
    private void calcularAlvos( float ax, float ay, float aw, float ah ) {
        
        int n = Math.max( passo.qtdNos, 1 );
        int prof = Math.max( passo.profundidade, 1 );
        float margemX = 38;
        float topo = 74;
        float base = 34;
        
        float dx = Math.min( 66, ( aw - 2 * margemX ) / Math.max( n - 1, 1 ) );
        float dy = Math.min( 80, ( ah - topo - base ) / Math.max( prof - 1, 1 ) );
        raio = Util.limitar( Math.min( Math.min( dx * 0.8f, dy * 0.4f ), 19 ), 6, 19 );
        
        float x0 = ax + aw / 2 - ( n - 1 ) * dx / 2;
        float y0 = ay + topo;
        
        for ( NoVisual v : passo.nos ) {
            if ( !v.flutuante ) {
                Anim a = nos.get( v.id );
                a.tx = x0 + v.ranque * dx;
                a.ty = y0 + v.nivel * dy;
            }
        }
        
        for ( NoVisual v : passo.nos ) {
            if ( v.flutuante ) {
                Anim a = nos.get( v.id );
                Anim alvo = v.alvoId >= 0 ? nos.get( v.alvoId ) : null;
                if ( alvo != null ) {
                    // o nó novo paira junto ao nó comparado, sem sair da área
                    a.tx = Util.limitar( alvo.tx + v.lado * raio * 2.1f, ax + raio + 4, ax + aw - raio - 4 );
                    a.ty = alvo.ty - raio * 2.3f;
                } else {
                    a.tx = ax + aw / 2;
                    a.ty = y0 - raio * 2.3f;
                }
            }
        }
        
    }
    
    //**************************************************************************
    // cores
    //**************************************************************************
    
    private int corFundo( NoVisual v ) {
        switch ( v.cor ) {
            case VERMELHO: return Tema.NO_VERMELHO;
            case PRETO: return Tema.NO_PRETO;
            default: return Tema.misturar( Tema.NO_BASE, acento, 0.38 );
        }
    }
    
    private int corAnel( NoVisual v ) {
        if ( v.destaque != Destaque.NENHUM ) {
            return Tema.destaque( v.destaque );
        }
        switch ( v.cor ) {
            case VERMELHO: return Tema.NO_VERMELHO_ANEL;
            case PRETO: return Tema.NO_PRETO_ANEL;
            default: return Tema.misturar( acento, 0xFFFFFFFF, 0.35 );
        }
    }
    
    private float brilhoAlvo( NoVisual v ) {
        switch ( v.destaque ) {
            case NENHUM: return 0;
            case CAMINHO: return 0.3f;
            case VISITADO: return 0.5f;
            default: return 1;
        }
    }
    
    private static float[] rgb( int c ) {
        return new float[] { ( c >> 16 ) & 255, ( c >> 8 ) & 255, c & 255 };
    }
    
    private static int cor( float[] c, float alfa ) {
        return ( (int) Util.limitar( alfa * 255, 0, 255 ) << 24 ) 
                | ( (int) Util.limitar( c[0], 0, 255 ) << 16 ) 
                | ( (int) Util.limitar( c[1], 0, 255 ) << 8 ) 
                | (int) Util.limitar( c[2], 0, 255 );
    }
    
    //**************************************************************************
    // desenho
    //**************************************************************************
    
    public void desenhar( Pincel g ) {
        
        if ( passo == null ) {
            return;
        }
        
        desenharArestas( g );
        
        for ( Anim a : nos.values() ) {
            if ( !a.v.flutuante || a.saindo ) {
                desenharNo( g, a );
            }
        }
        
        for ( Anim a : nos.values() ) {
            if ( !a.saindo ) {
                desenharEtiquetas( g, a );
            }
        }
        
        for ( Anim a : nos.values() ) {
            if ( a.v.flutuante && !a.saindo ) {
                desenharNo( g, a );
                desenharEtiquetas( g, a );
            }
        }
        
        particulas.desenhar( g );
        
    }
    
    private void desenharArestas( Pincel g ) {
        
        for ( NoVisual v : passo.nos ) {
            
            if ( v.flutuante || v.paiId < 0 ) {
                continue;
            }
            
            Anim filho = nos.get( v.id );
            Anim pai = nos.get( v.paiId );
            if ( filho == null || pai == null ) {
                continue;
            }
            
            float a = Math.min( filho.alfa, pai.alfa );
            if ( v.arestaDestacada ) {
                g.linha( pai.x, pai.y, filho.x, filho.y, Tema.alfa( Tema.AMBAR, a ), 3.6f );
            } else {
                g.linha( pai.x, pai.y, filho.x, filho.y, Tema.alfa( Tema.LINHA, a * 0.75 ), 2.2f );
            }
            
        }
        
    }
    
    private void desenharNo( Pincel g, Anim a ) {
        
        float r = raio * a.esc;
        if ( r < 1 ) {
            return;
        }
        
        float pulso = 0.7f + 0.3f * (float) Math.sin( tempo * 6 );
        int anel = cor( a.anel, a.alfa );
        
        g.brilho( a.x, a.y, r, cor( a.anel, 1 ), a.glow * pulso * a.alfa );
        
        g.circulo( a.x + 1.5f, a.y + 3, r, Tema.alfa( 0xFF000000, 0.31 * a.alfa ) );
        g.circulo( a.x, a.y, r, cor( a.fill, a.alfa ) );
        g.circulo( a.x - r * 0.28f, a.y - r * 0.34f, r * 0.5f, Tema.alfa( 0xFFFFFFFF, 0.10 * a.alfa ) );
        g.aro( a.x, a.y, r, anel, Math.max( 1.8f, r * 0.12f ) );
        
        if ( a.v.duploPreto ) {
            float p = 0.5f + 0.5f * (float) Math.sin( tempo * 7 );
            g.aro( a.x, a.y, r + 4, Tema.alfa( 0xFFFFFFFF, 0.9 * a.alfa ), 2f );
            g.aro( a.x, a.y, r + 8 + 1.5f * p, Tema.alfa( 0xFFFFFFFF, 0.47 * a.alfa ), 2f );
        }
        
        String s = String.valueOf( a.v.chave );
        float tam = Math.max( 7, s.length() <= 2 ? r * 0.95f : r * 0.72f );
        g.textoCentro( s, a.x, a.y, tam, Tema.alfa( Tema.TEXTO, a.alfa ), true );
        
    }
    
    private void desenharEtiquetas( Pincel g, Anim a ) {
        
        if ( a.esc < 0.6f ) {
            return;
        }
        
        float r = raio * a.esc;
        float tam = Util.limitar( raio * 0.5f, 9, 11.5f );
        
        if ( a.v.papel != null ) {
            float w = g.largura( a.v.papel, tam, true ) + 12;
            float lado = a.v.paiId < 0 ? 0 : ( a.v.ehEsq ? -1 : 1 );
            float cx = a.x + lado * ( w / 2 + r * 0.25f );
            float cy = a.y - r - 10;
            int c = a.v.destaque == Destaque.NENHUM ? Tema.ROXO : Tema.destaque( a.v.destaque );
            g.retanguloArred( cx - w / 2, cy - 9, w, 18, 9, Tema.FOLHA );
            g.bordaArred( cx - w / 2, cy - 9, w, 18, 9, c, 1.4f );
            g.textoCentro( a.v.papel, cx, cy, tam, c, true );
        }
        
        if ( a.v.selo != null ) {
            float w = g.largura( a.v.selo, tam, true ) + 10;
            float cy = a.y + r + 11;
            int c = a.v.seloAlerta ? Tema.ALERTA : Tema.VERDE;
            g.retanguloArred( a.x - w / 2, cy - 8, w, 16, 8, Tema.PAINEL_ESCURO );
            g.bordaArred( a.x - w / 2, cy - 8, w, 16, 8, Tema.alfa( c, 0.8 ), 1.2f );
            g.textoCentro( a.v.selo, a.x, cy, tam, c, true );
        }
        
    }
    
}
