package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.net.URL;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Game extends Applet implements Runnable, KeyListener {

    // Copyright information.

    String copyName = "Asteroids";
    String copyVers = "Version 1.3";
    String copyInfo = "Copyright 1998-2001 by Mike Hall";
    String copyLink = "http://www.brainjar.com";
    String copyText = copyName + '\n' + copyVers + '\n'
            + copyInfo + '\n' + copyLink;

    // Thread control variables.

    Thread loadThread;
    Thread loopThread;

    // Constants

    static final int DELAY = 20;                         // Milliseconds between screen and
    static final int FPS = Math.round(1000 / DELAY);   // the resulting frame rate.

    static final int MAX_SHOTS = 8;          // Maximum number of sprites
    static final int MAX_ROCKS = 8;          // for photons, asteroids and
    static final int MAX_SCRAP = 40;          // explosions.

    static final int SCRAP_COUNT = 2 * FPS;  // Timer counter starting values
    static final int HYPER_COUNT = 3 * FPS;  // calculated using number of
    static final int MISSLE_COUNT = 4 * FPS;  // seconds x frames per second.
    static final int STORM_PAUSE = 2 * FPS;

    static final int MIN_ROCK_SIDES = 6; // Ranges for asteroid shape, size
    static final int MAX_ROCK_SIDES = 16; // speed and rotation.
    static final int MIN_ROCK_SIZE = 20;
    static final int MAX_ROCK_SIZE = 40;
    static final double MIN_ROCK_SPEED = 40.0 / FPS;
    static final double MAX_ROCK_SPEED = 240.0 / FPS;
    static final double MAX_ROCK_SPIN = Math.PI / FPS;

    static final int MAX_SHIPS = 3;           // Starting number of ships for each game.
    static final int UFO_PASSES = 3;          // Number of passes for flying saucer per appearance.

    // Ship's rotation and acceleration rates and maximum speed.

    static final double SHIP_ANGLE_STEP = Math.PI / FPS;
    static final double SHIP_SPEED_STEP = 15.0 / FPS;
    static final double MAX_SHIP_SPEED = 1.25 * MAX_ROCK_SPEED;

    static final int FIRE_DELAY = 50;         // Minimum number of milliseconds required between photon shots.

    // Probablility of flying saucer firing a missle during any given frame (other conditions must be met).

    static final double MISSLE_PROBABILITY = 0.45 / FPS;

    static final int BIG_POINTS = 25;     // Points scored for shooting
    static final int SMALL_POINTS = 50;     // various objects.
    static final int UFO_POINTS = 250;
    static final int MISSLE_POINTS = 500;

    // Background stars.

    int numStars;
    Point[] stars;

    // Number of points the must be scored to earn a new ship or to cause the
    // flying saucer to appear.

    static final int NEW_SHIP_POINTS = 5000;
    static final int NEW_UFO_POINTS = 2750;

    // Game data.

    int score;
    int highScore;


    // Flags for game state and options.

    boolean loaded = false;
    boolean paused;
    boolean playing;
    boolean sound;
    boolean detail;

    // Key flags.

    boolean left = false;
    boolean right = false;
    boolean up = false;
    boolean down = false;

    // Sprite objects.

    Ship ship;
    Thruster fwdThruster, revThruster;
    Ufo ufo;
    Missile missile;
    Photon[] photons = new Photon[MAX_SHOTS];
    Asteroid[] asteroids = new Asteroid[MAX_ROCKS];
    Explosion[] explosions = new Explosion[MAX_SCRAP];

    // Sound object
    Sound sounds = new Sound();

    // Off screen image.

    Dimension offDimension;
    Image offImage;
    Graphics offGraphics;

    // Data for the screen font.

    Font font = new Font("Helvetica", Font.BOLD, 12);
    FontMetrics fm = getFontMetrics(font);
    int fontWidth = fm.getMaxAdvance();
    int fontHeight = fm.getHeight();

    public String getAppletInfo() {

        // Return copyright information.

        return (copyText);
    }

    public void init() {

        Dimension d = getSize();
        int i;

        // Display copyright information.

        System.out.println(copyText);

        // Set up key event handling and set focus to applet window.

        addKeyListener(this);
        requestFocus();

        // Save the screen size.

        Sprite.width = d.width;
        Sprite.height = d.height;

        // Generate the starry background.

        numStars = Sprite.width * Sprite.height / 5000;
        stars = new Point[numStars];
        for (i = 0; i < numStars; i++)
            stars[i] = new Point((int) (Math.random() * Sprite.width), (int) (Math.random() * Sprite.height));

        // Create shape for the ship sprite.
        ship = new Ship();

        // Create shapes for the ship thrusters.
        fwdThruster = new Thruster(true);
        revThruster = new Thruster(false);

        // Create shape for each photon sprites.
        for (i = 0; i < MAX_SHOTS; i++) {
            photons[i] = new Photon();
        }

        // Create shape for the flying saucer.
        ufo = new Ufo();

        // Create shape for the guided missle.
        missile = new Missile();

        // Create asteroid sprites.
        for (i = 0; i < MAX_ROCKS; i++)
            asteroids[i] = new Asteroid();

        // Create explosion sprites.
        for (i = 0; i < MAX_SCRAP; i++)
            explosions[i] = new Explosion();

        // Initialize game data and put us in 'game over' mode.
        highScore = 0;
        sound = true;
        detail = true;
        initGame();
        endGame();
    }

    public void initGame() {

        // Initialize game data and sprites.
        score = 0;
        ship.shipsLeft = MAX_SHIPS;
        for (Asteroid asteroid : asteroids){
            asteroid.asteroidsSpeed = MIN_ROCK_SPEED;
        }
        ship.newShipScore = NEW_SHIP_POINTS;
        ufo.newUfoScore = NEW_UFO_POINTS;

        initShip();
        initPhotons();
        stopUfo();
        stopMissle();
        initAsteroids();
        initExplosions();
        playing = true;
        paused = false;
        photonTime = System.currentTimeMillis();
    }

    public void run() {

        int i, j;
        long startTime;

        // Lower this thread's priority and get the current time.

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        startTime = System.currentTimeMillis();

        // Run thread for loading sounds.

        if (!loaded && Thread.currentThread() == loadThread) {
            sounds.loadSounds();
            loaded = true;
            loadThread.stop();
        }

        // This is the main loop.

        while (Thread.currentThread() == loopThread) {

            if (!paused) {

                // Move and process all sprites.

                updateShip();
                updatePhotons();
                updateUfo();
                updateMissle();
                updateAsteroids();
                updateExplosions();

                // Check the score and advance high score, add a new ship or start the
                // flying saucer as necessary.

                if (score > highScore)
                    highScore = score;
                if (score > newShipScore) {
                    newShipScore += NEW_SHIP_POINTS;
                    shipsLeft++;
                }
                if (playing && score > newUfoScore && !ufo.active) {
                    newUfoScore += NEW_UFO_POINTS;
                    ufoPassesLeft = UFO_PASSES;
                    initUfo();
                }

                // If all asteroids have been destroyed create a new batch.

                if (asteroidsLeft <= 0)
                    if (--asteroidsCounter <= 0)
                        initAsteroids();
            }

            // Update the screen and set the timer for the next loop.

            repaint();
            try {
                startTime += DELAY;
                Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }





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
            ship.x = Math.random() * AsteroidsSprite.width;
            ship.y = Math.random() * AsteroidsSprite.height;
            hyperCounter = HYPER_COUNT;
            if (sound & !paused)
                warpSound.play();
        }

        // 'P' key: toggle pause mode and start or stop any active looping sound
        // clips.

        if (c == 'p') {
            if (paused) {
                if (sound && misslePlaying)
                    missleSound.loop();
                if (sound && saucerPlaying)
                    saucerSound.loop();
                if (sound && thrustersPlaying)
                    thrustersSound.loop();
            }
            else {
                if (misslePlaying)
                    missleSound.stop();
                if (saucerPlaying)
                    saucerSound.stop();
                if (thrustersPlaying)
                    thrustersSound.stop();
            }
            paused = !paused;
        }

        // 'M' key: toggle sound on or off and stop any looping sound clips.

        if (c == 'm' && loaded) {
            if (sound) {
                crashSound.stop();
                explosionSound.stop();
                fireSound.stop();
                missleSound.stop();
                saucerSound.stop();
                thrustersSound.stop();
                warpSound.stop();
            }
            else {
                if (misslePlaying && !paused)
                    missleSound.loop();
                if (saucerPlaying && !paused)
                    saucerSound.loop();
                if (thrustersPlaying && !paused)
                    thrustersSound.loop();
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

        if (!up && !down && thrustersPlaying) {
            thrustersSound.stop();
            thrustersPlaying = false;
        }
    }

    public void keyTyped(KeyEvent e) {}

}
