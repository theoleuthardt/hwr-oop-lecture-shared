package hwr.oop.poker.application.ports.in;

public interface GameActionUseCase {


  static GameActionCommandBuilder newCommandBuilder() {
    return new GameActionCommandBuilder();
  }

  void gameAction(GameActionCommand command);

  record GameActionCommand(String handId, String playerId, String playType, int targetChipValue) {
    // nothing to do here
  }

  class GameActionCommandBuilder {

    private String handId;
    private String playerId;
    private String playType;
    private int targetChipValue;

    private GameActionCommandBuilder() {
    }

    public GameActionCommandBuilder playerId(String playerId) {
      this.playerId = playerId;
      return this;
    }

    public GameActionCommandBuilder type(String playType) {
      this.playType = playType;
      return this;
    }

    public GameActionCommandBuilder toChips(int targetChipValue) {
      this.targetChipValue = targetChipValue;
      return this;
    }

    public GameActionCommandBuilder handId(String handId) {
      this.handId = handId;
      return this;
    }

    public GameActionCommand build() {
      return new GameActionCommand(handId, playerId, playType, targetChipValue);
    }
  }
}
