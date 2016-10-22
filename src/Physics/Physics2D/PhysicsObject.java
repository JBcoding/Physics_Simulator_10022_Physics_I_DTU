package Physics.Physics2D;

/**
 * Created by madsbjoern on 16/10/2016.
 */
public interface PhysicsObject {
    String getName();

    String getInfo();

    PhysicsObject deepCopy();

    void copyFromObject(PhysicsObject object);
}
