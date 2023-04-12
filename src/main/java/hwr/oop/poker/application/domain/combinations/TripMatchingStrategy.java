package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Symbol;

import java.util.List;

import static hwr.oop.poker.application.domain.combinations.Combination.Label.TRIPS;

final class TripMatchingStrategy implements CombinationDetectionStrategy {
    private final AnalysisFlyweightFactory flyweightFactory;

    public TripMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {
        this.flyweightFactory = flyweightFactory;
    }

    @Override
    public CombinationDetectionResult match(List<Card> cards) {
        final var helper = flyweightFactory.get(cards);
        final List<Symbol> symbols = helper.symbolsWithTrips()
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .toList();
        if (symbols.isEmpty()) {
            return CombinationDetectionResult.failure(TRIPS);
        } else {
            final List<List<Card>> candidates = symbols.stream()
                    .map(helper::cardsWith)
                    .toList();
            assert candidates.stream().allMatch(c -> c.size() == 3);
            return CombinationDetectionResult.success(TRIPS, candidates);
        }
    }
}
