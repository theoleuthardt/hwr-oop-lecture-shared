package hwr.oop.poker.application.ports.in;

import java.util.Map;
import java.util.Objects;

public interface CreateGameUseCase {

  static CreateGameCommandBuilder newCommandBuilder() {
    return new CreateGameCommandBuilder();
  }

  void createGame(CreateGameCommand command);

  class CreateGameCommandBuilder {

    private String gameId;
    private Map<String, Integer> playersAndStacks;
    private int smallBlind;

    private CreateGameCommandBuilder() {
      // nothing to do here
    }

    public CreateGameCommandBuilder playersAndStacks(Map<String, Integer> playersAndStacks) {
      this.playersAndStacks = playersAndStacks;
      return this;
    }

    public CreateGameCommandBuilder smallBlind(int smallBlind) {
      this.smallBlind = smallBlind;
      return this;
    }

    public CreateGameCommand build() {
      return new CreateGameCommand(gameId, playersAndStacks, smallBlind);
    }

    public CreateGameCommandBuilder gameId(String gameId) {
      this.gameId = gameId;
      return this;
    }
  }

  record CreateGameCommand(
      String gameId,
      Map<String, Integer> playersAndStacks,
      int smallBlind
  ) {

    public CreateGameCommand {
      Objects.requireNonNull(gameId);
      Objects.requireNonNull(playersAndStacks);
    }
  }

}
