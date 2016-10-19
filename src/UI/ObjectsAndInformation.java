package UI;

import Physics.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by madsbjoern on 16/10/2016.
 */
public class ObjectsAndInformation extends HBox {
    private PhysicsController pc;
    private CanvasController cc;
    private MainWindow parent;
    private ListView<String> list;
    private TextArea infoBox;
    private TextField gravityTextField;
    private PhysicsObject currentObject;
    private int lastVersion;

    public ObjectsAndInformation(PhysicsController pc, CanvasController cc, MainWindow parent) {
        super();
        this.pc = pc;
        this.cc = cc;
        this.parent = parent;

        setStyle("-fx-background-color: #129906;");
        setPadding(new javafx.geometry.Insets(15, 12, 15, 12));
        setSpacing(10);

        list = new ListView<String>();
        list.setPrefSize(250, 100);
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                currentObject = getObjectFromName(newValue);
                updateInfoBox();
            }
        });
        infoBox = new TextArea();
        infoBox.setEditable(false);
        infoBox.setPrefSize(470, 100);
        getChildren().addAll(list, infoBox);

        // Canvas controls
        Button UpButton = new Button();
        UpButton.setText("Up");
        UpButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.up();}});
        Button DownButton = new Button();
        DownButton.setText("Down");
        DownButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.down();}});
        Button LeftButton = new Button();
        LeftButton.setText("Left");
        LeftButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.left();}});
        Button RightButton = new Button();
        RightButton.setText("Right");
        RightButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.right();}});
        Button ZoomIn = new Button();
        ZoomIn.setText("Zoom In");
        ZoomIn.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.zoomIn();}});
        Button ZoomOut = new Button();
        ZoomOut.setText("Zoom Out");
        ZoomOut.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.zoomOut();}});
        UpButton.setPrefSize(120, 20);
        DownButton.setPrefSize(120, 20);
        LeftButton.setPrefSize(60, 20);
        RightButton.setPrefSize(60, 20);
        ZoomIn.setPrefSize(100, 20);
        ZoomOut.setPrefSize(100, 20);
        Button EditObject = new Button();
        EditObject.setText("Edit");
        EditObject.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {edit();}});
        Button DeleteObject = new Button();
        DeleteObject.setText("Delete");
        DeleteObject.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {delete();}});
        EditObject.setPrefSize(100, 20);
        DeleteObject.setPrefSize(100, 20);
        getChildren().addAll(new VBox(UpButton, new HBox(LeftButton, RightButton), DownButton), new VBox(ZoomIn, ZoomOut, EditObject, DeleteObject));

        gravityTextField = new TextField("9.8");
        gravityTextField.setPrefSize(80, 20);
        Button setGravity = new Button();
        setGravity.setText("Set gravity");
        setGravity.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {setGravity();}});
        Button ResetButton = new Button();
        ResetButton.setText("Reset");
        ResetButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {reset();}});
        ResetButton.setPrefSize(100, 20);
        getChildren().add(new VBox(gravityTextField, setGravity, ResetButton));

        update();
    }

    public void reset() {
        if (AlertBox.show("Are you sure?", "Want to reset", "sure?") == AlertResponse.BUTTON_OK) {
            pc.restoreFromDeepCopy(new PhysicsController());
            parent.reDraw();
        }
    }

    public void delete() {
        if (AlertBox.show("Are you sure", "Want to delete", "sure?") == AlertResponse.BUTTON_OK) {
            if (currentObject != null) {
                pc.deleteObject(currentObject);
                parent.reDraw();
            } else {
                AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
            }
        }
    }

    public void edit() {
        if (currentObject != null) {
            if (currentObject.getClass() == Box1DMovement.class) {
                new NewBox1DMovement(pc, parent, (Box1DMovement)currentObject);
            } else if (currentObject.getClass() == RopeJoint.class) {
                new NewRopeJoint(pc, parent, (RopeJoint)currentObject);
            } else if (currentObject.getClass() == Spring.class) {
                new NewSpring(pc, parent, (Spring)currentObject);
            } else if (currentObject.getClass() == Box2DMovement.class) {
                new NewBox2DMovement(pc, parent, (Box2DMovement)currentObject);
            }
        } else {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }

    public void setGravity() {
        try {
            PhysicsConstants.gravity = Double.parseDouble(gravityTextField.getText());
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }

    public void updateInfoBox() {
        if (currentObject != null) {
            infoBox.setText(currentObject.getInfo());
        } else {
            infoBox.setText("No object selected");
        }
    }

    public void update() {
        if (list.getItems().size() != pc.getAllObjects().size() || pc.getVersion() != lastVersion) {
            list.getItems().clear();
            for (PhysicsObject po : pc.getAllObjects()) {
                list.getItems().add(po.getName());
            }
            lastVersion = pc.getVersion();
            currentObject = null;
        }
        updateInfoBox();
    }

    public PhysicsObject getObjectFromName(String name) {
        for (PhysicsObject po : pc.getAllObjects()) {
            if (po.getName().equals(name)) {
                return po;
            }
        }
        return null;
    }
}
