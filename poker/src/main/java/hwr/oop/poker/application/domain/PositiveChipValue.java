package hwr.oop.poker.application.domain;

record PositiveChipValue(long value) implements ChipValue {

  PositiveChipValue {
    assertIsPositive(value);
  }

  private void assertIsPositive(long value) {
    if (value < 0) {
      throw new NegativeChipCountException("Can not create chip value below 0," +
          " but tried to create chip value of " + value);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChipValue that)) {
      return false;
    }
    return value == that.value();
  }

  @Override
  public String toString() {
    return "ChipValue{" + value + '}';
  }
}
