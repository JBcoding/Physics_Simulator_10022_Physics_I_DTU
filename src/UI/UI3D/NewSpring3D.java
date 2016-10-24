package UI.UI3D;

import Physics.Physics3D.Box3D;
import Physics.Physics3D.Spring3D;
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
public class NewSpring3D extends Stage {
    private int width = 430;
    private int height = 400;

    private TextField positionX, positionY, positionZ, springConstant, length;
    private ComboBox<Box3D> boxSelector1, boxSelector2;
    private RadioButton boxOrPointBox, boxOrPointPoint;
    private PhysicsController pc;
    private MainWindow parent;

    private Spring3D spring;

    public NewSpring3D(PhysicsController pc, MainWindow parent, Spring3D spring) {
        this.pc = pc;
        this.parent = parent;
        this.spring = spring;

        VBox root = new VBox();
        Scene scene = new Scene(root, width, height);
        this.setMaxWidth(width);
        this.setMaxHeight(height);
        this.setMinWidth(width);
        this.setMinHeight(height);
        setTitle("New Spring 3D");
        setScene(scene);
        scene.setOnKeyPressed((KeyEvent evt)->{
            if ((evt.getCode() == KeyCode.ESCAPE)) {
                close();
            }
        });
        positionX = new TextField((spring != null && spring.getPoint() != null) ? String.valueOf(spring.getPoint().getX()) : "0");
        positionY = new TextField((spring != null && spring.getPoint() != null) ? String.valueOf(spring.getPoint().getY()) : "0");
        positionZ = new TextField((spring != null && spring.getPoint() != null) ? String.valueOf(spring.getPoint().getZ()) : "0");
        springConstant = new TextField((spring != null) ? String.valueOf(spring.getSpringConstant()) : "1");
        length = new TextField((spring != null) ? String.valueOf(spring.getLength()) : "1");
        boxSelector1 = new ComboBox();
        boxSelector1.getItems().addAll(pc.getBox3Ds());
        boxSelector2 = new ComboBox();
        boxSelector2.getItems().addAll(pc.getBox3Ds());
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
                                    new Label("If fixed point, where?"), new HBox(positionX, positionY, positionZ),
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
            Box3D b1 = boxSelector1.getValue();
            Box3D b2 = boxSelector2.getValue();
            Vector3D position = new Vector3D(Double.parseDouble(positionX.getText()), Double.parseDouble(positionY.getText()), Double.parseDouble(positionZ.getText()));
            double springConstant = Double.parseDouble(this.springConstant.getText());
            double length = Double.parseDouble(this.length.getText());
            if (b1 == null) {
                throw new NullPointerException();
            }
            Spring3D s;
            if (boxOrPointBox.isSelected()) {
                if (b2 == null) {
                    throw new NullPointerException();
                }
                if (b1 == b2) { // yes i want to check pointer ref's
                    throw new IllegalArgumentException();
                }
                s = new Spring3D(b1, b2, springConstant, length);
            } else {
                s = new Spring3D(b1, position, springConstant, length);
            }
            if (spring != null) {
                spring.copyFromObject(s);
            } else {
                pc.addSpring3D(s);
            }
            parent.reDraw();
            close();
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }
}
