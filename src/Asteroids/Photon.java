package Asteroids;

import static Asteroids.Game.MAX_SHOTS;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Photon extends Sprite {


    public Photon() {
        this.shape.addPoint(1, 1);
        this.shape.addPoint(1, -1);
        this.shape.addPoint(-1, 1);
        this.shape.addPoint(-1, -1);
    }

    public void initPhoton() {
        this.active = false;
    }

    public void updatePhoton() {

        // Move any active photons. Stop it when its counter has expired.

        if (this.active) {
            if (!this.advance())
                this.render();
            else
                this.active = false;
        }
    }

}
