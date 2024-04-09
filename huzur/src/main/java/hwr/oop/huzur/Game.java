package hwr.oop.huzur;

import java.util.Comparator;
import java.util.Objects;

public class Game {

  private final Color trump;

  public Game(Color trump) {
    this.trump = trump;
    Objects.requireNonNull(trump);
  }

  public boolean isTrump(Card card) {
    return card.isAlwaysTrump() || card.hasColor(trump);
  }

  public Comparator<Card> strenghtComparator() {
    return new GameCardComparator(this);
  }

  private record GameCardComparator(Game game) implements Comparator<Card> {

    @Override
    public int compare(Card first, Card second) {
      if (game.isTrump(first) && game.isTrump(second)) {
        return Integer.compare(first.strength(), second.strength());
      } else {
        if (game.isTrump(first) && !game.isTrump(second)) {
          return 1;
        } else if (!game.isTrump(first) && game.isTrump(second)) {
          return -1;
        } else {
          if (first.sameColorAs(second)) {
            return Integer.compare(first.strength(), second.strength());
          } else {
            return 0;  // not weaker, not stronger, thus equal
          }
        }
      }
    }
  }
}
