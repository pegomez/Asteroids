package Asteroids;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */
public class Ufo extends Sprite{
    // Flying saucer data.

    int ufoPassesLeft;    // Counter for number of flying saucer passes.
    int ufoCounter;       // Timer counter used to track each flying saucer pass.
    int newUfoScore;

    public Ufo(){
        this.shape.addPoint(-15, 0);
        this.shape.addPoint(-10, -5);
        this.shape.addPoint(-5, -5);
        this.shape.addPoint(-5, -8);
        this.shape.addPoint(5, -8);
        this.shape.addPoint(5, -5);
        this.shape.addPoint(10, -5);
        this.shape.addPoint(15, 0);
        this.shape.addPoint(10, 5);
        this.shape.addPoint(-10, 5);
    }
}
