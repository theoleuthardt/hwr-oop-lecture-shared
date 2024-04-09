package hwr.oop.huzur.tests;

import hwr.oop.huzur.Card;
import hwr.oop.huzur.Color;
import hwr.oop.huzur.cards.NormalCard;
import hwr.oop.huzur.Sign;
import java.util.Arrays;
import java.util.List;

public class HuzurTestUtil {

  public static List<Card> allCardsOfColor(Color color) {
    return Arrays.stream(Sign.values()).map(sign -> (Card) new NormalCard(color, sign)).toList();
  }

  public static List<Card> allCardsOfSign(Sign sign) {
    return Arrays.stream(Color.values()).map(color -> (Card) new NormalCard(color, sign)).toList();
  }

}
