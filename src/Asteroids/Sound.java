package Asteroids;

import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

import static Asteroids.Game.DELAY;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Sound extends Game{

    // Sound clips.
    AudioClip crashSound;
    AudioClip explosionSound;
    AudioClip fireSound;
    AudioClip missileSound;
    AudioClip saucerSound;
    AudioClip thrustersSound;
    AudioClip warpSound;

    // Flags for looping sound clips.
    boolean thrustersPlaying;
    boolean saucerPlaying;
    boolean missilePlaying;

    // Counter and total used to track the loading of the sound clips.
    int clipTotal   = 0;
    int clipsLoaded = 0;

    public void loadSounds(Game g) {

        // Load all sound clips by playing and immediately stopping them. Update
        // counter and total for display.

        try {
            crashSound     = g.getAudioClip(new URL(g.getCodeBase(), "crash.au"));
            clipTotal++;
            explosionSound = g.getAudioClip(new URL(g.getCodeBase(), "explosion.au"));
            clipTotal++;
            fireSound      = g.getAudioClip(new URL(g.getCodeBase(), "fire.au"));
            clipTotal++;
            missileSound    = g.getAudioClip(new URL(g.getCodeBase(), "missle.au"));
            clipTotal++;
            saucerSound    = g.getAudioClip(new URL(g.getCodeBase(), "saucer.au"));
            clipTotal++;
            thrustersSound = g.getAudioClip(new URL(g.getCodeBase(), "thrusters.au"));
            clipTotal++;
            warpSound      = g.getAudioClip(new URL(g.getCodeBase(), "warp.au"));
            clipTotal++;
        }
        catch (MalformedURLException e) {e.getMessage();}

        try {
            crashSound.play();     crashSound.stop();     clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            explosionSound.play(); explosionSound.stop(); clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            fireSound.play();      fireSound.stop();      clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            missileSound.play();    missileSound.stop();    clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            saucerSound.play();    saucerSound.stop();    clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            thrustersSound.play(); thrustersSound.stop(); clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
            warpSound.play();      warpSound.stop();      clipsLoaded++;
            g.repaint(); Thread.currentThread().sleep(DELAY);
        }
        catch (InterruptedException e) {}
    }

}
