package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;

import java.util.List;

import static hwr.oop.poker.application.domain.combinations.Combination.Label.QUADS;

final class QuadsMatchingStrategy implements CombinationDetectionStrategy {
    private final AnalysisFlyweightFactory flyweightFactory;

    public QuadsMatchingStrategy(AnalysisFlyweightFactory flyweightFactory) {
        this.flyweightFactory = flyweightFactory;
    }

    @Override
    public CombinationDetectionResult match(List<Card> cards) {
        final var analysisSupport = flyweightFactory.get(cards);
        final var optional = analysisSupport.symbolsWithQuads().findFirst();
        if (optional.isEmpty()) {
            return CombinationDetectionResult.failure(QUADS);
        } else {
            final var symbolWithQuads = optional.get();
            final var quads = analysisSupport.cardsWith(symbolWithQuads);
            return CombinationDetectionResult.success(QUADS, List.of(quads));
        }
    }
}
