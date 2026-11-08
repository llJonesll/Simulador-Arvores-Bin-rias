package simulador.ui;

import java.util.ArrayList;
import java.util.List;
import simulador.modelo.Passo.Tom;
import simulador.modelo.TipoArvore;

/**
 * Conteúdo do tutorial: as páginas e suas ilustrações.
 * 
 * @author Prof. Dr. David Buzatto
 */
public final class PaginasTutorial {
    
    private PaginasTutorial() {
    }
    
    public static List<PaginaTutorial> todas() {
        
        List<PaginaTutorial> l = new ArrayList<>();
        
        l.add( new PaginaTutorial( "Bem-vindo ao Simulador", PaginasTutorial::boasVindas, 
            "Veja, passo a passo, como funcionam três árvores de busca: ABB, AVL e Vermelho-Preto.", 
            "Cada operação é executada de verdade e dividida em passos animados e explicados.", 
            "Na tela inicial, toque no cartão da árvore que você quer estudar." ) );
        
        l.add( new PaginaTutorial( "Inserir, buscar e remover", PaginasTutorial::operacoes, 
            "Monte a chave (0 a 999) no teclado numérico da parte de baixo da tela.", 
            "Toque em Inserir, Buscar ou Remover. Aleatório insere uma chave sorteada e Limpar esvazia a árvore.", 
            "A seta com X apaga o último dígito." ) );
        
        l.add( new PaginaTutorial( "Acompanhe cada passo", PaginasTutorial::reproducao, 
            "Os passos tocam sozinhos. Use play para pausar e as setas para avançar ou voltar um passo.", 
            "O botão 1x troca a velocidade. Auto ou Manual define se os passos tocam sozinhos ou só quando você avançar.", 
            "Toque ou arraste na linha do tempo do painel para saltar para qualquer passo." ) );
        
        l.add( new PaginaTutorial( "Entenda o desenho", PaginasTutorial::legenda, 
            "O brilho indica o papel do nó no passo. As etiquetas (z, pai, tio, irmão...) mostram quem é quem.", 
            "AVL: o selo sob o nó mostra a altura (h) e o fator de balanceamento (fb); fora de -1 a +1 fica vermelho.", 
            "Vermelho-Preto: as cores são as da regra. O nó com anel duplo é o \"duplo-preto\" da remoção." ) );
        
        l.add( new PaginaTutorial( "Templates: casos prontos", PaginasTutorial::templates, 
            "Templates abre cenários para estudar: rotações da AVL, casos da Vermelho-Preto e remoções da ABB.", 
            "Escolha um cenário: a árvore é montada e a chave já vem preenchida.", 
            "O botão da operação sugerida pulsa: toque nele para ver o caso acontecer." ) );
        
        l.add( new PaginaTutorial( "Percursos e dicas", PaginasTutorial::dicas, 
            "Percursos mostra a ordem de visita (em ordem, pré, pós e largura) e a saída acumulada no painel.", 
            "O botão voltar do Android fecha janelas e volta ao menu. O botão ? abre este tutorial.", 
            "Dica: insira chaves crescentes na ABB e depois na AVL e compare as alturas." ) );
        
        return l;
        
    }
    
    //**************************************************************************
    // ilustrações
    //**************************************************************************
    
    private static void boasVindas( Pincel g, float cx, float cy, float t ) {
        TipoArvore[] tipos = TipoArvore.values();
        for ( int i = 0; i < tipos.length; i++ ) {
            int c = Tema.acento( tipos[i] );
            float x = cx + ( i - 1 ) * 100;
            float y = cy + (float) Math.sin( t * 2 + i ) * 4;
            g.circulo( x, y, 40, Tema.alfa( c, 0.10 ) );
            g.circulo( x, y, 30, Tema.misturar( Tema.NO_BASE, c, 0.4 ) );
            g.aro( x, y, 30, c, 2.6f );
            g.textoCentro( tipos[i].sigla, x, y, 19, Tema.TEXTO, true );
        }
    }
    
    private static final CampoNumero CAMPO = new CampoNumero();
    private static final Botao B_INSERIR = new Botao( "Inserir", Tema.VERDE, null );
    private static final Botao B_BUSCAR = new Botao( "Buscar", Tema.CIANO, null );
    private static final Botao B_REMOVER = new Botao( "Remover", Tema.LARANJA, null );
    
    static {
        CAMPO.definir( 42 );
        for ( Botao b : new Botao[] { B_INSERIR, B_BUSCAR, B_REMOVER } ) {
            b.tamTexto = 12.5f;
        }
    }
    
    private static void operacoes( Pincel g, float cx, float cy, float t ) {
        float x = cx - 160;
        float y = cy - 34;
        CAMPO.desenhar( g, x, y, 78, 40, t, Tema.acento( TipoArvore.AVL ) );
        B_INSERIR.pos( x + 84, y, 76, 40 ).desenhar( g, t );
        B_BUSCAR.pos( x + 166, y, 70, 40 ).desenhar( g, t );
        B_REMOVER.pos( x + 242, y, 78, 40 ).desenhar( g, t );
        
        // mini teclado numérico
        for ( int i = 0; i < 5; i++ ) {
            float bx = x + i * 66;
            g.retanguloArred( bx, y + 50, 60, 26, 8, Tema.misturar( Tema.BOTAO, Tema.LINHA, 0.2 ) );
            g.textoCentro( String.valueOf( i + 1 ), bx + 30, y + 63, 14, Tema.TEXTO, true );
        }
        g.textoCentro( "monte a chave e escolha a operação", cx, cy + 54, 11.5f, Tema.TEXTO_SUAVE, false );
    }
    
    private static final Botao[] PLAYER = {
        new Botao( Botao.Icone.INICIO, Tema.acento( TipoArvore.AVL ), null ),
        new Botao( Botao.Icone.ANTERIOR, Tema.acento( TipoArvore.AVL ), null ),
        new Botao( Botao.Icone.PAUSA, Tema.acento( TipoArvore.AVL ), null ),
        new Botao( Botao.Icone.PROXIMO, Tema.acento( TipoArvore.AVL ), null ),
        new Botao( Botao.Icone.FIM, Tema.acento( TipoArvore.AVL ), null ),
        new Botao( "1x", Tema.acento( TipoArvore.AVL ), null )
    };
    
    private static void reproducao( Pincel g, float cx, float cy, float t ) {
        float larg = 42;
        float gap = 6;
        float total = PLAYER.length * larg + ( PLAYER.length - 1 ) * gap;
        float x = cx - total / 2;
        for ( Botao b : PLAYER ) {
            b.pos( x, cy - 44, larg, 38 ).desenhar( g, t );
            x += larg + gap;
        }
        
        Tom[] tons = { Tom.INFO, Tom.INFO, Tom.INFO, Tom.SUCESSO, Tom.INFO, Tom.ALERTA, Tom.INFO, Tom.INFO, Tom.SUCESSO };
        float w = 280;
        float ty = cy + 22;
        int atual = (int) ( t * 1.2f ) % tons.length;
        g.linha( cx - w / 2, ty, cx + w / 2, ty, 0x32FFFFFF, 2.6f );
        for ( int i = 0; i < tons.length; i++ ) {
            float px = cx - w / 2 + i * w / ( tons.length - 1 );
            g.circulo( px, ty, i == atual ? 7 : 4, Tema.alfa( Tema.tom( tons[i] ), i <= atual ? 1 : 0.4 ) );
        }
        g.textoCentro( "linha do tempo (toque ou arraste)", cx, ty + 22, 11.5f, Tema.TEXTO_SUAVE, false );
    }
    
    private static void no( Pincel g, float x, float y, String chave, int fundo, int anel, double brilho, String legenda ) {
        g.brilho( x, y, 15, anel, brilho );
        g.circulo( x, y, 15, fundo );
        g.aro( x, y, 15, anel, 2.4f );
        g.textoCentro( chave, x, y, 13, Tema.TEXTO, true );
        g.textoCentro( legenda, x, y + 28, 10, Tema.TEXTO_SUAVE, false );
    }
    
    private static void legenda( Pincel g, float cx, float cy, float t ) {
        int base = Tema.misturar( Tema.NO_BASE, Tema.acento( TipoArvore.ABB ), 0.38 );
        double p = 0.7 + 0.3 * Math.sin( t * 6 );
        float dx = 76;
        float x0 = cx - dx * 1.5f;
        float y1 = cy - 36;
        float y2 = cy + 30;
        no( g, x0, y1, "30", base, Tema.AMBAR, p, "Comparando" );
        no( g, x0 + dx, y1, "40", base, Tema.VERDE, p, "Encontrado" );
        no( g, x0 + dx * 2, y1, "20", base, Tema.CIANO, p, "Novo" );
        no( g, x0 + dx * 3, y1, "50", base, Tema.ROXO, p, "Pivô" );
        no( g, x0, y2, "70", base, Tema.ALERTA, p, "Problema" );
        no( g, x0 + dx, y2, "35", Tema.NO_VERMELHO, Tema.NO_VERMELHO_ANEL, 0, "Vermelho" );
        no( g, x0 + dx * 2, y2, "60", Tema.NO_PRETO, Tema.NO_PRETO_ANEL, 0, "Preto" );
        
        // selo da AVL
        float sx = x0 + dx * 3;
        g.circulo( sx, y2, 15, base );
        g.aro( sx, y2, 15, Tema.misturar( Tema.acento( TipoArvore.ABB ), 0xFFFFFFFF, 0.35 ), 2.4f );
        g.textoCentro( "80", sx, y2, 13, Tema.TEXTO, true );
        g.retanguloArred( sx - 28, y2 + 19, 56, 14, 7, Tema.PAINEL_ESCURO );
        g.textoCentro( "h3 fb +2", sx, y2 + 26, 9.5f, Tema.ALERTA, true );
    }
    
    private static final Botao B_INSERIR_DICA = new Botao( "Inserir", Tema.VERDE, null );
    
    static {
        B_INSERIR_DICA.pulsar = true;
        B_INSERIR_DICA.tamTexto = 13;
    }
    
    private static void templates( Pincel g, float cx, float cy, float t ) {
        float w = 300;
        float h = 64;
        float x = cx - w / 2;
        float y = cy - 56;
        g.retanguloArred( x, y, w, h, 14, Tema.BOTAO );
        g.bordaArred( x, y, w, h, 14, Tema.alfa( Tema.VERDE, 0.6 ), 1.4f );
        g.textoEsq( "Caso LL: esquerda-esquerda", x + 12, y + 16, 13, Tema.TEXTO, true );
        g.retanguloArred( x + w - 72, y + 8, 62, 16, 8, Tema.alfa( Tema.VERDE, 0.22 ) );
        g.textoCentro( "INSERIR 10", x + w - 41, y + 16, 9, Tema.VERDE, true );
        g.textoEsq( "Inserir 10 deixa a raiz pesada à esquerda.", x + 12, y + 38, 11, Tema.TEXTO_SUAVE, false );
        g.textoEsq( "Solução: rotação simples à direita.", x + 12, y + 53, 11, Tema.TEXTO_SUAVE, false );
        
        float seta = (float) Math.sin( t * 6 ) * 3;
        g.triangulo( cx - 70 + seta, cy + 28, cx - 70 + seta, cy + 52, cx - 48 + seta, cy + 40, Tema.AMBAR );
        B_INSERIR_DICA.pos( cx - 36, cy + 22, 110, 36 ).desenhar( g, t );
    }
    
    private static void dicas( Pincel g, float cx, float cy, float t ) {
        
        // linha do tempo com um "toque" (círculo que se expande)
        float w = 260;
        float ty = cy - 20;
        g.linha( cx - w / 2, ty, cx + w / 2, ty, 0x32FFFFFF, 2.6f );
        for ( int i = 0; i < 7; i++ ) {
            g.circulo( cx - w / 2 + i * w / 6, ty, 4, Tema.alfa( Tema.tom( Tom.INFO ), 0.8 ) );
        }
        float fase = ( t * 0.8f ) % 1;
        float tx = cx - w / 2 + w * 0.65f;
        g.circulo( tx, ty, 6 + 22 * fase, Tema.alfa( Tema.AJUDA, 0.35 * ( 1 - fase ) ) );
        g.circulo( tx, ty, 8, Tema.alfa( Tema.AJUDA, 0.9 ) );
        
        // três "pílulas" de percurso
        String[] nomes = { "Em ordem", "Pré", "Pós", "Largura" };
        float bw = 66;
        float x = cx - ( nomes.length * bw + ( nomes.length - 1 ) * 6 ) / 2;
        for ( String n : nomes ) {
            g.retanguloArred( x, cy + 14, bw, 30, 10, Tema.misturar( Tema.BOTAO, Tema.TEAL, 0.3 ) );
            g.bordaArred( x, cy + 14, bw, 30, 10, Tema.alfa( Tema.TEAL, 0.7 ), 1.2f );
            g.textoCentro( n, x + bw / 2, cy + 29, 11.5f, Tema.TEXTO, true );
            x += bw + 6;
        }
    }
    
}
