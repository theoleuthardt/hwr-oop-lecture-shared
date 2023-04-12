package hwr.oop.poker.application.domain;

public interface ChipValue extends Comparable<ChipValue> {
    static ChipValue of(long value) {
        return new PositiveChipValue(value);
    }

    static ChipValue zero() {
        return ChipValue.of(0);
    }

    static ChipValue minRaise(ChipValue bet) {
        return ChipValue.of(bet.value() * 2);
    }

    long value();

    default ChipValue minus(ChipValue other) {
        return ChipValue.of(value() - other.value());
    }

    default ChipValue plus(ChipValue other) {
        return ChipValue.of(value() + other.value());
    }

    @Override
    default int compareTo(ChipValue o) {
        return Long.compare(value(), o.value());
    }

    default boolean isLessThan(ChipValue other) {
        return compareTo(other) < 0;
    }

    class NegativeChipCountException extends RuntimeException {

        public NegativeChipCountException(String message) {
            super(message);
        }
    }

}
