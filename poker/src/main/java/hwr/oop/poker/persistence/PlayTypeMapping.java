package hwr.oop.poker.persistence;

import hwr.oop.poker.application.domain.betting.Play;
import hwr.oop.poker.application.domain.betting.Play.Type;
import java.util.Map;

public enum PlayTypeMapping {
  CHECK("CH", Type.CHECK),
  CALL("CA", Type.CALL),
  RAISE("R", Type.RAISE),
  BET("B", Type.BET),
  FOLD("F", Type.FOLD);

  PlayTypeMapping(String csv, Type type) {
    this.type = type;
    this.csv = csv;
  }

  private final Play.Type type;
  private final String csv;

  private static final Map<String, Type> mapFromString = Map.of(
      CHECK.csv, CHECK.type,
      CALL.csv, CALL.type,
      RAISE.csv, RAISE.type,
      BET.csv, BET.type,
      FOLD.csv, FOLD.type
  );

  private static final Map<Type, String> mapFromType = Map.of(
      CHECK.type, CHECK.csv,
      CALL.type, CALL.csv,
      RAISE.type, RAISE.csv,
      BET.type, BET.csv,
      FOLD.type, FOLD.csv
  );

  public static String byType(Type type) {
    return mapFromType.get(type);
  }

  public static Type byCsv(String csv) {
    return mapFromString.get(csv);
  }
}
