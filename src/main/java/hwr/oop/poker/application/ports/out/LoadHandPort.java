package hwr.oop.poker.application.ports.out;

import hwr.oop.poker.application.domain.Hand;
import hwr.oop.poker.application.domain.HandId;

public interface LoadHandPort {
    Hand loadById(HandId id);
}
