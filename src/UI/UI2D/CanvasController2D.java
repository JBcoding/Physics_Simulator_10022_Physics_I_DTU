package UI.UI2D;

import Physics.*;
import Physics.Physics2D.*;
import UI.CanvasController;
import UI.MainWindow;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by madsbjoern on 16/10/2016.
 */
public class CanvasController2D extends CanvasController {
    private Canvas canvas;
    private Vector2D offset;
    private Vector2D halfACanvas;

    public CanvasController2D(MainWindow parent) {
        this.canvas = new Canvas(900, 601);
        this.parent = parent;

        zoomFactor = 128.0;
        halfACanvas = new Vector2D(canvas.getWidth() / 2, canvas.getHeight() / 2);
        offset = halfACanvas.deepCopy();
    }

    public Vector2D transformPoint(Vector2D v) {
        return v.add(offset).sub(halfACanvas).scaleY(-1).scale(zoomFactor).add(halfACanvas);
    }

    public double transformAngle(double angle) {
        return -angle;
    }

    @Override
    public Node getNode() {
        return canvas;
    }

    public void update(PhysicsController pc) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTCORAL);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);

        List<Box> boxes = pc.getBoxes();
        for (Box b : boxes) {
            Vector2D pos = transformPoint(b.getPosition());
            if (b.getClass() == Box1DMovement.class) {
                Box1DMovement box = (Box1DMovement)b;
                double angle = transformAngle(box.getAngle());
                drawBoxWithRotation(gc, angle, pos);
                drawLineAtAngle(gc, angle, pos);
            } else if (b.getClass() == Box2DMovement.class) {
                drawBoxWithRotation(gc, 0, pos);
            }
            gc.fillText(b.getName(), pos.getX(), pos.getY());
        }

        gc.setFill(Color.BLUE);
        gc.setStroke(Color.BLUE);
        List<Spring> springs = pc.getSprings();
        for (Spring s : springs) {
            Vector2D p1 = transformPoint(s.getFirstPoint());
            Vector2D p2 = transformPoint(s.getSecondPoint());
            gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            Vector2D middle = p1.add(p2).scale(1 / 2.0);
            gc.fillText(s.getName(), middle.getX(), middle.getY());
        }

        gc.setFill(Color.GREEN);
        gc.setStroke(Color.GREEN);
        List<RopeJoint> ropeJoints = pc.getRopeJoints();
        for (RopeJoint r : ropeJoints) {
            Vector2D p = transformPoint(r.getPoint());
            Vector2D bp1 = transformPoint(r.getBox1().getPosition());
            Vector2D bp2 = transformPoint(r.getBox2().getPosition());
            Vector2D anchorPoint = transformPoint(r.getPoint());
            drawCircleAtPointWithRadius(gc, anchorPoint, 20);
            gc.fillText(r.getName(), anchorPoint.getX() + 10, anchorPoint.getY());
            gc.strokeLine(p.getX(), p.getY(), bp1.getX(), bp1.getY());
            gc.strokeLine(p.getX(), p.getY(), bp2.getX(), bp2.getY());
        }
    }

    public void drawCircleAtPointWithRadius(GraphicsContext gc, Vector2D point, double radius) {
        gc.strokeOval(point.getX() - radius / 2, point.getY() - radius / 2, radius, radius);
    }

    public void drawBoxWithRotation(GraphicsContext gc, double angle, Vector2D position) {
        double[] x = new double[4];
        double[] y = new double[4];
        for (int i = 0; i < 4; i ++) {
            x[i] = Math.cos(angle + i * Math.PI / 2 + Math.PI / 4) * 50 + position.getX();
            y[i] = Math.sin(angle + i * Math.PI / 2 + Math.PI / 4) * 50 + position.getY();
        }
        gc.strokePolygon(x, y, 4);
    }

    public void drawLineAtAngle(GraphicsContext gc, double angle, Vector2D position) {
        double[] x = new double[2];
        double[] y = new double[2];
        for (int i = 0; i < 2; i ++) {
            x[i] = Math.cos(angle + i * Math.PI) * 50 + position.getX();
            y[i] = Math.sin(angle + i * Math.PI) * 50 + position.getY();
        }
        gc.strokeLine(x[0], y[0], x[1], y[1]);
    }

    @Override
    public void up() {
        offset = offset.add(new Vector2D(0, halfACanvas.getY() / zoomFactor / 2));
        parent.reDraw();
    }

    @Override
    public void down() {
        offset = offset.add(new Vector2D(0, -halfACanvas.getY() / zoomFactor / 2));
        parent.reDraw();
    }

    @Override
    public void left() {
        offset = offset.add(new Vector2D(-halfACanvas.getX() / zoomFactor / 2, 0));
        parent.reDraw();
    }

    @Override
    public void right() {
        offset = offset.add(new Vector2D(halfACanvas.getX() / zoomFactor / 2, 0));
        parent.reDraw();
    }

    // Intentionally not doing anything
    @Override
    public void forward() {}
    @Override
    public void backward() {}

    public void zoomIn() {
        zoomFactor *= 2;
        parent.reDraw();
    }

    public void zoomOut() {
        zoomFactor /= 2;
        parent.reDraw();
    }
}
