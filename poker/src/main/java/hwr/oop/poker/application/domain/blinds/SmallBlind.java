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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SmallBlind that = (SmallBlind) o;
    return value == that.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
