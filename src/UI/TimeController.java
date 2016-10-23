package UI;

import Physics.PhysicsController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

/**
 * Created by madsbjoern on 16/10/2016.
 */
public class TimeController extends VBox {
    private PhysicsController pc, backupPC;
    private MainWindow parent;

    private TextField timeStepTextField, timeStepsPerFrame;
    private Timeline timeline;
    private TextArea infoBox;

    public TimeController(PhysicsController pc, MainWindow parent) {
        super();
        this.pc = pc;
        this.parent = parent;

        setStyle("-fx-background-color: #ee8100;");
        setPadding(new javafx.geometry.Insets(15, 12, 15, 12));
        setSpacing(10);

        // time step
        getChildren().add(new Label("Current time step:"));
        timeStepTextField = new TextField(String.valueOf(pc.getTimeStep()));
        getChildren().add(timeStepTextField);
        Button timeStepButton = new Button();
        timeStepButton.setText("set time step");
        timeStepButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {setTimeStep();}});
        getChildren().add(timeStepButton);

        // go forward x time step
        getChildren().add(new Label("Go forward:"));
        Button goForward1TimeStep = new Button();
        goForward1TimeStep.setText("1 time step");
        goForward1TimeStep.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {goForwardXTimeSteps(1);}});
        getChildren().add(goForward1TimeStep);
        Button goForward10TimeStep = new Button();
        goForward10TimeStep.setText("10 time steps");
        goForward10TimeStep.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {goForwardXTimeSteps(10);}});
        getChildren().add(goForward10TimeStep);
        Button goForward100TimeStep = new Button();
        goForward100TimeStep.setText("100 time steps");
        goForward100TimeStep.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {goForwardXTimeSteps(100);}});
        getChildren().add(goForward100TimeStep);
        Button goForward1000TimeStep = new Button();
        goForward1000TimeStep.setText("1 000 time steps");
        goForward1000TimeStep.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {goForwardXTimeSteps(1000);}});
        getChildren().add(goForward1000TimeStep);
        Button goForward10000TimeStep = new Button();
        goForward10000TimeStep.setText("10 000 time steps");
        goForward10000TimeStep.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {goForwardXTimeSteps(10000);}});
        getChildren().add(goForward10000TimeStep);

        // play
        getChildren().add(new Label("Time steps per frame"));
        timeStepsPerFrame = new TextField("17");
        getChildren().add(timeStepsPerFrame);
        Button Play = new Button();
        Play.setText("Play");
        Play.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {play();}});
        Button Stop = new Button();
        Stop.setText("Stop");
        Stop.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {stop();}});
        getChildren().add(new HBox(Play, Stop));

        // backup and restore
        Button Backup = new Button();
        Backup.setText("Backup");
        Backup.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {backup();}});
        Button Restore = new Button();
        Restore.setText("Restore");
        Restore.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {restore();}});
        getChildren().add(new HBox(Backup, Restore));

        // info box
        infoBox = new TextArea();
        infoBox.setEditable(false);
        infoBox.setPrefSize(80, 120);
        getChildren().add(infoBox);
        updateInfoBox();
    }

    public void backup() {
        backupPC = pc.deepCopy();
    }

    public void restore() {
        if (backupPC != null) {
            pc.restoreFromDeepCopy(backupPC);
            parent.reDraw();
        } else {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }

    public void play() {
        if (timeline != null) {
            timeline.stop();
        }
        try {
            timeline = new Timeline(new KeyFrame(
                    Duration.millis(17), // 58.823529412 FPS / close enough
                    ae -> goForwardXTimeSteps(Integer.parseInt(timeStepsPerFrame.getText()))));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }

    public void stop() {
        timeline.stop();
    }

    public void goForwardXTimeSteps(int x) {
        for (int i = 0; i < x; i ++) {
            pc.update();
        }
        parent.reDraw();
    }

    public void setTimeStep() {
        try {
            pc.setTimeStep(Double.parseDouble(timeStepTextField.getText()));
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
        updateInfoBox();
    }

    public void updateInfoBox() {
        infoBox.setText("Time: " + pc.getTime() + " s\nTime step: " + pc.getTimeStep());
    }
}
