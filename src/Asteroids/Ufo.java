package Asteroids;

import static Asteroids.Game.*;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Ufo extends Sprite {

    // Flying saucer data.
    int ufoPassesLeft;    // Counter for number of flying saucer passes.
    int ufoCounter;       // Timer counter used to track each flying saucer pass.
    int newUfoScore;

    public Ufo(){
        this.shape.addPoint(-15, 0);
        this.shape.addPoint(-10, -5);
        this.shape.addPoint(-5, -5);
        this.shape.addPoint(-5, -8);
        this.shape.addPoint(5, -8);
        this.shape.addPoint(5, -5);
        this.shape.addPoint(10, -5);
        this.shape.addPoint(15, 0);
        this.shape.addPoint(10, 5);
        this.shape.addPoint(-10, 5);
    }

    public void initUfo(Game g) {

        double angle, speed;

        // Randomly set flying saucer at left or right edge of the screen.

        this.active = true;
        this.x = -Sprite.width / 2;
        this.y = Math.random() * 2 * Sprite.height - Sprite.height;
        angle = Math.random() * Math.PI / 4 - Math.PI / 2;
        speed = MAX_ROCK_SPEED / 2 + Math.random() * (MAX_ROCK_SPEED / 2);
        this.deltaX = speed * -Math.sin(angle);
        this.deltaY = speed *  Math.cos(angle);
        if (Math.random() < 0.5) {
            this.x = Sprite.width / 2;
            this.deltaX = -this.deltaX;
        }
        if (this.y > 0)
            this.deltaY = this.deltaY;
        this.render();

        g.sounds.saucerPlaying = true;
        if (g.sound)
            g.sounds.saucerSound.loop();

        ufoCounter = (int) Math.abs(Sprite.width / this.deltaX);
    }

    public void updateUfo(Game g) {

        int i, d;

        // Move the flying saucer and check for collision with a photon. Stop it
        // when its counter has expired.

        if (this.active)
        {
            if (--ufoCounter <= 0) {
                if (--ufoPassesLeft > 0)
                    initUfo(g);
                else
                    stopUfo(g);
            }
            if (this.active)
            {
                this.advance();
                this.render();
                for (i = 0; i < MAX_SHOTS; i++)
                    if (g.photons[i].active && this.isColliding(g.photons[i])) {
                        if (g.sound)
                            g.sounds.crashSound.play();
                        g.explode(this);
                        stopUfo(g);
                        g.score += UFO_POINTS;
                    }

                // On occassion, fire a missle at the ship if the saucer is not too close to it.

                d = (int) Math.max(Math.abs(this.x - g.ship.x), Math.abs(this.y - g.ship.y));
                if (g.ship.active && g.hyperCounter <= 0 &&
                        this.active && !g.missile.active &&
                        d > MAX_ROCK_SPEED * FPS / 2 &&
                        Math.random() < MISSLE_PROBABILITY)
                    g.missile.initMissile(g);
            }
        }
    }

    public void stopUfo(Game g) {

        this.active = false;
        if (g.loaded)
            g.sounds.saucerSound.stop();
        g.sounds.saucerPlaying = false;
        ufoCounter = 0;
        ufoPassesLeft = 0;
    }
}