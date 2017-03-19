package Asteroids;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Game extends Applet implements Runnable {

    // Copyright information.
    String copyName = "Asteroids";
    String copyVers = "Version 1.4";
    String copyInfo = "Copyright 1998-2017 by Mike Hall";
    String copyLink = "http://www.brainjar.com";
    String thanks   = "Special thanks: Pedro Gomez Lopez, Miguel Angel Sanchez Cifo";
    String copyText = copyName + '\n' + copyVers + '\n' + copyInfo + '\n' + copyLink;

    // Data for the screen font.
    Font font = new Font("Helvetica", Font.BOLD, 12);
    FontMetrics fm = getFontMetrics(font);
    int fontWidth = fm.getMaxAdvance();
    int fontHeight = fm.getHeight();

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
    static int score;
    static int highScore;

    // Flags for game state and options.
    static boolean loaded = false;
    static boolean paused;
    static boolean playing;
    static boolean sound;
    static boolean detail;

    // Key flags.
    static boolean left = false;
    static boolean right = false;
    static boolean up = false;
    static boolean down = false;

    // Sprite objects.
    static Ship ship;
    static Ufo ufo;
    static Missile missile;
    static Photon[] photons = new Photon[MAX_SHOTS];
    static Asteroid[] asteroids = new Asteroid[MAX_ROCKS];
    static Explosion[] explosions = new Explosion[MAX_SCRAP];

    // Off screen image.
    Dimension offDimension;
    Image offImage;
    Graphics offGraphics;

    // Variables related with the game
    static int asteroidsCounter;                            // Break-time counter.
    static int asteroidsLeft;                               // Number of active asteroids.
    static boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
    static double    asteroidsSpeed;               // Asteroid speed.
    static int shipsLeft;              // Number of ships left in game, including current one.
    static int shipCounter;            // Timer counter for ship explosion.
    static int hyperCounter;   // Timer counter for hyperspace.
    static int missileCounter;    // Counter for life of missle.
    static int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
    static int explosionIndex;                             // Next available explosion sprite.
    static long  photonTime;     // Time value used to keep firing rate constant.
    static int   photonIndex;    // Index to next available photon sprite.
    static int ufoPassesLeft;    // Counter for number of flying saucer passes.
    static int ufoCounter;       // Timer counter used to track each flying saucer pass.
    static int newUfoScore;
    static int newShipScore;


    // Sound object
    static Sound sounds = new Sound();

    // Counter and total used to track the loading of the sound clips.
    static int clipTotal  = 0;
    static int clipsLoaded = 0;

    // Keyboard object
    static Keyboard controls = new Keyboard();


    public String getAppletInfo() {

        // Return copyright information.
        return (copyText);
    }

    public void init() {

        Dimension d = getSize();
        int i;

        // Display copyright information.
        System.out.println(copyText);
        System.out.println(thanks);

        // Set up key event handling and set focus to applet window.
        addKeyListener(controls);
        requestFocus();

        // Save the screen size.
        SpaceElement.width = d.width;
        SpaceElement.height = d.height;

        // Generate the starry background.
        numStars = SpaceElement.width * SpaceElement.height / 5000;
        stars = new Point[numStars];
        for (i = 0; i < numStars; i++)
            stars[i] = new Point((int) (Math.random() * SpaceElement.width), (int) (Math.random() * SpaceElement.height));

        // Create shape for the ship sprite.
        ship = new Ship();
        Ship.setShip(ship);

        // Create shape for each photon sprites.
        for (i = 0; i < MAX_SHOTS; i++) {
            photons[i] = new Photon();
            Photon.setPhoton(photons[i]);
        }

        // Create shape for the flying saucer.
        ufo = new Ufo();
        Ufo.setUfo(ufo);

        // Create shape for the guided missle.
        missile = new Missile();
        Missile.setMissile(missile);

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
        asteroidsSpeed = MIN_ROCK_SPEED;
        newShipScore = NEW_SHIP_POINTS;
        newUfoScore = NEW_UFO_POINTS;

        Ship.initShip(ship);
        Photon.initPhotons(photons);
        Ufo.stopUfo(ufo);
        Missile.stopMissile(missile);
        Asteroid.initAsteroids(asteroids, asteroidIsSmall);
        Explosion.initExplosions(explosions,explosionCounter);

        playing = true;
        paused = false;
        photonTime = System.currentTimeMillis();
    }

    public static void endGame() {

        // Stop ship, flying saucer, guided missile and associated sounds.
        playing = false;
        Ship.stopShip(ship);
        Ufo.stopUfo(ufo);
        Missile.stopMissile(missile);
    }

    public void start() {

        if (loopThread == null) {
            loopThread = new Thread(this);
            loopThread.start();
        }
        if (!loaded && loadThread == null) {
            loadThread = new Thread(this);
            loadThread.start();
        }
    }

    public void stop() {

        if (loopThread != null) {
            loopThread.stop();
            loopThread = null;
        }
        if (loadThread != null) {
            loadThread.stop();
            loadThread = null;
        }
    }

    public void run() {

        int i, j;
        int shipU = 0;
        long startTime;

        // Lower this thread's priority and get the current time.
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        startTime = System.currentTimeMillis();

        // Run thread for loading sounds.
        if (!loaded && Thread.currentThread() == loadThread) {
            sounds.loadSounds(this);
            loaded = true;
            loadThread.stop();
        }

        // This is the main loop.

        while (Thread.currentThread() == loopThread) {

            if (!paused) {

                // Move and process all sprites.

                Ship.updateShip(ship);
                Photon.updatePhotons(photons);
                Ufo.updateUfo(ufo, photons, ship, missile);
                Missile.updateMissile(missile, photons, ship, ufo);
                Asteroid.updateAsteroids(asteroids, photons, asteroidIsSmall, ship, missile, ufo);
                Explosion.updateExplosions(explosions, explosionCounter);


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
                    // INIT UFO
                    Ufo.initUfo(ufo);
                }

                // If all asteroids have been destroyed create a new batch.

                if (asteroidsLeft <= 0)
                    if (--asteroidsCounter <= 0)
                        //INIT ASTEROIDS
                        Asteroid.initAsteroids(asteroids,asteroidIsSmall);
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


    public static void explode(SpaceElement s) {

        int c, i, j;
        int cx, cy;

        // Create sprites for explosion animation. The each individual line segment
        // of the given sprite is used to create a new sprite that will move
        // outward  from the sprite's original position with a random rotation.

        s.render();
        c = 2;
        if (detail || s.sprite.npoints < 6)
            c = 1;
        for (i = 0; i < s.sprite.npoints; i += c) {
            explosionIndex++;
            if (explosionIndex >= MAX_SCRAP)
                explosionIndex = 0;
            explosions[explosionIndex].active = true;
            explosions[explosionIndex].shape = new Polygon();
            j = i + 1;
            if (j >= s.sprite.npoints)
                j -= s.sprite.npoints;
            cx = (int) ((s.shape.xpoints[i] + s.shape.xpoints[j]) / 2);
            cy = (int) ((s.shape.ypoints[i] + s.shape.ypoints[j]) / 2);
            explosions[explosionIndex].shape.addPoint(
                    s.shape.xpoints[i] - cx,
                    s.shape.ypoints[i] - cy);
            explosions[explosionIndex].shape.addPoint(
                    s.shape.xpoints[j] - cx,
                    s.shape.ypoints[j] - cy);
            explosions[explosionIndex].x = s.x + cx;
            explosions[explosionIndex].y = s.y + cy;
            explosions[explosionIndex].angle = s.angle;
            explosions[explosionIndex].deltaAngle = 4 * (Math.random() * 2 * MAX_ROCK_SPIN - MAX_ROCK_SPIN);
            explosions[explosionIndex].deltaX = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.deltaX) / 2;
            explosions[explosionIndex].deltaY = (Math.random() * 2 * MAX_ROCK_SPEED - MAX_ROCK_SPEED + s.deltaY) / 2;
            explosionCounter[explosionIndex] = SCRAP_COUNT;
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {

        Dimension d = getSize();
        int i;
        int c;
        String s;
        int w, h;
        int x, y;

        // Create the off screen graphics context, if no good one exists.

        if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
            offDimension = d;
            offImage = createImage(d.width, d.height);
            offGraphics = offImage.getGraphics();
        }

        // Fill in background and stars.

        offGraphics.setColor(Color.black);
        offGraphics.fillRect(0, 0, d.width, d.height);
        if (detail) {
            offGraphics.setColor(Color.white);
            for (i = 0; i < numStars; i++)
                offGraphics.drawLine(stars[i].x, stars[i].y, stars[i].x, stars[i].y);
        }

        // Draw photon bullets.

        offGraphics.setColor(Color.white);
        for (i = 0; i < MAX_SHOTS; i++)
            if (photons[i].active)
                offGraphics.drawPolygon(photons[i].sprite);

        // Draw the guided missle, counter is used to quickly fade color to black
        // when near expiration.

        c = Math.min(missileCounter * 24, 255);
        offGraphics.setColor(new Color(c, c, c));
        if (missile.active) {
            offGraphics.drawPolygon(missile.sprite);
            offGraphics.drawLine(missile.sprite.xpoints[missile.sprite.npoints - 1], missile.sprite.ypoints[missile.sprite.npoints - 1],
                    missile.sprite.xpoints[0], missile.sprite.ypoints[0]);
        }

        // Draw the asteroids.

        for (i = 0; i < MAX_ROCKS; i++)
            if (asteroids[i].active) {
                if (detail) {
                    offGraphics.setColor(Color.black);
                    offGraphics.fillPolygon(asteroids[i].sprite);
                }
                offGraphics.setColor(Color.white);
                offGraphics.drawPolygon(asteroids[i].sprite);
                offGraphics.drawLine(asteroids[i].sprite.xpoints[asteroids[i].sprite.npoints - 1], asteroids[i].sprite.ypoints[asteroids[i].sprite.npoints - 1],
                        asteroids[i].sprite.xpoints[0], asteroids[i].sprite.ypoints[0]);
            }

        // Draw the flying saucer.

        if (ufo.active) {
            if (detail) {
                offGraphics.setColor(Color.black);
                offGraphics.fillPolygon(ufo.sprite);
            }
            offGraphics.setColor(Color.white);
            offGraphics.drawPolygon(ufo.sprite);
            offGraphics.drawLine(ufo.sprite.xpoints[ufo.sprite.npoints - 1], ufo.sprite.ypoints[ufo.sprite.npoints - 1],
                    ufo.sprite.xpoints[0], ufo.sprite.ypoints[0]);
        }

        // Draw the ship, counter is used to fade color to white on hyperspace.

        c = 255 - (255 / HYPER_COUNT) * hyperCounter;
        if (ship.active) {
            if (detail && hyperCounter == 0) {
                offGraphics.setColor(Color.black);
                offGraphics.fillPolygon(ship.sprite);
            }
            offGraphics.setColor(new Color(c, c, c));
            offGraphics.drawPolygon(ship.sprite);
            offGraphics.drawLine(ship.sprite.xpoints[ship.sprite.npoints - 1], ship.sprite.ypoints[ship.sprite.npoints - 1],
                    ship.sprite.xpoints[0], ship.sprite.ypoints[0]);

            // Draw thruster exhaust if thrusters are on. Do it randomly to get a
            // flicker effect.

            if (!paused && detail && Math.random() < 0.5) {
                if (up) {
                    offGraphics.drawPolygon(ship.thrusters[0].sprite);
                    offGraphics.drawLine(ship.thrusters[0].sprite.xpoints[ship.thrusters[0].sprite.npoints - 1], ship.thrusters[0].sprite.ypoints[ship.thrusters[0].sprite.npoints - 1],
                            ship.thrusters[0].sprite.xpoints[0], ship.thrusters[0].sprite.ypoints[0]);
                }
                if (down) {
                    offGraphics.drawPolygon(ship.thrusters[1].sprite);
                    offGraphics.drawLine(ship.thrusters[1].sprite.xpoints[ship.thrusters[1].sprite.npoints - 1], ship.thrusters[1].sprite.ypoints[ship.thrusters[1].sprite.npoints - 1],
                            ship.thrusters[1].sprite.xpoints[0], ship.thrusters[1].sprite.ypoints[0]);
                }
            }
        }

        // Draw any explosion debris, counters are used to fade color to black.

        for (i = 0; i < MAX_SCRAP; i++)
            if (explosions[i].active) {
                c = (255 / SCRAP_COUNT) * explosionCounter [i];
                offGraphics.setColor(new Color(c, c, c));
                offGraphics.drawPolygon(explosions[i].sprite);
            }

        // Display status and messages.

        offGraphics.setFont(font);
        offGraphics.setColor(Color.white);

        offGraphics.drawString("Score: " + score, fontWidth, fontHeight);
        offGraphics.drawString("Ships: " + shipsLeft, fontWidth, d.height - fontHeight);
        s = "High: " + highScore;
        offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), fontHeight);
        if (!sound) {
            s = "Mute";
            offGraphics.drawString(s, d.width - (fontWidth + fm.stringWidth(s)), d.height - fontHeight);
        }

        if (!playing) {
            s = copyName;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - 2 * fontHeight);
            s = copyVers;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 - fontHeight);
            s = copyInfo;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + fontHeight);
            s = copyLink;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 2 + 2 * fontHeight);
            s = thanks;
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 20 * fontHeight);
            if (!loaded) {
                s = "Loading sounds...";
                w = 4 * fontWidth + fm.stringWidth(s);
                h = fontHeight;
                x = (d.width - w) / 2;
                y = 3 * d.height / 4 - fm.getMaxAscent();
                offGraphics.setColor(Color.black);
                offGraphics.fillRect(x, y, w, h);
                offGraphics.setColor(Color.gray);
                if (clipTotal > 0)
                    offGraphics.fillRect(x, y, (int) (w * clipsLoaded / clipTotal), h);
                offGraphics.setColor(Color.white);
                offGraphics.drawRect(x, y, w, h);
                offGraphics.drawString(s, x + 2 * fontWidth, y + fm.getMaxAscent());
            }
            else {
                s = "Game Over";
                offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
                s = "'S' to Start";
                offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4 + fontHeight);
            }
        }
        else if (paused) {
            s = "Game Paused";
            offGraphics.drawString(s, (d.width - fm.stringWidth(s)) / 2, d.height / 4);
        }

        // Copy the off screen buffer to the screen.

        g.drawImage(offImage, 0, 0, this);
    }

}