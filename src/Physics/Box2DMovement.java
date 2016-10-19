package Physics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madsbjoern on 19/10/2016.
 */
public class Box2DMovement extends Box {
    private List<ForceWorkObject> forces;

    public Box2DMovement() {
        position = new Vector2D();
        velocity = new Vector2D();
        mass = 1;

        gravityDisabled = true;
        referenceToOtherBoxes = false;
        forces = new ArrayList<>();

        name = "Box2DMovement " + BoxID;
        BoxID ++;
    }

    public Box2DMovement(Vector2D position) {
        this();
        this.position = position;
    }


    public Box2DMovement(Vector2D position, double mass) {
        this(position);
        this.mass = mass;
    }

    public Box2DMovement(Vector2D position, double mass, Vector2D velocity) {
        this(position, mass);
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

    public Vector2D otherForces() {
        Vector2D otherForces = new Vector2D();
        for (ForceWorkObject f : forces) {
            otherForces = otherForces.add(f.getCurrentForce());
        }
        return otherForces;
    }

    public void update(double deltaTime, int updateRound) { // updateRound = 1 or 2, first round is for boxes with a friction box and second round is for boxes without friction boxes
        if (updateRound == 1) {
            return;
        }

        addGravityForce();

        // updates
        // find acceleration
        Vector2D acceleration = new Vector2D();
        for (ForceWorkObject f : forces) {
            Vector2D force = f.getCurrentForce();
            acceleration = acceleration.add(force.scale(1.0 / mass));
        }
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
        String info =  name + "\nPosition: " + position + "\nVelocity: " + velocity + "\nMass: " + mass;
        for (ForceWorkObject work : forces) {
            info += "\n\t" + work.getName() + " = " + work.getWork() + " J";
        }
        info += "\nKinetic energy: " + (1/2.0 * mass * velocity.getMagnitude() * velocity.getMagnitude()) + " J";
        info += "\nPotential energy: " + position.getY() * mass * PhysicsConstants.gravity + " J";
        info += "\nDisabled gravity: " + gravityDisabled;
        return info;
    }

    @Override
    public PhysicsObject deepCopy() {
        Box2DMovement b = new Box2DMovement(position.deepCopy(), mass, velocity.deepCopy());
        b.name = name;
        for (ForceWorkObject f : forces) {
            b.forces.add(f.deepCopy());
        }
        b.oldPosition = oldPosition;
        b.lastAcceleration = lastAcceleration;
        b.gravityDisabled = gravityDisabled;
        lastDeepCopy = b;
        return b;
    }

    @Override
    public void copyFromObject(PhysicsObject object) {
        Box2DMovement b = (Box2DMovement)object;
        position = b.position;
        velocity = b.velocity;
        oldPosition = b.oldPosition;
        lastAcceleration = b.lastAcceleration;
        mass = b.mass;
        gravityDisabled = b.gravityDisabled;
    }
}
