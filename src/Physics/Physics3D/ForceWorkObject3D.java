package Physics.Physics3D;

import java.util.Arrays;

/**
 * Created by madsbjoern on 22/10/2016.
 */
public class ForceWorkObject3D {
    private String name;
    private double work;
    private Vector3D currentForce;

    public ForceWorkObject3D(String name, Vector3D currentForce) {
        this.name = name;
        this.currentForce = currentForce;
        work = 0;
    }

    public String getName() {
        return name;
    }

    public double getWork() {
        return work;
    }

    public Vector3D getCurrentForce() {
        return currentForce;
    }

    public void updateWorkAndResetCurrentForce(Vector3D movement) {
        if (!currentForce.isZero()) {
            work += currentForce.projectionOn(movement).getMagnitude();
        }
        currentForce = new Vector3D();
    }

    public void setCurrentForce(Vector3D currentForce) {
        this.currentForce = currentForce;
    }

    public ForceWorkObject3D deepCopy() {
        ForceWorkObject3D f = new ForceWorkObject3D(name, currentForce.deepCopy());
        f.work = work;
        return f;
    }
}
