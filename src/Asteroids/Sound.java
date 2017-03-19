package Asteroids;

import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

public class Sound extends Game {

    // Flags for looping sound clips.
    static boolean thrustersPlaying;
    static boolean saucerPlaying;
    static boolean missilePlaying;

    // Sound clips.
    static AudioClip crashSound;
    static AudioClip explosionSound;
    static AudioClip fireSound;
    static AudioClip missileSound;
    static AudioClip saucerSound;
    static AudioClip thrustersSound;
    static AudioClip warpSound;

    public static void loadSounds(Game g) {

        // Load all sound clips by playing and immediately stopping them. Update
        // counter and total for display.

        try {

            crashSound     = g.getAudioClip(new URL(g.getCodeBase(), "crash.au"));
            Game.clipTotal++;
            explosionSound = g.getAudioClip(new URL(g.getCodeBase(), "explosion.au"));
            Game.clipTotal++;
            fireSound      = g.getAudioClip(new URL(g.getCodeBase(), "fire.au"));
            Game.clipTotal++;
            missileSound    = g.getAudioClip(new URL(g.getCodeBase(), "missle.au"));
            Game.clipTotal++;
            saucerSound    = g.getAudioClip(new URL(g.getCodeBase(), "saucer.au"));
            Game.clipTotal++;
            thrustersSound = g.getAudioClip(new URL(g.getCodeBase(), "thrusters.au"));
            Game.clipTotal++;
            warpSound      = g.getAudioClip(new URL(g.getCodeBase(), "warp.au"));
            Game.clipTotal++;
        }
        catch (MalformedURLException e) {e.getMessage();}

        try {
            crashSound.play();     crashSound.stop();     Game.clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            explosionSound.play(); explosionSound.stop(); Game.clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            fireSound.play();      fireSound.stop();      Game.clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            missileSound.play();    missileSound.stop();  Game.clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            saucerSound.play();    saucerSound.stop();    Game.clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            thrustersSound.play(); thrustersSound.stop(); Game.clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            warpSound.play();      warpSound.stop();      Game.clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
        }
        catch (InterruptedException e) {}
    }
}
