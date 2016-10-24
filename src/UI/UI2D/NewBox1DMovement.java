package UI.UI2D;

import Physics.Physics2D.Box;
import Physics.Physics2D.Box1DMovement;
import Physics.Physics2D.Vector2D;
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
import Physics.*;

/**
 * Created by madsbjoern on 16/10/2016.
 */
public class NewBox1DMovement extends Stage {
    private int width = 200;
    private int height = 480;

    private TextField positionX, positionY, velocityX, velocityY, angle, mass, staticFrictionConstant, kineticFrictionConstant;
    private PhysicsController pc;
    private MainWindow parent;
    private ComboBox<Box> boxSelector;
    private CheckBox isGravityDisabled;

    private Box1DMovement box;

    public NewBox1DMovement(PhysicsController pc, MainWindow parent, Box1DMovement box) {
        this.pc = pc;
        this.parent = parent;
        this.box = box;

        VBox root = new VBox();
        Scene scene = new Scene(root, width, height);
        this.setMaxWidth(width);
        this.setMaxHeight(height);
        this.setMinWidth(width);
        this.setMinHeight(height);
        setTitle("New Box 1D Movement");
        setScene(scene);
        scene.setOnKeyPressed((KeyEvent evt)->{
            if ((evt.getCode() == KeyCode.ESCAPE)) {
                close();
            }
        });
        positionX = new TextField((box != null) ? String.valueOf(box.getPosition().getX()) : "0");
        positionY = new TextField((box != null) ? String.valueOf(box.getPosition().getY()) : "0");
        velocityX = new TextField((box != null) ? String.valueOf(box.getVelocity().getX()) : "0");
        velocityY = new TextField((box != null) ? String.valueOf(box.getVelocity().getY()) : "0");
        angle = new TextField((box != null) ? String.valueOf(box.getAngle()) : "0");
        mass = new TextField((box != null) ? String.valueOf(box.getMass()) : "1");
        staticFrictionConstant = new TextField((box != null) ? String.valueOf(box.getStaticFrictionConstant()) : "0");
        kineticFrictionConstant = new TextField((box != null) ? String.valueOf(box.getKineticFrictionConstant()) : "0");
        boxSelector = new ComboBox();
        boxSelector.getItems().addAll(pc.getBoxes());
        isGravityDisabled = new CheckBox("Disable Gravity");
        if (box != null) {
            isGravityDisabled.setSelected(box.getGravityDisabled());
            if (box.getFrictionBox() != null) {
                boxSelector.getSelectionModel().select(box.getFrictionBox());
            }
        }
        root.getChildren().addAll(new Label("Position (X, Y) in meter"), new HBox(positionX, positionY),
                                  new Label("Velocity (X, Y) in m/s"), new HBox(velocityX, velocityY),
                                  new Label("Angle in radians"), angle,
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
            Vector2D position = new Vector2D(Double.parseDouble(positionX.getText()), Double.parseDouble(positionY.getText()));
            Vector2D velocity = new Vector2D(Double.parseDouble(velocityX.getText()), Double.parseDouble(velocityY.getText()));
            double angle = Double.parseDouble(this.angle.getText());
            double mass = Double.parseDouble(this.mass.getText());
            double staticFrictionConstant = Double.parseDouble(this.staticFrictionConstant.getText());
            double kineticFrictionConstant = Double.parseDouble(this.kineticFrictionConstant.getText());
            boolean gravityEnabled = isGravityDisabled.isSelected();
            Box frictionBox = boxSelector.getSelectionModel().getSelectedItem();
            if (frictionBox != null && frictionBox.getClass() == Box1DMovement.class) {
                if (((Box1DMovement)frictionBox).getFrictionBox() != null) {
                    AlertBox.info("Error", "You can't have a friction box, that also have a friction box", Alert.AlertType.ERROR);
                    throw new IllegalArgumentException();
                }
            }
            Box1DMovement newBox = new Box1DMovement(position, angle, mass, velocity);
            newBox.setStaticFrictionConstant(staticFrictionConstant);
            newBox.setKineticFrictionConstant(kineticFrictionConstant);
            newBox.setGravityDisabled(gravityEnabled);
            newBox.setFrictionBox(frictionBox);
            if (box != null) {
                box.copyFromObject(newBox);
            } else {
                pc.addBox(newBox);
            }
            parent.reDraw();
            close();
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }
}
