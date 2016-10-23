package UI;

import Physics.*;
import Physics.Physics2D.*;
import Physics.Physics3D.*;
import UI.UI2D.NewBox1DMovement;
import UI.UI2D.NewBox2DMovement;
import UI.UI2D.NewRopeJoint;
import UI.UI2D.NewSpring;
import UI.UI3D.NewBox3D;
import UI.UI3D.NewRopeJoint3D;
import UI.UI3D.NewSpring3D;
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
    private TextField gravityTextField, gravityTextFieldX, gravityTextFieldY, gravityTextFieldZ;
    private PhysicsObject currentObject;
    private PhysicsObject3D currentObject3D;
    private int lastVersion;

    private boolean _3D;

    public ObjectsAndInformation(PhysicsController pc, CanvasController cc, MainWindow parent, boolean _3D) {
        super();
        this.pc = pc;
        this.cc = cc;
        this.parent = parent;
        this._3D = _3D;

        setStyle("-fx-background-color: #129906;");
        setPadding(new javafx.geometry.Insets(15, 12, 15, 12));
        setSpacing(10);

        list = new ListView<String>();
        list.setPrefSize(200, 100);
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                currentObject = getObjectFromName(newValue);
                currentObject3D = getObjectFromName3D(newValue);
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
        Button ForwardButton = new Button();
        ForwardButton.setText("Forward");
        ForwardButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.forward();}});
        Button BackwardsButton = new Button();
        BackwardsButton.setText("Backward");
        BackwardsButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.backward();}});
        Button ZoomIn = new Button();
        ZoomIn.setText("Zoom In");
        ZoomIn.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.zoomIn();}});
        Button ZoomOut = new Button();
        ZoomOut.setText("Zoom Out");
        ZoomOut.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {cc.zoomOut();}});
        UpButton.setPrefSize(60, 20);
        DownButton.setPrefSize(60, 20);
        LeftButton.setPrefSize(60, 20);
        RightButton.setPrefSize(60, 20);
        ForwardButton.setPrefSize(60, 20);
        BackwardsButton.setPrefSize(60, 20);
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
        HBox forwadAndBackward = new HBox(ForwardButton, BackwardsButton);
        if (!_3D) {
            forwadAndBackward = new HBox();
        }
        getChildren().addAll(new VBox(new HBox(UpButton, DownButton), new HBox(LeftButton, RightButton), forwadAndBackward), new VBox(ZoomIn, ZoomOut, EditObject, DeleteObject));

        gravityTextField = new TextField("9.8");
        gravityTextField.setPrefSize(80, 20);
        gravityTextFieldX = new TextField("0");
        gravityTextFieldX.setPrefSize(50, 20);
        gravityTextFieldY = new TextField("-9.8");
        gravityTextFieldY.setPrefSize(50, 20);
        gravityTextFieldZ = new TextField("0");
        gravityTextFieldZ.setPrefSize(50, 20);
        HBox gravityBox = new HBox(gravityTextFieldX, gravityTextFieldY, gravityTextFieldZ);
        Button setGravity = new Button();
        setGravity.setText("Set gravity");
        setGravity.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {setGravity();}});
        Button ResetButton = new Button();
        ResetButton.setText("Reset");
        ResetButton.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {reset();}});
        ResetButton.setPrefSize(100, 20);
        if (!_3D) {
            getChildren().add(new VBox(gravityTextField, setGravity, ResetButton));
        } else {
            getChildren().add(new VBox(gravityBox, setGravity, ResetButton));
        }


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
            } else if (currentObject3D != null) {
                pc.deleteObject3D(currentObject3D);
                parent.reDraw();
            } else {
                AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
            }
        }
    }

    public void edit() {
        if (!_3D) {
            if (currentObject != null) {
                if (currentObject.getClass() == Box1DMovement.class) {
                    new NewBox1DMovement(pc, parent, (Box1DMovement) currentObject);
                } else if (currentObject.getClass() == RopeJoint.class) {
                    new NewRopeJoint(pc, parent, (RopeJoint) currentObject);
                } else if (currentObject.getClass() == Spring.class) {
                    new NewSpring(pc, parent, (Spring) currentObject);
                } else if (currentObject.getClass() == Box2DMovement.class) {
                    new NewBox2DMovement(pc, parent, (Box2DMovement) currentObject);
                }
            } else {
                AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
            }
        } else {
            if (currentObject3D != null) {
                if (currentObject3D.getClass() == Box3D1DMovement.class || currentObject3D.getClass() == Box3D3DMovement.class) {
                    new NewBox3D(pc, parent, (Box3D) currentObject3D);
                } else if (currentObject3D.getClass() == Spring3D.class) {
                    new NewSpring3D(pc, parent, (Spring3D) currentObject3D);
                } else if (currentObject3D.getClass() == RopeJoint3D.class) {
                    new NewRopeJoint3D(pc, parent, (RopeJoint3D) currentObject3D);
                }
            } else {
                AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
            }
        }
    }

    public void setGravity() {
        try {
            if (!_3D) {
                PhysicsConstants.gravity = Double.parseDouble(gravityTextField.getText());
            } else {
                PhysicsConstants.gravityVector = new Vector3D(Double.parseDouble(gravityTextFieldX.getText()), Double.parseDouble(gravityTextFieldY.getText()), Double.parseDouble(gravityTextFieldZ.getText()));
            }
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }

    public void updateInfoBox() {
        if (!_3D) {
            if (currentObject != null) {
                infoBox.setText(currentObject.getInfo());
            } else {
                infoBox.setText("No object selected");
            }
        } else {
            if (currentObject3D != null) {
                infoBox.setText(currentObject3D.getInfo());
            } else {
                infoBox.setText("No object selected");
            }
        }
    }

    public void update() {
        if (!_3D) {
            if (list.getItems().size() != pc.getAllObjects().size() || pc.getVersion() != lastVersion) {
                list.getItems().clear();
                for (PhysicsObject po : pc.getAllObjects()) {
                    list.getItems().add(po.getName());
                }
                lastVersion = pc.getVersion();
                currentObject = null;
            }
        } else {
            if (list.getItems().size() != pc.getAllObjects3D().size() || pc.getVersion() != lastVersion) {
                list.getItems().clear();
                for (PhysicsObject3D po : pc.getAllObjects3D()) {
                    list.getItems().add(po.getName());
                }
                lastVersion = pc.getVersion();
                currentObject3D = null;
            }
        }
        updateInfoBox();
    }

    public PhysicsObject3D getObjectFromName3D(String name) {
        for (PhysicsObject3D po : pc.getAllObjects3D()) {
            if (po.getName().equals(name)) {
                return po;
            }
        }
        return null;
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
