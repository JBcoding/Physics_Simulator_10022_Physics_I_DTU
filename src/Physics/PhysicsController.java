package Physics;

import Physics.Physics2D.Box;
import Physics.Physics2D.PhysicsObject;
import Physics.Physics2D.RopeJoint;
import Physics.Physics2D.Spring;

import java.util.*;

/**
 * Created by madsbjoern on 10/10/2016.
 */
public class PhysicsController {
    private List<Box> boxes;
    private List<Spring> springs;
    private List<RopeJoint> ropeJoints;
    private double time;
    private double timeStep;
    private int version;

    public PhysicsController() {
        boxes = new ArrayList<>();
        springs = new ArrayList<>();
        ropeJoints = new ArrayList<>();
        time = 0;
        timeStep = 1 / 1000.0;
    }

    public void addBox(Box b) {
        boxes.add(b);
    }

    public void addSpring(Spring s) {
        springs.add(s);
    }

    public void addRopeJoint(RopeJoint r) {
        ropeJoints.add(r);
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public List<Spring> getSprings() {
        return springs;
    }

    public List<RopeJoint> getRopeJoints() {
        return ropeJoints;
    }

    public List<PhysicsObject> getAllObjects() {
        List<PhysicsObject> allObjects = new ArrayList<>();
        allObjects.addAll(boxes);
        allObjects.addAll(springs);
        allObjects.addAll(ropeJoints);
        return allObjects;
    }

    public double getTime() {
        return time;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public PhysicsController deepCopy() {
        PhysicsController newPC = new PhysicsController();
        // IMPORTANT MUST COPY BOXES BEFORE SPRINGS AND ROPE_JOINTS (SEE LAST_DEEP_COPY IN BOX)
        for (Box b : boxes) {
            if (!b.getReferenceToOtherBoxes()) {
                newPC.addBox((Box) b.deepCopy());
            }
        }
        for (Box b : boxes) {
            if (b.getReferenceToOtherBoxes()) {
                newPC.addBox((Box) b.deepCopy());
            }
        }
        for (Spring s : springs) {
            newPC.addSpring((Spring)s.deepCopy());
        }
        for (RopeJoint r : ropeJoints) {
            newPC.addRopeJoint((RopeJoint)r.deepCopy());
        }
        newPC.setTime(time);
        newPC.setTimeStep(timeStep);
        return newPC;
    }

    public void restoreFromDeepCopy(PhysicsController deepCopy) {
        boxes.clear();
        springs.clear();
        ropeJoints.clear();
        time = deepCopy.time;
        timeStep = deepCopy.timeStep;
        // IMPORTANT MUST COPY BOXES BEFORE SPRINGS AND ROPE_JOINTS (SEE LAST_DEEP_COPY IN BOX)
        for (Box b : deepCopy.boxes) {
            if (!b.getReferenceToOtherBoxes()) {
                addBox((Box) b.deepCopy());
            }
        }
        for (Box b : deepCopy.boxes) {
            if (b.getReferenceToOtherBoxes()) {
                addBox((Box) b.deepCopy());
            }
        }
        for (Spring s : deepCopy.springs) {
            addSpring((Spring)s.deepCopy());
        }
        for (RopeJoint r : deepCopy.ropeJoints) {
            addRopeJoint((RopeJoint)r.deepCopy());
        }
        version ++;
    }

    public void update() {
        for (Spring s : springs) {
            s.update();
        }
        for (Box b : boxes) {
            b.update(timeStep, 1);
        }
        for (Box b : boxes) {
            b.update(timeStep, 2);
        }
        for (RopeJoint r : ropeJoints) {
            r.update(timeStep);
        }
        time += timeStep;
    }

    public int getVersion() {
        return version;
    }

    public void deleteObject(PhysicsObject o) {
        springs.remove(o);
        boxes.remove(o);
        ropeJoints.remove(o);
        version ++;
    }
}
