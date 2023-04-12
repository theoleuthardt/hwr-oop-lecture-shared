package hwr.oop.poker.tests.domain;

import hwr.oop.poker.application.domain.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Provide Players that are identifiable by a unique name")
class IdentifiablePlayerTest {

    @Test
    @DisplayName("Players with different idString (1 and 2), are not equal")
    void twoPlayers_DifferentIdentifier_AreNotEqual() {
        final Player firstPlayer = new Player("1");
        final Player secondPlayer = new Player("2");
        assertThat(firstPlayer).isNotEqualTo(secondPlayer);
    }

    @Test
    @DisplayName("Players with same idString (both 1), are not equal")
    void twoPlayers_SameIdentifier_AreEqual() {
        final Player firstPlayer = new Player("1");
        final Player secondPlayer = new Player("1");
        assertThat(firstPlayer).isEqualTo(secondPlayer);
    }

    @Test
    @DisplayName("#toString, Player 1337, should contain 1337")
    void toString_ContainsPlayerId() {
        final Player player = new Player("1337");
        final String toString = player.toString();
        assertThat(toString).contains("1337");
    }
}
