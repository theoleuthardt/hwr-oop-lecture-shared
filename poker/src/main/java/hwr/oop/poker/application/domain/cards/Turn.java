package hwr.oop.poker.application.domain.cards;

import hwr.oop.poker.application.domain.Card;
import java.util.stream.Stream;

public class Turn implements Card.Provider {

  private final Card card;

  public static Turn of(Card card) {
    return new Turn(card);
  }

  private Turn(Card card) {
    assert card != null;
    this.card = card;
  }

  @Override
  public Stream<Card> cards() {
    return Stream.of(card);
  }

  @Override
  public String toString() {
    return "Turn{" + card + '}';
  }

}
