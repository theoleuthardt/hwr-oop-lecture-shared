package hwr.oop.poker.application.domain.blinds;

import hwr.oop.poker.application.domain.ChipValue;
import java.util.Objects;

public class BlindConfiguration {

  private final ChipValue smallBlind;
  private final ChipValue bigBlind;

  public static BlindConfiguration create(SmallBlind smallBlind) {
    return new BlindConfiguration(smallBlind);
  }

  private BlindConfiguration(SmallBlind smallBlind) {
    this.smallBlind = smallBlind;
    this.bigBlind = smallBlind.bigBlind();
  }

  public ChipValue blindsValue() {
    return bigBlind.plus(smallBlind);
  }

  public ChipValue bigBlind() {
    return bigBlind;
  }

  public ChipValue smallBlind() {
    return smallBlind;
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
    return Objects.equals(smallBlind, that.smallBlind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(smallBlind);
  }

  @Override
  public String toString() {
    return "Blinds{" +
        "smallBlindString=" + smallBlind.value() +
        ", bigBlind=" + bigBlind.value() +
        '}';
  }
}
