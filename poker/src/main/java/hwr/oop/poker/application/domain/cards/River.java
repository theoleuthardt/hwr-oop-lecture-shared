package hwr.oop.poker.application.domain.cards;

import hwr.oop.poker.application.domain.Card;
import java.util.stream.Stream;

public class River implements Card.Provider {

  private final Card card;

  public static River of(Card card) {
    return new River(card);
  }

  private River(Card card) {
    assertValidRiver(card);
    this.card = card;
  }

  private void assertValidRiver(Card card) {
    if (card == null) {
      throw new InvalidRiverException();
    }
  }

  @Override
  public Stream<Card> cards() {
    return Stream.of(card);
  }

  @Override
  public String toString() {
    return "River{" + card + '}';
  }

  private static class InvalidRiverException extends RuntimeException {

    public InvalidRiverException() {
      super("Cannot create river, requires card not to be null, but it was");
    }
  }
}
