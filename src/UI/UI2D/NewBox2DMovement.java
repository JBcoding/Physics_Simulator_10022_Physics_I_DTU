package UI.UI2D;

import Physics.Physics2D.Box2DMovement;
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
public class NewBox2DMovement extends Stage {
    private int width = 200;
    private int height = 300;

    private TextField positionX, positionY, velocityX, velocityY, mass;
    private PhysicsController pc;
    private MainWindow parent;
    private CheckBox isGravityDisabled;

    private Box2DMovement box;

    public NewBox2DMovement(PhysicsController pc, MainWindow parent, Box2DMovement box) {
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
        mass = new TextField((box != null) ? String.valueOf(box.getMass()) : "1");
        isGravityDisabled = new CheckBox("Disable Gravity");
        if (box != null) {
            isGravityDisabled.setSelected(box.getGravityDisabled());
        }
        root.getChildren().addAll(new Label("Position (X, Y) in meter"), new HBox(positionX, positionY),
                new Label("Velocity (X, Y) in m/s"), new HBox(velocityX, velocityY),
                new Label("Mass in kg"), mass,
                isGravityDisabled);
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
            double mass = Double.parseDouble(this.mass.getText());
            boolean gravityEnabled = isGravityDisabled.isSelected();
            Box2DMovement newBox = new Box2DMovement(position, mass, velocity);
            newBox.setGravityDisabled(gravityEnabled);
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
