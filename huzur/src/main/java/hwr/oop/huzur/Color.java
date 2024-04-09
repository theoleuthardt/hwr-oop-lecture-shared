package hwr.oop.huzur;

import java.util.Arrays;
import java.util.List;

public enum Color {
  SPADES, HEARTS, DIAMONDS, CLUBS;

  public static List<Color> all() {
    return Arrays.asList(Color.values());
  }

}
