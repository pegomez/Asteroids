package Asteroids;

import java.awt.*;

import static Asteroids.Game.MAX_SCRAP;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Explosion extends Sprite {

    // Explosion data.
    int a = 0;

    public void initExplosion(Game g) {
            this.shape = new Polygon();
            this.active = false;
            if(a == 0){
                g.explosionIndex = 0;
                a++;}
    }

    public void updateExplosions() {


        // Move any active explosion debris. Stop explosion when its counter has
        // expired.
            if (this.active) {
                this.advance();
                this.render();
            }
    }

}
