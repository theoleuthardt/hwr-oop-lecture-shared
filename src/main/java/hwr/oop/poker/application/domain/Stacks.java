package hwr.oop.poker.application.domain;

import hwr.oop.poker.application.domain.betting.Play;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Stacks implements Function<Player, ChipValue> {
    private final Map<Player, ChipValue> map;

    private Stacks(Map<Player, ChipValue> map) {
        assert !map.entrySet().isEmpty() && !map.containsKey(null);
        this.map = map;
    }

    public static StacksBuilder newBuilder() {
        return new StacksBuilder();
    }

    @Override
    public ChipValue apply(Player player) {
        return ofPlayer(player);
    }

    public ChipValue ofPlayer(Player player) {
        return map.get(player);
    }

    public Stacks apply(Play play) {
        final Player player = play.player();
        if (map.containsKey(player)) {
            final var newChipValue = stackSizeApplied(play);
            final var updatedImmutableMap = mapWithPlayApplied(player, newChipValue);
            return new Stacks(updatedImmutableMap);
        } else {
            final var availablePlayers = map.keySet();
            throw new InvalidPlayerForStackException("Tried to apply play for " + player
                    + ", expected any of " + availablePlayers);
        }
    }

    private Map<Player, ChipValue> mapWithPlayApplied(Player player, ChipValue newChipValue) {
        final var mutableMap = new HashMap<>(map);
        mutableMap.put(player, newChipValue);
        return Collections.unmodifiableMap(mutableMap);
    }

    private ChipValue stackSizeApplied(Play play) {
        final var player = play.player();
        final var stack = map.get(player);
        final var chipsPlayed = play.chipValue();
        final ChipValue newChipValue;
        try {
            newChipValue = stack.minus(chipsPlayed);
        } catch (ChipValue.NegativeChipCountException e) {
            final String message = player.toString() + " has only " + stack + "," +
                    " but tried to play " + chipsPlayed.toString();
            throw new InvalidPlayForStackException(message, e);
        }
        return newChipValue;
    }

    public static class StacksBuilder {

        private final Map<Player, ChipValue> stacks;

        private StacksBuilder() {
            this.stacks = new HashMap<>();
        }

        public ContextStackBuilder of(Player player) {
            return new ContextStackBuilder(this, player);
        }

        private StacksBuilder player(Player player, ChipValue stackSize) {
            stacks.put(player, stackSize);
            return this;
        }

        public Stacks build() {
            final var unmodifiableMap = Collections.unmodifiableMap(stacks);
            return new Stacks(unmodifiableMap);
        }
    }

    public static class ContextStackBuilder {
        private final StacksBuilder builder;
        private final Player player;

        private ContextStackBuilder(StacksBuilder builder, Player player) {
            this.builder = builder;
            this.player = player;
        }

        public StacksBuilder is(long value) {
            final ChipValue stackSize = ChipValue.of(value);
            return builder.player(player, stackSize);
        }
    }

    public static class InvalidPlayForStackException extends RuntimeException {
        public InvalidPlayForStackException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidPlayerForStackException extends RuntimeException {
        public InvalidPlayerForStackException(String message) {
            super(message);
        }
    }
}
