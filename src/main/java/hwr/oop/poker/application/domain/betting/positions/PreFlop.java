package hwr.oop.poker.application.domain.betting.positions;

import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.cards.CommunityCards;
import hwr.oop.poker.application.domain.cards.CommunityCardsProvider;

import java.util.Objects;
import java.util.Optional;

class PreFlop implements RoundPosition {

    @Override
    public int position() {
        return 0;
    }

    @Override
    public boolean shouldCauseBurn() {
        return false;
    }

    @Override
    public CommunityCardsProvider buildCardsFor(Deck deck, CommunityCardsProvider currentCards) {
        return CommunityCards.empty();
    }

    @Override
    public Optional<RoundPosition> nextPosition() {
        return Optional.of(RoundPosition.FLOP);
    }

    @Override
    public Optional<RoundPosition> previous() {
        return Optional.empty();
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
