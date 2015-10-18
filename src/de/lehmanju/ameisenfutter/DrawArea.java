package de.lehmanju.ameisenfutter;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DrawArea implements EventHandler<MouseEvent> {
    Pane rootPane;
    TitledPane tPane;
    int width = 20;
    Speicher speicher;

    public DrawArea(TitledPane p, Speicher sp, int width) {
        this.width = width;
        rootPane = new Pane();
        rootPane.setCache(true);
        tPane = p;
        speicher = sp;
        int canvaslaenge = (int) (Math.floor(1024 / (width - 1) / 10) * 10);
        // auf Zehner gerundet
        //System.out.println(canvaslaenge);
        Canvas part = new Canvas(canvaslaenge * (width - 1), canvaslaenge * (width - 1));
        GraphicsContext gc = part.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.GREEN);
        for (int l = 0; l < canvaslaenge && l < sp.groesseX; l++) {
            for (int k = 0; k < canvaslaenge && k < sp.groesseY; k++) {
                gc.strokeLine(l * (width - 1), k * (width - 1), l * (width - 1) + width, k * (width - 1));
                gc.strokeLine(l * (width - 1), k * (width - 1), l * (width - 1), k * (width - 1) + width);
            }
        }
        Image img = part.snapshot(null, null);
        BackgroundImage bImg = new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        rootPane.setPrefSize(((width - 1) * sp.groesseX), ((width - 1) * sp.groesseY));
        rootPane.setBackground(new Background(null, bImg));
        rootPane.addEventHandler(MouseEvent.MOUSE_MOVED, this);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(rootPane);
        scroll.setHvalue(scroll.getHmax() / 2);
        scroll.setVvalue(scroll.getVmax() / 2);
        tPane.setContent(scroll);
    }

    public void drawImage(ImageView view, boolean draw) {
        if (draw)
            rootPane.getChildren().add(view);
        else
            rootPane.getChildren().remove(view);
    }

    @Override
    public void handle(MouseEvent event) {
        int x = (int) Math.floor(event.getX() / (width - 1));
        int y = (int) Math.floor(event.getY() / (width - 1));
        if (x >= speicher.groesseX || y >= speicher.groesseY)
            return;
        String text;
        if (x == speicher.nestX && y == speicher.nestY)
            text = "(X:" + (x + 1) + ";Y:" + (y + 1) + ") Ameisen: " + speicher.amVerteilung[x][y] + "; Futter: " + speicher.futterNest
                    + "; Pheromone: " + speicher.pheromone[x][y];
        else
            text = "(X:" + (x + 1) + ";Y:" + (y + 1) + ") Ameisen: " + speicher.amVerteilung[x][y] + "; Futter: "
                    + speicher.futterVerteilung[x][y] + "; Pheromone: " + speicher.pheromone[x][y];
        tPane.setText(text);
    }

    public void removeAll() {
        rootPane.getChildren().clear();
    }

}
