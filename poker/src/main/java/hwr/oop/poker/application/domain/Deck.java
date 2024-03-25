package hwr.oop.poker.application.domain;

import java.util.ArrayList;
import java.util.List;

public interface Deck {

  boolean isEmpty();

  Card top();

  void burn();

  default Card draw() {
    if (isEmpty()) {
      throw new DrawFromEmptyDeckException("Cannot #draw if Deck #isEmpty");
    }
    final Card card = top();
    burn();
    return card;
  }

  default List<Card> drawAllCards() {
    List<Card> cards = new ArrayList<>();
    while (!isEmpty()) {
      Card card = draw();
      cards.add(card);
    }
    return cards;
  }

  Deck copy();

  class DrawFromEmptyDeckException extends RuntimeException {

    public DrawFromEmptyDeckException(String message) {
      super(message);
    }
  }
}
