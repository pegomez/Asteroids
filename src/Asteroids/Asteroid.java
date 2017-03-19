package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;
import Asteroids.Game.*;

import static Asteroids.Game.*;

public class Asteroid extends SpaceElement {

    public static void initAsteroids(Asteroid[] as, boolean[] aSmall) {
        int i, j;
        int s;
        double theta, r;
        int x, y;

        // Create random shapes, positions and movements for each asteroid.

        for (i = 0; i < Game.MAX_ROCKS; i++) {

            // Create a jagged shape for the asteroid and give it a random rotation.

            as[i].shape = new Polygon();
            s = Game.MIN_ROCK_SIDES + (int) (Math.random() * (Game.MAX_ROCK_SIDES - Game.MIN_ROCK_SIDES));
            for (j = 0; j < s; j ++) {
                theta = 2 * Math.PI / s * j;
                r = Game.MIN_ROCK_SIZE + (int) (Math.random() * (Game.MAX_ROCK_SIZE - Game.MIN_ROCK_SIZE));
                x = (int) -Math.round(r * Math.sin(theta));
                y = (int)  Math.round(r * Math.cos(theta));
                as[i].shape.addPoint(x, y);
            }
            as[i].active = true;
            as[i].angle = 0.0;
            as[i].deltaAngle = Math.random() * 2 * Game.MAX_ROCK_SPIN - Game.MAX_ROCK_SPIN;

            // Place the asteroid at one edge of the screen.

            if (Math.random() < 0.5) {
                as[i].x = -SpaceElement.width / 2;
                if (Math.random() < 0.5)
                    as[i].x = SpaceElement.width / 2;
                as[i].y = Math.random() * SpaceElement.height;
            }
            else {
                as[i].x = Math.random() * SpaceElement.width;
                as[i].y = -SpaceElement.height / 2;
                if (Math.random() < 0.5)
                    as[i].y = SpaceElement.height / 2;
            }

            // Set a random motion for the asteroid.

            as[i].deltaX = Math.random() * Game.asteroidsSpeed;
            if (Math.random() < 0.5)
                as[i].deltaX = -as[i].deltaX;
            as[i].deltaY = Math.random() * Game.asteroidsSpeed;
            if (Math.random() < 0.5)
                as[i].deltaY = -as[i].deltaY;

            as[i].render();
            asteroidIsSmall[i] = false;
        }

        Game.asteroidsCounter = Game.STORM_PAUSE;
        Game.asteroidsLeft = Game.MAX_ROCKS;
        if (Game.asteroidsSpeed < Game.MAX_ROCK_SPEED)
            Game.asteroidsSpeed += 0.5;
    }

    public static void initSmallAsteroids(int n, Asteroid[] a, boolean[] aS) {

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
        tempX = a[n].x;
        tempY = a[n].y;
        do {
            if (!a[i].active) {
                a[i].shape = new Polygon();
                s = Game.MIN_ROCK_SIDES + (int) (Math.random() * (Game.MAX_ROCK_SIDES - Game.MIN_ROCK_SIDES));
                for (j = 0; j < s; j ++) {
                    theta = 2 * Math.PI / s * j;
                    r = (Game.MIN_ROCK_SIZE + (int) (Math.random() * (Game.MAX_ROCK_SIZE - Game.MIN_ROCK_SIZE))) / 2;
                    x = (int) -Math.round(r * Math.sin(theta));
                    y = (int)  Math.round(r * Math.cos(theta));
                    a[i].shape.addPoint(x, y);
                }
                a[i].active = true;
                a[i].angle = 0.0;
                a[i].deltaAngle = Math.random() * 2 * Game.MAX_ROCK_SPIN - Game.MAX_ROCK_SPIN;
                a[i].x = tempX;
                a[i].y = tempY;
                a[i].deltaX = Math.random() * 2 * Game.asteroidsSpeed - Game.asteroidsSpeed;
                a[i].deltaY = Math.random() * 2 * Game.asteroidsSpeed - Game.asteroidsSpeed;
                a[i].render();
                aS[i] = true;
                count++;
                Game.asteroidsLeft++;
            }
            i++;
        } while (i < Game.MAX_ROCKS && count < 2);
    }

    public static void updateAsteroids(Asteroid[] a, Photon[] p, boolean[] aS, Ship s, Missile m, Ufo u) {

        int i, j;

        // Move any active asteroids and check for collisions.

        for (i = 0; i < Game.MAX_ROCKS; i++)
            if (a[i].active) {
                a[i].advance();
                a[i].render();

                // If hit by photon, kill asteroid and advance score. If asteroid is
                // large, make some smaller ones to replace it.

                for (j = 0; j < Game.MAX_SHOTS; j++)
                    if (p[j].active && a[i].active && a[i].isColliding(p[j])) {
                        Game.asteroidsLeft--;
                        a[i].active = false;
                        p[j].active = false;
                        if (Game.sound)
                            Game.sounds.explosionSound.play();
                        Game.explode(a[i]);
                        if (!aS[i]) {
                            Game.score += Game.BIG_POINTS;
                            Asteroid.initSmallAsteroids(i, a, aS);
                        }
                        else
                            Game.score += Game.SMALL_POINTS;
                    }

                // If the ship is not in hyperspace, see if it is hit.

                if (s.active && Game.hyperCounter <= 0 &&
                        a[i].active && a[i].isColliding(s)) {
                    if (Game.sound)
                        Game.sounds.crashSound.play();
                    Game.explode(s);
                    Ship.stopShip(s);
                    Ufo.stopUfo(u);
                    Missile.stopMissile(m);
                }
            }
    }
}
