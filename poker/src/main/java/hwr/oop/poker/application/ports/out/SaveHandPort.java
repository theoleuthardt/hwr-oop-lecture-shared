package hwr.oop.poker.application.ports.out;

import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;

public interface SaveHandPort {

  void saveHand(HandId id, Hand hand);

}
