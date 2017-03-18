package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;
import Asteroids.Game.*;

import static Asteroids.Game.*;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Asteroid extends Sprite{
    // Asteroid data.

    //boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
    boolean   asteroidIsSmall = false;      // Asteroid size flag.
    double    asteroidsSpeed;               // Asteroid speed.

    public Asteroid(){

    }

    public void initAsteroid() {

        int i, j;
        int s;
        double theta, r;
        int x, y;

            // Create a jagged shape for the asteroid and give it a random rotation.

            this.shape = new Polygon();
            s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
            for (j = 0; j < s; j ++) {
                theta = 2 * Math.PI / s * j;
                r = MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE));
                x = (int) -Math.round(r * Math.sin(theta));
                y = (int)  Math.round(r * Math.cos(theta));
                this.shape.addPoint(x, y);
            }
            this.active = true;
            this.angle = 0.0;
            this.deltaAngle = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;

            // Place the asteroid at one edge of the screen.

            if (Math.random() < 0.5) {
                this.x = -Sprite.width / 2;
                if (Math.random() < 0.5)
                    this.x = Sprite.width / 2;
                this.y = Math.random() * Sprite.height;
            }
            else {
                this.x = Math.random() * Sprite.width;
                this.y = -Sprite.height / 2;
                if (Math.random() < 0.5)
                    this.y = Sprite.height / 2;
            }

            // Set a random motion for the asteroid.

        this.deltaX = Math.random() * asteroidsSpeed;
            if (Math.random() < 0.5)
                this.deltaX = -this.deltaX;
        this.deltaY = Math.random() * asteroidsSpeed;
            if (Math.random() < 0.5)
                this.deltaY = -this.deltaY;

        this.render();
            this.asteroidIsSmall = false;

        if (asteroidsSpeed < MAX_ROCK_SPEED)
            asteroidsSpeed += 0.5;
    }

    public void initSmallAsteroids(Game g) {

        int count;
        int i, j;
        int s;
        double tempX, tempY;
        double theta, r;
        int x, y;

        // Create one or two smaller asteroids from a larger one using inactive
        // asteroids. The new asteroids will be placed in the same position as the
        // old one but will have a new, smaller shape and new, randomly generated
        // movements.

        count = 0;
        i = 0;
        tempX = this.x;
        tempY = this.y;
        do {
            if (!g.asteroids[i].active) {
                g.asteroids[i].shape = new Polygon();
                s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
                for (j = 0; j < s; j ++) {
                    theta = 2 * Math.PI / s * j;
                    r = (MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE))) / 2;
                    x = (int) -Math.round(r * Math.sin(theta));
                    y = (int)  Math.round(r * Math.cos(theta));
                    g.asteroids[i].shape.addPoint(x, y);
                }
                g.asteroids[i].active = true;
                g.asteroids[i].angle = 0.0;
                g.asteroids[i].deltaAngle = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;
                g.asteroids[i].x = tempX;
                g.asteroids[i].y = tempY;
                g.asteroids[i].deltaX = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
                g.asteroids[i].deltaY = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
                g.asteroids[i].render();
                g.asteroids[i].asteroidIsSmall = true;
                count++;
                g.asteroidsLeft++;
            }
            i++;
        } while (i < MAX_ROCKS && count < 2);
    }

    public void updateAsteroids(Game g) {

        int j;
        // Move any active asteroids and check for collisions.
            if (this.active) {
                this.advance();
                this.render();

                // If hit by photon, kill asteroid and advance score. If asteroid is
                // large, make some smaller ones to replace it.

                for (j = 0; j < MAX_SHOTS; j++)
                    if (g.photons[j].active && this.active && this.isColliding(g.photons[j])) {
                        g.asteroidsLeft--;
                        this.active = false;
                        g.photons[j].active = false;
                        if (g.sound)
                            g.sounds.explosionSound.play();
                        g.explode(this);
                        if (!this.asteroidIsSmall) {
                            g.score += BIG_POINTS;
                            initSmallAsteroids(g);
                        }
                        else
                            g.score += SMALL_POINTS;
                    }

                // If the ship is not in hyperspace, see if it is hit.
                if (g.ship.active && g.hyperCounter <= 0 &&
                        this.active && this.isColliding(g.ship)) {
                    if (g.sound)
                        g.sounds.crashSound.play();
                    g.explode(g.ship);
                    g.ship.stopShip(g);
                    g.ufo.stopUfo(g);
                    g.missile.stopMissile(g);
                }
            }
    }
}
