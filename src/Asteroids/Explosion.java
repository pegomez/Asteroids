package Asteroids;

import static Asteroids.Game.MAX_SCRAP;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Explosion extends Sprite{
    // Explosion data.

    int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
    int   explosionIndex;                         // Next available explosion sprite.

    public Explosion(){

    }
}
