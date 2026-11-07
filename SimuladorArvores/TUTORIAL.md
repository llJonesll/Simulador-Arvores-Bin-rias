# Tutorial de uso

Este é o mesmo conteúdo do tutorial dentro do programa (botão **?** no canto
inferior direito, ou as teclas **F1** / **H**).

## 1. Escolhendo a árvore

No menu principal escolha com o mouse, com as setas + Enter ou com as teclas
**1** (ABB), **2** (AVL) e **3** (Vermelho-Preto). O botão **Menu** (ou **Esc**)
volta para esta tela.

| Árvore | Ideia | O que observar |
|---|---|---|
| ABB | menores à esquerda, maiores à direita | pode virar uma lista se as chaves chegarem em ordem |
| AVL | fator de balanceamento (fb) entre -1 e +1 | rotações simples e duplas (LL, RR, LR, RL) |
| Vermelho-Preto | regras de cor | recoloração e rotações; "duplo-preto" na remoção |

## 2. Inserir, buscar e remover

1. Digite uma chave de **0 a 999** no campo (sempre pronto para receber o teclado).
2. Escolha a operação:
   * **Inserir** (Enter)
   * **Buscar** (B)
   * **Remover** (Del ou R)
   * **Aleatório** (A) insere uma chave sorteada que ainda não existe
3. **↑** e **↓** ajustam o valor do campo; **Backspace** apaga.
4. **Limpar** (C) esvazia a árvore.

Não é possível ter chaves repetidas, e o limite é de 63 nós.

## 3. Acompanhando os passos

Cada operação vira uma sequência de passos, explicados no painel
**Passo a passo** à direita.

* **Espaço** (ou o botão play) pausa e retoma; **→** avança um passo; **←** volta;
  **Home** e **End** vão ao primeiro e ao último passo.
* **+** e **-** mudam a velocidade (0.5x a 3x).
* **Modo: Manual** (M) faz a operação começar parada, para você avançar quando quiser.
* A **linha do tempo** mostra todos os passos (azul = informação, verde = sucesso,
  amarelo = alerta). Clique nela para saltar para qualquer passo.

## 4. Entendendo o desenho

| Visual | Significado |
|---|---|
| Brilho amarelo | nó sendo comparado |
| Aresta amarela | caminho percorrido |
| Verde | encontrado (ou altura inalterada na AVL) |
| Ciano | nó novo |
| Roxo | pivô de rotação |
| Vermelho brilhante | problema (desbalanceamento ou dois vermelhos seguidos) |
| Laranja | nó que será removido |
| Etiquetas z, pai, tio, avô, irmão, sobrinho, x | papel do nó no caso em análise |

**AVL:** o selo sob cada nó mostra `h` (altura) e `fb` (altura da esquerda menos
altura da direita). Fica vermelho quando sai de -1 a +1.

**Vermelho-Preto:** as cores dos nós são as cores da regra. Um nó com anel duplo
é o "duplo-preto": ele carrega um "preto extra" que precisa ser resolvido antes
de ser removido.

## 5. Templates

O botão **Templates** (T) abre cenários prontos. Escolha com o clique ou com
as teclas **1** a **8**: a árvore é montada, a chave é preenchida e o botão da
operação sugerida pulsa. Basta clicar nele (ou teclar Enter).

* **ABB:** árvore equilibrada, pior caso (lista), zigue-zague, remover folha,
  remover nó com um filho, remover nó com dois filhos, percursos.
* **AVL:** casos LL, RR, LR e RL na inserção; rotação simples e dupla na
  remoção; sequência crescente.
* **Vermelho-Preto:** tio vermelho, tio preto em linha, tio preto em joelho;
  remover nó vermelho; remoção com sobrinho de fora vermelho, sobrinho de
  dentro vermelho, irmão vermelho e sem sobrinhos vermelhos.

## 6. Percursos

**Em ordem**, **Pré**, **Pós** e **Largura** visitam os nós um a um e mostram a
saída acumulada no painel. Em uma ABB o percurso em ordem produz as chaves em
ordem crescente.

## 7. Exercícios sugeridos

1. Na ABB, insira 10, 20, 30, 40, 50 e depois busque 50. Quantas comparações?
2. Na AVL, insira a mesma sequência. Compare a altura com a da ABB.
3. Na AVL, abra os templates LL, RR, LR e RL e preveja a rotação antes de inserir.
4. Na Vermelho-Preto, insira 1 a 10 em ordem e observe cada recoloração.
5. Na ABB, remova uma raiz com dois filhos e identifique o sucessor.

## Atalhos

| Tecla | Ação |
|---|---|
| Enter | inserir |
| B | buscar |
| Del / R | remover |
| A | aleatório |
| C | limpar |
| T | templates |
| Espaço | play / pausa |
| ← / → | passo anterior / seguinte |
| Home / End | primeiro / último passo |
| M | modo auto / manual |
| F1 / H | tutorial |
| Esc | voltar / fechar |
