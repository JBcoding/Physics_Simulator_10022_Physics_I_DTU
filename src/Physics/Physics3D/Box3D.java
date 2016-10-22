package Physics.Physics3D;

import Physics.PhysicsConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public abstract class Box3D implements PhysicsObject3D {
    protected Vector3D position, oldPosition;
    protected Vector3D velocity;
    protected Vector3D lastAcceleration;
    protected double mass;
    protected double staticFrictionConstant; // assuming static friction constant is equal or greater than kinetic friction constant
    protected double kineticFrictionConstant;
    protected boolean gravityDisabled;
    protected boolean referenceToOtherBoxes;
    protected List<ForceWorkObject3D> forces;
    protected Box3D frictionBox;
    protected Box3D lastDeepCopy;

    protected String name;
    protected static int boxID;

    public Box3D() {
        position = new Vector3D();
        velocity = new Vector3D();
        mass = 1;
        staticFrictionConstant = 0;
        kineticFrictionConstant = 0;
        gravityDisabled = false;
        referenceToOtherBoxes = false;
        forces = new ArrayList<>();
        frictionBox = null;
    }

    public Box3D (Vector3D position, Vector3D velocity) {
        this();
        this.position = position;
        this.velocity = velocity;
    }

    public Vector3D getPosition() {
        return position;
    }

    public Vector3D getVelocity() {
        return velocity;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void setStaticFrictionConstant(double staticFrictionConstant) {
        this.staticFrictionConstant = staticFrictionConstant;
    }

    public double getStaticFrictionConstant() {
        return staticFrictionConstant;
    }

    public void setKineticFrictionConstant(double kineticFrictionConstant) {
        this.kineticFrictionConstant = kineticFrictionConstant;
    }

    public double getKineticFrictionConstant() {
        return kineticFrictionConstant;
    }

    public void setGravityDisabled(boolean gravityDisabled) {
        this.gravityDisabled = gravityDisabled;
    }

    public boolean getGravityDisabled() {
        return gravityDisabled;
    }

    public boolean getReferenceToOtherBoxes() {
        return referenceToOtherBoxes;
    }

    public void setFrictionBox(Box3D frictionBox) {
        this.frictionBox = frictionBox;
        if (frictionBox == null) {
            referenceToOtherBoxes = false;
        } else {
            referenceToOtherBoxes = true;
        }
    }

    public Box3D getFrictionBox() {
        return frictionBox;
    }

    public boolean updateDecision(double deltaTime, int updateRound) {
        if (updateRound == 1) {
            if (frictionBox == null) {
                return false;
            }
        } else if (updateRound == 2) {
            if (frictionBox != null) {
                return false;
            }
        } else if (updateRound == 3) {
            // update ForceWorkObjects
            Vector3D deltaPosition = position.sub(oldPosition);
            for (ForceWorkObject3D f : forces) {
                f.updateWorkAndResetCurrentForce(deltaPosition);
            }
            return false;
        }
        return true;
    }
    /* UpdateRounds
     * 1. round is for boxes that have a reference to other boxes
     * 2. round is for boxes that DO NOT have a reference to other boxes
     * 3. round is AFTER RopeJoint3D and updates / resets all work force objects
     */

    public void addForce(String name, Vector3D newForce) {
        for (ForceWorkObject3D f : forces) {
            if (f.getName().equals(name)) {
                f.setCurrentForce(newForce);
                return;
            }
        }
        forces.add(new ForceWorkObject3D(name, newForce));
    }

    protected void addFrictionForce(double deltaTime) { // must be added as the last force
        if (frictionBox != null) {
            addFrictionForceWithBox(deltaTime);
            return;
        }

        Vector3D force;
        if (velocity.isZero()) {
            // we are standing still, so we use the static friction force
            Vector3D otherForces = getOtherForces();
            if (otherForces.getMagnitude() > getStaticFrictionForce()) {
                // we are about to move, so we use kinetic friction
                force = otherForces.getUnitVector().scale(-getKineticFrictionForce());
            } else {
                // the static force is to great, so we just equal out all other forces
                force = otherForces.scale(-1);
            }
        } else {
            // we are moving, so we use the kinetic friction force
            force = velocity.getUnitVector().scale(-getKineticFrictionForce());
        }
        addForce("Friction on " + name, force);
    }

    public void addFrictionForceWithBox(double deltaTime) {
        Vector3D deltaVelocity = velocity.sub(frictionBox.getVelocity());
        Vector3D deltaAcceleration = deltaVelocity.scale(1 / deltaTime); // only works if deltatime is equal to the time step last update
        Vector3D force = deltaAcceleration.scale(mass);
        if (force.getMagnitude() <= getStaticFrictionForce()) {
            addForce("Friction on " + name, frictionBox.getVelocity().getUnitVector().scale(force.getMagnitude()));
        } else {
            addForce("Friction on " + name, frictionBox.getVelocity().getUnitVector().scale(getKineticFrictionForce()));
        }
    }

    private double getStaticFrictionForce() {
        return getNormalForce().scale(staticFrictionConstant).getMagnitude();
    }

    private double getKineticFrictionForce() {
        return getNormalForce().scale(kineticFrictionConstant).getMagnitude();
    }

    private Vector3D getOtherForces() {
        Vector3D otherForces = new Vector3D();
        for (ForceWorkObject3D f : forces) {
            otherForces = otherForces.add(f.getCurrentForce());
        }
        return otherForces;
    }

    public abstract Vector3D getNormalForce();

    protected void addGravityForce() {
        if (!gravityDisabled) {
            addForce("Gravity on " + name, PhysicsConstants.gravityVector.scale(mass));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract void update(double deltaTime, int updateRound);

    @Override
    public String getInfo() {
        String info = name;
        info += "\nPosition: " + position;
        info += "\nVelocity: " + velocity;
        info += "\nMass: " + mass;
        info += "\nStaticFrictionConstant: " + staticFrictionConstant;
        info += "\nKineticFrictionConstant: " + kineticFrictionConstant;
        info += "\nGravityDisabled: " + gravityDisabled;
        info += "\nFrictionBox: " + ((frictionBox == null) ? "null" : frictionBox.getName());
        for (ForceWorkObject3D work : forces) {
            info += "\n\t" + work.getName() + " = " + work.getWork() + " J";
        }
        return info;
    }

    @Override
    public abstract PhysicsObject3D deepCopy();

    @Override
    public void copyFromObject(PhysicsObject3D o) {
        Box3D b = (Box3D)o;
        position = b.position.deepCopy();
        velocity = b.velocity.deepCopy();
        mass = b.mass;
        staticFrictionConstant = b.staticFrictionConstant;
        kineticFrictionConstant = b.kineticFrictionConstant;
        gravityDisabled = b.gravityDisabled;
        referenceToOtherBoxes = b.referenceToOtherBoxes;
        // don't copy forces
        frictionBox = b.frictionBox;
        oldPosition = b.oldPosition;
        lastAcceleration = b.lastAcceleration;
    }

    public Box3D getLastDeepCopy() {
        return lastDeepCopy;
    }

    protected void setPosition(Vector3D position) {
        this.position = position;
    }

    protected Vector3D getOldPosition() {
        return oldPosition;
    }

    protected Vector3D getLastAcceleration() {
        return lastAcceleration;
    }

    protected void setVelocity(Vector3D velocity) {
        this.velocity = velocity;
    }
}
