package Physics.Physics2D;

/**
 * Created by madsbjoern on 16/10/2016.
 */
public class Spring implements PhysicsObject {
    private double springConstant;
    private double length;
    private Box box1, box2;
    private Vector2D point;
    private String name;

    private static int SpringID;

    public Spring(Box box1, Box box2, double springConstant, double length) {
        this.box1 = box1;
        this.box2 = box2;
        this.springConstant = springConstant;
        this.length = length;
        this.point = null;

        name = "Spring " + SpringID;
        SpringID ++;
    }

    public Spring(Box box, Vector2D point, double springConstant, double length) {
        this(box, (Box)null, springConstant, length);
        this.point = point;
    }

    public double getCurrentLength() {
        if (point == null) {
            return box1.getPosition().sub(box2.getPosition()).getMagnitude();
        } else {
            return box1.getPosition().sub(point).getMagnitude();
        }
    }

    public Vector2D getDirection() {
        if (point == null) {
            return box1.getPosition().sub(box2.getPosition()).scale(1 / getCurrentLength());
        } else {
            return box1.getPosition().sub(point).scale(1 / getCurrentLength());
        }
    }

    public Vector2D getFirstPoint() {
        return box1.getPosition();
    }

    public Vector2D getSecondPoint() {
        if (box2 == null) {
            return point;
        } else {
            return box2.getPosition();
        }
    }

    public void update() {
        Vector2D force = getDirection().scale(getCurrentLength() - length).scale(springConstant);
        box1.addForce(name + " on " + box1.name, force.scale(-1));
        if (box2 != null) {
            box2.addForce(name + " on " + box2.name, force);
        }
    }

    @Override
    public String getName() {
        return name;
    }

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

    @Override
    public PhysicsObject deepCopy() {
        Spring s;
        if (box2 == null) {
            s = new Spring(box1.getLastDeepCopy(), point.deepCopy(), springConstant, length);
        } else {
            s = new Spring(box1.getLastDeepCopy(), box2.getLastDeepCopy(), springConstant, length);
        }
        s.name = name;
        return s;
    }

    @Override
    public void copyFromObject(PhysicsObject object) {
        Spring s = (Spring)object;
        box1 = s.box1;
        box2 = s.box2;
        springConstant = s.springConstant;
        length = s.length;
    }

    public Vector2D getPoint() {
        return point;
    }

    public double getSpringConstant() {
        return springConstant;
    }

    public double getLength() {
        return length;
    }

    public Box getBox1() {
        return box1;
    }

    public Box getBox2() {
        return box2;
    }
}
