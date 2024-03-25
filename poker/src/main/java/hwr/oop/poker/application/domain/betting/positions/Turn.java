package hwr.oop.poker.application.domain.betting.positions;

import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.cards.CommunityCards;
import hwr.oop.poker.application.domain.cards.CommunityCardsProvider;
import java.util.Objects;
import java.util.Optional;

class Turn implements RoundPosition {

  @Override
  public int position() {
    return 2;
  }

  @Override
  public boolean shouldCauseBurn() {
    return true;
  }

  @Override
  public CommunityCardsProvider buildCardsFor(Deck deck, CommunityCardsProvider currentCards) {
    return CommunityCards
        .flop(currentCards.flop().orElseThrow())
        .turn(deck.draw())
        .noRiver();
  }

  @Override
  public Optional<RoundPosition> nextPosition() {
    return Optional.of(RoundPosition.RIVER);
  }

  @Override
  public Optional<RoundPosition> previous() {
    return Optional.of(RoundPosition.FLOP);
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
