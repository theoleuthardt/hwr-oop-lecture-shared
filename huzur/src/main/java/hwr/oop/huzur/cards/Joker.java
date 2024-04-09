package hwr.oop.huzur.cards;

import hwr.oop.huzur.Card;
import hwr.oop.huzur.Color;
import hwr.oop.huzur.Sign;

public record Joker(int strength) implements Card {


  public static Card first() {
    return new Joker(13337);
  }

  public static Card second() {
    return new Joker(1337);
  }


  @Override
  public boolean hasColor(Color trump) {
    return false;
  }

  @Override
  public boolean isAlwaysTrump() {
    return true;
  }

  @Override
  public boolean sameColorAs(Card other) {
    return false;
  }

  @Override
  public boolean hasSign(Sign sign) {
    return false;
  }

}
