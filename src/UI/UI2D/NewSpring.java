package UI.UI2D;

import Physics.Physics2D.Box;
import Physics.Physics2D.Spring;
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
public class NewSpring extends Stage {
    private int width = 430;
    private int height = 340;

    private TextField positionX, positionY, springConstant, length;
    private ComboBox<Box> boxSelector1, boxSelector2;
    private RadioButton boxOrPointBox, boxOrPointPoint;
    private PhysicsController pc;
    private MainWindow parent;

    private Spring spring;

    public NewSpring(PhysicsController pc, MainWindow parent, Spring spring) {
        this.pc = pc;
        this.parent = parent;
        this.spring = spring;

        VBox root = new VBox();
        Scene scene = new Scene(root, width, height);
        this.setMaxWidth(width);
        this.setMaxHeight(height);
        this.setMinWidth(width);
        this.setMinHeight(height);
        setTitle("New Spring");
        setScene(scene);
        scene.setOnKeyPressed((KeyEvent evt)->{
            if ((evt.getCode() == KeyCode.ESCAPE)) {
                close();
            }
        });
        positionX = new TextField((spring != null && spring.getPoint() != null) ? String.valueOf(spring.getPoint().getX()) : "0");
        positionY = new TextField((spring != null && spring.getPoint() != null) ? String.valueOf(spring.getPoint().getY()) : "0");
        springConstant = new TextField((spring != null) ? String.valueOf(spring.getSpringConstant()) : "1");
        length = new TextField((spring != null) ? String.valueOf(spring.getLength()) : "1");
        boxSelector1 = new ComboBox();
        boxSelector1.getItems().addAll(pc.getBoxes());
        boxSelector2 = new ComboBox();
        boxSelector2.getItems().addAll(pc.getBoxes());
        if (spring != null) {
            boxSelector1.getSelectionModel().select(spring.getBox1());
            if (spring.getBox2() != null) {
                boxSelector2.getSelectionModel().select(spring.getBox2());
            }
        }
        boxOrPointBox = new RadioButton("Another box");
        boxOrPointPoint = new RadioButton("Fixed point");
        ToggleGroup group = new ToggleGroup();
        boxOrPointBox.setToggleGroup(group);
        boxOrPointPoint.setToggleGroup(group);
        boxOrPointBox.setSelected(true);
        if (spring != null && spring.getPoint() != null) {
            boxOrPointPoint.setSelected(true);
        }
        root.getChildren().addAll(new Label("Which box must be in the first end of the spring?"), boxSelector1,
                                  new Label("Should the other end be connected to a fixed point or to another box?"), new HBox(boxOrPointBox, boxOrPointPoint),
                                  new Label("If fixed point, where?"), new HBox(positionX, positionY),
                                  new Label("If another box, which?"), boxSelector2,
                                  new Label("Spring constant in N/m"), springConstant,
                                  new Label("Default length in meter"), length);
        Button close = new Button("Cancel");
        Button add = new Button((spring != null) ? "Change" : "Add");
        close.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {close();}});
        add.setOnAction(new EventHandler<ActionEvent>() {public void handle(ActionEvent event) {addAndClose();}});
        close.setPrefSize(width, 20);
        add.setPrefSize(width, 20);
        root.getChildren().addAll(add, close);
        show();
    }

    public void addAndClose() {
        try {
            Box b1 = boxSelector1.getValue();
            Box b2 = boxSelector2.getValue();
            Vector2D position = new Vector2D(Double.parseDouble(positionX.getText()), Double.parseDouble(positionY.getText()));
            double springConstant = Double.parseDouble(this.springConstant.getText());
            double length = Double.parseDouble(this.length.getText());
            if (b1 == null) {
                throw new NullPointerException();
            }
            Spring s;
            if (boxOrPointBox.isSelected()) {
                if (b2 == null) {
                    throw new NullPointerException();
                }
                if (b1 == b2) { // yes i want to check pointer ref's
                    throw new IllegalArgumentException();
                }
                s = new Spring(b1, b2, springConstant, length);
            } else {
                s = new Spring(b1, position, springConstant, length);
            }
            if (spring != null) {
                spring.copyFromObject(s);
            } else {
                pc.addSpring(s);
            }
            parent.reDraw();
            close();
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }
}
