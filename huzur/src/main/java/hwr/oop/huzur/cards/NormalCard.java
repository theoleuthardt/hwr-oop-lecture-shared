package hwr.oop.huzur.cards;


import hwr.oop.huzur.Card;
import hwr.oop.huzur.Color;
import hwr.oop.huzur.Sign;
import java.util.Objects;

public record NormalCard(Color color, Sign sign) implements Card {

  public NormalCard {
    Objects.requireNonNull(color);
    Objects.requireNonNull(sign);
  }

  @Override
  public boolean hasColor(Color color) {
    return color == this.color;
  }

  @Override
  public boolean hasSign(Sign sign) {
    return sign == this.sign;
  }

  @Override
  public int strength() {
    return sign.strength();
  }

  @Override
  public boolean isAlwaysTrump() {
    return false;
  }

  @Override
  public boolean sameColorAs(Card second) {
    return second.hasColor(this.color);
  }

}
