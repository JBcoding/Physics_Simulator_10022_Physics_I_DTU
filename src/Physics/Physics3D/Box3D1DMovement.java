package Physics.Physics3D;

import Physics.PhysicsConstants;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public class Box3D1DMovement extends Box3D {
    private Vector3D direction; // most be an unit vector

    public Box3D1DMovement(Vector3D position, Vector3D velocity, Vector3D direction) {
        super(position, velocity);
        this.direction = direction.getUnitVector();
        this.name = "Box3D1DMovement " + boxID;
        boxID ++;
    }

    @Override
    protected Vector3D getOtherForces() {
        Vector3D otherForces = new Vector3D();
        for (ForceWorkObject3D f : forces) {
            otherForces = otherForces.add(f.getCurrentForce());
        }
        return otherForces.projectionOn(direction);
    }

    @Override
    public Vector3D getNormalForce() {
        double magnitude = Math.abs(Math.sin(Math.acos(direction.projectionOn(PhysicsConstants.gravityVector).getMagnitude())));
        return PhysicsConstants.gravityVector.scale(magnitude).scale(mass);
    }

    @Override
    public void update(double deltaTime, int updateRound) {
        if (!super.updateDecision(deltaTime, updateRound)) {
            return;
        }

        addGravityForce();
        addFrictionForce(deltaTime); // always add this last, just before the calculations underneath

        // updates
        // find acceleration
        Vector3D acceleration = new Vector3D();
        for (ForceWorkObject3D f : forces) {
            Vector3D force = f.getCurrentForce();
            acceleration = acceleration.add(force.scale(1.0 / mass));
        }
        // make sure acc and vel is at the right angle
        acceleration = acceleration.projectionOn(direction);
        velocity = velocity.projectionOn(direction);
        // update position and velocity
        oldPosition = position.deepCopy();
        position = position.add(velocity.scale(deltaTime)).add(acceleration.scale(deltaTime).scale(deltaTime).scale(1/2.0));
        velocity = velocity.add(acceleration.scale(deltaTime));
        lastAcceleration = acceleration;
    }

    @Override
    public String getInfo() {
        String info = super.getInfo();
        // something
        return info;
    }

    @Override
    public PhysicsObject3D deepCopy() {
        Box3D1DMovement b = new Box3D1DMovement(position.deepCopy(), velocity.deepCopy(), direction.deepCopy());
        b.oldPosition = oldPosition.deepCopy();
        b.lastAcceleration = lastAcceleration.deepCopy();
        b.mass = mass;
        b.staticFrictionConstant = staticFrictionConstant;
        b.kineticFrictionConstant = kineticFrictionConstant;
        b.gravityDisabled = gravityDisabled;
        b.referenceToOtherBoxes = referenceToOtherBoxes;
        for (ForceWorkObject3D f : forces) {
            b.forces.add(f.deepCopy());
        }
        if (frictionBox != null) b.frictionBox = frictionBox.lastDeepCopy;
        b.name = name;
        lastDeepCopy = b;
        return b;
    }

    @Override
    public void copyFromObject(PhysicsObject3D o) {
        super.copyFromObject(o);
        Box3D1DMovement b = (Box3D1DMovement)o;
        direction = b.direction;
    }

    public Vector3D getDirection() {
        return direction;
    }
}