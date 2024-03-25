package hwr.oop.poker.application.domain.decks;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Color;
import hwr.oop.poker.application.domain.Deck;
import hwr.oop.poker.application.domain.Symbol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomDeck implements Deck {

  private final List<Card> cards;

  public RandomDeck() {
    this.cards = new ArrayList<>();
    for (Color color : Color.values()) {
      for (Symbol symbol : Symbol.values()) {
        final Card card = new Card(color, symbol);
        this.cards.add(card);
      }
    }
    Collections.shuffle(cards);
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
