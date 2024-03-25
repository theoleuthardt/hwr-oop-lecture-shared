package hwr.oop.poker.application.domain.blinds;

import hwr.oop.poker.application.domain.ChipValue;
import java.util.Objects;

public class SmallBlind implements ChipValue {

  private final long value;
  private final ChipValue bigBlind;

  public static SmallBlind of(long value) {
    return new SmallBlind(value);
  }

  private SmallBlind(long value) {
    this.value = value;
    this.bigBlind = ChipValue.of(value * 2);
  }

  @Override
  public long value() {
    return value;
  }

  public ChipValue bigBlind() {
    return bigBlind;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChipValue chipCount)) {
      return false;
    }
    return value == chipCount.value();
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "SmallBlind{" + value + "}";
  }
}
