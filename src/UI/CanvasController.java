package UI;

import Physics.PhysicsController;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;

/**
 * Created by madsbjoern on 23/10/2016.
 */
public abstract class CanvasController {
    protected double zoomFactor;
    protected MainWindow parent;

    public abstract Node getNode();

    public abstract void update(PhysicsController pc);

    public abstract void up();

    public abstract void down();

    public abstract void left();

    public abstract void right();

    public abstract void forward();

    public abstract void backward();

    public abstract void zoomIn();

    public abstract void zoomOut();
}
