package simulador.ui;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import java.awt.Color;
import java.util.List;
import simulador.modelo.Passo.Tom;
import simulador.modelo.TipoArvore;

/**
 * Conteúdo do tutorial: as páginas e suas ilustrações.
 * 
 * 
 * @author Prof. Dr. David Buzatto (modificado por João Carlos Silveira e José Pedro)
 */
public final class PaginasTutorial {
    
    private PaginasTutorial() {
    }
    
    public static List<PaginaTutorial> todas() {
        return List.of( 
            new PaginaTutorial( "Bem-vindo ao Simulador de Árvores", PaginasTutorial::boasVindas, 
                "Veja, passo a passo, como funcionam as árvores: ABB, AVL e LLRB (Visão 2-3).", 
                "Créditos: João Carlos e José Pedro. Engine: JSGE (Prof. David Buzatto).",
                "No menu, escolha uma árvore com o mouse, setas + Enter ou teclas 1, 2 e 3." ),
            new PaginaTutorial( "Inserir, buscar e remover", PaginasTutorial::operacoes, 
                "Digite uma chave de 0 a 999 no campo e tecle Enter para inserir.", 
                "Com a chave digitada, use Buscar (B) ou Remover (Del). Aleatório (A) insere uma chave sorteada.", 
                "As setas para cima e para baixo ajustam o valor do campo; Backspace apaga." ),
            new PaginaTutorial( "Acompanhe cada passo", PaginasTutorial::reproducao, 
                "Os passos tocam sozinhos. Pause com Espaço; avance com a seta direita e volte com a esquerda.", 
                "Use + e - para a velocidade. Em Modo: Manual (M), os passos só avançam quando você pedir.", 
                "Clique na linha do tempo do painel lateral para saltar para qualquer passo." ),
            new PaginaTutorial( "Entenda o desenho", PaginasTutorial::legenda, 
                "O brilho indica o papel do nó no passo. As etiquetas (z, pai, tio, irmão...) mostram quem é quem.", 
                "AVL: o selo abaixo do nó mostra a altura (h) e o fator de balanceamento (fb); fora de -1 a +1 fica vermelho.", 
                "LLRB (2-3): as conexões vermelhas horizontais representam nós unidos (3-nós) inclinados à esquerda." ),
            new PaginaTutorial( "Templates: casos prontos", PaginasTutorial::templates, 
                "Templates (T) abre cenários para estudar: rotações da AVL, regras LLRB e remoções da ABB.", 
                "Escolha com o clique ou com as teclas 1 a 8. A árvore é montada e a chave já vem preenchida.", 
                "O botão da operação sugerida pulsa: clique nele (ou tecle Enter) para ver o caso acontecer." ),
            new PaginaTutorial( "Percursos e atalhos", PaginasTutorial::atalhos, 
                "Em ordem, Pré, Pós e Largura mostram a ordem de visita dos nós e a saída acumulada.", 
                "Esc volta ao menu. F1 (ou H) abre este tutorial a qualquer momento.", 
                "Dica: tente inserir chaves em ordem crescente na ABB e depois na LLRB e compare as alturas." )
        );
    }
    
    //**************************************************************************
    // ilustrações
    //**************************************************************************
    
    private static void boasVindas( EngineFrame e, double cx, double cy, double t ) {
        TipoArvore[] tipos = TipoArvore.values();
        for ( int i = 0; i < tipos.length; i++ ) {
            Color c = Tema.acento( tipos[i] );
            double x = cx + ( i - 1 ) * 190;
            double y = cy + Math.sin( t * 2 + i ) * 4;
            e.fillCircle( x, y, 52, Tema.alfa( c, 0.10 ) );
            e.fillCircle( x, y, 40, Tema.misturar( Tema.NO_BASE, c, 0.4 ) );
            e.setStrokeLineWidth( 3f );
            e.drawCircle( x, y, 40, c );
            e.resetStrokeToDefault();
            Desenho.textoCentro( e, tipos[i].sigla, x, y, 24, Tema.TEXTO, true );
        }
    }
    
    private static final CampoNumero CAMPO = new CampoNumero();
    private static final Botao B_INSERIR = new Botao( "Inserir", Tema.VERDE );
    private static final Botao B_BUSCAR = new Botao( "Buscar", Tema.CIANO );
    private static final Botao B_REMOVER = new Botao( "Remover", Tema.LARANJA );
    
    static {
        CAMPO.definir( 42 );
        B_INSERIR.atalho = "Enter";
        B_BUSCAR.atalho = "B";
        B_REMOVER.atalho = "Del";
    }
    
    private static void operacoes( EngineFrame e, double cx, double cy, double t ) {
        double x = cx - 215;
        double y = cy - 22;
        CAMPO.pos( x, y, 96, 44 ).desenhar( e, t, Tema.acento( TipoArvore.AVL ) );
        B_INSERIR.pos( x + 110, y, 104, 44 ).desenhar( e, t );
        B_BUSCAR.pos( x + 222, y, 96, 44 ).desenhar( e, t );
        B_REMOVER.pos( x + 326, y, 104, 44 ).desenhar( e, t );
        Desenho.textoCentro( e, "digite a chave e escolha a operação", cx, cy + 56, 14, Tema.TEXTO_SUAVE, false );
    }
    
    private static final Botao[] PLAYER = {
        new Botao( Botao.Icone.INICIO, Tema.acento( TipoArvore.AVL ) ),
        new Botao( Botao.Icone.ANTERIOR, Tema.acento( TipoArvore.AVL ) ),
        new Botao( Botao.Icone.PAUSA, Tema.acento( TipoArvore.AVL ) ),
        new Botao( Botao.Icone.PROXIMO, Tema.acento( TipoArvore.AVL ) ),
        new Botao( Botao.Icone.FIM, Tema.acento( TipoArvore.AVL ) ),
        new Botao( Botao.Icone.MENOS, Tema.acento( TipoArvore.AVL ) ),
        new Botao( Botao.Icone.MAIS, Tema.acento( TipoArvore.AVL ) )
    };
    
    private static void reproducao( EngineFrame e, double cx, double cy, double t ) {
        double[] larg = { 46, 46, 58, 46, 46, 38, 38 };
        double[] gap = { 6, 6, 6, 6, 18, 56, 0 };
        double total = 0;
        for ( int i = 0; i < larg.length; i++ ) {
            total += larg[i] + gap[i];
        }
        double x = cx - total / 2;
        for ( int i = 0; i < PLAYER.length; i++ ) {
            PLAYER[i].pos( x, cy - 40, larg[i], 44 ).desenhar( e, t );
            x += larg[i] + gap[i];
        }
        
        Tom[] tons = { Tom.INFO, Tom.INFO, Tom.INFO, Tom.SUCESSO, Tom.INFO, Tom.ALERTA, Tom.INFO, Tom.INFO, Tom.SUCESSO };
        double w = 340;
        double ty = cy + 40;
        int atual = (int) ( t * 1.2 ) % tons.length;
        e.setStrokeLineWidth( 3f );
        e.drawLine( cx - w / 2, ty, cx + w / 2, ty, new Color( 255, 255, 255, 50 ) );
        e.resetStrokeToDefault();
        for ( int i = 0; i < tons.length; i++ ) {
            double px = cx - w / 2 + i * w / ( tons.length - 1 );
            e.fillCircle( px, ty, i == atual ? 9 : 5, Tema.alfa( Tema.tom( tons[i] ), i <= atual ? 1 : 0.4 ) );
        }
        Desenho.textoCentro( e, "linha do tempo (clicável)", cx, ty + 24, 13, Tema.TEXTO_SUAVE, false );
    }
    
    private static void no( EngineFrame e, double x, double y, String chave, Color fundo, Color anel, double brilho, String legenda ) {
        Desenho.brilho( e, x, y, 20, anel, brilho );
        e.fillCircle( x, y, 20, fundo );
        e.setStrokeLineWidth( 3f );
        e.drawCircle( x, y, 20, anel );
        e.resetStrokeToDefault();
        Desenho.textoCentro( e, chave, x, y, 17, Tema.TEXTO, true );
        Desenho.textoCentro( e, legenda, x, y + 50, 12, Tema.TEXTO_SUAVE, false );
    }
    
    private static void legenda( EngineFrame e, double cx, double cy, double t ) {
        Color base = Tema.misturar( Tema.NO_BASE, Tema.acento( TipoArvore.ABB ), 0.38 );
        double p = 0.7 + 0.3 * Math.sin( t * 6 );
        double x0 = cx - 308;
        double dx = 88;
        double y = cy - 10;
        no( e, x0, y, "30", base, Tema.AMBAR, p, "Comparando" );
        no( e, x0 + dx, y, "40", base, Tema.VERDE, p, "Encontrado" );
        no( e, x0 + dx * 2, y, "20", base, Tema.CIANO, p, "Novo" );
        no( e, x0 + dx * 3, y, "50", base, Tema.ROXO, p, "Pivô" );
        no( e, x0 + dx * 4, y, "70", base, Tema.ALERTA, p, "Problema" );
        no( e, x0 + dx * 5, y, "35", Tema.NO_VERMELHO, Tema.NO_VERMELHO_ANEL, 0, "Link Verm." );
        no( e, x0 + dx * 6, y, "60", Tema.NO_PRETO, Tema.NO_PRETO_ANEL, 0, "Preto" );
        
        double sx = x0 + dx * 4;
        e.fillRoundRectangle( sx - 36, y + 24, 72, 18, 12, new Color( 0x10132E ) );
        Desenho.textoCentro( e, "h3  fb +2", sx, y + 33, 11, Tema.ALERTA, true );
    }
    
    private static final Botao B_INSERIR_DICA = new Botao( "Inserir", Tema.VERDE );
    
    static {
        B_INSERIR_DICA.atalho = "Enter";
        B_INSERIR_DICA.pulsar = true;
    }
    
    private static void templates( EngineFrame e, double cx, double cy, double t ) {
        double w = 340;
        double h = 96;
        double x = cx - 300;
        double y = cy - h / 2;
        Color verde = Tema.VERDE;
        e.fillRoundRectangle( x, y, w, h, 18, new Color( 0x232957 ) );
        e.setStrokeLineWidth( 1.8f );
        e.drawRoundRectangle( x, y, w, h, 18, Tema.alfa( verde, 0.6 ) );
        e.resetStrokeToDefault();
        Desenho.texto( e, "Caso LL: esquerda-esquerda", x + 16, y + 24, 15, Tema.TEXTO, true );
        e.fillRoundRectangle( x + w - 98, y + 14, 86, 20, 12, Tema.alfa( verde, 0.22 ) );
        Desenho.textoCentro( e, "INSERIR 10", x + w - 55, y + 24, 11, verde, true );
        Desenho.texto( e, "Inserir 10 deixa a raiz pesada à esquerda.", x + 16, y + 52, 13, Tema.TEXTO_SUAVE, false );
        Desenho.texto( e, "Solução: rotação simples à direita.", x + 16, y + 72, 13, Tema.TEXTO_SUAVE, false );
        
        double bx = cx + 130;
        double seta = Math.sin( t * 6 ) * 4;
        e.fillTriangle( cx + 62 + seta, cy - 12, cx + 62 + seta, cy + 12, cx + 86 + seta, cy, Tema.AMBAR );
        B_INSERIR_DICA.pos( bx, cy - 22, 120, 44 ).desenhar( e, t );
        Desenho.textoCentro( e, "botão sugerido", bx + 60, cy + 46, 13, Tema.TEXTO_SUAVE, false );
    }
    
    private static void tecla( EngineFrame e, double x, double y, String tecla, String texto ) {
        double w = Math.max( 34, Desenho.largura( e, tecla, 14, true ) + 20 );
        e.fillRoundRectangle( x, y + 3, w, 32, 9, new Color( 0, 0, 0, 90 ) );
        e.fillRoundRectangle( x, y, w, 32, 9, new Color( 0x2C3270 ) );
        e.setStrokeLineWidth( 1.6f );
        e.drawRoundRectangle( x, y, w, 32, 9, Tema.alfa( Tema.TEXTO, 0.45 ) );
        e.resetStrokeToDefault();
        Desenho.textoCentro( e, tecla, x + w / 2, y + 16, 14, Tema.TEXTO, true );
        Desenho.texto( e, texto, x + w + 10, y + 16, 14, Tema.TEXTO_SUAVE, false );
    }
    
    private static void atalhos( EngineFrame e, double cx, double cy, double t ) {
        String[][] itens = { 
            { "Enter", "Inserir" }, { "B", "Buscar" }, { "Del", "Remover" }, 
            { "Espaço", "Play / pausa" }, { "<  >", "Passo a passo" }, { "Esc", "Menu" } 
        };
        for ( int i = 0; i < itens.length; i++ ) {
            tecla( e, cx - 320 + ( i % 3 ) * 220, cy - 44 + ( i / 3 ) * 56, itens[i][0], itens[i][1] );
        }
    }
    
}