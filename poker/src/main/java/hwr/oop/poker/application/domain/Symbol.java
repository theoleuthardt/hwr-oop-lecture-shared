package hwr.oop.poker.application.domain;

import java.util.Arrays;
import java.util.Comparator;

public enum Symbol {
  TWO("2", 2),
  THREE("3", 3),
  FOUR("4", 4),
  FIVE("5", 5),
  SIX("6", 6),
  SEVEN("7", 7),
  EIGHT("8", 8),
  NINE("9", 9),
  TEN("T", 10),
  JACK("J", 11),
  QUEEN("Q", 12),
  KING("K", 13),
  ACE("A", 14);

  public static final Comparator<Symbol> DESCENDING_BY_STRENGTH = (o1, o2) -> Integer.compare(
      o2.strength(), o1.strength());

  private final String stringRepresentation;
  private final int strength;

  Symbol(String stringRepresentation, int strength) {
    this.stringRepresentation = stringRepresentation;
    this.strength = strength;
  }

  public static Symbol of(int strength) {
    return Arrays.stream(Symbol.values())
        .filter(s -> s.strength() == strength)
        .findFirst().orElseThrow();
  }

  public String stringRepresentation() {
    return stringRepresentation;
  }

  public int strength() {
    return strength;
  }
}
