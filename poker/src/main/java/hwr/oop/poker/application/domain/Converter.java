package hwr.oop.poker.application.domain;

import java.util.Arrays;
import java.util.List;

public class Converter {

  public static Converter create() {
    return new Converter();
  }

  private Converter() {
    // nothing to do
  }

  public Card from(String singleCardString) {
    final var symbolString = singleCardString.substring(0, 1);
    final var colorString = singleCardString.substring(1, 2);
    return new Card(
        parseColor(colorString),
        parseSymbol(symbolString)
    );
  }

  private Symbol parseSymbol(String symbolString) {
    final var element = Arrays.stream(Symbol.values())
        .filter(s -> s.stringRepresentation().equals(symbolString))
        .findFirst();
    if (element.isEmpty()) {
      throw new IllegalArgumentException("Can not parse symbol," +
          " expected: [23456789TJQKA], actual: " + symbolString);
    } else {
      return element.get();
    }

  }

  private Color parseColor(String colorString) {
    final var element = Arrays.stream(Color.values())
        .filter(s -> s.stringRepresentation().equals(colorString))
        .findFirst();
    if (element.isEmpty()) {
      throw new IllegalArgumentException("Can not parse color from string," +
          " expected: [HDSC], actual: " + colorString);
    } else {
      return element.get();
    }
  }

  public List<Card> convert(String cards) {
    return Arrays.stream(cards.split(","))
        .map(this::from)
        .toList();
  }
}
