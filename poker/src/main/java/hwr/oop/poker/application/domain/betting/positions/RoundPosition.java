package hwr.oop.poker.application.domain.betting.positions;


import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.cards.CommunityCardsProvider;
import java.util.Optional;

public interface RoundPosition {

  RoundPosition PRE_FLOP = new PreFlop();
  RoundPosition FLOP = new Flop();
  RoundPosition TURN = new Turn();
  RoundPosition RIVER = new River();

  int position();

  boolean shouldCauseBurn();

  CommunityCardsProvider buildCardsFor(Deck deck, CommunityCardsProvider currentCards);

  Optional<RoundPosition> nextPosition();

  Optional<RoundPosition> previous();

  default void ifRequiresBurn(Runnable runnable) {
    if (shouldCauseBurn()) {
      runnable.run();
    }
  }

  default RoundPosition latest(RoundPosition other) {
    if (position() > other.position()) {
      return this;
    } else {
      return other;
    }
  }
}
