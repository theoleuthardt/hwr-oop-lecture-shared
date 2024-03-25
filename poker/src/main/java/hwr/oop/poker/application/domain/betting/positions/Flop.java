package hwr.oop.poker.application.domain.betting.positions;

import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.cards.CommunityCards;
import hwr.oop.poker.application.domain.cards.CommunityCardsProvider;

import java.util.Objects;
import java.util.Optional;

class Flop implements RoundPosition {

    @Override
    public int position() {
        return 1;
    }

    @Override
    public boolean shouldCauseBurn() {
        return true;
    }

    @Override
    public CommunityCardsProvider buildCardsFor(Deck deck, CommunityCardsProvider currentCards) {
        return CommunityCards
                .flop(deck.draw(), deck.draw(), deck.draw())
                .noTurnNoRiver();
    }

    @Override
    public Optional<RoundPosition> nextPosition() {
        return Optional.of(RoundPosition.TURN);
    }

    @Override
    public Optional<RoundPosition> previous() {
        return Optional.of(RoundPosition.PRE_FLOP);
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
