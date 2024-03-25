package hwr.oop.poker.application.domain.blinds;

import hwr.oop.poker.application.domain.ChipValue;
import java.util.Objects;

public record SmallBlind(long value, ChipValue bigBlind) implements ChipValue {

  public SmallBlind {
    if (value < 0) {
      throw new IllegalArgumentException("SmallBlind must be positive");
    }
    Objects.requireNonNull(bigBlind);
  }

  public static SmallBlind of(long value) {
    return new SmallBlind(value);
  }

  private SmallBlind(long value) {
    this(value, ChipValue.of(value * 2));
  }


}
