package hwr.oop.poker.application.domain.betting;

import hwr.oop.poker.application.domain.ChipValue;
import hwr.oop.poker.application.domain.Player;

public class Play {
    private final Player player;
    private final ChipValue chipsTotal;
    private final ChipValue chipsAdded;
    private final Type type;

    private Play(Player player, ChipValue chipsTotal, ChipValue chipsAdded, Type type) {
        this.player = player;
        this.chipsTotal = chipsTotal;
        this.chipsAdded = chipsAdded;
        this.type = type;
    }

    public static Play fold(Player player) {
        return new Play(player, ChipValue.zero(), ChipValue.zero(), Type.FOLD);
    }

    public static Play check(Player player) {
        return new Play(player, ChipValue.zero(), ChipValue.zero(), Play.Type.CHECK);
    }

    public static Play bet(Player player, ChipValue amount) {
        return new Play(player, amount, amount, Play.Type.BET);
    }

    public static Play call(Player player, ChipValue target, ChipValue amount) {
        return new Play(player, target, amount, Play.Type.CALL);
    }

    public static Play raiseBy(Player player, ChipValue target, ChipValue amount) {
        return new Play(player, target, amount, Play.Type.RAISE);
    }

    public static Play smallBlind(Player player, ChipValue amount) {
        return new Play(player, amount, amount, Type.SMALL_BLIND);
    }

    public static Play bigBlind(Player player, ChipValue amount) {
        return new Play(player, amount, amount, Type.BIG_BLIND);
    }

    public boolean playedBy(Player player) {
        return this.player.equals(player);
    }

    public Player player() {
        return player;
    }

    public ChipValue chipValue() {
        return chipsAdded;
    }

    public Type type() {
        return type;
    }

    public boolean increasedChipsInPot() {
        return type.hasIncreasedChipsInPod();
    }

    public boolean hasIncreasedTargetValue() {
        return type.hasIncreasedRequiredChips();
    }

    public boolean isCheck() {
        return type == Type.CHECK;
    }

    public boolean isFold() {
        return type == Type.FOLD;
    }

    public ChipValue totalChipValue() {
        return chipsTotal;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Play{")
                .append(player).append(" ")
                .append(type);
        if (increasedChipsInPot()) {
            builder
                    .append(" added ").append(chipsAdded.value())
                    .append(" to ").append(chipsTotal.value());
        }
        builder.append("}");
        return builder.toString();
    }

    public boolean isBigBlind() {
        return type == Type.BIG_BLIND;
    }

    public enum Type {
        BET(true, true), FOLD(false, false), RAISE(true, true), CHECK(false, false), CALL(true, false), SMALL_BLIND(true, true), BIG_BLIND(true, true);

        private final boolean increasedChips;
        private final boolean isRaise;

        Type(boolean increasedChips, boolean isRaise) {
            this.increasedChips = increasedChips;
            this.isRaise = isRaise;
        }

        public boolean hasIncreasedChipsInPod() {
            return increasedChips;
        }

        public boolean hasIncreasedRequiredChips() {
            return isRaise;
        }
    }
}
