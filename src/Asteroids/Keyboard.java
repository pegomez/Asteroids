package Asteroids;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

public class Keyboard extends Game implements KeyListener {

    public void keyPressed(KeyEvent e) {

        char c;

        // Check if any cursor keys have been pressed and set flags.

        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            left = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            right = true;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            up = true;
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down = true;

        if ((up || down) && ship.active && !sounds.thrustersPlaying) {
            if (sound && !paused)
                sounds.thrustersSound.loop();
            sounds.thrustersPlaying = true;
        }

        // Spacebar: fire a photon and start its counter.

        if (e.getKeyChar() == ' ' && ship.active) {
            if (sound & !paused)
                sounds.fireSound.play();
            photonTime = System.currentTimeMillis();
            photonIndex++;
            if (photonIndex >= MAX_SHOTS)
                photonIndex = 0;
            photons[photonIndex].active = true;
            photons[photonIndex].x = ship.x;
            photons[photonIndex].y = ship.y;
            photons[photonIndex].deltaX = 2 * MAX_ROCK_SPEED * -Math.sin(ship.angle);
            photons[photonIndex].deltaY = 2 * MAX_ROCK_SPEED *  Math.cos(ship.angle);
        }

        // Allow upper or lower case characters for remaining keys.

        c = Character.toLowerCase(e.getKeyChar());

        // 'H' key: warp ship into hyperspace by moving to a random location and
        // starting counter.

        if (c == 'h' && ship.active && hyperCounter <= 0) {
            ship.x = Math.random() * SpaceElement.width;
            ship.y = Math.random() * SpaceElement.height;
            hyperCounter = HYPER_COUNT;
            if (sound & !paused)
                sounds.warpSound.play();
        }

        // 'P' key: toggle pause mode and start or stop any active looping sound
        // clips.

        if (c == 'p') {
            if (paused) {
                if (sound && sounds.missilePlaying)
                    sounds.missileSound.loop();
                if (sound && sounds.saucerPlaying)
                    sounds.saucerSound.loop();
                if (sound && sounds.thrustersPlaying)
                    sounds.thrustersSound.loop();
            }
            else {
                if (sounds.missilePlaying)
                    sounds.missileSound.stop();
                if (sounds.saucerPlaying)
                    sounds.saucerSound.stop();
                if (sounds.thrustersPlaying)
                    sounds.thrustersSound.stop();
            }
            paused = !paused;
        }

        // 'M' key: toggle sound on or off and stop any looping sound clips.

        if (c == 'm' && loaded) {
            if (sound) {
                sounds.crashSound.stop();
                sounds.explosionSound.stop();
                sounds.fireSound.stop();
                sounds.missileSound.stop();
                sounds.saucerSound.stop();
                sounds.thrustersSound.stop();
                sounds.warpSound.stop();
            }
            else {
                if (sounds.missilePlaying && !paused)
                    sounds.missileSound.loop();
                if (sounds.saucerPlaying && !paused)
                    sounds.saucerSound.loop();
                if (sounds.thrustersPlaying && !paused)
                    sounds.thrustersSound.loop();
            }
            sound = !sound;
        }

        // 'D' key: toggle graphics detail on or off.

        if (c == 'd')
            detail = !detail;

        // 'S' key: start the game, if not already in progress.

        if (c == 's' && loaded && !playing)
            initGame();

        // 'HOME' key: jump to web site (undocumented).

        if (e.getKeyCode() == KeyEvent.VK_HOME)
            try {
                getAppletContext().showDocument(new URL(copyLink));
            }
            catch (Exception excp) {}
    }

    public void keyReleased(KeyEvent e) {

        // Check if any cursor keys where released and set flags.

        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            left = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            right = false;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            up = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            down = false;

        if (!up && !down && sounds.thrustersPlaying) {
            sounds.thrustersSound.stop();
            sounds.thrustersPlaying = false;
        }
    }

    public void keyTyped(KeyEvent e) {}
}
