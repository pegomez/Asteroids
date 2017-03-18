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
public class Ship extends Sprite {

    // Ship data.

    int shipsLeft;       // Number of ships left in game, including current one.
    int shipCounter;     // Timer counter for ship explosion.
    int hyperCounter;    // Timer counter for hyperspace.
    int newShipScore;

    public Ship()
    {
        this.shape.addPoint(0, -10);
        this.shape.addPoint(7, 10);
        this.shape.addPoint(-7, 10);
    }



}
