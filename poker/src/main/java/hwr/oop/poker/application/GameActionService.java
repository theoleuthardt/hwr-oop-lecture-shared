package hwr.oop.poker.application;

import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;
import hwr.oop.poker.application.domain.Player;
import hwr.oop.poker.application.domain.betting.BettingRound;
import hwr.oop.poker.application.domain.betting.Play.Type;
import hwr.oop.poker.application.ports.in.GameActionUseCase;
import hwr.oop.poker.application.ports.out.LoadHandPort;
import hwr.oop.poker.application.ports.out.SaveHandPort;

public class GameActionService implements GameActionUseCase {

  private final LoadHandPort loadHandPort;
  private final SaveHandPort saveHandPort;

  public GameActionService(LoadHandPort loadHandPort, SaveHandPort saveHandPort) {
    this.loadHandPort = loadHandPort;
    this.saveHandPort = saveHandPort;
  }

  @Override
  public void gameAction(GameActionCommand command) {
    final var handId = convertHandId(command);
    final Hand hand = loadHandPort.loadById(handId);
    final Hand updated = hand.onCurrentRound(r -> play(r, command));
    saveHandPort.saveHand(handId, updated);
  }

  private Player convertPlayer(GameActionCommand command) {
    return new Player(command.playerId());
  }

  private HandId convertHandId(GameActionCommand command) {
    return new HandId(command.handId());
  }

  private BettingRound play(BettingRound round, GameActionCommand command) {
    final var intermediate = round.with(convertPlayer(command));
    final var type = Type.valueOf(command.playType());
    return switch (type) {
      case BET -> intermediate.bet(command.targetChipValue());
      case FOLD -> intermediate.fold();
      case RAISE -> intermediate.raiseTo(command.targetChipValue());
      case CHECK -> intermediate.check();
      case CALL -> intermediate.call();
      default -> throw new IllegalArgumentException("Invalid Play!");
    };
  }
}
