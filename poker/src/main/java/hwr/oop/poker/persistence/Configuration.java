package hwr.oop.poker.persistence;

import java.nio.file.Path;

public record Configuration(Path csvFilePath) {

  public static class Builder {

    private Path csvFilePath;

    Builder() {
      this.csvFilePath = null;
    }

    public Configuration build() {
      return new Configuration(csvFilePath);
    }

    public Builder csvFile(Path path) {
      this.csvFilePath = path;
      return this;
    }
  }
}
