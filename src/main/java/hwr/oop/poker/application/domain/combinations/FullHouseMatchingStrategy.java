package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static hwr.oop.poker.application.domain.combinations.Combination.Label.FULL_HOUSE;

final class FullHouseMatchingStrategy implements CombinationDetectionStrategy {
    private final CombinationDetectionStrategy pair;
    private final CombinationDetectionStrategy trips;

    public FullHouseMatchingStrategy(CombinationDetectionStrategy pair, CombinationDetectionStrategy trips) {
        this.pair = pair;
        this.trips = trips;
    }

    @Override
    public CombinationDetectionResult match(List<Card> cards) {
        final var pairResult = pair.match(cards);
        final var tripResult = trips.match(cards);
        final var isFullHouse = isHandFullHouseBasedOnResults(pairResult, tripResult);
        if (isFullHouse) {
            final List<List<Card>> candidates = buildAllCandidates(
                    tripResult.alternatives(),
                    pairResult.alternatives()
            );
            return CombinationDetectionResult.success(FULL_HOUSE, candidates);
        } else {
            return CombinationDetectionResult.failure(FULL_HOUSE);
        }
    }

    private boolean isHandFullHouseBasedOnResults(CombinationDetectionResult pairResult, CombinationDetectionResult tripResult) {
        return areBothSuccessful(pairResult, tripResult) || hasMultipleAlternatives(tripResult);
    }

    private boolean hasMultipleAlternatives(CombinationDetectionResult tripResult) {
        return tripResult.successful() && tripResult.alternatives().size() > 1;
    }

    private boolean areBothSuccessful(CombinationDetectionResult first, CombinationDetectionResult second) {
        return first.successful() && second.successful();
    }

    private List<List<Card>> buildAllCandidates(List<List<Card>> allTrips, List<List<Card>> allPairs) {
        Stream<List<Card>> result = Stream.empty();
        if (allPairs != null) {
            final Stream<List<Card>> candidates = combineSetsWithPairs(allTrips, allPairs);
            result = Stream.concat(result, candidates);
        }
        final var pairsFromTrips = createPairsFromTrips(allTrips);
        final Stream<List<Card>> complexCandidates = combineSetsWithPairs(allTrips, pairsFromTrips);
        return Stream.concat(result, complexCandidates)
                .toList();
    }

    private List<List<Card>> createPairsFromTrips(List<List<Card>> allTrips) {
        return allTrips.stream()
                .map(candidate -> candidate.subList(0, 2))
                .toList();
    }

    private Stream<List<Card>> combineSetsWithPairs(List<List<Card>> allTrips, List<List<Card>> allPairs) {
        return allTrips.stream()
                .map(setCandidate -> buildCandidates(setCandidate, allPairs))
                .flatMap(Collection::stream);
    }

    private List<List<Card>> buildCandidates(List<Card> setCandidate, List<List<Card>> allPairs) {
        return allPairs.stream()
                .map(pairCandidate -> combine(setCandidate, pairCandidate))
                .filter(this::hasMoreThanOneSymbol)
                .toList();
    }

    private boolean hasMoreThanOneSymbol(List<Card> candidate) {
        final long symbolCount = candidate.stream()
                .map(Card::symbol)
                .distinct().count();
        return symbolCount > 1;
    }

    private List<Card> combine(List<Card> setCandidate, List<Card> pairCandidate) {
        return Stream.concat(setCandidate.stream(), pairCandidate.stream()).toList();
    }
}
