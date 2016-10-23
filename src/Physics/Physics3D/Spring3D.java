package Physics.Physics3D;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public class Spring3D implements PhysicsObject3D {
    private Box3D box1, box2;
    private Vector3D point;
    private double length;
    private double springConstant;

    private String name;
    private static int springID;

    private Spring3D() {
        name = "Spring3D " + springID;
        springID ++;
        point = null;
    }

    public Spring3D(Box3D box1, Box3D box2, double springConstant, double length) {
        this();
        this.box1 = box1;
        this.box2 = box2;
        this.springConstant = springConstant;
        this.length = length;
    }

    public Spring3D(Box3D box1, Vector3D point, double springConstant, double length) {
        this();
        this.box1 = box1;
        this.point = point;
        this.springConstant = springConstant;
        this.length = length;
    }

    public void update() {
        Vector3D force = getDirection().scale(getCurrentLength() - length).scale(springConstant);
        box1.addForce(name + " on " + box1.name, force.scale(-1));
        if (box2 != null) {
            box2.addForce(name + " on " + box2.name, force);
        }
    }

    private Vector3D getDirection() {
        if (point == null) {
            return box1.getPosition().sub(box2.getPosition()).scale(1 / getCurrentLength());
        } else {
            return box1.getPosition().sub(point).scale(1 / getCurrentLength());
        }
    }

    public double getCurrentLength() {
        if (point == null) {
            return box1.getPosition().sub(box2.getPosition()).getMagnitude();
        } else {
            return box1.getPosition().sub(point).getMagnitude();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void copyFromObject(PhysicsObject3D o) {
        Spring3D s = (Spring3D)o;
        this.box1 = s.box1;
        if (s.point != null) {
            this.box2 = null;
            this.point = s.point.deepCopy();
        } else {
            this.point = null;
            this.box2 = s.box2;
        }
        this.length = s.length;
        this.springConstant = s.springConstant;
    }

    @Override
    public PhysicsObject3D deepCopy() {
        Spring3D s;
        if (box2 == null) {
            s = new Spring3D(box1.getLastDeepCopy(), point.deepCopy(), springConstant, length);
        } else {
            s = new Spring3D(box1.getLastDeepCopy(), box2.getLastDeepCopy(), springConstant, length);
        }
        s.name = name;
        return s;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String getInfo() {
        String info = name + "\nPoint 1: " + box1.name + "\nPoint 2: ";
        if (box2 == null) {
            info += "Fixed point at " + point;
        } else {
            info += box2.name;
        }
        info += "\nDefault length: " + length + "\nSpring Constant: " + springConstant + "\nCurrent length: " + getCurrentLength();
        info += "\nEnergy in each of the objects is " + (1/2.0 * Math.pow(getCurrentLength() - length, 2) * springConstant) + " J";
        return info;
    }

    public Vector3D getFirstPoint() {
        return box1.getPosition();
    }

    public Vector3D getSecondPoint() {
        if (point == null) {
            return box2.getPosition();
        } else {
            return point;
        }
    }

    public Vector3D getPoint() {
        return point;
    }

    public double getSpringConstant() {
        return springConstant;
    }

    public double getLength() {
        return length;
    }

    public Box3D getBox1() {
        return box1;
    }

    public Box3D getBox2() {
        return box2;
    }
}
