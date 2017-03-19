package Asteroids;

import sun.jvm.hotspot.memory.Space;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Thruster extends SpaceElement
{

    public Thruster(boolean option)
    {
        if(option) {
            this.shape.addPoint(0, 12);
            this.shape.addPoint(-3, 16);
            this.shape.addPoint(0, 26);
            this.shape.addPoint(3, 16);
        }
        else
        {
            this.shape.addPoint(-2, 12);
            this.shape.addPoint(-4, 14);
            this.shape.addPoint(-2, 20);
            this.shape.addPoint(0, 14);
            this.shape.addPoint(2, 12);
            this.shape.addPoint(4, 14);
            this.shape.addPoint(2, 20);
            this.shape.addPoint(0, 14);
        }
    }

}
