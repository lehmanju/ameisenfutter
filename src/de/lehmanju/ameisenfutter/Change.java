package de.lehmanju.ameisenfutter;

public class Change {
  protected char type;
  protected int x, y;

  public Change(char type, int x, int y) {
    this.type = type;
    this.x = x;
    this.y = y;
  }

  @Override
  public int hashCode() {
    int hash = (int) type * 20 + x * 10 + y * 10;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Change) {
      Change aenderung = (Change) obj;
      if (aenderung.type == this.type && aenderung.x == this.x && aenderung.y == this.y)
        return true;
    }
    return false;
  }

  public String toString() {
    return "Change[" + type + "," + x + "," + y + "]";
  }
}
