package hwr.oop.poker.application.domain.cards;

import hwr.oop.poker.application.domain.Card;
import java.util.stream.Stream;

public class River implements Card.Provider {

  private final Card card;

  public static River of(Card card) {
    return new River(card);
  }

  private River(Card card) {
    assert card != null;
    this.card = card;
  }

  @Override
  public Stream<Card> cards() {
    return Stream.of(card);
  }

  @Override
  public String toString() {
    return "River{" + card + '}';
  }

}
