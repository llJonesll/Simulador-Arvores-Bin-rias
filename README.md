# Simulador de Árvores

Simulador visual de três árvores binárias de busca, feito com a [JSGE](https://github.com/davidbuzatto/JSGE).
Cada operação é executada de verdade e dividida em passos animados e explicados.

* **ABB**: Árvore Binária de Busca
* **AVL**: balanceamento por fator de balanceamento e rotações (LL, RR, LR, RL)
* **RN**: Árvore Vermelho-Preto (recoloração e rotações; casos 1 a 3 na inserção e 1 a 4 na remoção)

Em todas é possível inserir, buscar, remover e percorrer (em ordem, pré-ordem, pós-ordem e em largura).

## Requisitos e execução

* Projeto NetBeans (Ant) com a biblioteca **JSGE** (`JSGE-Latest`, já configurada no projeto) e Java 17 ou superior.
* Classe principal: `simulador.Main`. A janela abre com 1280x720 e pode ser redimensionada.
* Argumento de JVM do projeto: `--enable-native-access=ALL-UNNAMED`.

## Tutorial

O botão **?** no canto inferior direito (ou as teclas **F1** / **H**) abre um tutorial de 6 páginas dentro do
programa. O mesmo conteúdo, com exercícios sugeridos, está em [TUTORIAL.md](TUTORIAL.md).

## Como usar

No menu, escolha a árvore (mouse, setas + Enter, ou teclas 1, 2, 3). No simulador:

| Ação | Botão | Atalho |
|---|---|---|
| Inserir a chave digitada | Inserir | Enter |
| Buscar | Buscar | B |
| Remover | Remover | Del ou R |
| Inserir chave aleatória | Aleatório | A |
| Limpar | Limpar | C |
| Abrir os templates | Templates | T (1 a 8 escolhe) |
| Percursos | Em ordem, Pré, Pós, Largura | |
| Play / pausa | play | Espaço |
| Passo seguinte / anterior | passo seguinte / anterior | → / ← |
| Início / fim | início / fim | Home / End |
| Velocidade | + e - | |
| Auto ou passo a passo | Modo | M |
| Abrir o tutorial | ? (canto inferior direito) | F1 ou H |
| Voltar ao menu | Menu | Esc |

Digite a chave (0 a 999) no campo; ↑ e ↓ ajustam o valor e Backspace apaga. Clique na linha do
tempo do painel "Passo a passo" para saltar para qualquer passo.

## Entendendo o desenho

* **Brilhos:** amarelo = comparando, ciano = nó novo, verde = encontrado, roxo = pivô de rotação,
  vermelho = problema, laranja = nó a remover. A aresta amarela é o caminho percorrido.
* **Etiquetas** (z, pai, tio, avô, irmão, sobrinho, x) mostram o papel de cada nó no caso em análise.
* **AVL:** o selo sob cada nó mostra a altura (`h`) e o fator de balanceamento (`fb`); fica vermelho fora de -1 a +1.
* **Vermelho-Preto:** as cores dos nós são as da regra. O anel duplo marca o nó "duplo-preto" da remoção.

## Templates

Cada árvore tem cenários prontos. O template monta a árvore, preenche a chave
e faz o botão da operação sugerida pulsar.

* **ABB:** árvore equilibrada, pior caso (lista), zigue-zague, remover folha, nó com um filho, nó com dois filhos e percursos.
* **AVL:** casos LL, RR, LR e RL na inserção, rotação simples e dupla na remoção e sequência crescente.
* **Vermelho-Preto:** tio vermelho, tio preto em linha, tio preto em joelho, remover nó vermelho e os quatro casos de remoção.

Para criar o seu, adicione um `new Template(...)` em `Templates.java`.

## Estrutura

```
simulador/
  Main.java                 janela e troca de telas (com fade)
  modelo/                   algoritmos, sem depender da engine
    Arvore.java             busca, inserção, remoção, rotações (base das três)
    Gravador.java           grava cada passo como uma "foto" da árvore
    ABB.java  AVL.java  ArvoreRN.java
    Percurso.java           em ordem, pré, pós, largura
    Marcas.java             destaques e etiquetas de um passo
    Passo.java  NoVisual.java  No.java
    Template.java  Templates.java  TipoArvore.java
  ui/                       interface
    TelaMenu.java  CartaoArvore.java  FundoAnimado.java
    TelaSimulador.java      só orquestra os componentes abaixo
    Visualizador.java       desenha a árvore e anima a transição entre passos
    Reprodutor.java         auto / manual, velocidade, avançar e voltar
    BarraControles.java  BarraTopo.java  PainelPassos.java  PopupTemplates.java
    Ajuda.java              botão "?" e tutorial (vale para qualquer tela)
    BotaoAjuda.java  Tutorial.java  PaginaTutorial.java  PaginasTutorial.java
    Botao.java  CampoNumero.java  Aviso.java  Particulas.java  Fundo.java
    Desenho.java  Tema.java  Tela.java
```

## Como funciona

Cada operação roda o algoritmo de verdade sobre a árvore e, a cada etapa,
grava um `Passo` (foto completa + explicação). O `Visualizador` compara fotos
consecutivas pelo `id` dos nós e anima a diferença: nós deslizam nas rotações,
mudam de cor na recoloração, nascem do pai na inserção e somem na remoção.
Como o modelo não depende da engine, ele pode ser testado sozinho.

## O que vem da JSGE

Da JSGE vêm a janela e o laço do jogo (`create`, `update`, `draw`), a entrada de
mouse e teclado e as primitivas de desenho 2D. Botões, campo de texto, animações,
partículas, telas, layout e o tutorial são código deste projeto.

## Observações
* O limite é de 63 nós, para o desenho continuar legível.
