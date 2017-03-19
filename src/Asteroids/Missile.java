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
public class Missile extends SpaceElement {

    public static void setMissile (Missile missile){
        missile.shape.addPoint(0, -4);
        missile.shape.addPoint(1, -3);
        missile.shape.addPoint(1, 3);
        missile.shape.addPoint(2, 4);
        missile.shape.addPoint(-2, 4);
        missile.shape.addPoint(-1, 3);
        missile.shape.addPoint(-1, -3);
    }

    public static void initMissile(Missile m, Ufo u) {

        m.active = true;
        m.angle = 0.0;
        m.deltaAngle = 0.0;
        m.x =u.x;
        m.y = u.y;
        m.deltaX = 0.0;
        m.deltaY = 0.0;
        m.render();
        Game.missileCounter = MISSLE_COUNT;
        if (Game.sound)
            Game.sounds.missileSound.loop();
        Game.sounds.missilePlaying = true;
    }

    public static void updateMissile (Missile m, Photon[] p, Ship s, Ufo u) {

        // Move the guided missle and check for collision with ship or photon. Stop
        // it when its counter has expired.

        if (m.active) {
            if (--Game.missileCounter <= 0)
                stopMissile(m);
            else {
                guideMissile(s, m);
                m.advance();
                m.render();
                for (int i = 0; i < Game.MAX_SHOTS; i++)
                    if (p[i].active && m.isColliding(p[i])) {
                        if (Game.sound)
                            Game.sounds.crashSound.play();
                        Game.explode(m);
                        stopMissile(m);
                        Game.score += Game.MISSLE_POINTS;
                    }
                if (m.active && s.active &&
                        Game.hyperCounter <= 0 && s.isColliding(m)) {
                    if (Game.sound)
                        Game.sounds.crashSound.play();
                    Game.explode(s);
                    Ship.stopShip(s);
                    Ufo.stopUfo(u);
                    stopMissile(m);
                }
            }
        }
    }

    public static void guideMissile(Ship s, Missile m) {

        double dx, dy, angle;

        if (!s.active || Game.hyperCounter > 0)
            return;

        // Find the angle needed to hit the ship.

        dx = s.x - m.x;
        dy = s.y - m.y;
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
                angle = - angle;
            if (dx < 0)
                angle = Math.PI - angle;
        }

        // Adjust angle for screen coordinates.

        m.angle =  angle - Math.PI / 2;

        // Change the missle's angle so that it points toward the ship.

        m.deltaX = 0.75 * MAX_ROCK_SPEED * -Math.sin( m.angle);
        m.deltaY = 0.75 * MAX_ROCK_SPEED *  Math.cos( m.angle);
    }

    public static void stopMissile(Missile m) {

        m.active = false;
        if (Game.loaded)
            Game.sounds.missileSound.stop();
        Game.sounds.missilePlaying = false;
        Game.missileCounter = 0;
    }
}