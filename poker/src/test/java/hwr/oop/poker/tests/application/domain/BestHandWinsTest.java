package hwr.oop.poker.tests.application.domain;

import static org.assertj.core.api.Assertions.assertThat;

import hwr.oop.poker.application.domain.Card;
import hwr.oop.poker.application.domain.Converter;
import hwr.oop.poker.application.domain.combinations.Combination;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


class BestHandWinsTest {

  private Converter converter;

  @BeforeEach
  void setUp() {
    converter = Converter.create();
  }

  @DisplayName("If Combination detects no hand, its a HIGH CARD")
  @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
  @CsvSource(delimiter = '-', value = {
      "2H,4S,6D,8C,TH,QS,AD - AD,QS,TH,8C,6D - AD,QS,TH,8C,6D",
      "3S,5D,7C,9H,JS,QD,KC - KC,QD,JS,9H,7C - KC,QD,JS,9H,7C",
      "AS,KS,QC,JC,9H,8H,7D - AS,KS,QC,JC,9H - AS,KS,QC,JC,9H",
  })
  void highCard(String inputCardsString, String expectedCombinationString,
      String expectedKickersString) {
    // given
    final var cards = converter.convert(inputCardsString);
    // when
    final var combo = Combination.of(cards);
    // then
    assertHasLabel(combo, Combination.Label.HIGH_CARD);
    assertCombinationIs5CardsIncluding(combo, expectedCombinationString);
    assertKickersContain(combo, expectedKickersString);
  }

  @DisplayName("Combination can detect single PAIRs")
  @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
  @CsvSource(delimiter = '-', value = {
      "2H,4S,6D,8C,TH,TS,QD - TH,TS - QD,8C,6D",
      "AS,AC,KD,JH,TS,2C,4C - AS,AC - KD,JH,TS",
      "KC,JD,9C,7H,5D,TS,KH - KC,KH - JD,TS,9C",
  })
  void singlePairs_IndividualStrategy(String inputCardsString, String expectedCombinationString,
      String expectedKickersString) {
    // given
    final var cards = converter.convert(inputCardsString);
    // when
    final var combo = Combination.of(cards);
    // then
    assertHasLabel(combo, Combination.Label.PAIR);
    assertKickersContain(combo, expectedKickersString);
    assertCombinationIs5CardsIncluding(combo, expectedCombinationString);
  }

  @DisplayName("Combination can detect TWO PAIRs")
  @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
  @CsvSource(delimiter = '-', value = {
      "AS,AD,KS,KD,2C,3C,4C - AS,AD,KS,KD,4C - 4C",
      "2S,3S,6C,JC,JH,QS,QD - QS,QD,JC,JH,6C - 6C",
      "2C,2H,6C,6H,KC,KH,AS - KC,KH,6C,6H,AS - AS",
  })
  void twoPairs(String inputCards, String bestCombinationString, String kickersString) {
    // given
    final var cards = converter.convert(inputCards);
    // when
    final var combo = Combination.of(cards);
    // then
    assertHasLabel(combo, Combination.Label.TWO_PAIRS);
    assertKickersContain(combo, kickersString);
    assertCombinationIs5CardsIncluding(combo, bestCombinationString);
  }

  @DisplayName("Combination can detect TRIPS/sets")
  @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
  @CsvSource(delimiter = '-', value = {
      "AS,AD,AC,2C,3C,5C,6S - AS,AD,AC,6S,5C - 6S,5C",
      "AH,KD,8H,8D,8S,3C,2S - 8H,8D,8S,AH,KD - AH,KD",
      "JH,QD,KC,AS,2H,2D,2S - 2H,2D,2S,AS,KC - AS,KC",
  })
  void trips(String inputCards, String expectedCombinationString, String expectedKickersString) {
    // given
    final var cards = converter.convert(inputCards);
    // when
    final var combo = Combination.of(cards);
    // then
    assertHasLabel(combo, Combination.Label.TRIPS);
    assertKickersContain(combo, expectedKickersString);
    assertCombinationIs5CardsIncluding(combo, expectedCombinationString);
  }

  @DisplayName("Combination can detect STRAIGHTs")
  @ParameterizedTest(name = "cards ({0}), combination ({1}), no kickers")
  @CsvSource(delimiter = '-', value = {
      "AS,KC,QH,JD,TS,3C,4H - AS,KC,QH,JD,TS",
      "9C,2H,TS,8H,JD,QH,AS - QH,JD,TS,9C,8H",
      "5D,2H,4S,6H,7C,3C,8S - 8S,7C,6H,5D,4S",
  })
  void straights(String inputCards, String expectedCombinationString) {
    // given
    final var cards = converter.convert(inputCards);
    // when
    final var combination = Combination.of(cards);
    // then
    assertHasLabel(combination, Combination.Label.STRAIGHT);
    assertKickersEmpty(combination);
    assertCombinationIs5CardsIncluding(combination, expectedCombinationString);
  }

  @DisplayName("Combination can detect FLUSHes")
  @ParameterizedTest(name = "cards ({0}), combination ({1})")
  @CsvSource(delimiter = '-', value = {
      "JS,AS,7S,KS,QS,8S,9S - AS,KS,QS,JS,9S",
      "3D,TD,KD,6D,2D,QD,7D - KD,QD,TD,7D,6D",
      "JH,TH,KD,AH,QD,9H,3H - AH,JH,TH,9H,3H",
  })
  void flushes(String inputCardsString, String expectedCombinationString) {
    // given
    final var cards = converter.convert(inputCardsString);
    // when
    final var combo = Combination.of(cards);
    // then
    assertHasLabel(combo, Combination.Label.FLUSH);
    assertKickersEmpty(combo);
    assertCombinationIs5CardsIncluding(combo, expectedCombinationString);
  }

  @DisplayName("Combination can detect FULL HOUSEs")
  @ParameterizedTest(name = "cards ({0}), combination ({1})")
  @CsvSource(delimiter = '-', value = {
      "AH,2C,KD,KS,AD,3C,AS - AS,AH,AD,KD,KS",
      "KH,QD,2C,QH,KD,KS,QS - KS,KD,KH,QD,QH",
      "2D,AD,TC,AH,2S,AS,TH - AD,AH,AS,TC,TH"
  })
  void fullHouse(String inputCardsString, String expectedCombinationString) {
    // given
    final var cards = converter.convert(inputCardsString);
    // when
    final var combo = Combination.of(cards);
    // then
    assertKickersEmpty(combo);
    assertHasLabel(combo, Combination.Label.FULL_HOUSE);
    assertCombinationIs5CardsIncluding(combo, expectedCombinationString);
  }

  @DisplayName("Combination can detect QUADs")
  @ParameterizedTest(name = "cards ({0}), combination ({1}), kickers ({2})")
  @CsvSource(delimiter = '-', value = {
      "3D,2S,AD,AS,AH,AC,4C - AD,AS,AH,AC - 4C",
      "2S,2D,QS,AS,2C,KS,2H - 2S,2D,2C,2H - AS",
      "TH,TC,TS,5S,TD,2C,3D - TH,TC,TS,TD - 5S",
  })
  void quads(String inputCardsString, String expectedCombinationString,
      String expectedKickersString) {
    // given
    final var cards = converter.convert(inputCardsString);
    // when
    final var combo = Combination.of(cards);
    // then
    assertHasLabel(combo, Combination.Label.QUADS);
    assertKickersContain(combo, expectedKickersString);
    assertCombinationIs5CardsIncluding(combo, expectedCombinationString);
  }

  @DisplayName("Combination can detect STRAIGHT FLUSHes")
  @ParameterizedTest(name = "cards ({0}), combination ({1})")
  @CsvSource(delimiter = '-', value = {
      "AS,KS,QS,JS,TS,9S,8S - AS,KS,QS,JS,TS",
      "TS,8S,AD,QS,KD,JS,9S - QS,JS,TS,9S,8S",
      "6S,2S,5S,3S,8D,7D,4S - 6S,5S,4S,3S,2S",
  })
  void straightFlushes(String inputCardsString, String expectedCombinationString) {
    // given
    final var cards = converter.convert(inputCardsString);
    // when
    final var combo = Combination.of(cards);
    // then
    assertKickersEmpty(combo);
    assertHasLabel(combo, Combination.Label.STRAIGHT_FLUSH);
    assertCombinationIs5CardsIncluding(combo, expectedCombinationString);
  }

  @Test
  void straightFlushBeatsQuads() {
    final var straightFlush = combinationFor("2S,3S,4S,5S,6S");
    final var quads = combinationFor("AS,AC,AD,AH,KH");
    assertLabelDifferent(straightFlush, quads);
    assertThat(straightFlush).isGreaterThan(quads);
  }

  @Test
  void quadsBeatFullHouses() {
    final var quads = combinationFor("2S,2C,2D,2S,3H");
    final var fullHouse = combinationFor("AS,AC,AD,KS,KH");
    assertLabelDifferent(quads, fullHouse);
    assertThat(quads).isGreaterThan(fullHouse);
  }

  @Test
  void fullHousesBeatFlushes() {
    final var fullHouse = combinationFor("2S,2C,2D,3S,3H");
    final var flush = combinationFor("AS,KS,QS,JS,9S");
    assertLabelDifferent(fullHouse, flush);
    assertThat(fullHouse).isGreaterThan(flush);
  }

  @Test
  void flushBeatsStraight() {
    final var flush = combinationFor("2S,3S,4S,5S,7S");
    final var straight = combinationFor("AS,KD,QH,JC,TS");
    assertLabelDifferent(flush, straight);
    assertThat(flush).isGreaterThan(straight);
  }

  @Test
  void straightBeatsSet() {
    final var straight = combinationFor("2S,3D,4H,5C,6S");
    final var trips = combinationFor("AS,AD,AH,2C,3S");
    assertLabelDifferent(straight, trips);
    assertThat(straight).isGreaterThan(trips);
  }

  @Test
  void setBeatsTwoPairs() {
    final var set = combinationFor("2S,2D,2H,3C,4S");
    final var twoPairs = combinationFor("AS,AD,KS,KD,QS");
    assertThat(set.label()).isEqualTo(Combination.Label.TRIPS);
    assertThat(twoPairs.label()).isEqualTo(Combination.Label.TWO_PAIRS);
    assertThat(set).isGreaterThan(twoPairs);
  }

  @Test
  void twoPairBeatsPair() {
    final var twoPair = combinationFor("2S,2D,3S,3D,4S");
    final var singlePair = combinationFor("AS,AC,KS,QC,JD");
    assertThat(twoPair.label()).isEqualTo(Combination.Label.TWO_PAIRS);
    assertThat(singlePair.label()).isEqualTo(Combination.Label.PAIR);
    assertThat(twoPair).isGreaterThan(singlePair);
  }

  @Test
  void singlePairBeatsHighCard() {
    final var singlePair = combinationFor("2S,2C,3S,4C,5D");
    final var highCard = combinationFor("AS,KD,QH,JC,9D");
    assertLabelDifferent(singlePair, highCard);
    assertThat(singlePair).isGreaterThan(highCard);
  }

  @Test
  void aceFullKingsFull_AceFullWins() {
    final var acesFullOfKingsCombo = combinationFor("AS,AC,AD,2S,2H");
    final var kingsFullOfThreesCombo = combinationFor("KS,KC,KD,3S,3H");
    assertSameLabel(acesFullOfKingsCombo, kingsFullOfThreesCombo);
    assertThat(acesFullOfKingsCombo).isGreaterThan(kingsFullOfThreesCombo);
  }

  @Test
  void aceFullOfKings_AcesFullOfQueens_AcesFullOfKingsWins() {
    final var acesFullOfKingsCombo = combinationFor("AS,AC,AD,KS,KH");
    final var acesFullOfQueensCombo = combinationFor("AH,AC,AD,QS,QH");
    assertThat(acesFullOfKingsCombo).isGreaterThan(acesFullOfQueensCombo);
  }

  private Combination combinationFor(String cardsAsString) {
    final var cards = converter.convert(cardsAsString);
    return Combination.of(cards);
  }

  private void assertLabelDifferent(Combination first, Combination second) {
    final var firstLabel = first.label();
    final var secondLabel = second.label();
    assertThat(firstLabel).isNotEqualTo(secondLabel);
  }

  private void assertSameLabel(Combination first, Combination second) {
    final var firstLabel = first.label();
    final var secondLabel = second.label();
    assertThat(firstLabel).isEqualTo(secondLabel);
  }

  private void assertKickersContain(Combination sut, String expectedKickersString) {
    final var expected = converter.convert(expectedKickersString);
    assertKickersContain(sut, expected);
  }

  private void assertKickersContain(Combination sut, List<Card> expected) {
    final var actual = sut.kickers();
    assertThat(actual).containsAll(expected);
  }

  private void assertCombinationIs5CardsIncluding(Combination sut,
      String expectedCombinationString) {
    final var expected = converter.convert(expectedCombinationString);
    assertCombinationIs5CardsIncluding(sut, expected);
  }

  private void assertCombinationIs5CardsIncluding(Combination sut, List<Card> expected) {
    final var actual = sut.cards();
    assertThat(actual)
        .containsAll(expected)
        .hasSize(5);
  }

  private void assertKickersEmpty(Combination sut) {
    final var actual = sut.kickers();
    assertThat(actual).isEmpty();
  }

  private void assertHasLabel(Combination sut, Combination.Label expected) {
    final var label = sut.label();
    assertThat(label).isEqualTo(expected);
  }

}

