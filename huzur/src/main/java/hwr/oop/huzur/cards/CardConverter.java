package hwr.oop.huzur.cards;

import hwr.oop.huzur.Card;
import hwr.oop.huzur.Color;
import hwr.oop.huzur.Sign;

public class CardConverter {

  public Card convert(String string) {
    if (string.length() != 2) {
      throw new InvalidCardStringFormatException(string);
    }
    final String first = string.substring(0, 1);
    final String second = string.substring(1, 2);
    return switch (string) {
      case "J1" -> Joker.first();
      case "J2" -> Joker.second();
      default -> new NormalCard(convertColor(first), convertSign(second));
    };
  }

  private Color convertColor(String string) {
    return switch (string) {
      case "C" -> Color.CLUBS;
      case "S" -> Color.SPADES;
      case "H" -> Color.HEARTS;
      case "D" -> Color.DIAMONDS;
      default -> throw new UnknownColorException(string);
    };
  }

  private Sign convertSign(String string) {
    return switch (string) {
      case "7" -> Sign.SEVEN;
      case "8" -> Sign.EIGHT;
      case "9" -> Sign.NINE;
      case "T" -> Sign.TEN;
      case "J" -> Sign.JACK;
      case "Q" -> Sign.QUEEN;
      case "K" -> Sign.KING;
      case "3" -> Sign.THREE;
      case "2" -> Sign.TWO;
      case "A" -> Sign.ACE;
      default -> throw new UnknownSignException(string);
    };
  }

  private static class InvalidCardStringFormatException extends RuntimeException {

    public InvalidCardStringFormatException(String cardString) {
      super("Cannot convert String to Card, expected two char string, got " + cardString);
    }
  }

  private static class UnknownColorException extends RuntimeException {

    public UnknownColorException(String colorString) {
      super("Cannot convert Color, got: " + colorString);
    }
  }

  private static class UnknownSignException extends RuntimeException {

    public UnknownSignException(String signString) {
      super("Cannot convert Sign, got: " + signString);
    }
  }
}
