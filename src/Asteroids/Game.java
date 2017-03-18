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
    Game game = new Game();

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
    private boolean loaded = false;
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

    // Varaibles related with the game
    int asteroidsCounter;                            // Break-time counter.
    int asteroidsLeft;                               // Number of active asteroids.
    int shipsLeft;              // Number of ships left in game, including current one.
    int shipCounter;            // Timer counter for ship explosion.
    int hyperCounter;   // Timer counter for hyperspace.
    int missileCounter;    // Counter for life of missle.
    int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
    long  photonTime;     // Time value used to keep firing rate constant.
    int   photonIndex;    // Index to next available photon sprite.

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
        shipsLeft = MAX_SHIPS;
        for (int i = 0; i<asteroids.length;i++){
            asteroids[i].asteroidsSpeed = MIN_ROCK_SPEED;
        }
        ship.newShipScore = NEW_SHIP_POINTS;
        ufo.newUfoScore = NEW_UFO_POINTS;

        ship.initShip();

        if (loaded)
            sounds.thrustersSound.stop();
            sounds.thrustersPlaying = false;
        hyperCounter = 0;

        for(int i = 0;i<photons.length;i++){
            photons[i].initPhoton();
        }
        photonIndex = 0;

        ufo.stopUfo();
        if (loaded)
            sounds.saucerSound.stop();
            sounds.saucerPlaying = false;

        missile.stopMissle();
        if (loaded)
            sounds.missileSound.stop();
            sounds.missilePlaying = false;
        missileCounter = 0;

        for(int i = 0;i<asteroids.length;i++){
            asteroids[i].initAsteroid();
        }
        asteroidsCounter = STORM_PAUSE;
        asteroidsLeft = MAX_ROCKS;

        for(int i = 0;i<explosions.length;i++){
            explosions[i].initExplosion();
            explosionCounter[i] = 0;
        }
        playing = true;
        paused = false;
        photonTime = System.currentTimeMillis();
    }

    public void endGame() {

        // Stop ship, flying saucer, guided missile and associated sounds.

        playing = false;
        ship.stopShip();
        ufo.stopUfo();
        missile.stopMissle();
    }

    public void run() {

        int i, j;
        int shipU = 0;
        long startTime;

        // Lower this thread's priority and get the current time.

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        startTime = System.currentTimeMillis();

        // Run thread for loading sounds.

        if (!isLoaded() && Thread.currentThread() == loadThread) {
            sounds.loadSounds();
            setLoaded(true);
            loadThread.stop();
        }

        // This is the main loop.

        while (Thread.currentThread() == loopThread) {

            if (!paused) {

                // Move and process all sprites.
                if (playing) {
                    shipU = ship.updateShip(right,left,up,down,hyperCounter,shipCounter,shipsLeft);
                }
                if (shipU == HYPER_COUNT){
                    ship.initShip();
                    if (loaded)
                        sounds.thrustersSound.stop();
                    sounds.thrustersPlaying = false;
                    hyperCounter = 0;
                }
                else endGame();

                for (int w = 0;w<photons.length;w++){
                    photons[w].updatePhoton();
                }

                ufo.updateUfo();

                updateMissle();
                updateAsteroids();
                updateExplosions();

                // Check the score and advance high score, add a new ship or start the
                // flying saucer as necessary.

                if (score > highScore)
                    highScore = score;
                if (score > ship.newShipScore) {
                    ship.newShipScore += NEW_SHIP_POINTS;
                    shipsLeft++;
                }
                if (playing && score > ufo.newUfoScore && !ufo.active) {
                    ufo.newUfoScore += NEW_UFO_POINTS;
                    ufo.ufoPassesLeft = UFO_PASSES;
                    // INIT UFO
                    initUfo();
                }

                // If all asteroids have been destroyed create a new batch.

                if (asteroidsLeft <= 0)
                    if (--asteroidsCounter <= 0)
                        //INIT ASTEROIDS
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

        if (c == 'm' && isLoaded()) {
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

        if (c == 's' && isLoaded() && !playing)
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

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}