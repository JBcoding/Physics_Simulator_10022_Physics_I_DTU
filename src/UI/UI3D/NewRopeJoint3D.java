package UI.UI3D;

import Physics.Physics3D.Box3D;
import Physics.Physics3D.RopeJoint3D;
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
public class NewRopeJoint3D extends Stage {
    private int width = 320;
    private int height = 210;

    private TextField positionX, positionY, positionZ;
    private ComboBox<Box3D> boxSelector1, boxSelector2;
    private PhysicsController pc;
    private MainWindow parent;

    private RopeJoint3D ropeJoint;

    public NewRopeJoint3D(PhysicsController pc, MainWindow parent, RopeJoint3D ropeJoint) {
        this.pc = pc;
        this.parent = parent;
        this.ropeJoint = ropeJoint;

        VBox root = new VBox();
        Scene scene = new Scene(root, width, height);
        this.setMaxWidth(width);
        this.setMaxHeight(height);
        this.setMinWidth(width);
        this.setMinHeight(height);
        setTitle("New Rope Joint 3D");
        setScene(scene);
        scene.setOnKeyPressed((KeyEvent evt)->{
            if ((evt.getCode() == KeyCode.ESCAPE)) {
                close();
            }
        });
        positionX = new TextField((ropeJoint != null) ? String.valueOf(ropeJoint.getPoint().getX()) : "0");
        positionY = new TextField((ropeJoint != null) ? String.valueOf(ropeJoint.getPoint().getY()) : "0");
        positionZ = new TextField((ropeJoint != null) ? String.valueOf(ropeJoint.getPoint().getZ()) : "0");
        boxSelector1 = new ComboBox();
        boxSelector1.getItems().addAll(pc.getBox3Ds());
        boxSelector2 = new ComboBox();
        boxSelector2.getItems().addAll(pc.getBox3Ds());
        if (ropeJoint != null) {
            boxSelector1.getSelectionModel().select(ropeJoint.getBox1());
            boxSelector2.getSelectionModel().select(ropeJoint.getBox2());
        }
        root.getChildren().addAll(new Label("Which box must be in the first end of the rope?"), boxSelector1,
                new Label("Second end?"), boxSelector2,
                new Label("Fixed point, where?"), new HBox(positionX, positionY, positionZ));
        Button close = new Button("Cancel");
        Button add = new Button((ropeJoint != null) ? "Change" : "Add");
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
            if (b1 == null || b2 == null || b1 == b2) {
                throw new NullPointerException();
            }
            RopeJoint3D r = new RopeJoint3D(b1, b2, position);
            if (ropeJoint != null) {
                ropeJoint.copyFromObject(r);
            } else {
                pc.addRopeJoint3D(r);
            }
            parent.reDraw();
            close();
        } catch (Exception e) {
            AlertBox.info("Error", "Something went wrong", Alert.AlertType.ERROR);
        }
    }
}
