package Physics.Physics3D;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public class Vector3D {
    private double x, y, z;

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D direction, double length) {
        direction = direction.getUnitVector();
        this.x = direction.x * length;
        this.y = direction.y * length;
        this.z = direction.z * length;
    }

    public Vector3D scale(double s) {
        return new Vector3D(x * s, y * s, z * s);
    }

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D sub(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Vector3D cross(Vector3D v) {
        return new Vector3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vector3D getUnitVector() {
        double magnitude = getMagnitude();
        return new Vector3D(x / magnitude, y / magnitude, z / magnitude);
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D deepCopy() {
        return new Vector3D(x, y, z);
    }

    public boolean isZero() {
        if (getMagnitude() < 5e-9) {
            x = 0;
            y = 0;
            z = 0;
            return true;
        } else {
            return false;
        }
    }

    public Vector3D projectionOn(Vector3D v) {
        return v.getUnitVector().scale(dot(v) / v.getMagnitude());
    }

    public String toString() {
        return "Vec3D(" + x + ", " + y + ", " + z + ")";
    }
}
