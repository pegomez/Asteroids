package Asteroids;

import static Asteroids.Game.MAX_SHOTS;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Photon extends SpaceElement {


    public static void setPhoton(Photon photon) {
        photon.shape.addPoint(1, 1);
        photon.shape.addPoint(1, -1);
        photon.shape.addPoint(-1, 1);
        photon.shape.addPoint(-1, -1);
    }

    public static void initPhotons(Photon[] photons) {
        int i;
        for (i = 0; i < Game.MAX_SHOTS; i++)
            photons[i].active = false;
            Game.photonIndex = 0;
    }

    public static void updatePhotons(Photon[] p) {

        // Move any active photons. Stop it when its counter has expired.
        for (int i = 0; i < Game.MAX_SHOTS; i++)
            if (p[i].active) {
                if (!p[i].advance())
                    p[i].render();
                else
                    p[i].active = false;
            }
    }

}
