package Asteroids;

import static Asteroids.Game.*;

public class Ship extends SpaceElement {

    // Ship data.

    Thruster[] thrusters = new Thruster[2];

    public static void setShip(Ship ship)
    {
        ship.shape.addPoint(0, -10);
        ship.shape.addPoint(7, 10);
        ship.shape.addPoint(-7, 10);

        // Create shapes for the ship thrusters.
        ship.thrusters[0] = new Thruster(true);
        ship.thrusters[1] = new Thruster(false);
    }

    public static void initShip(Ship ship) {

        // Reset the ship sprite at the center of the screen.
        ship.active = true;
        ship.angle = 0.0;
        ship.deltaAngle = 0.0;
        ship.x = 0.0;
        ship.y = 0.0;
        ship.deltaX = 0.0;
        ship.deltaY = 0.0;
        ship.render();

        // Initialize thruster sprites.
        ship.thrusters[0].x = ship.x;
        ship.thrusters[0].y = ship.y;
        ship.thrusters[0].angle = ship.angle;
        ship.thrusters[0].render();
        ship.thrusters[1].x = ship.x;
        ship.thrusters[1].y = ship.y;
        ship.thrusters[1].angle = ship.angle;
        ship.thrusters[1].render();

        if (Game.loaded)
            Game.sounds.thrustersSound.stop();
        Game.sounds.thrustersPlaying = false;
        Game.hyperCounter = 0;
    }

    public static void updateShip(Ship ship) {

        double dx, dy, speed;

        if (!Game.playing)
            return;

        // Rotate the ship if left or right cursor key is down.

        if (Game.left) {
            ship.angle += Game.SHIP_ANGLE_STEP;
            if (ship.angle > 2 * Math.PI)
                ship.angle -= 2 * Math.PI;
        }
        if (Game.right) {
            ship.angle -= Game.SHIP_ANGLE_STEP;
            if (ship.angle < 0)
                ship.angle += 2 * Math.PI;
        }

        // Fire thrusters if up or down cursor key is down.

        dx = Game.SHIP_SPEED_STEP * -Math.sin(ship.angle);
        dy = Game.SHIP_SPEED_STEP *  Math.cos(ship.angle);
        if (Game.up) {
            ship.deltaX += dx;
            ship.deltaY += dy;
        }
        if (Game.down) {
            ship.deltaX -= dx;
            ship.deltaY -= dy;
        }

        // Don't let ship go past the speed limit.

        if (Game.up || Game.down) {
            speed = Math.sqrt(ship.deltaX * ship.deltaX + ship.deltaY * ship.deltaY);
            if (speed > Game.MAX_SHIP_SPEED) {
                dx = Game.MAX_SHIP_SPEED * -Math.sin(ship.angle);
                dy = Game.MAX_SHIP_SPEED *  Math.cos(ship.angle);
                if (Game.up)
                    ship.deltaX = dx;
                else
                    ship.deltaX = -dx;
                if (Game.up)
                    ship.deltaY = dy;
                else
                    ship.deltaY = -dy;
            }
        }

        // Move the ship. If it is currently in hyperspace, advance the countdown.

        if (ship.active) {
            ship.advance();
            ship.render();
            if (Game.hyperCounter > 0)
                Game.hyperCounter--;

            // Update the thruster sprites to match the ship sprite.

            ship.thrusters[0].x = ship.x;
            ship.thrusters[0].y = ship.y;
            ship.thrusters[0].angle = ship.angle;
            ship.thrusters[0].render();
            ship.thrusters[1].x = ship.x;
            ship.thrusters[1].y = ship.y;
            ship.thrusters[1].angle = ship.angle;
            ship.thrusters[1].render();
        }

        // Ship is exploding, advance the countdown or create a new ship if it is
        // done exploding. The new ship is added as though it were in hyperspace.
        // (This gives the player time to move the ship if it is in imminent
        // danger.) If that was the last ship, end the game.

        else
        if (--Game.shipCounter <= 0)
            if (Game.shipsLeft > 0) {
                Ship.initShip(ship);
                Game.hyperCounter = Game.HYPER_COUNT;
            }
        else
        Game.endGame();
    }

    public static void stopShip(Ship s) {

        s.active = false;
        Game.shipCounter = SCRAP_COUNT;
        if (Game.shipsLeft > 0)
            Game.shipsLeft--;
        if (Game.loaded)
            Game.sounds.thrustersSound.stop();
        Game.sounds.thrustersPlaying = false;
    }

}
