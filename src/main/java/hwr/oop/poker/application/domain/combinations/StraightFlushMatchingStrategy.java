package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;

import java.util.List;

import static hwr.oop.poker.application.domain.combinations.Combination.Label.STRAIGHT_FLUSH;

final class StraightFlushMatchingStrategy implements CombinationDetectionStrategy {
    private final CombinationDetectionStrategy straights;

    public StraightFlushMatchingStrategy(CombinationDetectionStrategy straights) {
        this.straights = straights;
    }

    @Override
    public CombinationDetectionResult match(List<Card> cards) {
        final var straightResult = straights.match(cards);
        final boolean noStraight = !straightResult.successful();
        if (noStraight) {
            return CombinationDetectionResult.failure(STRAIGHT_FLUSH);
        } else {
            return matchUsing(straightResult);
        }
    }

    private CombinationDetectionResult matchUsing(CombinationDetectionResult successfulStraightResult) {
        assertIsReallySuccessful(successfulStraightResult);
        final var straightFlushes = selectStraightsOfSameColor(successfulStraightResult);
        final var noStraightFlush = straightFlushes.isEmpty();
        if (noStraightFlush) {
            return CombinationDetectionResult.failure(STRAIGHT_FLUSH);
        } else {
            return CombinationDetectionResult.success(STRAIGHT_FLUSH, straightFlushes);
        }
    }

    private void assertIsReallySuccessful(CombinationDetectionResult successfulStraightResult) {
        assert successfulStraightResult.label() == Combination.Label.STRAIGHT;
        assert successfulStraightResult.successful();
    }

    private List<List<Card>> selectStraightsOfSameColor(CombinationDetectionResult straightResult) {
        final var candidates = straightResult.alternatives();
        return filterOutFlushes(candidates);
    }

    private List<List<Card>> filterOutFlushes(List<List<Card>> candidates) {
        return candidates.stream()
                .filter(this::isDistinctColor)  // straight strategy always prefers most common color
                .toList();
    }

    private boolean isDistinctColor(List<Card> candidate) {
        return numberOfDistinctColors(candidate) == 1;
    }

    private long numberOfDistinctColors(List<Card> candidate) {
        return candidate.stream()
                .map(Card::color)
                .distinct().count();
    }
}
