# Simulador de Árvores

Simulador visual de três árvores binárias, feito com a [JSGE](https://github.com/davidbuzatto/JSGE):

* **ABB**: Árvore Binária de Busca
* **AVL**: balanceamento por fator de balanceamento e rotações (LL, RR, LR, RL)
* **RN**: Árvore Vermelho-Preto (recoloração e rotações, casos 1 a 3 na inserção e 1 a 4 na remoção)

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
| Play / pausa | ▶ | Espaço |
| Passo seguinte / anterior | ▶\| / \|◀ | → / ← |
| Início / fim | ◀◀ / ▶▶ | Home / End |
| Auto ou passo a passo | Modo | M |
| Voltar ao menu | Menu | Esc |

Digite a chave (0 a 999) no campo; ↑ e ↓ ajustam o valor. Clique na linha do
tempo do painel "Passo a passo" para saltar para qualquer passo.

## Templates

Cada árvore tem cenários prontos. O template monta a árvore, preenche a chave
e faz o botão da operação sugerida pulsar. Exemplos: os quatro casos de
rotação da AVL (inserção e remoção), tio vermelho / tio preto em linha / tio
preto em joelho na Vermelho-Preto, os quatro casos de remoção da
Vermelho-Preto e os três tipos de remoção da ABB. Para criar o seu, adicione
um `new Template(...)` em `Templates.java`.

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
    Botao.java  CampoNumero.java  Aviso.java  Particulas.java  Fundo.java
    Desenho.java  Tema.java  Tela.java
```

## Como funciona

Cada operação roda o algoritmo de verdade sobre a árvore e, a cada etapa,
grava um `Passo` (foto completa + explicação). O `Visualizador` compara fotos
consecutivas pelo `id` dos nós e anima a diferença: nós deslizam nas rotações,
mudam de cor na recoloração, nascem do pai na inserção e somem na remoção.
Como o modelo não depende da engine, ele pode ser testado sozinho.
