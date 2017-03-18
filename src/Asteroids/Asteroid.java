package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;
import static Asteroids.Game.MAX_ROCKS;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Asteroid extends Sprite{
    // Asteroid data.

    boolean[] asteroidIsSmall = new boolean[MAX_ROCKS];    // Asteroid size flag.
    int       asteroidsCounter;                            // Break-time counter.
    double    asteroidsSpeed;                              // Asteroid speed.
    int       asteroidsLeft;                               // Number of active asteroids.

    public Asteroid(){

    }
}
