package UI;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import Physics.*;

/**
 * Created by madsbjoern on 16/10/2016.
 */
public class MainWindow extends Application {
    public static final int WINDOW_ID_NEW_BOX_1D_MOVEMENT = 1;
    public static final int WINDOW_ID_NEW_SPRING = 2;
    public static final int WINDOW_ID_NEW_ROPE_JOINT = 3;
    public static final int WINDOW_ID_NEW_BOX_2D_MOVEMENT = 4;

    private int width = 1080;
    private int height = 810;

    private PhysicsController physicsController;
    private CanvasController canvasController;
    private ObjectsAndInformation objectsAndInformation;
    private TimeController timeController;

    @Override
    public void start(Stage primaryStage) {
        physicsController = new PhysicsController();
        Canvas canvas = new Canvas(900, 601);
        canvasController = new CanvasController(canvas, this);
        objectsAndInformation = new ObjectsAndInformation(physicsController, canvasController, this);
        timeController = new TimeController(physicsController, this);

        primaryStage.setTitle("Physics");
        HBox top = new HBox();
        addButtons(top);
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(canvas);
        root.setBottom(objectsAndInformation);
        root.setRight(timeController);
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.setMaxWidth(width);
        primaryStage.setMaxHeight(height);
        primaryStage.setMinWidth(width);
        primaryStage.setMinHeight(height);
        primaryStage.show();

        reDraw();
    }

    public void addButtons(HBox root) {
        root.setStyle("-fx-background-color: #336699;");
        root.setPadding(new Insets(15, 12, 15, 12));
        root.setSpacing(10);

        Button Box1DM = new Button();
        Box1DM.setText("Add Box 1D Movement");
        Box1DM.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_BOX_1D_MOVEMENT);}});
        root.getChildren().add(Box1DM);

        Button Spring = new Button();
        Spring.setText("Add Spring");
        Spring.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_SPRING);}});
        root.getChildren().add(Spring);

        Button RopeJoint = new Button();
        RopeJoint.setText("Add Rope Joint");
        RopeJoint.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_ROPE_JOINT);}});
        root.getChildren().add(RopeJoint);

        Button Box2DM = new Button();
        Box2DM.setText("Add Box 2D Movement");
        Box2DM.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_BOX_2D_MOVEMENT);}});
        root.getChildren().add(Box2DM);
    }

    public void openWindow(int windowID) {
        switch (windowID) {
            case WINDOW_ID_NEW_BOX_1D_MOVEMENT : new NewBox1DMovement(physicsController, this, null); break;
            case WINDOW_ID_NEW_SPRING : new NewSpring(physicsController, this, null); break;
            case WINDOW_ID_NEW_ROPE_JOINT : new NewRopeJoint(physicsController, this, null); break;
            case WINDOW_ID_NEW_BOX_2D_MOVEMENT : new NewBox2DMovement(physicsController, this, null); break;
            default: break;
        }
    }

    public void reDraw() {
        canvasController.update(physicsController);
        objectsAndInformation.update();
        timeController.updateInfoBox();
    }
}
