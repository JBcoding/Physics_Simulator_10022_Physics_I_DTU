package Physics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madsbjoern on 10/10/2016.
 */
public class Box1DMovement extends Box {
    private double angle; // with horizontal, in radians
    private double staticFrictionConstant;
    private double kineticFrictionConstant;
    private List<ForceWorkObject> forces;
    private Box frictionBox;

    public Box1DMovement() {
        angle = 0;
        position = new Vector2D();
        velocity = new Vector2D();
        mass = 1;

        staticFrictionConstant = 0;
        kineticFrictionConstant = 0;
        gravityDisabled = true;
        referenceToOtherBoxes = false;
        forces = new ArrayList<>();

        name = "Box1DMovement " + BoxID;
        BoxID ++;
    }

    public Box1DMovement(Vector2D position) {
        this();
        this.position = position;
    }

    public Box1DMovement(Vector2D position, double angle) {
        this(position);

        // angle is in interval ]-Pi; Pi]
        this.angle = angle % (2 * Math.PI);
        if (this.angle > Math.PI) {
            this.angle -= 2 * Math.PI;
        }
    }

    public Box1DMovement(Vector2D position, double angle, double mass) {
        this(position, angle);
        this.mass = mass;
    }

    public Box1DMovement(Vector2D position, double angle, double mass, Vector2D velocity) {
        this(position, angle, mass);
        this.velocity = velocity;
    }

    public void addForce(String name, Vector2D newForce) {
        for (ForceWorkObject f : forces) {
            if (f.getName().equals(name)) {
                f.setCurrentForce(newForce);
                return;
            }
        }
        forces.add(new ForceWorkObject(name, newForce));
    }

    public void setFrictionBox(Box frictionBox) {
        this.frictionBox = frictionBox;
        if (frictionBox == null) {
            referenceToOtherBoxes = false;
        } else {
            referenceToOtherBoxes = true;
        }
    }

    public double getAngle() {
        return angle;
    }

    public double getNormalForce() { // normal kraften
        return Math.abs(Math.cos(angle) * mass * PhysicsConstants.gravity);
    }

    public Vector2D otherForces() {
        Vector2D otherForces = new Vector2D();
        for (ForceWorkObject f : forces) {
            otherForces = otherForces.add(f.getCurrentForce());
        }
        return otherForces;
    }

    public void addFrictionForce(double deltaTime) {
        if (frictionBox != null) {
            addFrictionForceWithBox(deltaTime);
            return;
        }

        double forceAngle;
        double forceMagnitude;

        if (velocity.isZero()) {
            forceMagnitude = getStaticFrictionForce();
            Vector2D otherForces = otherForces().projectVectorToAngle(angle);
            forceAngle = otherForces.getAngle() + Math.PI;
            if (forceMagnitude >= otherForces.getMagnitude()) {
                forceMagnitude = otherForces.getMagnitude();
            } else {
                forceMagnitude = getKineticFrictionForce();
            }
        } else {
            forceMagnitude = getKineticFrictionForce();
            forceAngle = velocity.getAngle() + Math.PI;
        }
        addForce("Friction on " + name, (new Vector2D()).getVector2DFromAngleAndMagnitude(forceAngle, forceMagnitude));
    }

    public void addFrictionForceWithBox(double deltaTime) {
        Vector2D deltaVelocity = frictionBox.getVelocity().projectVectorToAngle(angle).sub(velocity);
        Vector2D deltaAcceleration = deltaVelocity.scale(1 / deltaTime); // only works if deltatime is equal to the time step last update
        Vector2D force = deltaAcceleration.scale(mass);
        if (force.getMagnitude() <= getStaticFrictionForce()) {
            addForce("Friction on " + name, force);
        } else {
            addForce("Friction on " + name, (new Vector2D()).getVector2DFromAngleAndMagnitude(force.getAngle(), getKineticFrictionForce()));
        }
    }

    public double getStaticFrictionForce() {
        return staticFrictionConstant * getNormalForce();
    }

    public double getKineticFrictionForce() {
        return kineticFrictionConstant * getNormalForce();
    }

    public double getStaticFrictionConstant() {
        return staticFrictionConstant;
    }

    public double getKineticFrictionConstant() {
        return kineticFrictionConstant;
    }

    public void setStaticFrictionConstant(double staticFrictionConstant) {
        this.staticFrictionConstant = staticFrictionConstant;
    }

    public void setKineticFrictionConstant(double kineticFrictionConstant) {
        this.kineticFrictionConstant = kineticFrictionConstant;
    }

    public void update(double deltaTime, int updateRound) { // updateRound = 1 or 2, first round is for boxes with a friction box and second round is for boxes without friction boxes
        if (updateRound == 1) {
            if (frictionBox == null) {
                return;
            }
        } else if (updateRound == 2) {
            if (frictionBox != null) {
                return;
            }
        }

        addGravityForce();
        addFrictionForce(deltaTime); // always add this last, just before the calculations underneath

        // updates
        // find acceleration
        Vector2D acceleration = new Vector2D();
        for (ForceWorkObject f : forces) {
            Vector2D force = f.getCurrentForce();
            acceleration = acceleration.add(force.scale(1.0 / mass));
        }
        // make sure acc and vel is at the right angle
        acceleration = acceleration.projectVectorToAngle(angle);
        velocity = velocity.projectVectorToAngle(angle);
        // update position and velocity
        oldPosition = position.deepCopy();
        position = position.add(velocity.scale(deltaTime)).add(acceleration.scale(deltaTime).scale(deltaTime).scale(1/2.0));
        velocity = velocity.add(acceleration.scale(deltaTime));
        Vector2D deltaPosition = position.sub(oldPosition);
        lastAcceleration = acceleration;
        // update ForceWorkObjects
        for (ForceWorkObject f : forces) {
            f.updateWorkAndResetCurrentForce(deltaPosition);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        String info =  name + "\nPosition: " + position + "\nVelocity: " + velocity + "\nMass: " + mass + "\nAngle: " + angle + "\nStatic Friction Constant: " + staticFrictionConstant + "\nKinetic Friction Constant: " + kineticFrictionConstant;
        for (ForceWorkObject work : forces) {
            info += "\n\t" + work.getName() + " = " + work.getWork() + " J";
        }
        info += "\nKinetic energy: " + (1/2.0 * mass * velocity.getMagnitude() * velocity.getMagnitude()) + " J";
        info += "\nPotential energy: " + position.getY() * mass * PhysicsConstants.gravity + " J";
        info += "\nDisabled gravity: " + gravityDisabled;
        info += "\nFriction box: " + ((frictionBox != null) ? frictionBox.getName() : "null");
        return info;
    }

    @Override
    public PhysicsObject deepCopy() {
        Box1DMovement b = new Box1DMovement(position.deepCopy(), angle, mass, velocity.deepCopy());
        b.name = name;
        b.staticFrictionConstant = staticFrictionConstant;
        b.kineticFrictionConstant = kineticFrictionConstant;
        for (ForceWorkObject f : forces) {
            b.forces.add(f.deepCopy());
        }
        b.oldPosition = oldPosition;
        b.lastAcceleration = lastAcceleration;
        if (frictionBox != null) {
            b.frictionBox = frictionBox.getLastDeepCopy();
        }
        b.gravityDisabled = gravityDisabled;
        lastDeepCopy = b;
        return b;
    }

    @Override
    public void copyFromObject(PhysicsObject object) {
        Box1DMovement b = (Box1DMovement)object;
        position = b.position;
        velocity = b.velocity;
        oldPosition = b.oldPosition;
        lastAcceleration = b.lastAcceleration;
        mass = b.mass;
        angle = b.angle;
        staticFrictionConstant = b.staticFrictionConstant;
        kineticFrictionConstant = b.kineticFrictionConstant;
        frictionBox = b.frictionBox;
        gravityDisabled = b.gravityDisabled;
    }

    public Box getFrictionBox() {
        return frictionBox;
    }
}
