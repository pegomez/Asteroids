package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

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
                this.x = -AsteroidsSprite.width / 2;
                if (Math.random() < 0.5)
                    this.x = AsteroidsSprite.width / 2;
                this.y = Math.random() * AsteroidsSprite.height;
            }
            else {
                this.x = Math.random() * AsteroidsSprite.width;
                this.y = -AsteroidsSprite.height / 2;
                if (Math.random() < 0.5)
                    this.y = AsteroidsSprite.height / 2;
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

    public void initSmallAsteroids(int n) {

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
        tempX = asteroids[n].x;
        tempY = asteroids[n].y;
        do {
            if (!asteroids[i].active) {
                asteroids[i].shape = new Polygon();
                s = MIN_ROCK_SIDES + (int) (Math.random() * (MAX_ROCK_SIDES - MIN_ROCK_SIDES));
                for (j = 0; j < s; j ++) {
                    theta = 2 * Math.PI / s * j;
                    r = (MIN_ROCK_SIZE + (int) (Math.random() * (MAX_ROCK_SIZE - MIN_ROCK_SIZE))) / 2;
                    x = (int) -Math.round(r * Math.sin(theta));
                    y = (int)  Math.round(r * Math.cos(theta));
                    asteroids[i].shape.addPoint(x, y);
                }
                asteroids[i].active = true;
                asteroids[i].angle = 0.0;
                asteroids[i].deltaAngle = Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN;
                asteroids[i].x = tempX;
                asteroids[i].y = tempY;
                asteroids[i].deltaX = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
                asteroids[i].deltaY = Math.random() * 2 * asteroidsSpeed - asteroidsSpeed;
                asteroids[i].render();
                asteroidIsSmall[i] = true;
                count++;
                asteroidsLeft++;
            }
            i++;
        } while (i < MAX_ROCKS && count < 2);
    }

    public void updateAsteroids() {

        int i, j;

        // Move any active asteroids and check for collisions.

        for (i = 0; i < MAX_ROCKS; i++)
            if (asteroids[i].active) {
                asteroids[i].advance();
                asteroids[i].render();

                // If hit by photon, kill asteroid and advance score. If asteroid is
                // large, make some smaller ones to replace it.

                for (j = 0; j < MAX_SHOTS; j++)
                    if (photons[j].active && asteroids[i].active && asteroids[i].isColliding(photons[j])) {
                        asteroidsLeft--;
                        asteroids[i].active = false;
                        photons[j].active = false;
                        if (sound)
                            explosionSound.play();
                        explode(asteroids[i]);
                        if (!asteroidIsSmall[i]) {
                            score += BIG_POINTS;
                            initSmallAsteroids(i);
                        }
                        else
                            score += SMALL_POINTS;
                    }

                // If the ship is not in hyperspace, see if it is hit.

                if (ship.active && hyperCounter <= 0 &&
                        asteroids[i].active && asteroids[i].isColliding(ship)) {
                    if (sound)
                        crashSound.play();
                    explode(ship);
                    stopShip();
                    stopUfo();
                    stopMissle();
                }
            }
    }
}
