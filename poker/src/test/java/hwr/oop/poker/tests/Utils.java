package hwr.oop.poker.tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public class Utils {

  public static Path resourceAsPath(String name) {
    final var loader = Utils.class.getClassLoader();
    final var stuff = loader.getResource(name);
    assert stuff != null;
    try {
      final URI uri = stuff.toURI();
      return Path.of(uri);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
