package simulador.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import simulador.modelo.Arvore;
import simulador.modelo.Passo;
import simulador.modelo.Passo.Tom;
import simulador.modelo.Percurso;
import simulador.modelo.Template;
import simulador.modelo.Template.Operacao;
import simulador.modelo.Templates;
import simulador.modelo.TipoArvore;
import simulador.ui.BarraControles.Acao;

/**
 * Tela de um simulador de árvore. Apenas orquestra os componentes:
 * o modelo gera os passos, o {@link Reprodutor} os reproduz e o
 * {@link Visualizador} os desenha, enquanto barra, painel e popup cuidam
 * da interface de toque.
 * 
 * @author Prof. Dr. David Buzatto
 */
public class TelaSimulador implements Tela {
    
    private final Aplicativo app;
    private final TipoArvore tipo;
    private final Arvore arvore;
    private final Random rnd = new Random();
    
    private final Visualizador viz;
    private final Reprodutor rep;
    private final BarraTopo topo;
    private final BarraControles controles;
    private final PainelPassos painel;
    private final PopupLista popup = new PopupLista();
    private final Aviso aviso = new Aviso();
    private final int acento;
    
    private Operacao dica = Operacao.NENHUMA;
    private float tempo;
    
    // área do desenho da árvore (calculada a cada quadro)
    private float areaX, areaY, areaW, areaH;
    
    public TelaSimulador( Aplicativo app, TipoArvore tipo ) {
        
        this.app = app;
        this.tipo = tipo;
        this.arvore = tipo.criar();
        this.acento = Tema.acento( tipo );
        
        viz = new Visualizador( acento );
        rep = new Reprodutor( viz );
        topo = new BarraTopo( tipo, acento );
        controles = new BarraControles( acento );
        painel = new PainelPassos( acento );
        
        arvore.carregar( 50, 30, 70, 20, 40 );
        rep.iniciar( unico( arvore.estadoAtual( Tom.INFO, "Bem-vindo: " + tipo.nome, textoBoasVindas() ) ) );
        
    }
    
    private static List<Passo> unico( Passo p ) {
        return Collections.singletonList( p );
    }
    
    private String textoBoasVindas() {
        String base = "Montamos uma árvore de exemplo com 5 chaves. Digite uma chave no teclado e toque em Inserir, "
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
    // layout e atualização
    //**************************************************************************
    
    private void posicionar() {
        
        float W = app.largura();
        float H = app.altura();
        
        float dockY = H - BarraControles.alturaPara( H );
        float painelH = Util.limitar( H * 0.21f, H < 720 ? 132 : 150, 200 );
        float painelY = dockY - painelH - 4;
        
        controles.posicionar( 6, dockY, W - 12, H );
        painel.posicionar( 6, painelY, W - 12, painelH );
        
        areaX = 6;
        areaY = BarraTopo.ALTURA;
        areaW = W - 12;
        areaH = painelY - 6 - areaY;
        
    }
    
    @Override
    public void atualizar( float dt ) {
        
        tempo += dt;
        posicionar();
        
        topo.atualizar( dt );
        controles.atualizar( dt, rep, dica );
        popup.atualizar( dt );
        rep.atualizar( dt );
        viz.atualizar( dt, areaX, areaY, areaW, areaH );
        aviso.atualizar( dt );
        
    }
    
    //**************************************************************************
    // toque
    //**************************************************************************
    
    @Override
    public void toque( int acao, float x, float y ) {
        
        if ( popup.visivel() ) {
            PopupLista.Item item = popup.toque( acao, x, y );
            if ( item != null ) {
                app.vibrar();
                if ( item.dado instanceof Template ) {
                    carregar( (Template) item.dado );
                } else {
                    percorrer( (Percurso) item.dado );
                }
            }
            return;
        }
        
        if ( painel.toque( acao, x, y, rep ) ) {
            return;
        }
        
        if ( topo.toque( acao, x, y ) ) {
            app.voltarAoMenu();
            return;
        }
        
        Acao a = controles.toque( acao, x, y );
        if ( a != Acao.NENHUMA ) {
            app.vibrar();
            executar( a );
        }
        
    }
    
    @Override
    public boolean ocultaAjuda() {
        return popup.visivel();
    }
    
    @Override
    public boolean voltar() {
        if ( popup.visivel() ) {
            popup.fechar();
        } else {
            app.voltarAoMenu();
        }
        return true;
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
            case TEMPLATES: abrirTemplates(); break;
            case PERCURSOS: abrirPercursos(); break;
            case INICIO: rep.inicio(); break;
            case ANTERIOR: rep.anterior(); break;
            case PLAY: rep.alternarPlay(); break;
            case PROXIMO: rep.proximo(); break;
            case FIM: rep.fim(); break;
            case VELOCIDADE: rep.ciclarVelocidade(); break;
            case MODO: rep.alternarModo(); break;
            default: break;
        }
    }
    
    private void operarComChave( Operacao op ) {
        
        Integer chave = controles.campo.valor();
        if ( chave == null ) {
            controles.campo.tremer();
            aviso.mostrar( "Monte uma chave de 0 a " + CampoNumero.MAX, Tom.ALERTA );
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
        rep.iniciar( unico( arvore.estadoAtual( Tom.INFO, "Árvore limpa", "Todos os nós foram removidos. Comece de novo inserindo uma chave." ) ) );
        dica = Operacao.NENHUMA;
    }
    
    private void carregar( Template t ) {
        
        rep.finalizar();
        arvore.limpar();
        arvore.carregar( t.base );
        
        rep.iniciar( unico( arvore.estadoAtual( Tom.INFO, "Template: " + t.nome, t.descricao + "\n" + t.dica ) ) );
        
        dica = t.operacao;
        if ( t.operacao != Operacao.NENHUMA ) {
            controles.campo.definir( t.chave );
        } else {
            controles.campo.limpar();
        }
        
    }
    
    private void abrirTemplates() {
        List<PopupLista.Item> itens = new ArrayList<>();
        for ( Template t : Templates.de( tipo ) ) {
            String selo = t.operacao == Operacao.NENHUMA ? null : t.operacao.name() + " " + t.chave;
            int cor = t.operacao == Operacao.INSERIR ? Tema.VERDE 
                    : t.operacao == Operacao.REMOVER ? Tema.LARANJA 
                    : t.operacao == Operacao.BUSCAR ? Tema.CIANO : Tema.TEXTO_SUAVE;
            itens.add( new PopupLista.Item( t.nome, t.descricao, selo, cor, t ) );
        }
        popup.abrir( "Templates", "Toque em um cenário para montar a árvore.", itens, acento );
    }
    
    private void abrirPercursos() {
        List<PopupLista.Item> itens = new ArrayList<>();
        for ( Percurso p : Percurso.values() ) {
            itens.add( new PopupLista.Item( p.rotulo, p.regra, null, Tema.TEAL, p ) );
        }
        popup.abrir( "Percursos", "Veja a ordem em que os nós são visitados.", itens, Tema.TEAL );
    }
    
    //**************************************************************************
    // desenho
    //**************************************************************************
    
    @Override
    public void desenhar( Pincel g ) {
        
        float W = app.largura();
        float H = app.altura();
        
        Fundo.gradiente( g, W, H );
        topo.desenhar( g, W, tempo, viz.passo() );
        
        desenharArea( g );
        g.clipe( areaX, areaY, areaW, areaH );
        viz.desenhar( g );
        g.fimClipe();
        aviso.desenhar( g, areaX + areaW / 2, areaY + areaH - 26 );
        
        painel.desenhar( g, tempo, rep );
        controles.desenhar( g, tempo );
        
        popup.desenhar( g, W, H, tempo );
        
    }
    
    private void desenharArea( Pincel g ) {
        
        g.retanguloArred( areaX, areaY, areaW, areaH, 16, 0x08FFFFFF );
        g.bordaArred( areaX, areaY, areaW, areaH, 16, Tema.BORDA, 1.2f );
        g.clipe( areaX + 1, areaY + 1, areaW - 2, areaH - 2 );
        Fundo.grade( g, areaX, areaY, areaW, areaH, 32, tempo * 3 );
        g.fimClipe();
        
        Passo p = viz.passo();
        if ( p != null && p.qtdNos == 0 ) {
            float cx = areaX + areaW / 2;
            float cy = areaY + areaH / 2;
            g.textoCentro( "Árvore vazia", cx, cy - 14, 24, Tema.TEXTO, true );
            g.textoCentro( "Monte uma chave e toque em Inserir", cx, cy + 14, 13, Tema.TEXTO_SUAVE, false );
            float bob = (float) Math.sin( tempo * 3 ) * 5;
            float ay = areaY + areaH - 46 + bob;
            g.triangulo( cx - 9, ay, cx + 9, ay, cx, ay + 13, acento );
        }
        
    }
    
}
