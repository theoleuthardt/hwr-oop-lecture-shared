package hwr.oop.poker.cli;

import hwr.oop.poker.application.ports.in.CreateGameUseCase;
import hwr.oop.poker.application.ports.in.GameActionUseCase;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CLIAdapter {

  private final GameActionUseCase gameActionUseCase;
  private final CreateGameUseCase createGameUseCase;
  private final Scanner scanner;
  private final PrintStream printStream;

  public CLIAdapter(GameActionUseCase gameActionUseCase, CreateGameUseCase createGameUseCase,
      InputStream inputStream, OutputStream outputStream) {
    this.gameActionUseCase = gameActionUseCase;
    this.createGameUseCase = createGameUseCase;
    this.scanner = new Scanner(inputStream);
    this.printStream = new PrintStream(outputStream);
  }

  public void start() {
    displayMainMenu();
    final var nextLine = scanner.nextLine();
    if (nextLine.equals("1")) {
      createNewGameDialog();
    } else {
      printStream.println("Invalid input, bye!");
    }
  }

  private void createNewGameDialog() {
    printStream.println("Now creating a new game!");
    printStream.println("Enter number of players:");
    final int numberOfPlayers = scanner.nextInt();
    final List<String> playerIds = new ArrayList<>();
    final List<String> stackSizes = new ArrayList<>();
    for (int i = 1; i <= numberOfPlayers; i++) {
      printStream.println("Player #" + i + ", idString should be?");
      final String playerId = forceNextLine();
      playerIds.add(playerId);
      printStream.println(" Stack size of Player " + playerId + " should be?");
      final String stackSize = forceNextLine();
      stackSizes.add(stackSize);
    }
    printStream.println("Game created, ID: " + UUID.randomUUID());
  }

  private String forceNextLine() {
    while (true) {
      final String nextLine = scanner.nextLine();
      if (!nextLine.isBlank()) {
        return nextLine;
      }
    }
  }

  private void displayMainMenu() {
    printStream.println("HWR OOP Poker App");
    printStream.println("------------------------------------------");
    printStream.println("| options:                               |");
    printStream.println("|  [1] Create a new Poker Game           |");
    printStream.println("------------------------------------------");
  }
}
