package hwr.oop.huzur.tests;


import static hwr.oop.huzur.tests.HuzurTestUtil.allCardsOfColor;

import hwr.oop.huzur.Card;
import hwr.oop.huzur.Color;
import hwr.oop.huzur.Game;
import hwr.oop.huzur.cards.CardConverter;
import hwr.oop.huzur.cards.Joker;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TrumpCardsAreOverNonTrumpCardsTest {

  @Test
  void bothJokers_AreAlwaysTrump() {
    final SoftAssertions soft = new SoftAssertions();
    soft.assertThat(Joker.first().isAlwaysTrump()).isTrue();
    soft.assertThat(Joker.second().isAlwaysTrump()).isTrue();
    soft.assertAll();
  }

  @ParameterizedTest
  @EnumSource(Color.class)
  void allGames_BothJokers_AreAlwaysTrump(Color trumpColor) {
    final var first = Joker.first();
    final var second = Joker.second();
    final var game = new Game(trumpColor);
    final var soft = new SoftAssertions();
    soft.assertThat(game.isTrump(first)).isTrue();
    soft.assertThat(game.isTrump(second)).isTrue();
    soft.assertAll();
  }

  @ParameterizedTest
  @EnumSource(Color.class)
  void allCardsOfTrumpColor_AreTrump(Color trumpColor) {
    final var game = new Game(trumpColor);
    final var soft = new SoftAssertions();
    allCardsOfColor(trumpColor).forEach(card -> soft.assertThat(game.isTrump(card)).isTrue());
    soft.assertAll();
  }

  @Test
  void heartsAreTrump_DiamondsClubsAndSpadesAreNotTrump() {
    final var game = new Game(Color.HEARTS);
    final var soft = new SoftAssertions();
    Stream.of(Color.DIAMONDS, Color.CLUBS, Color.SPADES)
        .forEach(color -> allCardsOfColor(color)
            .forEach(card -> soft.assertThat(game.isTrump(card)).isFalse())
        );
    soft.assertAll();
  }

  @Test
  void spadesAreTrump_TrumpCards_SpadesAreInOrder() {
    final var game = new Game(Color.SPADES);
    final var cmp = game.strenghtComparator();
    ComparatorAssert.of(cmp)
        .weakerThan("S7", "S8")
        .weakerThan("S8", "S9")
        .weakerThan("S9", "ST")
        .weakerThan("ST", "SJ")
        .weakerThan("SJ", "SQ")
        .weakerThan("SQ", "SK")
        .weakerThan("SK", "S3")
        .weakerThan("S3", "S2")
        .weakerThan("S2", "SA")
        .run();
  }

  @Test
  void diamondsAreTrump_ColorCards_SpadesAreInOrder() {
    final var game = new Game(Color.DIAMONDS);
    final var cmp = game.strenghtComparator();
    ComparatorAssert.of(cmp)
        .weakerThan("S7", "S8")
        .weakerThan("S8", "S9")
        .weakerThan("S9", "ST")
        .weakerThan("ST", "SJ")
        .weakerThan("SJ", "SQ")
        .weakerThan("SQ", "SK")
        .weakerThan("SK", "S3")
        .weakerThan("S3", "S2")
        .weakerThan("S2", "SA")
        .run();
  }

  @Test
  void clubsAreTrump_SevenOfClubs_StrongerThanAllColorAces() {
    final var game = new Game(Color.CLUBS);
    final var comparator = game.strenghtComparator();
    ComparatorAssert.of(comparator)
        .strongerThan("C7", "HA")
        .strongerThan("C7", "DA")
        .strongerThan("C7", "SA")
        .run();
  }

  @Test
  void clubsAreTrump_AceOfSpades_NotStrongerThanSevenOfDiamonds() {
    final var game = new Game(Color.CLUBS);
    final var comparator = game.strenghtComparator();
    ComparatorAssert.of(comparator)
        .notStrongerThan("SA", "D7")
        .notStrongerThan("SA", "H7")
        .strongerThan("SA", "S7")
        .run();
  }

  @Test
  void heartsAreTrump_AceOfHearts_WeakerThanJokers() {
    final var game = new Game(Color.HEARTS);
    final var comparator = game.strenghtComparator();
    ComparatorAssert.of(comparator)
        .weakerThan("HA", "J1")
        .weakerThan("HA", "J2")
        .strongerThan("J1", "J2")
        .run();
  }

  private static class ComparatorAssert {

    private final SoftAssertions soft;
    private final Comparator<Card> comparator;
    private final CardConverter converter;

    private static ComparatorAssert of(Comparator<Card> comparator) {
      return new ComparatorAssert(comparator);
    }

    private ComparatorAssert(Comparator<Card> comparator) {
      this.comparator = comparator;
      this.soft = new SoftAssertions();
      this.converter = new CardConverter();
      Objects.requireNonNull(comparator);
    }

    private ComparatorAssert strongerThan(Card bigger, Card smaller) {
      soft.assertThat(comparator.compare(bigger, smaller)).isPositive().isNotZero();
      soft.assertThat(comparator.compare(smaller, bigger)).isNegative().isNotZero();
      return this;
    }

    private ComparatorAssert notStrongerThan(Card first, Card second) {
      soft.assertThat(comparator.compare(first, second)).isNotPositive();
      soft.assertThat(comparator.compare(second, first)).isNotPositive();
      return this;
    }

    private ComparatorAssert strongerThan(String bigger, String smaller) {
      return strongerThan(converter.convert(bigger), converter.convert(smaller));
    }

    private ComparatorAssert weakerThan(Card smaller, Card bigger) {
      return strongerThan(bigger, smaller);
    }

    private ComparatorAssert weakerThan(String smaller, String bigger) {
      return strongerThan(bigger, smaller);
    }

    public ComparatorAssert notStrongerThan(String first, String second) {
      return notStrongerThan(converter.convert(first), converter.convert(second));
    }

    private void run() {
      soft.assertAll();
    }
  }
}
