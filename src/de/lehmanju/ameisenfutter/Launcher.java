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
    boolean errorB = true;
    @FXML
    TitledPane titledPane;
    @FXML
    AnchorPane root;
    Stage stageS;
    // Einstellungsobjekte:
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
        //mainScene = reset(100, new int[] { 500, 500 }, new int[] { 250, 250 }, 5, 1000, 1);
        sp = new Speicher(5, 100, 50, 500, 500);
        area = new DrawArea(titledPane, sp, width);
        Scene scene = new Scene(root, 800, 700);
        stage.setScene(scene);
        try
        {
            stage.show();
        } catch (Exception e)
        {
            //Anzeige Ausnahme
        }
        Simulator sim = new Simulator(sp);
        simService = new SimulationService(sim, sp, width);
        List<GChange> initCh = simService.getInitList();
        for (GChange ch : initCh)
            area.drawImage(ch.view, ch.draw);
        simService.setOnSucceeded(this);
        simService.setPeriod(new Duration(2000));
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
    public void changeSettings()
    {
        pauseSimulation();
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
        confirm.setHeaderText("M\u00F6chtest du diese Werte wirklich verwenden?");
        confirm.setContentText(
                "Der bisherige Zustand wird gel\u00F6scht und eine komplett\nneue Simulation wird gestartet.");
        confirm.showAndWait().ifPresent(result ->
        {
            if (result == ButtonType.OK)
            {
                try
                {
                    int newAmeisen = sp.ameisen;
                    int[] newGroesse = new int[] { sp.groesseX, sp.groesseY };
                    int[] newNestP = new int[] { sp.nestX, sp.nestY };
                    int newfutterQ = sp.futterStellen;
                    int newtimeout = sp.timeout;
                    int newIt = sp.iterations;
                    if (!ameisenF.getText().equals(""))
                        // System.out.println("Text:" + ameisenF.getText());
                        newAmeisen = Integer.valueOf(ameisenF.getText());
                    if (!groesseXF.getText().equals(""))
                        newGroesse[0] = Integer.valueOf(groesseXF.getText());
                    if (!groesseYF.getText().equals(""))
                        newGroesse[1] = Integer.valueOf(groesseYF.getText());
                    if (!fquellenF.getText().equals(""))
                        newfutterQ = Integer.valueOf(fquellenF.getText());
                    if (!timeoutF.getText().equals(""))
                        newtimeout = Integer.valueOf(timeoutF.getText());
                    if (!iterationsF.getText().equals(""))
                        newIt = Integer.valueOf(iterationsF.getText());
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
                            errorB = false;
                        }
                    }
                } catch (NumberFormatException ex)
                {
                    System.err.println("Fehler beim Konvertieren!");
                    ex.printStackTrace();
                }
            }
        });
        if (errorB)
        {
            Alert error = new Alert(AlertType.ERROR);
            error.setHeaderText("Es gab Fehler beim Konvertieren der Einstellungen");
            error.setContentText(
                    "\u00DCberpr\u00FCnochmal deine Eingaben\n(Zahlen gr\u00F6\u00DFer 0 und miteinander kompatibel?)");
            error.show();
        }
        errorB = true;
        root.setDisable(false);
    }

    public void reset(int ameisen, int[] groesse, int[] nest, int futterQ, int timeout, int iterations)
    {
        sp = new Speicher(futterQ, ameisen, 50, groesse[0], groesse[1], nest[0], nest[1], timeout, iterations);
        area = new DrawArea(titledPane, sp, width);
        Simulator sim = new Simulator(sp);
        simService = new SimulationService(sim, sp, width);
        List<GChange> initCh = simService.getInitList();
        for (GChange ch : initCh)
            area.drawImage(ch.view, ch.draw);
        simService.setOnSucceeded(this);
        simService.setPeriod(new Duration(2000));
    }

    @FXML
    public void cancel()
    {
        stageS.close();
        root.setDisable(false);
    }
}
