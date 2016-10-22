package Physics.Physics2D;

/**
 * Created by madsbjoern on 17/10/2016.
 */
public class RopeJoint implements PhysicsObject {
    private Box box1, box2;
    private Vector2D point;
    private double length;

    private String name;
    private static int ropeJointID;

    public RopeJoint(Box box1, Box box2, Vector2D point) {
        this.box1 = box1;
        this.box2 = box2;
        this.point = point;
        length = getCurrentLength();

        name = "RopeJoint " + ropeJointID;
        ropeJointID ++;
    }

    public double getCurrentLength() {
        return getDistanceToBox(box1) + getDistanceToBox(box2);
    }

    public void update(double deltaTime) {
        double newLength = getCurrentLength();
        if (newLength > length) {
            // the more you weigh, the less you move :)
            double lengthToSpare = newLength - length;
            double mass1 = getAngleMassOfBox(box1);
            double mass2 = getAngleMassOfBox(box2);
            double totalMass = mass1 + mass2;
            double toMove1 = lengthToSpare * mass2 / totalMass; // yes i mean box2 weight, for the calculation of box1 movement
            double toMove2 = lengthToSpare * mass1 / totalMass;
            if (getDistanceToBox(box1) - toMove1 < getMinDistanceToBox(box1)) {
                double distanceChange = getDistanceToBox(box1) - getMinDistanceToBox(box1);
                toMove1 -= distanceChange;
                toMove2 += distanceChange;
                // stop box 2 speed
            } else if (getDistanceToBox(box2) - toMove2 < getMinDistanceToBox(box2)) {
                double distanceChange = getDistanceToBox(box2) - getMinDistanceToBox(box2);
                toMove2 -= distanceChange;
                toMove1 += distanceChange;
                // stop box 1 speed
            }
            box1.setPosition(getNewPositionOfBox(box1, toMove1));
            box2.setPosition(getNewPositionOfBox(box2, toMove2));
            box1.setVelocity(box1.getPosition().sub(box1.getOldPosition()).scale(1/deltaTime).add(box1.getLastAcceleration().scale(1/2.0).scale(deltaTime)));
            box2.setVelocity(box2.getPosition().sub(box2.getOldPosition()).scale(1/deltaTime).add(box2.getLastAcceleration().scale(1/2.0).scale(deltaTime)));
        }
    }

    public double getAngleMassOfBox(Box box) {
        if (box.getClass() == Box1DMovement.class) {
            Box1DMovement b = (Box1DMovement)box;
            double deltaAngle = b.getAngle() - point.sub(b.getPosition()).getAngle();
            return Math.min(b.getMass() / Math.abs(Math.cos(deltaAngle)), 1e30);
        } else if (box.getClass() == Box2DMovement.class) {
            return box.getMass();
        }
        return Double.NaN;
    }

    public Vector2D getNewPositionOfBox(Box box, double toMove) {
        Vector2D result = getNewPositionOfBoxFaulty(box, toMove);
        double change = 0.000000001;
        toMove += change;
        while (result.getY() != result.getY()) { // only when Y is NaN
            toMove -= change;
            result = getNewPositionOfBoxFaulty(box, toMove);
            toMove += change;
            change *= 2;
        }
        return result;
    }

    private Vector2D getNewPositionOfBoxFaulty(Box box, double toMove) {
        if (box.getClass() == Box1DMovement.class) {
            Box1DMovement b = (Box1DMovement)box;
            Vector2D currentPos = b.getPosition();
            Vector2D direction = (new Vector2D()).getVector2DFromAngleAndMagnitude(b.getAngle(), 1);
            double A = currentPos.getX() - point.getX(), C = currentPos.getY() - point.getY(), B = direction.getX(), D = direction.getY(), E = getDistanceToBox(box) - toMove;
            // solve equation (has 1 or 2 solutions)
            // u = ( (+ -) sqrt((2 a b+2 c d)^2-4 (b^2+d^2) (a^2+c^2-e^2))-2 a b-2 c d)/(2 (b^2+d^2))
            double u1 = (-Math.sqrt(Math.pow((2 * A * B + 2 * C * D), 2) - 4 * (Math.pow(B, 2) + Math.pow(D, 2)) * (Math.pow(A, 2) + Math.pow(C, 2) - Math.pow(E, 2))) - 2 * A * B - 2 * C * D) / (2 * (Math.pow(B, 2) + Math.pow(D, 2)));
            double u2 = (Math.sqrt(Math.pow((2 * A * B + 2 * C * D), 2) - 4 * (Math.pow(B, 2) + Math.pow(D, 2)) * (Math.pow(A, 2) + Math.pow(C, 2) - Math.pow(E, 2))) - 2 * A * B - 2 * C * D) / (2 * (Math.pow(B, 2) + Math.pow(D, 2)));
            double u = u1;
            if (Math.abs(u2) < Math.abs(u1)) {
                u = u2;
            }
            return currentPos.add(direction.scale(u));
        } else if (box.getClass() == Box2DMovement.class) {
            Vector2D deltaPosition = point.sub(box.getPosition());
            return point.sub((new Vector2D()).getVector2DFromAngleAndMagnitude(deltaPosition.getAngle(), deltaPosition.getMagnitude() - toMove));
        }
        return null;
    }

    public double getDistanceToBox(Box box) {
        return box.getPosition().sub(point).getMagnitude();
    }

    public double getMinDistanceToBox(Box box) {
        if (box.getClass() == Box1DMovement.class) {
            Box1DMovement b = (Box1DMovement)box;
            return point.sub(b.getPosition()).projectVectorToAngle(b.getAngle()).add(b.getPosition()).sub(point).getMagnitude(); // Math's
        } else if (box.getClass() == Box2DMovement.class) {
            return 0.0;
        }
        throw new IllegalArgumentException("ADD MORE BOX CLASSES IN ROPE JOINT, ALSO ADD IT IN FUNCTION getNewPositionOfBoxFaulty and Vector2DgetAngleMathOfBox");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        String info = name + "\nBox1: " + box1.getName() + "\nBox2: " + box2.getName() + "\nLength: " + length + "\nPoint: " + point + "\nCurrent length: " + getCurrentLength() + "\nLength to box1: " + getDistanceToBox(box1) + "\nLength to box2: " + getDistanceToBox(box2) + "\nMin distance to box1: " + getMinDistanceToBox(box1) + "\nMin distance to box2: " + getMinDistanceToBox(box2);
        return info;
    }

    @Override
    public PhysicsObject deepCopy() {
        RopeJoint newRopeJoint = new RopeJoint(box1.getLastDeepCopy(), box2.getLastDeepCopy(), point.deepCopy());
        newRopeJoint.name = name;
        newRopeJoint.length = length;
        return newRopeJoint;
    }

    public Vector2D getPoint() {
        return point;
    }

    public Box getBox1() {
        return box1;
    }

    public Box getBox2() {
        return box2;
    }

    @Override
    public void copyFromObject(PhysicsObject object) {
        RopeJoint r = (RopeJoint)object;
        length = r.length;
        box1 = r.box1;
        box2 = r.box2;
        point = r.point;
    }
}
