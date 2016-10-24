package UI.UI3D;

import Physics.Physics3D.Box3D;
import Physics.Physics3D.Box3D1DMovement;
import Physics.Physics3D.Box3D3DMovement;
import Physics.Physics3D.Vector3D;
import Physics.PhysicsController;
import UI.AlertBox;
import UI.MainWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by madsbjoern on 23/10/2016.
 */
public class NewBox3D extends Stage {
    private int width = 200;
    private int height = 500;

    private TextField positionX, positionY, positionZ, velocityX, velocityY, velocityZ, directionX, directionY, directionZ, mass, staticFrictionConstant, kineticFrictionConstant;
    private PhysicsController pc;
    private MainWindow parent;
    private ComboBox<Box3D> boxSelector;
    private CheckBox isGravityDisabled, hasDirection;

    private Box3D box;

    public NewBox3D(PhysicsController pc, MainWindow parent, Box3D box) {
        this.pc = pc;
        this.parent = parent;
        this.box = box;

        VBox root = new VBox();
        Scene scene = new Scene(root, width, height);
        this.setMaxWidth(width);
        this.setMaxHeight(height);
        this.setMinWidth(width);
        this.setMinHeight(height);
        setTitle("New Box 3D");
        setScene(scene);
        scene.setOnKeyPressed((KeyEvent evt)->{
            if ((evt.getCode() == KeyCode.ESCAPE)) {
                close();
            }
        });
        positionX = new TextField((box != null) ? String.valueOf(box.getPosition().getX()) : "0");
        positionY = new TextField((box != null) ? String.valueOf(box.getPosition().getY()) : "0");
        positionZ = new TextField((box != null) ? String.valueOf(box.getPosition().getZ()) : "0");
        velocityX = new TextField((box != null) ? String.valueOf(box.getVelocity().getX()) : "0");
        velocityY = new TextField((box != null) ? String.valueOf(box.getVelocity().getY()) : "0");
        velocityZ = new TextField((box != null) ? String.valueOf(box.getVelocity().getZ()) : "0");
        directionX = new TextField((box != null && box.getClass() == Box3D1DMovement.class) ? String.valueOf(((Box3D1DMovement)box).getDirection().getX()) : "1");
        directionY = new TextField((box != null && box.getClass() == Box3D1DMovement.class) ? String.valueOf(((Box3D1DMovement)box).getDirection().getY()) : "0");
        directionZ = new TextField((box != null && box.getClass() == Box3D1DMovement.class) ? String.valueOf(((Box3D1DMovement)box).getDirection().getZ()) : "0");
        mass = new TextField((box != null) ? String.valueOf(box.getMass()) : "1");
        staticFrictionConstant = new TextField((box != null) ? String.valueOf(box.getStaticFrictionConstant()) : "0");
        kineticFrictionConstant = new TextField((box != null) ? String.valueOf(box.getKineticFrictionConstant()) : "0");
        boxSelector = new ComboBox();
        boxSelector.getItems().addAll(pc.getBox3Ds());
        isGravityDisabled = new CheckBox("Disable Gravity");
        hasDirection = new CheckBox("Has direction");
        if (box != null) {
            isGravityDisabled.setSelected(box.getGravityDisabled());
            if (box.getFrictionBox() != null) {
                boxSelector.getSelectionModel().select(box.getFrictionBox());
            }
            hasDirection.setSelected(box.getClass() == Box3D1DMovement.class);
        }
        root.getChildren().addAll(new Label("Position (X, Y, Z) in meter"), new HBox(positionX, positionY, positionZ),
                                    new Label("Velocity (X, Y, Z) in m/s"), new HBox(velocityX, velocityY, velocityZ),
                                    hasDirection,
                                    new Label("If it have a direction, which one?"), new HBox(directionX, directionY, directionZ),
                                    new Label("Mass in kg"), mass,
                                    new Label("Static Friction Constant"), staticFrictionConstant,
                                    new Label("kinetic Friction Constant"), kineticFrictionConstant,
                                    isGravityDisabled,
                                    new Label("Friction box (can be null)"), boxSelector);
        Button close = new Button("Cancel");
        Button add = new Button((box != null) ? "Change" : "Add");
        close.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {close();}});
        add.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {addAndClose();}});
        close.setPrefSize(width, 20);
        add.setPrefSize(width, 20);
        root.getChildren().addAll(add, close);
        show();
    }

    public void addAndClose() {
        try {
            Vector3D position = new Vector3D(Double.parseDouble(positionX.getText()), Double.parseDouble(positionY.getText()), Double.parseDouble(positionZ.getText()));
            Vector3D velocity = new Vector3D(Double.parseDouble(velocityX.getText()), Double.parseDouble(velocityY.getText()), Double.parseDouble(velocityZ.getText()));
            Vector3D direction = new Vector3D(Double.parseDouble(directionX.getText()), Double.parseDouble(directionY.getText()), Double.parseDouble(directionZ.getText()));
            double mass = Double.parseDouble(this.mass.getText());
            double staticFrictionConstant = Double.parseDouble(this.staticFrictionConstant.getText());
            double kineticFrictionConstant = Double.parseDouble(this.kineticFrictionConstant.getText());
            boolean gravityDisabled = isGravityDisabled.isSelected();
            boolean hasDirectionBool = hasDirection.isSelected();
            Box3D frictionBox = boxSelector.getSelectionModel().getSelectedItem();
            if (frictionBox != null) {
                if (frictionBox.getFrictionBox() != null) {
                    AlertBox.info("Error", "You can't have a friction box, that also have a friction box", Alert.AlertType.ERROR);
                    throw new IllegalArgumentException();
                }
            }
            Box3D newBox;
            if (hasDirectionBool) {
                newBox = new Box3D1DMovement(position, velocity, direction);
            } else {
                newBox = new Box3D3DMovement(position, velocity);
            }
            newBox.setMass(mass);
            newBox.setStaticFrictionConstant(staticFrictionConstant);
            newBox.setKineticFrictionConstant(kineticFrictionConstant);
            newBox.setGravityDisabled(gravityDisabled);
            newBox.setFrictionBox(frictionBox);
            if (box != null) {
                box.copyFromObject(newBox);
            } else {
                pc.addBox3D(newBox);
            }
            parent.reDraw();
            close();
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }
}
