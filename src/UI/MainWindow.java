package UI;

import UI.UI2D.*;
import UI.UI3D.CanvasController3D;
import UI.UI3D.NewBox3D;
import UI.UI3D.NewRopeJoint3D;
import UI.UI3D.NewSpring3D;
import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
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
    public static final int WINDOW_ID_NEW_BOX_3D = 5;
    public static final int WINDOW_ID_NEW_SPRING_3D = 6;
    public static final int WINDOW_ID_NEW_ROPE_JOINT_3D = 7;

    private int width = 1080;
    private int height = 830;

    private PhysicsController physicsController;
    private CanvasController canvasController;
    private ObjectsAndInformation objectsAndInformation;
    private TimeController timeController;

    private boolean _3D;

    @Override
    public void start(Stage primaryStage) {
        physicsController = new PhysicsController();
        if (AlertBox.show2Choices("2D or 3D?", "2D or 3D?", "Do you want to simulate 2D Physics or 3D Physics?", "2D", "3D") == AlertResponse.BUTTON_CHOICE1) {
            canvasController = new CanvasController2D(this);
            _3D = false;
        } else {
            canvasController = new CanvasController3D(this);
            _3D = true;
        }
        objectsAndInformation = new ObjectsAndInformation(physicsController, canvasController, this, _3D);
        timeController = new TimeController(physicsController, this);

        primaryStage.setTitle("Physics");
        HBox top = new HBox();
        addButtons(top);
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(canvasController.getNode());
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

        if (!_3D) {
            Button Box1DM = new Button();
            Box1DM.setText("Add Box 1D Movement");
            Box1DM.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_BOX_1D_MOVEMENT);}});
            root.getChildren().add(Box1DM);

            Button Box2DM = new Button();
            Box2DM.setText("Add Box 2D Movement");
            Box2DM.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_BOX_2D_MOVEMENT);}});
            root.getChildren().add(Box2DM);

            Button Spring = new Button();
            Spring.setText("Add Spring");
            Spring.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_SPRING);}});
            root.getChildren().add(Spring);

            Button RopeJoint = new Button();
            RopeJoint.setText("Add Rope Joint");
            RopeJoint.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_ROPE_JOINT);}});
            root.getChildren().add(RopeJoint);
        } else {
            Button Box3D = new Button();
            Box3D.setText("Add Box 3D");
            Box3D.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_BOX_3D);}});
            root.getChildren().add(Box3D);

            Button Spring = new Button();
            Spring.setText("Add Spring 3D");
            Spring.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_SPRING_3D);}});
            root.getChildren().add(Spring);

            Button RopeJoint = new Button();
            RopeJoint.setText("Add Rope Joint 3D");
            RopeJoint.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {openWindow(WINDOW_ID_NEW_ROPE_JOINT_3D);}});
            root.getChildren().add(RopeJoint);
        }
    }

    public void openWindow(int windowID) {
        switch (windowID) {
            case WINDOW_ID_NEW_BOX_1D_MOVEMENT : new NewBox1DMovement(physicsController, this, null); break;
            case WINDOW_ID_NEW_SPRING : new NewSpring(physicsController, this, null); break;
            case WINDOW_ID_NEW_ROPE_JOINT : new NewRopeJoint(physicsController, this, null); break;
            case WINDOW_ID_NEW_BOX_2D_MOVEMENT : new NewBox2DMovement(physicsController, this, null); break;
            case WINDOW_ID_NEW_BOX_3D : new NewBox3D(physicsController, this, null); break;
            case WINDOW_ID_NEW_SPRING_3D : new NewSpring3D(physicsController, this, null); break;
            case WINDOW_ID_NEW_ROPE_JOINT_3D : new NewRopeJoint3D(physicsController, this, null); break;
            default: break;
        }
    }

    public void reDraw() {
        canvasController.update(physicsController);
        objectsAndInformation.update();
        timeController.updateInfoBox();
    }
}
