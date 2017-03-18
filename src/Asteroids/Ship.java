package Asteroids;

import static Asteroids.Game.*;

/**
 * Created by pedrogomezlopez on 18/3/17.
 */

public class Ship extends Sprite {

    // Ship data.
    int newShipScore;
    Thruster[] thrusters = new Thruster[2];

    public Ship()
    {
        this.shape.addPoint(0, -10);
        this.shape.addPoint(7, 10);
        this.shape.addPoint(-7, 10);

        // Create shapes for the ship thrusters.
        this.thrusters[0] = new Thruster(true);
        this.thrusters[1] = new Thruster(false);

    }

    public void initShip(Game g) {

        // Reset the ship sprite at the center of the screen.
        this.active = true;
        this.angle = 0.0;
        this.deltaAngle = 0.0;
        this.x = 0.0;
        this.y = 0.0;
        this.deltaX = 0.0;
        this.deltaY = 0.0;
        this.render();

        // Initialize thruster sprites.
        this.thrusters[0].x = this.x;
        this.thrusters[0].y = this.y;
        this.thrusters[0].angle = this.angle;
        this.thrusters[0].render();
        this.thrusters[1].x = this.x;
        this.thrusters[1].y = this.y;
        this.thrusters[1].angle = this.angle;
        this.thrusters[1].render();

        if (g.sound)
            g.sounds.thrustersSound.stop();
        g.sounds.thrustersPlaying = false;
        g.hyperCounter = 0;
    }

    public void updateShip(Game g) {

        double dx, dy, speed;

        if(!g.playing)
            return;

        // Rotate the ship if left or right cursor key is down.
        if (g.left) {
            this.angle += SHIP_ANGLE_STEP;
            if (this.angle > 2 * Math.PI)
                this.angle -= 2 * Math.PI;
        }
        if (g.right) {
            this.angle -= SHIP_ANGLE_STEP;
            if (this.angle < 0)
                this.angle += 2 * Math.PI;
        }

        // Fire thrusters if up or down cursor key is down.
        dx = SHIP_SPEED_STEP * -Math.sin(this.angle);
        dy = SHIP_SPEED_STEP *  Math.cos(this.angle);
        if (g.up) {
            this.deltaX += dx;
            this.deltaY += dy;
        }
        if (g.down) {
            this.deltaX -= dx;
            this.deltaY -= dy;
        }

        // Don't let ship go past the speed limit.
        if (g.up || g.down) {
            speed = Math.sqrt(this.deltaX * this.deltaX + this.deltaY * this.deltaY);
            if (speed > MAX_SHIP_SPEED) {
                dx = MAX_SHIP_SPEED * -Math.sin(this.angle);
                dy = MAX_SHIP_SPEED *  Math.cos(this.angle);
                if (game.up)
                    this.deltaX = dx;
                else
                    this.deltaX = -dx;
                if (g.up)
                    this.deltaY = dy;
                else
                    this.deltaY = -dy;
            }
        }

        // Move the ship. If it is currently in hyperspace, advance the countdown.
        if (this.active) {
            this.advance();
            this.render();
            if (g.hyperCounter > 0)
                g.hyperCounter--;

            // Update the thruster sprites to match the ship sprite.
            this.thrusters[0].x = this.x;
            this.thrusters[0].y = this.y;
            this.thrusters[0].angle = this.angle;
            this.thrusters[0].render();
            this.thrusters[1].x = this.x;
            this.thrusters[1].y = this.y;
            this.thrusters[1].angle = this.angle;
            this.thrusters[1].render();
        }

        // Ship is exploding, advance the countdown or create a new ship if it is
        // done exploding. The new ship is added as though it were in hyperspace.
        // (This gives the player time to move the ship if it is in imminent
        // danger.) If that was the last ship, end the game.

        else
        if (--g.shipCounter <= 0)
            if (g.shipsLeft > 0)
            {
                initShip(g);
                g.hyperCounter = HYPER_COUNT;
            }
            else
                g.endGame();
    }

    public void stopShip(Game g) {

        this.active = false;
        g.shipCounter = SCRAP_COUNT;
        if (g.shipsLeft > 0)
            g.shipsLeft--;
        if (g.loaded)
            g.sounds.thrustersSound.stop();
        g.sounds.thrustersPlaying = false;
    }

}
