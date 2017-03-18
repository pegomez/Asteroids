package Asteroids;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Photon extends Sprite {

    // Photon data.
    int   photonIndex;    // Index to next available photon sprite.
    long  photonTime;     // Time value used to keep firing rate constant.

    public Photon() {
        this.shape.addPoint(1, 1);
        this.shape.addPoint(1, -1);
        this.shape.addPoint(-1, 1);
        this.shape.addPoint(-1, -1);
    }
}
