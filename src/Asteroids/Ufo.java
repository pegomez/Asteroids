package Asteroids;

import sun.jvm.hotspot.memory.Space;

import static Asteroids.Game.*;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Ufo extends SpaceElement {


    public static void setUfo(Ufo ufo){
        ufo.shape.addPoint(-15, 0);
        ufo.shape.addPoint(-10, -5);
        ufo.shape.addPoint(-5, -5);
        ufo.shape.addPoint(-5, -8);
        ufo.shape.addPoint(5, -8);
        ufo.shape.addPoint(5, -5);
        ufo.shape.addPoint(10, -5);
        ufo.shape.addPoint(15, 0);
        ufo.shape.addPoint(10, 5);
        ufo.shape.addPoint(-10, 5);
    }

    public static void initUfo(Ufo ufo) {

        double angle, speed;

        // Randomly set flying saucer at left or right edge of the screen.

        ufo.active = true;
        ufo.x = -SpaceElement.width / 2;
        ufo.y = Math.random() * 2 * SpaceElement.height - SpaceElement.height;
        angle = Math.random() * Math.PI / 4 - Math.PI / 2;
        speed = MAX_ROCK_SPEED / 2 + Math.random() * (MAX_ROCK_SPEED / 2);
        ufo.deltaX = speed * -Math.sin(angle);
        ufo.deltaY = speed *  Math.cos(angle);
        if (Math.random() < 0.5) {
            ufo.x = SpaceElement.width / 2;
            ufo.deltaX = -ufo.deltaX;
        }
        if (ufo.y > 0)
            ufo.deltaY = ufo.deltaY;
        ufo.render();

        Game.sounds.saucerPlaying = true;
        if (Game.sound)
            Game.sounds.saucerSound.loop();

        ufoCounter = (int) Math.abs(SpaceElement.width / ufo.deltaX);
    }

    public static void updateUfo(Ufo ufo, Photon[] photons, Ship ship, Missile missile) {

        int i, d;
        boolean wrapped;

        // Move the flying saucer and check for collision with a photon. Stop it
        // when its counter has expired.

        if (ufo.active) {
            if (-- Game.ufoCounter <= 0) {
                if (--Game.ufoPassesLeft > 0)
                    initUfo(ufo);
                //else
                //stopUfo();                      UNCOMMENT HERE
            }
            if (ufo.active) {
                ufo.advance();
                ufo.render();
                for (i = 0; i < Game.MAX_SHOTS; i++)
                    if (photons[i].active && ufo.isColliding(photons[i])) {
                        if (Game.sound)
                            Game.sounds.crashSound.play();

                        Game.explode(ufo);
                        stopUfo(ufo);
                        Game.score += Game.UFO_POINTS;
                    }

                // On occassion, fire a missile at the ship if the saucer is not too
                // close to it.

                d = (int) Math.max(Math.abs(ufo.x - ship.x), Math.abs(ufo.y - ship.y));
                if (ship.active && Game.hyperCounter <= 0 &&
                        ufo.active && !missile.active &&
                        d > Game.MAX_ROCK_SPEED * Game.FPS / 2 &&
                        Math.random() < Game.MISSLE_PROBABILITY);
                Missile.initMissile(missile, ufo);
            }
        }
    }

    public static void stopUfo(Ufo ufo) {
        ufo.active = false;
        if (Game.loaded)
            Game.sounds.saucerSound.stop();
        Game.sounds.saucerPlaying = false;
        Game.ufoCounter = 0;
        Game.ufoPassesLeft = 0;
    }
}