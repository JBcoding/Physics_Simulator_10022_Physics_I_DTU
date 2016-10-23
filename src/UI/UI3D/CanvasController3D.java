package UI.UI3D;

import Physics.Physics2D.Box1DMovement;
import Physics.Physics2D.Box2DMovement;
import Physics.Physics2D.Vector2D;
import Physics.Physics3D.*;
import Physics.PhysicsController;
import UI.CanvasController;
import UI.MainWindow;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by madsbjoern on 23/10/2016.
 */
public class CanvasController3D extends CanvasController {
    private Canvas cTop, cFront, cRight, cInfo;
    private GridPane gridPane;
    private Vector3D offset;
    private Vector2D halfACanvas;

    public CanvasController3D(MainWindow mainWindow) {
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(50);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(50);
        gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(col1, col2);
        gridPane.getRowConstraints().addAll(row1, row2);

        this.parent = mainWindow;

        cTop = new Canvas(450, 300);
        cInfo = new Canvas(450, 300);
        cFront = new Canvas(450, 300);
        cRight = new Canvas(450, 300);

        halfACanvas = new Vector2D(cTop.getWidth() / 2, cTop.getHeight() / 2);
        offset = new Vector3D(halfACanvas.getX(), halfACanvas.getY(), halfACanvas.getY());

        zoomFactor = 128.0;

        gridPane.add(cTop, 0, 0, 1, 1);
        gridPane.add(cInfo, 1, 0, 1, 1);
        gridPane.add(cFront, 0, 1, 1, 1);
        gridPane.add(cRight, 1, 1, 1, 1);
    }

    @Override
    public Node getNode() {
        return gridPane;
    }

    public Vector2D transformPoint(Vector3D v, Canvas c) {
        Vector2D res;
        if (c == cTop) {
            res = new Vector2D(v.getX() + offset.getX(), v.getZ() + offset.getZ());
            res = res.sub(halfACanvas);
        } else if (c == cFront) {
            res = new Vector2D(v.getX() + offset.getX(), v.getY() + offset.getY());
            res = res.sub(halfACanvas);
        } else {
            res = new Vector2D(v.getZ() + offset.getZ(), v.getY() + offset.getY());
            res = res.sub(new Vector2D(cTop.getHeight() / 2, cTop.getHeight() / 2));
        }
        res = res.scale(zoomFactor);
        res = res.scaleY(-1);
        res = res.add(halfACanvas);
        return res;
    }

    @Override
    public void update(PhysicsController pc) {
        List<Canvas> canvases = new ArrayList<>();
        canvases.add(cTop);
        canvases.add(cFront);
        canvases.add(cRight);
        for (Canvas c : canvases) {
            GraphicsContext gc = c.getGraphicsContext2D();
            gc.setFill(Color.LIGHTCORAL);
            gc.fillRect(0, 0, c.getWidth(), c.getHeight());
            gc.setFill(Color.BLACK);
            gc.setStroke(Color.BLACK);
            gc.strokeRect(0, 0, c.getWidth(), c.getHeight());
            if (c == cTop) {
                gc.fillText("Top", 10, 15);
            } else if (c == cFront) {
                gc.fillText("Front", 10, 15);
            } else {
                gc.fillText("Right", 10, 15);
            }

            List<Box3D> boxes = pc.getBox3Ds();
            for (Box3D b : boxes) {
                Vector2D pos = transformPoint(b.getPosition(), c);
                if (b.getClass() == Box3D1DMovement.class) {
                    Box3D1DMovement box = (Box3D1DMovement) b;
                    Vector2D directionStartPoint = transformPoint(b.getPosition().add(box.getDirection().scale(25 / zoomFactor)), c);
                    Vector2D directionEndPoint = transformPoint(b.getPosition().sub(box.getDirection().scale(25 / zoomFactor)), c);
                    gc.strokeLine(directionStartPoint.getX(), directionStartPoint.getY(), directionEndPoint.getX(), directionEndPoint.getY());
                }
                drawBox(gc, pos, 25);
                gc.fillText(b.getName(), pos.getX(), pos.getY());
            }

            gc.setFill(Color.BLUE);
            gc.setStroke(Color.BLUE);
            List<Spring3D> springs = pc.getSpring3Ds();
            for (Spring3D s : springs) {
                Vector2D p1 = transformPoint(s.getFirstPoint(), c);
                Vector2D p2 = transformPoint(s.getSecondPoint(), c);
                gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                Vector2D middle = p1.add(p2).scale(1 / 2.0);
                gc.fillText(s.getName(), middle.getX(), middle.getY());
            }

            gc.setFill(Color.GREEN);
            gc.setStroke(Color.GREEN);
            List<RopeJoint3D> ropeJoints = pc.getRopeJoint3Ds();
            for (RopeJoint3D r : ropeJoints) {
                Vector2D p = transformPoint(r.getPoint(), c);
                Vector2D bp1 = transformPoint(r.getBox1().getPosition(), c);
                Vector2D bp2 = transformPoint(r.getBox2().getPosition(), c);
                Vector2D anchorPoint = transformPoint(r.getPoint(), c);
                drawCircleAtPointWithRadius(gc, anchorPoint, 20);
                gc.fillText(r.getName(), anchorPoint.getX() + 10, anchorPoint.getY());
                gc.strokeLine(p.getX(), p.getY(), bp1.getX(), bp1.getY());
                gc.strokeLine(p.getX(), p.getY(), bp2.getX(), bp2.getY());
            }
        }
    }

    public void drawCircleAtPointWithRadius(GraphicsContext gc, Vector2D point, double radius) {
        gc.strokeOval(point.getX() - radius / 2, point.getY() - radius / 2, radius, radius);
    }

    public void drawBox(GraphicsContext gc, Vector2D pos, double size) {
        gc.strokeRect(pos.getX() - size / 2, pos.getY() - size / 2, size, size);
    }

    @Override
    public void up() {
        offset = offset.add(new Vector3D(0, halfACanvas.getX() / zoomFactor / 2, 0));
        parent.reDraw();
    }

    @Override
    public void down() {
        offset = offset.sub(new Vector3D(0, halfACanvas.getX() / zoomFactor / 2, 0));
        parent.reDraw();
    }

    @Override
    public void left() {
        offset = offset.add(new Vector3D(halfACanvas.getX() / zoomFactor / 2, 0, 0));
        parent.reDraw();
    }

    @Override
    public void right() {
        offset = offset.sub(new Vector3D(halfACanvas.getX() / zoomFactor / 2, 0, 0));
        parent.reDraw();
    }

    @Override
    public void forward() {
        offset = offset.add(new Vector3D(0, 0, halfACanvas.getX() / zoomFactor / 2));
        parent.reDraw();
    }

    @Override
    public void backward() {
        offset = offset.sub(new Vector3D(0, 0, halfACanvas.getX() / zoomFactor / 2));
        parent.reDraw();
    }

    @Override
    public void zoomIn() {
        zoomFactor *= 2;
        parent.reDraw();
    }

    @Override
    public void zoomOut() {
        zoomFactor /= 2;
        parent.reDraw();
    }
}
