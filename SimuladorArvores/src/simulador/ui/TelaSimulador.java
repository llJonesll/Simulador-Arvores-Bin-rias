package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.List;
import java.util.Random;
import simulador.Main;
import simulador.modelo.Arvore;
import simulador.modelo.Passo;
import simulador.modelo.Percurso;
import simulador.modelo.Passo.Tom;
import simulador.modelo.Template;
import simulador.modelo.Template.Operacao;
import simulador.modelo.Templates;
import simulador.modelo.TipoArvore;
import simulador.ui.BarraControles.Acao;

/**
 * Tela de um simulador de árvore. Apenas orquestra os componentes:
 * o modelo gera os passos, o {@link Reprodutor} os reproduz e o
 * {@link Visualizador} os desenha, enquanto barra, painel e popup cuidam
 * da interface.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class TelaSimulador implements Tela {
    
    private final Main m;
    private final TipoArvore tipo;
    private final Arvore arvore;
    private final Random rnd = new Random();
    
    private final Visualizador viz;
    private final Reprodutor rep;
    private final BarraTopo topo;
    private final BarraControles controles;
    private final PainelPassos painel;
    private final PopupTemplates popup;
    private final Aviso aviso = new Aviso();
    
    private Operacao dica = Operacao.NENHUMA;
    private double tempo;
    
    // área do desenho da árvore (calculada a cada quadro)
    private double areaX, areaY, areaW, areaH;
    
    public TelaSimulador( Main m, TipoArvore tipo ) {
        
        this.m = m;
        this.tipo = tipo;
        this.arvore = tipo.criar();
        
        Color acento = Tema.acento( tipo );
        viz = new Visualizador( acento );
        rep = new Reprodutor( viz );
        topo = new BarraTopo( tipo, acento );
        controles = new BarraControles( acento );
        painel = new PainelPassos( tipo, acento );
        popup = new PopupTemplates( acento );
        
        arvore.carregar( 50, 30, 70, 20, 40 );
        rep.iniciar( List.of( arvore.estadoAtual( Tom.INFO, "Bem-vindo: " + tipo.nome, textoBoasVindas() ) ) );
        
    }
    
    private String textoBoasVindas() {
        String base = "Já montamos uma árvore de exemplo com 5 chaves. Digite uma chave e tecle Enter para inserir, "
                + "ou abra os Templates para ver casos prontos.\n";
        switch ( tipo ) {
            case AVL:
                return base + "A AVL mantém o balanceamento com rotações simples e duplas.";
            case RN:
                return base + "A Vermelho-Preto equilibra a árvore recolorindo nós e rotacionando.";
            default:
                return base + "A ABB não se equilibra sozinha: tente inserir 60, 70, 80, 90...";
        }
    }
    
    //**************************************************************************
    // atualização
    //**************************************************************************
    
    @Override
    public void atualizar( double delta ) {
        
        tempo += delta;
        int largura = m.getScreenWidth();
        int altura = m.getScreenHeight();
        double mg = 16;
        double painelW = Math.max( 300, Math.min( 360, largura * 0.27 ) );
        double dockY = altura - BarraControles.ALTURA - mg;
        
        areaX = mg;
        areaY = BarraTopo.ALTURA;
        areaW = largura - painelW - 3 * mg;
        areaH = dockY - mg - areaY;
        
        boolean popupAberto = popup.visivel();
        Template escolhido = popup.atualizar( m, delta, largura, altura );
        if ( escolhido != null ) {
            carregar( escolhido );
        }
        
        // o popup "consome" o teclado e o mouse no quadro em que estava aberto
        boolean livre = !popupAberto && !popup.visivel();
        
        if ( livre && m.isKeyPressed( EngineFrame.KEY_ESCAPE ) ) {
            m.voltarAoMenu();
            return;
        }
        if ( topo.atualizar( m, delta, livre ) ) {
            m.voltarAoMenu();
            return;
        }
        
        painel.atualizar( m, largura - painelW - mg, areaY, painelW, areaH, rep, livre );
        executar( controles.atualizar( m, delta, mg, dockY, largura - 2 * mg, livre, rep, dica ) );
        
        rep.atualizar( delta );
        viz.atualizar( delta, areaX, areaY, areaW, areaH );
        aviso.atualizar( delta );
        
    }
    
    //**************************************************************************
    // ações
    //**************************************************************************
    
    private void executar( Acao acao ) {
        switch ( acao ) {
            case INSERIR: operarComChave( Operacao.INSERIR ); break;
            case BUSCAR: operarComChave( Operacao.BUSCAR ); break;
            case REMOVER: operarComChave( Operacao.REMOVER ); break;
            case ALEATORIO: inserirAleatoria(); break;
            case LIMPAR: limpar(); break;
            case TEMPLATES: popup.abrir( Templates.de( tipo ) ); break;
            case EM_ORDEM: percorrer( Percurso.EM_ORDEM ); break;
            case PRE_ORDEM: percorrer( Percurso.PRE_ORDEM ); break;
            case POS_ORDEM: percorrer( Percurso.POS_ORDEM ); break;
            case LARGURA: percorrer( Percurso.LARGURA ); break;
            case INICIO: rep.inicio(); break;
            case ANTERIOR: rep.anterior(); break;
            case PLAY: rep.alternarPlay(); break;
            case PROXIMO: rep.proximo(); break;
            case FIM: rep.fim(); break;
            case MAIS_RAPIDO: rep.maisRapido(); break;
            case MAIS_LENTO: rep.maisLento(); break;
            case MODO: rep.alternarModo(); break;
            default: break;
        }
    }
    
    private void operarComChave( Operacao op ) {
        
        Integer chave = controles.campo.valor();
        if ( chave == null ) {
            controles.campo.tremer();
            aviso.mostrar( "Digite uma chave de 0 a " + CampoNumero.MAX, Tom.ALERTA );
            return;
        }
        
        iniciarOperacao( op, chave );
        controles.campo.limpar();
        
    }
    
    private void iniciarOperacao( Operacao op, int chave ) {
        
        rep.finalizar();
        
        switch ( op ) {
            case INSERIR: rep.iniciar( arvore.inserir( chave ) ); break;
            case BUSCAR: rep.iniciar( arvore.buscar( chave ) ); break;
            default: rep.iniciar( arvore.remover( chave ) ); break;
        }
        
        if ( op == dica ) {
            dica = Operacao.NENHUMA;
        }
        
    }
    
    private void inserirAleatoria() {
        
        if ( arvore.tamanho() >= Arvore.MAX_NOS ) {
            aviso.mostrar( "A árvore está cheia (" + Arvore.MAX_NOS + " nós)", Tom.ALERTA );
            return;
        }
        
        int chave;
        do {
            chave = rnd.nextInt( 99 ) + 1;
        } while ( arvore.contem( chave ) );
        
        iniciarOperacao( Operacao.INSERIR, chave );
        
    }
    
    private void percorrer( Percurso p ) {
        rep.finalizar();
        rep.iniciar( arvore.percorrer( p ) );
        dica = Operacao.NENHUMA;
    }
    
    private void limpar() {
        rep.finalizar();
        arvore.limpar();
        rep.iniciar( List.of( arvore.estadoAtual( Tom.INFO, "Árvore limpa", "Todos os nós foram removidos. Comece de novo inserindo uma chave." ) ) );
        dica = Operacao.NENHUMA;
    }
    
    private void carregar( Template t ) {
        
        rep.finalizar();
        arvore.limpar();
        arvore.carregar( t.base );
        
        rep.iniciar( List.of( arvore.estadoAtual( Tom.INFO, "Template: " + t.nome, t.descricao + "\n" + t.dica ) ) );
        
        dica = t.operacao;
        if ( t.operacao != Operacao.NENHUMA ) {
            controles.campo.definir( t.chave );
        } else {
            controles.campo.limpar();
        }
        
    }
    
    //**************************************************************************
    // desenho
    //**************************************************************************
    
    @Override
    public void desenhar() {
        
        int largura = m.getScreenWidth();
        int altura = m.getScreenHeight();
        
        Fundo.gradiente( m, largura, altura );
        
        topo.desenhar( m, largura, tempo, viz.passo() );
        
        desenharArea();
        viz.desenhar( m );
        aviso.desenhar( m, areaX + areaW / 2, areaY + areaH - 34 );
        
        painel.desenhar( m, tempo, rep );
        controles.desenhar( m, tempo, rep );
        
        popup.desenhar( m, largura, altura, tempo );
        
    }
    
    private void desenharArea() {
        
        Desenho.painel( m, areaX, areaY, areaW, areaH, 18, new Color( 255, 255, 255, 8 ), Tema.BORDA );
        Fundo.grade( m, areaX + 1, areaY + 1, areaW - 2, areaH - 2, 40, tempo * 4 );
        
        Passo p = viz.passo();
        if ( p != null && p.qtdNos == 0 ) {
            double bob = Math.sin( tempo * 3 ) * 6;
            Desenho.textoCentro( m, "Árvore vazia", areaX + areaW / 2, areaY + areaH / 2 - 20, 30, Tema.TEXTO, true );
            Desenho.textoCentro( m, "Digite uma chave e tecle Enter", areaX + areaW / 2, areaY + areaH / 2 + 16, 17, Tema.TEXTO_SUAVE, false );
            double ax = areaX + areaW / 2;
            double ay = areaY + areaH - 56 + bob;
            m.fillTriangle( ax - 11, ay, ax + 11, ay, ax, ay + 16, Tema.acento( tipo ) );
        }
        
    }
    
}
