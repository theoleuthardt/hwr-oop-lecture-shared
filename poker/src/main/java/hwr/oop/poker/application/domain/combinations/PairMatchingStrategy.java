package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Symbol;

import java.util.List;

import static hwr.oop.poker.application.domain.combinations.Combination.Label.PAIR;

final class PairMatchingStrategy implements CombinationDetectionStrategy {
    private final AnalysisFlyweightFactory flyweightFactory;

    public PairMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {
        this.flyweightFactory = flyweightFactory;
    }

    @Override
    public CombinationDetectionResult match(List<Card> cards) {
        final AnalysisFlyweight helper = flyweightFactory.get(cards);
        final List<Symbol> pairedSymbols = helper.symbolsWithPairs()
                .sorted(Symbol.DESCENDING_BY_STRENGTH)
                .toList();
        if (pairedSymbols.isEmpty()) {
            return CombinationDetectionResult.failure(PAIR);
        } else {
            final List<List<Card>> candidates = pairedSymbols.stream()
                    .map(helper::cardsWith)
                    .toList();
            assert candidates.stream().allMatch(c -> c.size() == 2);
            return CombinationDetectionResult.success(PAIR, candidates);
        }
    }

}
