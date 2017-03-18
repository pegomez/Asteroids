package Asteroids;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Missile extends Sprite{
    // Missle data.

    int missleCounter;    // Counter for life of missle.

    public Missile(){
        this.shape.addPoint(0, -4);
        this.shape.addPoint(1, -3);
        this.shape.addPoint(1, 3);
        this.shape.addPoint(2, 4);
        this.shape.addPoint(-2, 4);
        this.shape.addPoint(-1, 3);
        this.shape.addPoint(-1, -3);
    }
}
