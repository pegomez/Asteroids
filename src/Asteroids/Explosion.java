package Asteroids;

import java.awt.*;

import static Asteroids.Game.MAX_SCRAP;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Explosion extends Sprite {

    // Explosion data.
    private int   explosionIndex;                         // Next available explosion sprite.
    int a = 0;

    public Explosion(){

    }

    public void initExplosion() {
            this.shape = new Polygon();
            this.active = false;
            if(a == 0){
                explosionIndex = 0;
                a++;}
    }

    public int getExplosionIndex() {
        return explosionIndex;
    }

    public void setExplosionIndex(int explosionIndex) {
        this.explosionIndex = explosionIndex;
    }
}
