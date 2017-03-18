package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Missile extends Sprite{
    // Missle data.



    public Missile(){
        this.shape.addPoint(0, -4);
        this.shape.addPoint(1, -3);
        this.shape.addPoint(1, 3);
        this.shape.addPoint(2, 4);
        this.shape.addPoint(-2, 4);
        this.shape.addPoint(-1, 3);
        this.shape.addPoint(-1, -3);
    }

    public void initMissle() {

        missle.active = true;
        missle.angle = 0.0;
        missle.deltaAngle = 0.0;
        missle.x = ufo.x;
        missle.y = ufo.y;
        missle.deltaX = 0.0;
        missle.deltaY = 0.0;
        missle.render();
        missleCounter = MISSLE_COUNT;
        if (sound)
            missleSound.loop();
        misslePlaying = true;
    }

    public void updateMissle() {

        int i;

        // Move the guided missle and check for collision with ship or photon. Stop
        // it when its counter has expired.

        if (missle.active) {
            if (--missleCounter <= 0)
                stopMissle();
            else {
                guideMissle();
                missle.advance();
                missle.render();
                for (i = 0; i < MAX_SHOTS; i++)
                    if (photons[i].active && missle.isColliding(photons[i])) {
                        if (sound)
                            crashSound.play();
                        explode(missle);
                        stopMissle();
                        score += MISSLE_POINTS;
                    }
                if (missle.active && ship.active &&
                        hyperCounter <= 0 && ship.isColliding(missle)) {
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

    public void guideMissle() {

        double dx, dy, angle;

        if (!ship.active || hyperCounter > 0)
            return;

        // Find the angle needed to hit the ship.

        dx = ship.x - missle.x;
        dy = ship.y - missle.y;
        if (dx == 0 && dy == 0)
            angle = 0;
        if (dx == 0) {
            if (dy < 0)
                angle = -Math.PI / 2;
            else
                angle = Math.PI / 2;
        }
        else {
            angle = Math.atan(Math.abs(dy / dx));
            if (dy > 0)
                angle = -angle;
            if (dx < 0)
                angle = Math.PI - angle;
        }

        // Adjust angle for screen coordinates.

        missle.angle = angle - Math.PI / 2;

        // Change the missle's angle so that it points toward the ship.

        missle.deltaX = 0.75 * MAX_ROCK_SPEED * -Math.sin(missle.angle);
        missle.deltaY = 0.75 * MAX_ROCK_SPEED *  Math.cos(missle.angle);
    }

    public void stopMissle() {

        this.active = false;

    }
}
