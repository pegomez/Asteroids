package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;
import static Asteroids.Game.MAX_SCRAP;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Explosion {
    // Explosion data.

    int[] explosionCounter = new int[MAX_SCRAP];  // Time counters for explosions.
    int   explosionIndex;                         // Next available explosion sprite.
}
