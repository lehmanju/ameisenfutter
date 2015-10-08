package de.lehmanju.ameisenfutter;

import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Launcher extends Application implements EventHandler<WorkerStateEvent>
{
    Speicher sp;
    DrawArea area;
    SimulationService simService;
    int width = 20;
    @FXML
    TitledPane titledPane;
    @FXML
    AnchorPane root;
    Stage stageS;
    //Einstellungsobjekte:
    @FXML
    TextField ameisenF;
    @FXML
    TextField groesseXF;
    @FXML
    TextField groesseYF;
    @FXML
    TextField nestXF;
    @FXML
    TextField nestYF;
    @FXML
    TextField fquellenF;
    @FXML
    TextField timeoutF;
    @FXML
    TextField iterationsF;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Fenster.fxml"));
        loader.setController(this);
        loader.load();
        sp = new Speicher(5, 100, 50, 500, 500);
        area = new DrawArea(titledPane, sp, width);
        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        stage.show();
        //stage.setWidth(stage.getWidth() + 1);
        Simulator sim = new Simulator(sp);
        simService = new SimulationService(sim, sp, width);
        List<GChange> initCh = simService.getInitList();
        for (GChange ch : initCh)
            area.drawImage(ch.view, ch.draw);
        simService.setOnSucceeded(this);
        simService.setPeriod(new Duration(2000));
        //simService.setIterations(10);
        //simService.start();
    }

    @Override
    public void handle(WorkerStateEvent wsevent)
    {
        List<GChange> changes = simService.getValue();
        for (GChange ch : changes)
        {
            area.drawImage(ch.view, ch.draw);
        }
    }

    @FXML
    public void startSimulation()
    {
        simService.restart();
    }

    @FXML
    public void pauseSimulation()
    {
        simService.pause();
    }

    @FXML
    public void resetSimulation()
    {
        simService.pause();
    }

    @FXML
    public void changeSettings()
    {
        root.setDisable(true);
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/Einstellungen.fxml"));
        loader.setController(this);
        Parent p = null;
        try
        {
            p = loader.load();
        } catch (IOException e)
        {
            System.out.println("Probleme beim Öffnen des Fensters");
            e.printStackTrace();
            return;
        }
        ameisenF.setPromptText(String.valueOf(sp.ameisen));
        groesseXF.setPromptText(String.valueOf(sp.groesseX));
        groesseYF.setPromptText(String.valueOf(sp.groesseY));
        nestXF.setPromptText(String.valueOf(sp.nestX + 1));
        nestYF.setPromptText(String.valueOf(sp.nestY + 1));
        fquellenF.setPromptText(String.valueOf(sp.futterStellen));
        timeoutF.setPromptText(String.valueOf(sp.timeout));
        iterationsF.setPromptText(String.valueOf(sp.iterations));
        stageS = new Stage();
        Scene sc = new Scene(p, 320, 270);
        stageS.setScene(sc);
        stageS.setResizable(false);
        stageS.show();
    }

    @FXML
    public void apply()
    {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setHeaderText("Möchtest du diese Werte wirklich verwenden?");
        confirm.setContentText("Der bisherige Zustand wird gelöscht und eine komplett neue Simulation wird gestartet.");
        confirm.showAndWait().ifPresent(result ->
        {
            if (result == ButtonType.OK)
            {
                try
                {
                    int newAmeisen = Integer.valueOf(ameisenF.getText());
                    int[] newGroesse = { Integer.valueOf(groesseXF.getText()), Integer.valueOf(groesseYF.getText()) };
                    int[] newNestP = { Integer.valueOf(nestXF.getText()), Integer.valueOf(nestYF.getText()) };
                    int newfutterQ = Integer.valueOf(fquellenF.getText());
                    int newtimeout = Integer.valueOf(timeoutF.getText());
                    int newIt = Integer.valueOf(iterationsF.getText());
                    boolean groesserNull = newAmeisen > 0 && newGroesse[0] > 0 && newGroesse[1] > 0 && newNestP[0] > 0
                            && newNestP[1] > 0 && newfutterQ > 0 && newtimeout > 0 && newIt > 0;
                    if (groesserNull)
                    {
                        if (newNestP[0] <= newGroesse[0] && newNestP[1] <= newGroesse[1]
                                && newfutterQ < (newGroesse[0] * newGroesse[1] - 1))
                        {
                            confirm.close();
                            stageS.close();
                            reset(newAmeisen, newGroesse, newNestP, newfutterQ, newtimeout, newIt);
                            root.setDisable(false);                           
                        }
                    }
                } catch (NumberFormatException ex)
                {
                    System.err.println("Werte sind nicht in Zahlen konvertierbar!");
                }
            }
        });
    }

    public void reset(int ameisen, int[] groesse, int[] nest, int futterQ, int timeout, int iterations)
    {

    }

    @FXML
    public void cancel()
    {
        stageS.close();
        root.setDisable(false);
    }
}
