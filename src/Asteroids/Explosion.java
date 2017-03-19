package Asteroids;

import java.awt.*;

public class Explosion extends SpaceElement {

    public static void initExplosions(Explosion[] explosions, int[] explosionCounter) {

        for (int i = 0; i < Game.MAX_SCRAP; i++) {
            explosions[i].shape = new Polygon();
            explosions[i].active = false;
            explosionCounter[i] = 0;
        }
        Game.explosionIndex = 0;
    }

    public static void updateExplosions(Explosion[] explosions, int[] explosionCounter) {


        // Move any active explosion debris. Stop explosion when its counter has
        // expired.
        for (int i = 0; i < Game.MAX_SCRAP; i++)
            if (explosions[i].active) {
                explosions[i].advance();
                explosions[i].render();
                if (-- explosionCounter[i] < 0)
                    explosions[i].active = false;
            }
    }

}
