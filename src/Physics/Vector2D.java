package Physics;

/**
 * Created by madsbjoern on 10/10/2016.
 */
public class Vector2D {
    private double x, y;

    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D scale(double s) {
        return new Vector2D(x * s, y * s);
    }

    public Vector2D scaleX(double s) { return new Vector2D(x * s, y);}

    public Vector2D scaleY(double s) {
        return new Vector2D(x, y * s);
    }

    public Vector2D sub(Vector2D v) {return new Vector2D(x - v.x, y - v.y);}

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public String toString() {
        return "Vec2D(" + x + ", " + y + ")";
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }

    public static Vector2D getVector2DFromAngleAndMagnitude(double angle, double magnitude) {
        return new Vector2D(Math.cos(angle) * magnitude, Math.sin(angle) * magnitude);
    }

    public Vector2D deepCopy() {
        return new Vector2D(x, y);
    }

    public boolean isZero() {
        boolean isZero = getMagnitude() < 0.0000000000005;
        if (isZero) {
            x = 0.0;
            y = 0.0;
        }
        return isZero;
    }

    public Vector2D projectVectorToAngle(double angle) {
        double deltaAngle = getAngle() - angle;
        return (new Vector2D()).getVector2DFromAngleAndMagnitude(angle, getMagnitude() * Math.cos(deltaAngle));
    }
}
