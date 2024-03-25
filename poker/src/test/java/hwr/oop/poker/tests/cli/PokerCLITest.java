package hwr.oop.poker.tests.cli;

import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.poker.application.CreateGameService;
import hwr.oop.poker.application.GameActionService;
import hwr.oop.poker.cli.CLIAdapter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PokerCLITest {

  private GameActionService gameActionUseCase;
  private CreateGameService createGameUseCase;

  @BeforeEach
  void setUp() {
    gameActionUseCase = new GameActionService();
    createGameUseCase = new CreateGameService();
  }

  @Test
  void cliAdapterStartUp_DisplaysWelcomeMessage() {
    // given
    final var outputStream = new ByteArrayOutputStream();
    final var inputStream = createInputStreamForInput("2\n");
    final var cliAdapter = new CLIAdapter(gameActionUseCase, createGameUseCase, inputStream,
        outputStream);
    // when
    cliAdapter.start();
    // then
    final var outputText = outputStream.toString();
    assertThat(outputText)
        .containsIgnoringCase("invalid input")
        .containsIgnoringCase("bye");
  }

  @Test
  void cliAdapterWrongInput_CLITerminates() {
    // given
    final var outputStream = new ByteArrayOutputStream();
    final var inputStream = createInputStreamForInput("\n");
    final var cliAdapter = new CLIAdapter(gameActionUseCase, createGameUseCase, inputStream,
        outputStream);
    // when
    cliAdapter.start();
    // then
    final var outputText = outputStream.toString();
    assertThat(outputText).contains("HWR OOP Poker App");
  }

  @Test
  void createNewGame_AddTwoPlayers_DisplaysNewGameCreated() {
    // given
    final var outputStream = new ByteArrayOutputStream();
    final var inputString = """
        1
        2
        Blablup
        10000
        Dubidub
        10000
        """;
    final var inputStream = createInputStreamForInput(inputString);
    final var cliAdapter = new CLIAdapter(gameActionUseCase, createGameUseCase, inputStream,
        outputStream);
    // when
    cliAdapter.start();
    // then
    final var outputText = outputStream.toString();
    assertThat(outputText).contains(
        "options:",
        "[1] Create a new Poker Game",
        "Now creating a new game!",
        "Enter number of players:",
        "Player #1, idString should be?", "Blablup",
        "Player #2, idString should be?", "Dubidub",
        "Game created, ID: "
    );
  }

  private InputStream createInputStreamForInput(String input) {
    byte[] inputInBytes = input.getBytes();
    return new ByteArrayInputStream(inputInBytes);
  }
}
