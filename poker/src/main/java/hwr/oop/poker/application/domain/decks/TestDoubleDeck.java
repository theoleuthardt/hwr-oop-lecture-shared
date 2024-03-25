package hwr.oop.poker.application.domain.decks;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Deck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDoubleDeck implements Deck {
    private final List<Card> cards;

    public TestDoubleDeck(Card... cards) {
        this(new ArrayList<>(Arrays.asList(cards)));
    }

    public TestDoubleDeck(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public Card top() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot peek at top Card if Deck is empty");
        }
        return cards.get(0);
    }

    @Override
    public void burn() {
        if (isEmpty()) {
            throw new DrawFromEmptyDeckException("Cannot burn card if Deck is empty");
        }
        cards.remove(0);
    }
}
