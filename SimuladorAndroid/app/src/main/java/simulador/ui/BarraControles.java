package simulador.ui;

import simulador.modelo.Template.Operacao;

/**
 * Painel inferior: reprodução dos passos, campo da chave, operações,
 * teclado numérico e atalhos (aleatório, limpar, templates, percursos).
 * Não executa nada: devolve a {@link Acao} pedida pela pessoa.
 * 
 * O botão "?" do tutorial fica sobre o canto inferior direito (reservado
 * aqui na última linha).
 * 
 * @author Prof. Dr. David Buzatto
 */
public class BarraControles {
    
    public enum Acao {
        NENHUMA, TECLA, INSERIR, BUSCAR, REMOVER, ALEATORIO, LIMPAR, TEMPLATES, PERCURSOS,
        INICIO, ANTERIOR, PLAY, PROXIMO, FIM, VELOCIDADE, MODO
    }
    
    /** Altura normal e altura em telas baixas (modo compacto). */
    public static final float ALTURA = 256;
    public static final float ALTURA_COMPACTA = 232;
    
    /** @return a altura da barra para uma tela de {@code alturaTela} dp. */
    public static float alturaPara( float alturaTela ) {
        return alturaTela < 720 ? ALTURA_COMPACTA : ALTURA;
    }
    
    private static final String APAGAR = "apagar";
    private static final float PAD = 8;
    private static final float GAP = 6;
    
    public final CampoNumero campo = new CampoNumero();
    
    private final int acento;
    private final GrupoBotoes grupo = new GrupoBotoes();
    private final Botao bInicio, bAnterior, bPlay, bProximo, bFim, bVelocidade, bModo;
    private final Botao bApagar, bInserir, bBuscar, bRemover;
    private final Botao bAleatorio, bLimpar, bTemplates, bPercursos;
    private final Botao[] digitos = new Botao[10];
    
    private float x, y, w;
    private float altura = ALTURA;
    private float campoX, campoY, campoW, campoH;
    
    public BarraControles( int acento ) {
        
        this.acento = acento;
        
        bInicio = grupo.add( new Botao( Botao.Icone.INICIO, acento, Acao.INICIO ) );
        bAnterior = grupo.add( new Botao( Botao.Icone.ANTERIOR, acento, Acao.ANTERIOR ) );
        bPlay = grupo.add( new Botao( Botao.Icone.PLAY, acento, Acao.PLAY ) );
        bProximo = grupo.add( new Botao( Botao.Icone.PROXIMO, acento, Acao.PROXIMO ) );
        bFim = grupo.add( new Botao( Botao.Icone.FIM, acento, Acao.FIM ) );
        bVelocidade = grupo.add( new Botao( "1x", acento, Acao.VELOCIDADE ) );
        bModo = grupo.add( new Botao( "Auto", acento, Acao.MODO ) );
        bModo.tamTexto = 12.5f;
        
        bApagar = grupo.add( new Botao( Botao.Icone.APAGAR, Tema.LARANJA, APAGAR ) );
        bInserir = grupo.add( new Botao( "Inserir", Tema.VERDE, Acao.INSERIR ) );
        bBuscar = grupo.add( new Botao( "Buscar", Tema.CIANO, Acao.BUSCAR ) );
        bRemover = grupo.add( new Botao( "Remover", Tema.LARANJA, Acao.REMOVER ) );
        
        for ( int d = 0; d < 10; d++ ) {
            digitos[d] = grupo.add( new Botao( String.valueOf( d ), Tema.LINHA, Integer.valueOf( d ) ) );
            digitos[d].tamTexto = 18;
            digitos[d].raio = 10;
        }
        
        bAleatorio = grupo.add( new Botao( "Aleatório", Tema.ROXO, Acao.ALEATORIO ) );
        bLimpar = grupo.add( new Botao( "Limpar", Tema.TEXTO_SUAVE, Acao.LIMPAR ) );
        bTemplates = grupo.add( new Botao( "Templates", Tema.AMBAR, Acao.TEMPLATES ) );
        bPercursos = grupo.add( new Botao( "Percursos", Tema.TEAL, Acao.PERCURSOS ) );
        
        for ( Botao b : new Botao[] { bInserir, bBuscar, bRemover, bAleatorio, bLimpar, bTemplates, bPercursos } ) {
            b.tamTexto = 13;
        }
        
    }
    
    //**************************************************************************
    // layout
    //**************************************************************************
    
    /** Divide a largura em colunas proporcionais aos pesos: devolve {xs, larguras}. */
    private float[][] dividir( float x0, float larg, float... pesos ) {
        float soma = 0;
        for ( float p : pesos ) {
            soma += p;
        }
        float unidade = ( larg - GAP * ( pesos.length - 1 ) ) / soma;
        float[][] r = new float[2][pesos.length];
        float cx = x0;
        for ( int i = 0; i < pesos.length; i++ ) {
            r[0][i] = cx;
            r[1][i] = pesos[i] * unidade;
            cx += r[1][i] + GAP;
        }
        return r;
    }
    
    public void posicionar( float x, float y, float w, float alturaTela ) {
        
        this.x = x;
        this.y = y;
        this.w = w;
        this.altura = alturaPara( alturaTela );
        
        boolean compacto = altura < ALTURA;
        float h1 = compacto ? 38 : 44;
        float h2 = compacto ? 42 : 48;
        float h3 = compacto ? 32 : 38;
        float h5 = 44; // o botão "?" tem 44 dp
        
        float ix = x + PAD;
        float iw = w - 2 * PAD;
        float y1 = y + PAD;
        float y2 = y1 + h1 + GAP;
        float y3 = y2 + h2 + GAP;
        float y4 = y3 + h3 + 4;
        float y5 = y4 + h3 + GAP;
        
        // linha 1: reprodução
        float[][] c = dividir( ix, iw, 1, 1, 1.3f, 1, 1, 1, 1.3f );
        Botao[] l1 = { bInicio, bAnterior, bPlay, bProximo, bFim, bVelocidade, bModo };
        for ( int i = 0; i < l1.length; i++ ) {
            l1[i].pos( c[0][i], y1, c[1][i], h1 );
        }
        
        // linha 2: campo, apagar e operações
        c = dividir( ix, iw, 1.7f, 1, 1.5f, 1.35f, 1.5f );
        campoX = c[0][0];
        campoY = y2;
        campoW = c[1][0];
        campoH = h2;
        bApagar.pos( c[0][1], y2, c[1][1], h2 );
        bInserir.pos( c[0][2], y2, c[1][2], h2 );
        bBuscar.pos( c[0][3], y2, c[1][3], h2 );
        bRemover.pos( c[0][4], y2, c[1][4], h2 );
        
        // linhas 3 e 4: teclado numérico (1..5 e 6..9,0)
        c = dividir( ix, iw, 1, 1, 1, 1, 1 );
        for ( int i = 0; i < 5; i++ ) {
            digitos[i + 1].pos( c[0][i], y3, c[1][i], h3 );
            digitos[( i + 6 ) % 10].pos( c[0][i], y4, c[1][i], h3 );
        }
        
        // linha 5: atalhos (o último espaço é do botão "?")
        c = dividir( ix, iw - BotaoAjuda.TAMANHO - GAP, 1, 1, 1, 1 );
        bAleatorio.pos( c[0][0], y5, c[1][0], h5 );
        bLimpar.pos( c[0][1], y5, c[1][1], h5 );
        bTemplates.pos( c[0][2], y5, c[1][2], h5 );
        bPercursos.pos( c[0][3], y5, c[1][3], h5 );
        
    }
    
    //**************************************************************************
    // atualização, toque e desenho
    //**************************************************************************
    
    public void atualizar( float dt, Reprodutor rep, Operacao dica ) {
        
        bInserir.pulsar = dica == Operacao.INSERIR;
        bBuscar.pulsar = dica == Operacao.BUSCAR;
        bRemover.pulsar = dica == Operacao.REMOVER;
        
        bPlay.icone = rep.tocando() ? Botao.Icone.PAUSA : Botao.Icone.PLAY;
        bPlay.ativo = rep.total() > 1;
        bInicio.ativo = bAnterior.ativo = !rep.noInicio();
        bProximo.ativo = bFim.ativo = !rep.noFim();
        bVelocidade.texto = rep.velocidadeTexto();
        bModo.texto = rep.automatico() ? "Auto" : "Manual";
        
        grupo.atualizar( dt );
        campo.atualizar( dt );
        
    }
    
    /** @return a ação pedida neste toque (dígitos e apagar são tratados aqui e devolvem TECLA). */
    public Acao toque( int acao, float px, float py ) {
        
        Botao b = grupo.toque( acao, px, py );
        
        if ( b == null ) {
            return Acao.NENHUMA;
        }
        if ( b.id instanceof Integer ) {
            campo.digitar( (Integer) b.id );
            return Acao.TECLA;
        }
        if ( b.id == APAGAR ) {
            campo.apagar();
            return Acao.TECLA;
        }
        
        return (Acao) b.id;
        
    }
    
    public void desenhar( Pincel g, float tempo ) {
        g.retanguloArred( x, y, w, altura - 6, 18, Tema.PAINEL );
        g.bordaArred( x, y, w, altura - 6, 18, Tema.BORDA, 1.2f );
        campo.desenhar( g, campoX, campoY, campoW, campoH, tempo, acento );
        grupo.desenhar( g, tempo );
    }
    
}
