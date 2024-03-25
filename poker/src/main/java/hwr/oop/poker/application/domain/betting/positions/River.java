package hwr.oop.poker.application.domain.betting.positions;

import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.cards.CommunityCards;
import hwr.oop.poker.application.domain.cards.CommunityCardsProvider;
import java.util.Objects;
import java.util.Optional;

class River implements RoundPosition {

  @Override
  public int position() {
    return 3;
  }

  @Override
  public boolean shouldCauseBurn() {
    return true;
  }

  @Override
  public CommunityCardsProvider buildCardsFor(Deck deck, CommunityCardsProvider currentCards) {
    return CommunityCards
        .flop(currentCards.flop().orElseThrow())
        .turn(currentCards.turn().orElseThrow())
        .river(deck.draw());
  }

  @Override
  public Optional<RoundPosition> nextPosition() {
    return Optional.empty();
  }

  @Override
  public Optional<RoundPosition> previous() {
    return Optional.of(RoundPosition.TURN);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RoundPosition roundPosition)) {
      return false;
    }
    return position() == roundPosition.position();
  }

  @Override
  public int hashCode() {
    return Objects.hash(position());
  }
}
