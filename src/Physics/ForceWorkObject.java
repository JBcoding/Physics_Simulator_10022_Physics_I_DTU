package Physics;

/**
 * Created by madsbjoern on 15/10/2016.
 */
public class ForceWorkObject {
    private String name;
    private double work;
    private Vector2D currentForce;

    public ForceWorkObject(String name, Vector2D currentForce) {
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

    public Vector2D getCurrentForce() {
        return currentForce;
    }

    public void updateWorkAndResetCurrentForce(Vector2D movement) {
        if (!currentForce.isZero()) {
            work += Math.cos(currentForce.getAngle() - movement.getAngle()) * movement.getMagnitude();
        }
        currentForce = new Vector2D();
    }

    public void setCurrentForce(Vector2D currentForce) {
        this.currentForce = currentForce;
    }

    public ForceWorkObject deepCopy() {
        ForceWorkObject f = new ForceWorkObject(name, currentForce.deepCopy());
        f.work = work;
        return f;
    }
}
