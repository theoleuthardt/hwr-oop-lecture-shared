package hwr.oop.poker.application.domain.combinations;

import java.util.List;

final class CombinationDetectionStrategyFactory {
    private final AnalysisFlyweightFactory analysisFlyweightFactory;

    public static CombinationDetectionStrategyFactory create() {
        return new CombinationDetectionStrategyFactory();
    }

    public CombinationDetectionStrategyFactory() {
        this.analysisFlyweightFactory = new AnalysisFlyweightFactory();
    }

    public List<CombinationDetectionStrategy> createAll() {
        return List.of(
                createStraightFlush(),
                createQuads(),
                createFullHouse(),
                createFlush(),
                createStraight(),
                createTrips(),
                createTwoPair(),
                createSinglePair()
        );
    }

    public CombinationDetectionStrategy createSinglePair() {
        return new PairMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createTwoPair() {
        return new TwoPairMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createTrips() {
        return new TripMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createStraight() {
        return new StraightMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createFlush() {
        return new FlushMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createFullHouse() {
        return new FullHouseMatchingStrategy(createSinglePair(), createTrips());
    }

    public CombinationDetectionStrategy createQuads() {
        return new QuadsMatchingStrategy(analysisFlyweightFactory);
    }

    public CombinationDetectionStrategy createStraightFlush() {
        return new StraightFlushMatchingStrategy(createStraight());
    }
}
