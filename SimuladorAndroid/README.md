# Simulador de Árvores para Android

Versão Android do simulador de **ABB**, **AVL** e **Vermelho-Preto**. É um projeto do Android Studio em
**Java**, compatível com **JDK 17**, sem nenhuma biblioteca externa (nem AndroidX) e sem a JSGE.

## Como abrir

1. Android Studio (Koala 2024.1 ou mais novo), com o **Gradle JDK = 17** (Settings > Build > Gradle).
2. *File > Open* e escolha a pasta `SimuladorAndroid`.
3. Aguarde o *Sync* (ele baixa o Gradle 8.7 e o Android Gradle Plugin 8.5.2; o SDK 34 pode ser instalado pelo próprio Studio).
4. Rode no emulador ou em um celular (*Run > app*).

| Item | Valor |
|---|---|
| Java | 17 (`sourceCompatibility` / `targetCompatibility` = `VERSION_17`) |
| Android Gradle Plugin | 8.5.2 (Gradle 8.7) |
| compileSdk / targetSdk | 34 |
| minSdk | 24 (Android 7.0) |
| applicationId | `br.com.davidbuzatto.simuladorarvores` |
| Orientação | retrato |

Se o Studio sugerir atualizar o plugin ou o Gradle, pode aceitar. O arquivo `gradle-wrapper.jar` não vem no
projeto (é binário); o Studio funciona sem ele. Para usar `./gradlew` no terminal, rode `gradle wrapper` uma vez.

## O que mudou em relação à versão desktop

* **Modelo idêntico:** `simulador/modelo` é a mesma pasta da versão desktop (Java puro: algoritmos, passos, templates).
* **Interface reescrita** em um único `View` que desenha em um `Canvas`. Todo o layout usa **dp**: o `Pincel`
  escala o canvas pela densidade da tela, e só ele conhece o `android.graphics`.
* **Toque no lugar de mouse e teclado:**
  * teclado numérico na tela (0 a 9 e apagar) no lugar do campo digitado;
  * toque em Inserir, Buscar e Remover; vibração curta a cada toque;
  * linha do tempo do painel: toque ou arraste para saltar entre passos;
  * Templates e Percursos abrem uma lista que sobe da parte de baixo (role arrastando);
  * tutorial em páginas: deslize para os lados, toque nos pontos ou nos botões;
  * o botão **voltar** do Android fecha popups, volta ao menu e, no menu, sai do app.
* O botão **?** (tutorial) continua no canto inferior direito.
* Em telas baixas (menos de 720 dp) a barra de controles e o painel ficam mais compactos.

## Estrutura

```
app/src/main/
  AndroidManifest.xml
  res/                          tema escuro, ícone (vetorial) e strings
  java/simulador/
    MainActivity.java           hospeda a View e repassa o botão voltar
    ArvoreView.java             laço de animação, desenho e toques (converte px em dp)
    modelo/                     algoritmos (iguais aos da versão desktop)
    ui/
      Aplicativo.java           núcleo sem Android: telas, fade, ajuda, tamanho em dp
      Pincel.java               único ponto que usa android.graphics
      TelaMenu  CartaoArvore  FundoAnimado
      TelaSimulador             só orquestra os componentes abaixo
      Visualizador  Reprodutor  PainelPassos  BarraTopo  BarraControles
      PopupLista                lista rolável (templates e percursos)
      Ajuda  BotaoAjuda  Tutorial  PaginaTutorial  PaginasTutorial
      Botao  GrupoBotoes  CampoNumero  Aviso  Particulas  Fundo
      Tema  Tela  Toque  Util
```

Como o `Aplicativo` não depende do Android, dá para testar a lógica e o layout fora do aparelho.

## Uso rápido

Na tela inicial, toque na árvore desejada. No simulador, monte a chave no teclado e toque em **Inserir**,
**Buscar** ou **Remover**. **Aleatório** insere uma chave sorteada, **Templates** abre cenários prontos e
**Percursos** mostra as quatro ordens de visita. O botão **?** abre o tutorial completo.

## Observações

* O limite é de 63 nós, para o desenho continuar legível.
* O app mantém a tela ligada enquanto está aberto (`FLAG_KEEP_SCREEN_ON`); remova a linha em `MainActivity` se não quiser.
* O tamanho do texto do sistema (fonte grande) não altera o app: os textos são desenhados em tamanho fixo em dp.
