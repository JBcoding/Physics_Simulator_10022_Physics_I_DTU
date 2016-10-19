package Physics;

/**
 * Created by madsbjoern on 10/10/2016.
 */
public abstract class Box implements PhysicsObject {
    protected Vector2D position, oldPosition;
    protected Vector2D velocity;
    protected Vector2D lastAcceleration;
    protected double mass;
    protected String name;
    protected static int BoxID;
    protected boolean gravityDisabled;
    protected boolean referenceToOtherBoxes;

    protected Box lastDeepCopy;

    public Box getLastDeepCopy() {
        return lastDeepCopy;
    }

    public Vector2D getPosition() {
        return position;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public double getMass() {
        return mass;
    }

    public abstract void addForce(String name, Vector2D newForce);

    public abstract void update(double deltaTime, int updateRound);

    public String toString() {return name;}

    protected void setPosition(Vector2D position) {
        this.position = position;
    }

    protected void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    public Vector2D getOldPosition() {
        return oldPosition;
    }

    public Vector2D getLastAcceleration() {
        return lastAcceleration;
    }

    public void addGravityForce() {
        addForce("Gravity on " + name, new Vector2D(0, (gravityDisabled) ? 0 : -mass * PhysicsConstants.gravity));
    }

    public void setGravityDisabled(boolean gravityDisabled) {
        this.gravityDisabled = gravityDisabled;
    }

    public boolean getReferenceToOtherBoxes() {
        return  referenceToOtherBoxes;
    }

    public boolean getGravityDisabled() {
        return gravityDisabled;
    }
}
