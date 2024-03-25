package hwr.oop.poker.application.domain.combinations;

import hwr.oop.poker.application.domain.Card;

import java.util.List;

interface CombinationDetectionStrategy {

    CombinationDetectionResult match(List<Card> cards);

}
