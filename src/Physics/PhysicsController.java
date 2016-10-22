package Physics;

import Physics.Physics2D.*;
import Physics.Physics3D.*;

import java.util.*;

/**
 * Created by madsbjoern on 10/10/2016.
 */
public class PhysicsController {
    private List<Box> boxes;
    private List<Spring> springs;
    private List<RopeJoint> ropeJoints;

    private List<Box3D> box3Ds;
    private List<Spring3D> spring3Ds;
    private List<RopeJoint3D> ropeJoint3Ds;

    private double time;
    private double timeStep;
    private int version;

    public PhysicsController() {
        boxes = new ArrayList<>();
        springs = new ArrayList<>();
        ropeJoints = new ArrayList<>();

        box3Ds = new ArrayList<>();
        spring3Ds = new ArrayList<>();
        ropeJoint3Ds = new ArrayList<>();

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

    public void addBox3D(Box3D b) {
        box3Ds.add(b);
    }

    public void addSpring3D(Spring3D s) {
        spring3Ds.add(s);
    }

    public void addRopeJoint3D(RopeJoint3D r) {
        ropeJoint3Ds.add(r);
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

    public List<Box3D> getBox3Ds() {
        return box3Ds;
    }

    public List<Spring3D> getSpring3Ds() {
        return spring3Ds;
    }

    public List<RopeJoint3D> getRopeJoint3Ds() {
        return ropeJoint3Ds;
    }

    public List<PhysicsObject> getAllObjects() {
        List<PhysicsObject> allObjects = new ArrayList<>();
        allObjects.addAll(boxes);
        allObjects.addAll(springs);
        allObjects.addAll(ropeJoints);
        return allObjects;
    }

    public List<PhysicsObject3D> getAllObjects3D() {
        List<PhysicsObject3D> allObjects = new ArrayList<>();
        allObjects.addAll(box3Ds);
        allObjects.addAll(spring3Ds);
        allObjects.addAll(ropeJoint3Ds);
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
        // 2D
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

        // 3D
        for (Box3D b : box3Ds) {
            if (!b.getReferenceToOtherBoxes()) {
                newPC.addBox3D((Box3D) b.deepCopy());
            }
        }
        for (Box3D b : box3Ds) {
            if (b.getReferenceToOtherBoxes()) {
                newPC.addBox3D((Box3D) b.deepCopy());
            }
        }
        for (Spring3D s : spring3Ds) {
            newPC.addSpring3D((Spring3D)s.deepCopy());
        }
        for (RopeJoint3D r : ropeJoint3Ds) {
            newPC.addRopeJoint3D((RopeJoint3D)r.deepCopy());
        }

        newPC.setTime(time);
        newPC.setTimeStep(timeStep);
        return newPC;
    }

    public void restoreFromDeepCopy(PhysicsController deepCopy) {
        boxes.clear();
        springs.clear();
        ropeJoints.clear();
        box3Ds.clear();
        spring3Ds.clear();
        ropeJoint3Ds.clear();
        time = deepCopy.time;
        timeStep = deepCopy.timeStep;
        // IMPORTANT MUST COPY BOXES BEFORE SPRINGS AND ROPE_JOINTS (SEE LAST_DEEP_COPY IN BOX)
        // 2D
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

        // 3D
        for (Box3D b : deepCopy.box3Ds) {
            if (!b.getReferenceToOtherBoxes()) {
                addBox((Box) b.deepCopy());
            }
        }
        for (Box3D b : deepCopy.box3Ds) {
            if (b.getReferenceToOtherBoxes()) {
                addBox((Box) b.deepCopy());
            }
        }
        for (Spring3D s : deepCopy.spring3Ds) {
            addSpring((Spring)s.deepCopy());
        }
        for (RopeJoint3D r : deepCopy.ropeJoint3Ds) {
            addRopeJoint((RopeJoint)r.deepCopy());
        }

        version ++;
    }

    public void update() {
        // 2D
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

        // 3D
        for (Spring3D s : spring3Ds) {
            s.update();
        }
        for (Box3D b : box3Ds) {
            b.update(timeStep, 1);
        }
        for (Box3D b : box3Ds) {
            b.update(timeStep, 2);
        }
        for (RopeJoint3D r : ropeJoint3Ds) {
            r.update(timeStep);
        }
        for (Box3D b : box3Ds) {
            b.update(timeStep, 3);
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

    public void deleteObject3D(PhysicsObject3D o) {
        spring3Ds.remove(o);
        box3Ds.remove(o);
        ropeJoint3Ds.remove(o);
        version ++;
    }
}
