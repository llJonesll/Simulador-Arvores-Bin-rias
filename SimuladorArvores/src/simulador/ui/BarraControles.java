package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import simulador.modelo.Template.Operacao;

/**
 * Barra inferior: campo da chave, operações, percursos, reprodução dos
 * passos e atalhos de teclado. Não executa nada: apenas devolve a
 * {@link Acao} pedida pela pessoa, para a tela decidir o que fazer.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class BarraControles {
    
    public enum Acao {
        NENHUMA, INSERIR, BUSCAR, REMOVER, ALEATORIO, LIMPAR, TEMPLATES,
        EM_ORDEM, PRE_ORDEM, POS_ORDEM, LARGURA,
        INICIO, ANTERIOR, PLAY, PROXIMO, FIM, MAIS_RAPIDO, MAIS_LENTO, MODO
    }
    
    public static final double ALTURA = 124;
    
    public final CampoNumero campo = new CampoNumero();
    
    private final Color acento;
    private final Botao bInserir = new Botao( "Inserir", Tema.VERDE );
    private final Botao bBuscar = new Botao( "Buscar", Tema.CIANO );
    private final Botao bRemover = new Botao( "Remover", Tema.LARANJA );
    private final Botao bAleatorio = new Botao( "Aleatório", Tema.ROXO );
    private final Botao bLimpar = new Botao( "Limpar", Tema.TEXTO_SUAVE );
    private final Botao bTemplates = new Botao( "Templates", Tema.AMBAR );
    private final Botao bOrdem = new Botao( "Em ordem", Tema.TEAL );
    private final Botao bPre = new Botao( "Pré", Tema.TEAL );
    private final Botao bPos = new Botao( "Pós", Tema.TEAL );
    private final Botao bLargura = new Botao( "Largura", Tema.TEAL );
    private final Botao bInicio;
    private final Botao bAnterior;
    private final Botao bPlay;
    private final Botao bProximo;
    private final Botao bFim;
    private final Botao bMenos;
    private final Botao bMais;
    private final Botao bModo;
    
    private double x, y, w;
    private double xVelocidade;
    
    public BarraControles( Color acento ) {
        this.acento = acento;
        bInicio = new Botao( Botao.Icone.INICIO, acento );
        bAnterior = new Botao( Botao.Icone.ANTERIOR, acento );
        bPlay = new Botao( Botao.Icone.PLAY, acento );
        bProximo = new Botao( Botao.Icone.PROXIMO, acento );
        bFim = new Botao( Botao.Icone.FIM, acento );
        bMenos = new Botao( Botao.Icone.MENOS, acento );
        bMais = new Botao( Botao.Icone.MAIS, acento );
        bModo = new Botao( "Modo: Auto", acento );
        bInserir.atalho = "Enter";
        bBuscar.atalho = "B";
        bRemover.atalho = "Del";
        bAleatorio.atalho = "A";
        bLimpar.atalho = "C";
        bTemplates.atalho = "T";
    }
    
    //**************************************************************************
    // atualização
    //**************************************************************************
    
    public Acao atualizar( EngineFrame e, double delta, double x, double y, double w, 
            boolean aceitaEntrada, Reprodutor rep, Operacao dica ) {
        
        this.x = x;
        this.y = y;
        this.w = w;
        posicionar();
        
        bInserir.pulsar = dica == Operacao.INSERIR;
        bBuscar.pulsar = dica == Operacao.BUSCAR;
        bRemover.pulsar = dica == Operacao.REMOVER;
        bPlay.icone = rep.tocando() ? Botao.Icone.PAUSA : Botao.Icone.PLAY;
        bPlay.ativo = rep.total() > 1;
        bInicio.ativo = bAnterior.ativo = !rep.noInicio();
        bProximo.ativo = bFim.ativo = !rep.noFim();
        bModo.texto = rep.automatico() ? "Modo: Auto" : "Modo: Manual";
        
        if ( aceitaEntrada ) {
            campo.atualizar( e, delta );
        }
        
        Acao atalho = aceitaEntrada ? atalhos( e ) : Acao.NENHUMA;
        
        Acao clique = Acao.NENHUMA;
        clique = testar( e, delta, aceitaEntrada, bInserir, Acao.INSERIR, clique );
        clique = testar( e, delta, aceitaEntrada, bBuscar, Acao.BUSCAR, clique );
        clique = testar( e, delta, aceitaEntrada, bRemover, Acao.REMOVER, clique );
        clique = testar( e, delta, aceitaEntrada, bAleatorio, Acao.ALEATORIO, clique );
        clique = testar( e, delta, aceitaEntrada, bLimpar, Acao.LIMPAR, clique );
        clique = testar( e, delta, aceitaEntrada, bTemplates, Acao.TEMPLATES, clique );
        clique = testar( e, delta, aceitaEntrada, bOrdem, Acao.EM_ORDEM, clique );
        clique = testar( e, delta, aceitaEntrada, bPre, Acao.PRE_ORDEM, clique );
        clique = testar( e, delta, aceitaEntrada, bPos, Acao.POS_ORDEM, clique );
        clique = testar( e, delta, aceitaEntrada, bLargura, Acao.LARGURA, clique );
        clique = testar( e, delta, aceitaEntrada, bInicio, Acao.INICIO, clique );
        clique = testar( e, delta, aceitaEntrada, bAnterior, Acao.ANTERIOR, clique );
        clique = testar( e, delta, aceitaEntrada, bPlay, Acao.PLAY, clique );
        clique = testar( e, delta, aceitaEntrada, bProximo, Acao.PROXIMO, clique );
        clique = testar( e, delta, aceitaEntrada, bFim, Acao.FIM, clique );
        clique = testar( e, delta, aceitaEntrada, bMenos, Acao.MAIS_LENTO, clique );
        clique = testar( e, delta, aceitaEntrada, bMais, Acao.MAIS_RAPIDO, clique );
        clique = testar( e, delta, aceitaEntrada, bModo, Acao.MODO, clique );
        
        return clique != Acao.NENHUMA ? clique : atalho;
        
    }
    
    private Acao testar( EngineFrame e, double delta, boolean aceita, Botao b, Acao acao, Acao atual ) {
        boolean clicado = b.atualizar( e, delta, aceita );
        return clicado && atual == Acao.NENHUMA ? acao : atual;
    }
    
    private Acao atalhos( EngineFrame e ) {
        if ( e.isKeyPressed( EngineFrame.KEY_ENTER ) ) return Acao.INSERIR;
        if ( e.isKeyPressed( EngineFrame.KEY_B ) ) return Acao.BUSCAR;
        if ( e.isKeyPressed( EngineFrame.KEY_DELETE ) || e.isKeyPressed( EngineFrame.KEY_R ) ) return Acao.REMOVER;
        if ( e.isKeyPressed( EngineFrame.KEY_A ) ) return Acao.ALEATORIO;
        if ( e.isKeyPressed( EngineFrame.KEY_C ) ) return Acao.LIMPAR;
        if ( e.isKeyPressed( EngineFrame.KEY_T ) ) return Acao.TEMPLATES;
        if ( e.isKeyPressed( EngineFrame.KEY_M ) ) return Acao.MODO;
        if ( e.isKeyPressed( EngineFrame.KEY_SPACE ) ) return Acao.PLAY;
        if ( e.isKeyPressed( EngineFrame.KEY_RIGHT ) ) return Acao.PROXIMO;
        if ( e.isKeyPressed( EngineFrame.KEY_LEFT ) ) return Acao.ANTERIOR;
        if ( e.isKeyPressed( EngineFrame.KEY_HOME ) ) return Acao.INICIO;
        if ( e.isKeyPressed( EngineFrame.KEY_END ) ) return Acao.FIM;
        return Acao.NENHUMA;
    }
    
    //**************************************************************************
    // posicionamento
    //**************************************************************************
    
    private void posicionar() {
        
        double pad = 16;
        double bh = 44;
        double l1 = y + 14;
        double l2 = l1 + bh + 10;
        
        // linha 1: campo + operações à esquerda; extras à direita
        double cx = x + pad;
        campo.pos( cx, l1, 96, bh );
        cx += 96 + 14;
        bInserir.pos( cx, l1, 104, bh );
        cx += 104 + 8;
        bBuscar.pos( cx, l1, 96, bh );
        cx += 96 + 8;
        bRemover.pos( cx, l1, 104, bh );
        
        double xr = x + w - pad;
        xr -= 124;
        bTemplates.pos( xr, l1, 124, bh );
        xr -= 8 + 92;
        bLimpar.pos( xr, l1, 92, bh );
        xr -= 8 + 116;
        bAleatorio.pos( xr, l1, 116, bh );
        
        // linha 2: reprodução à esquerda; percursos à direita
        cx = x + pad;
        bInicio.pos( cx, l2, 46, bh );
        cx += 46 + 6;
        bAnterior.pos( cx, l2, 46, bh );
        cx += 46 + 6;
        bPlay.pos( cx, l2, 58, bh );
        cx += 58 + 6;
        bProximo.pos( cx, l2, 46, bh );
        cx += 46 + 6;
        bFim.pos( cx, l2, 46, bh );
        cx += 46 + 18;
        bMenos.pos( cx, l2, 38, bh );
        xVelocidade = cx + 38 + 4;
        cx += 38 + 4 + 62 + 4;
        bMais.pos( cx, l2, 38, bh );
        cx += 38 + 18;
        bModo.pos( cx, l2, 136, bh );
        
        xr = x + w - pad;
        xr -= 96;
        bLargura.pos( xr, l2, 96, bh );
        xr -= 8 + 72;
        bPos.pos( xr, l2, 72, bh );
        xr -= 8 + 72;
        bPre.pos( xr, l2, 72, bh );
        xr -= 8 + 104;
        bOrdem.pos( xr, l2, 104, bh );
        
    }
    
    //**************************************************************************
    // desenho
    //**************************************************************************
    
    public void desenhar( EngineFrame e, double tempo, Reprodutor rep ) {
        
        Desenho.painel( e, x, y, w, ALTURA, 18, Tema.PAINEL, Tema.BORDA );
        
        campo.desenhar( e, tempo, acento );
        
        Botao[] todos = { bInserir, bBuscar, bRemover, bAleatorio, bLimpar, bTemplates, bOrdem, bPre, bPos, bLargura,
            bInicio, bAnterior, bPlay, bProximo, bFim, bMenos, bMais, bModo };
        for ( Botao b : todos ) {
            b.desenhar( e, tempo );
        }
        
        // velocidade entre os botões - e +
        double cy = bMenos.y + bMenos.h / 2;
        e.fillRoundRectangle( xVelocidade, cy - 14, 62, 28, 16, new Color( 0x0F1230 ) );
        Desenho.textoCentro( e, rep.velocidadeTexto(), xVelocidade + 31, cy, 14, Tema.TEXTO, true );
        
    }
    
}
