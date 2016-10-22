package Physics.Physics3D;

import Physics.PhysicsConstants;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public class Box3D3DMovement extends Box3D {

    public Box3D3DMovement(Vector3D position, Vector3D velocity) {
        super(position, velocity);
        this.name = "Box3D3DMovement " + boxID;
        boxID ++;
    }

    @Override
    public Vector3D getNormalForce() {
        return PhysicsConstants.gravityVector.scale(mass); // assume the object always is on a plane, with full friction (I know, I know)
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
        // update position and velocity
        oldPosition = position.deepCopy();
        position = position.add(velocity.scale(deltaTime)).add(acceleration.scale(deltaTime).scale(deltaTime).scale(1/2.0));
        velocity = velocity.add(acceleration.scale(deltaTime));
        lastAcceleration = acceleration;
    }

    @Override
    public PhysicsObject3D deepCopy() {
        Box3D3DMovement b = new Box3D3DMovement(position.deepCopy(), velocity.deepCopy());
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
}
