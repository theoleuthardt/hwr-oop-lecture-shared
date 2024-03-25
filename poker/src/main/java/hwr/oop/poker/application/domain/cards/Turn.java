package hwr.oop.poker.application.domain.cards;

import hwr.oop.poker.application.domain.Card;

import java.util.stream.Stream;

public class Turn implements Card.Provider {

    private final Card card;

    public static Turn of(Card card) {
        return new Turn(card);
    }

    private Turn(Card card) {
        assertValidTurn(card);
        this.card = card;
    }

    private void assertValidTurn(Card card) {
        if (card == null) {
            throw new InvalidTurnException();
        }
    }

    @Override
    public Stream<Card> cards() {
        return Stream.of(card);
    }

    @Override
    public String toString() {
        return "Turn{" + card + '}';
    }

    private static class InvalidTurnException extends RuntimeException {
        public InvalidTurnException() {
            super("Cannot create turn, requires card not to be null, but it was");
        }
    }
}
