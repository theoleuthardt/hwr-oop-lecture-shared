package hwr.oop.poker.application.domain.blinds;

import hwr.oop.poker.application.domain.ChipValue;
import java.util.Objects;

public record BlindConfiguration(ChipValue smallBlind, ChipValue bigBlind) {

  public BlindConfiguration {
    Objects.requireNonNull(smallBlind);
    Objects.requireNonNull(bigBlind);
  }

  public static BlindConfiguration create(SmallBlind smallBlind) {
    return new BlindConfiguration(smallBlind);
  }

  private BlindConfiguration(SmallBlind smallBlind) {
    this(smallBlind, smallBlind.bigBlind());
  }

  public ChipValue blindsValue() {
    return bigBlind.plus(smallBlind);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlindConfiguration that = (BlindConfiguration) o;
    return Objects.equals(smallBlind.value(), that.smallBlind.value());
  }

  @Override
  public int hashCode() {
    return Objects.hash(smallBlind.value());
  }
}
