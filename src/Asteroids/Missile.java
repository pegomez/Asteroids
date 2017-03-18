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
public class Missile extends Sprite{

    // Missile data
    Game gameMissile;

    public Missile(){
        this.shape.addPoint(0, -4);
        this.shape.addPoint(1, -3);
        this.shape.addPoint(1, 3);
        this.shape.addPoint(2, 4);
        this.shape.addPoint(-2, 4);
        this.shape.addPoint(-1, 3);
        this.shape.addPoint(-1, -3);
    }

    public void initMissile(Game g) {

        this.active = true;
        this.angle = 0.0;
        this.deltaAngle = 0.0;
        this.x = game.ufo.x;
        this.y = game.ufo.y;
        this.deltaX = 0.0;
        this.deltaY = 0.0;
        this.render();
        g.missileCounter = MISSLE_COUNT;
        if (g.sound)
            g.sounds.missileSound.loop();
        g.sounds.missilePlaying = true;
    }

    public void updateMissile(Game g) {

        int i;

        // Move the guided missle and check for collision with ship or photon. Stop
        // it when its counter has expired.

        if (this.active) {
            if (--g.missileCounter <= 0)
                stopMissile(g);
            else {
                guideMissle(g);
                this.advance();
                this.render();
                for (i = 0; i < MAX_SHOTS; i++)
                    if (g.photons[i].active && this.isColliding(g.photons[i])) {
                        if (g.sound)
                            g.sounds.crashSound.play();
                        g.explode(this);
                        stopMissile(g);
                        g.score += MISSLE_POINTS;
                    }
                if (this.active && g.ship.active &&
                        g.hyperCounter <= 0 && g.ship.isColliding(this)) {
                    if (g.sound)
                        g.sounds.crashSound.play();
                    g.explode(game.ship);
                    g.ship.stopShip(g);
                    g.ufo.stopUfo(g);
                    stopMissile(g);
                }
            }
        }
    }

    public void guideMissle(Game g) {

        double dx, dy, angle;

        if (!g.ship.active || g.hyperCounter > 0)
            return;

        // Find the angle needed to hit the ship.

        dx = g.ship.x - this.x;
        dy = g.ship.y - this.y;
        if (dx == 0 && dy == 0)
            this.angle = 0;
        if (dx == 0) {
            if (dy < 0)
                this.angle = -Math.PI / 2;
            else
                this.angle = Math.PI / 2;
        }
        else {
            this.angle = Math.atan(Math.abs(dy / dx));
            if (dy > 0)
                this.angle = - this.angle;
            if (dx < 0)
                this.angle = Math.PI - this.angle;
        }

        // Adjust angle for screen coordinates.

        this.angle =  this.angle - Math.PI / 2;

        // Change the missle's angle so that it points toward the ship.

        this.deltaX = 0.75 * MAX_ROCK_SPEED * -Math.sin( this.angle);
        this.deltaY = 0.75 * MAX_ROCK_SPEED *  Math.cos( this.angle);
    }

    public void stopMissile(Game g) {

        this.active = false;
        if (g.loaded)
            g.sounds.missileSound.stop();
        g.sounds.missilePlaying = false;
        g.missileCounter = 0;
    }
}
