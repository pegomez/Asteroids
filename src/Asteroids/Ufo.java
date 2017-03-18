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

    public void initUfo() {

        double angle, speed;

        // Randomly set flying saucer at left or right edge of the screen.

        this.active = true;
        this.x = -AsteroidsSprite.width / 2;
        this.y = Math.random() * 2 * AsteroidsSprite.height - AsteroidsSprite.height;
        angle = Math.random() * Math.PI / 4 - Math.PI / 2;
        speed = MAX_ROCK_SPEED / 2 + Math.random() * (MAX_ROCK_SPEED / 2);
        this.deltaX = speed * -Math.sin(angle);
        this.deltaY = speed *  Math.cos(angle);
        if (Math.random() < 0.5) {
            this.x = AsteroidsSprite.width / 2;
            this.deltaX = -this.deltaX;
        }
        if (this.y > 0)
            this.deltaY = this.deltaY;
        this.render();

        saucerPlaying = true;
        if (sound)
            saucerSound.loop();

        ufoCounter = (int) Math.abs(AsteroidsSprite.width / this.deltaX);
    }

    public void updateUfo() {

        int i, d;

        // Move the flying saucer and check for collision with a photon. Stop it
        // when its counter has expired.

        if (this.active)
        {
            if (--ufoCounter <= 0) {
                if (--ufoPassesLeft > 0)
                    initUfo();
                else
                    stopUfo();
            }
            if (this.active)
            {
                this.advance();
                this.render();
                for (i = 0; i < MAX_SHOTS; i++)
                    if (photons[i].active && this.isColliding(photons[i])) {
                        if (sound)
                            crashSound.play();
                        explode(ufo);
                        stopUfo();
                        score += UFO_POINTS;
                    }

                // On occassion, fire a missle at the ship if the saucer is not too
                // close to it.

                d = (int) Math.max(Math.abs(ufo.x - ship.x), Math.abs(ufo.y - ship.y));
                if (ship.active && hyperCounter <= 0 &&
                        ufo.active && !missle.active &&
                        d > MAX_ROCK_SPEED * FPS / 2 &&
                        Math.random() < MISSLE_PROBABILITY)
                    initMissle();
            }
        }
    }

    public void stopUfo() {

        this.active = false;
        ufoCounter = 0;
        ufoPassesLeft = 0;
    }
}