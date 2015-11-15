package de.lehmanju.ameisenfutter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Simulator {
  int mitteX, mitteY;
  Ameise[] amArray;
  private Set<Change> changes;
  Speicher speicher;

  protected class Ameise {
    int x, y;
    boolean futter;
    int lastDir;

    public Ameise() {
      x = 0;
      y = 0;
      futter = false;
      lastDir = -1;
    }

    public Ameise(Ameise other) {
      x = other.x;
      y = other.y;
      futter = other.futter;
      lastDir = other.lastDir;
    }
  }

  public Simulator(Speicher sp) {
    speicher = sp;
    initialize();
  }

  private void initialize() {
    amArray = new Ameise[speicher.ameisen];
    for (int i = 0; i < speicher.ameisen; i++) {
      amArray[i] = new Ameise();
      amArray[i].x = speicher.nestX;
      amArray[i].y = speicher.nestY;
      amArray[i].futter = false;
      amArray[i].lastDir = -1;
    }
  }

  public Set<Change> simulate(int iterations, int phTimeout) {
    changes = new HashSet<Change>();
    /*
     * Entferne alte Pheromonspuren, immer wenn phTimeout-Schritte vorueber sind
     */
    for (int count = 0; count < iterations; count++) {
      speicher.steps++;
      if ((speicher.steps % phTimeout) == 0)
        for (Iterator<XYPoint> it = speicher.phero.iterator(); it.hasNext();) {
          XYPoint p = it.next();
          if (speicher.pheromone[p.x][p.y] == 0)
            it.remove();
          if (speicher.pheromone[p.x][p.y] > 0) {
            speicher.pheromone[p.x][p.y]--;
            changes.add(new Change('P', p.x, p.y));
          }
        }
      for (int aN = 0; aN < speicher.ameisen; aN++) // Durchlaufe alle Ameisen
      {
        Ameise cA = amArray[aN];
        if (cA.futter) // Futter im Inventar der Ameise?
        {
          // Falls Ameise im Nest, lege Futter ab und fuege dem Nest Futter hinzu
          if (cA.x == speicher.nestX && cA.y == speicher.nestY) {
            cA.futter = false;
            speicher.futterNest++;
          } else
          // ansonsten erhoehe aktuelle Pheromonkonzentration und begib dich auf das naechste Feld
          // in Richtung Nest
          {
            speicher.pheromone[cA.x][cA.y]++;
            speicher.phero.add(new XYPoint(cA.x, cA.y));
            changes.add(new Change('P', cA.x, cA.y));
            toNest(cA); // setze naechstes Feld in Richtung Nest
          }
        } else {
          // Falls kein Futter im Inventar
          if (speicher.futterVerteilung[cA.x][cA.y] > 0) {
            cA.futter = true; // nimm Futter auf
            speicher.futterVerteilung[cA.x][cA.y]--;
            Change aen = new Change('F', cA.x, cA.y);
            changes.add(aen);
          } else {
            if (nextPPos(cA))
              /*
               * Falls kein Futter im Inventar und keine Futterstelle gefunden, suche nach hoechster
               * Konzentration in Umgebung und setze Ameise gegebenenfalls dorthin. Ansonsten weiter
               * zu zufaelliger Positionierung
               */
              continue;
            else {
              List<int[]> availDirections = getAvailableDirections(cA);
              int rD = ThreadLocalRandom.current().nextInt(0, availDirections.size());
              setAPos(cA, availDirections.get(rD)[0], availDirections.get(rD)[1]);
              cA.lastDir = availDirections.get(rD)[2];
            }
          }
        }
      }
    }
    return changes;
  }

  private boolean nextPPos(Ameise a) {
    List<int[]> availDirections = getAvailableDirections(a);
    // alle moeglichen Richtungen, in die die Ameise sich bewegen kann
    int[] xyD = {0, 0};
    int maxPhero = 0;
    boolean erfolgreich = false;
    for (int in = 0; in < availDirections.size(); in++) {
      int[] ar = availDirections.get(in);
      if (speicher.pheromone[ar[0] + a.x][ar[1] + a.y] > maxPhero
          && Math.abs((ar[0] + a.x) - speicher.nestX) >= Math.abs(a.x - speicher.nestX)
          && Math.abs((ar[1] + a.y) - speicher.nestY) >= Math.abs(a.y - speicher.nestY)) {
        maxPhero = speicher.pheromone[ar[0] + a.x][ar[1] + a.y];
        xyD[0] = ar[0];
        xyD[1] = ar[1];
        erfolgreich = true;
      }
    }
    if (erfolgreich)
      setAPos(a, xyD[0], xyD[1]);
    return erfolgreich;
  }

  /*
   * Berechnet Differenz zwischen Nest und aktueller Position. Danach wird die Ameise in Richtung
   * der Achse weitergeschickt, in der das Feld am weitesten entfernt ist.
   */
  private void toNest(Ameise a) {
    int dx = speicher.nestX - a.x;
    int dy = speicher.nestY - a.y;
    if (dx == dy || Math.abs(dx) > Math.abs(dy))
      setAPos(a, (int) Math.signum(dx), 0);
    else
      setAPos(a, 0, (int) Math.signum(dy));
  }

  protected void setAPos(Ameise a, int dx, int dy) {
    speicher.amVerteilung[a.x][a.y]--;// Ameise von aktueller Position "subtrahieren"
    Change ae1 = new Change('A', a.x, a.y);
    a.x += dx;
    a.y += dy;
    speicher.amVerteilung[a.x][a.y]++;// Ameise bei neuer Position hinzufuegen
    Change ae2 = new Change('A', a.x, a.y);
    changes.add(ae1);
    changes.add(ae2);
  }

  public List<int[]> getAvailableDirections(Ameise a) {
    ArrayList<int[]> tempL = new ArrayList<>();
    int dX = 0;
    int dY = 0;
    // i steht fuer die Richtung gegenden Uhrzeigersinn; 0 = ein Feld nach unten, 1 = ein Feld nach
    // links, etc.
    for (int i = 0; i < 4; i++) {
      switch (i) {
        case 0:
          dX = 0;
          dY = 1;
          break;
        case 1:
          dX = -1;
          dY = 0;
          break;
        case 2:
          dX = 0;
          dY = -1;
          break;
        case 3:
          dX = 1;
          dY = 0;
          break;
      }
      // neues Feld innerhalb der Grenzen?
      if ((dX + a.x) < speicher.groesseX && (dY + a.y) < speicher.groesseY && (dX + a.x) >= 0
          && (dY + a.y) >= 0 && a.lastDir != i)
        tempL.add(new int[] {dX, dY, i});
    }
    return tempL;
  }
}
