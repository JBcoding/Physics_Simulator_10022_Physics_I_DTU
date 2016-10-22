package Physics.Physics3D;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public interface PhysicsObject3D {
    String getName();
    String getInfo();
    PhysicsObject3D deepCopy();
    void copyFromObject(PhysicsObject3D o);
}
