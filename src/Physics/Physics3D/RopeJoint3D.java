package Physics.Physics3D;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public class RopeJoint3D implements PhysicsObject3D {
    private Box3D box1, box2;
    private Vector3D point;
    private double length;

    private String name;
    private static int ropeJointID;

    public RopeJoint3D(Box3D box1, Box3D box2, Vector3D point) {
        this.box1 = box1;
        this.box2 = box2;
        this.point = point;
        length = getCurrentLength();

        name = "RopeJoint3D " + ropeJointID;
        ropeJointID ++;
    }

    public double getCurrentLength() {
        return getDistanceToBox(box1) + getDistanceToBox(box2);
    }

    @SuppressWarnings("Duplicates")
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

    public double getAngleMassOfBox(Box3D box) {
        if (box.getClass() == Box3D1DMovement.class) {
            Box3D1DMovement b = (Box3D1DMovement)box;
            double divider = b.getDirection().projectionOn(point.sub(b.getPosition())).getMagnitude();
            return Math.min(b.getMass() / divider, 1e30);
        } else if (box.getClass() == Box3D3DMovement.class) {
            return box.getMass();
        }
        return Double.NaN;
    }

    public Vector3D getNewPositionOfBox(Box3D box, double toMove) {
        Vector3D result = getNewPositionOfBoxFaulty(box, toMove);
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

    private Vector3D getNewPositionOfBoxFaulty(Box3D box, double toMove) {
        if (box.getClass() == Box3D1DMovement.class) {
            Box3D1DMovement b = (Box3D1DMovement)box;
            Vector3D currentPos = b.getPosition();
            Vector3D direction = b.getDirection();
            double X = currentPos.getX(), Y = currentPos.getY(), Z = currentPos.getZ();
            double A = direction.getX(), B = direction.getY(), C = direction.getZ();
            double D = getDistanceToBox(box) - toMove;
            // solve equation (has 1 or 2 solutions)
            // u = ( (+-) sqrt((2 a x+2 b y+2 c z)^2-4 (a^2+b^2+c^2) (-d^2+x^2+y^2+z^2))-2 a x-2 b y-2 c z)/(2 (a^2+b^2+c^2))
            double u1 = (-Math.sqrt(Math.pow((2 * A * X + 2 * B * Y + 2 * C * Z), 2) - 4 * (A * A + B * B + C * C) * (-(D * D) + X * X + Y * Y + Z * Z)) - 2 * A * X - 2 * B * Y - 2 * C * Z) / (2 * (A * A + B * B + C * C));
            double u2 = (Math.sqrt(Math.pow((2 * A * X + 2 * B * Y + 2 * C * Z), 2) - 4 * (A * A + B * B + C * C) * (-(D * D) + X * X + Y * Y + Z * Z)) - 2 * A * X - 2 * B * Y - 2 * C * Z) / (2 * (A * A + B * B + C * C));
            double u = u1;
            if (Math.abs(u2) < Math.abs(u1)) {
                u = u2;
            }
            return currentPos.add(direction.scale(u));
        } else if (box.getClass() == Box3D3DMovement.class) {
            Vector3D deltaPosition = point.sub(box.getPosition());
            return point.sub(new Vector3D(deltaPosition.getUnitVector(), deltaPosition.getMagnitude() - toMove));
        }
        return null;
    }

    public double getDistanceToBox(Box3D box) {
        return box.getPosition().sub(point).getMagnitude();
    }

    public double getMinDistanceToBox(Box3D box) {
        if (box.getClass() == Box3D1DMovement.class) {
            Box3D1DMovement b = (Box3D1DMovement)box;
            return point.sub(b.getPosition()).projectionOn(b.getDirection()).add(b.getPosition()).sub(point).getMagnitude(); // Math's
        } else if (box.getClass() == Box3D3DMovement.class) {
            return 0.0;
        }
        throw new IllegalArgumentException("ADD MORE BOX CLASSES IN ROPE JOINT 3D, ALSO ADD IT IN FUNCTION getNewPositionOfBoxFaulty and Vector2DgetAngleMathOfBox");
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
    public PhysicsObject3D deepCopy() {
        RopeJoint3D newRopeJoint = new RopeJoint3D(box1.getLastDeepCopy(), box2.getLastDeepCopy(), point.deepCopy());
        newRopeJoint.name = name;
        newRopeJoint.length = length;
        return newRopeJoint;
    }

    public Vector3D getPoint() {
        return point;
    }

    public Box3D getBox1() {
        return box1;
    }

    public Box3D getBox2() {
        return box2;
    }

    @Override
    public void copyFromObject(PhysicsObject3D object) {
        RopeJoint3D r = (RopeJoint3D)object;
        length = r.length;
        box1 = r.box1;
        box2 = r.box2;
        point = r.point;
    }
}
