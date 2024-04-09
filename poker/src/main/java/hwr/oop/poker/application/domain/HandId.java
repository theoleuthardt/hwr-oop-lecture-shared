package hwr.oop.poker.application.domain;

import java.util.Objects;

public record HandId(String value) {
  // nothing else to do here!


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HandId handId = (HandId) o;
    return Objects.equals(value, handId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}