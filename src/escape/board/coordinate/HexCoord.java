package escape.board.coordinate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class HexCoord implements Coordinate {

    //vars
    private final int x, y;


    /**
     * Constructor for hex coordinates
     *
     * @param x the location on the x axis of the coordinate
     * @param y the location on the y axis of the coordinate
     */
    public HexCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x value for a given coordinate
     *
     * @return x
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Get the y value for a given coordinate
     *
     * @return
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Creates a new coordinate at the x and y provided
     *
     * @param x
     * @param y
     * @return
     */
    public static HexCoord newCoordinate(int x, int y) {
        return new HexCoord(x, y);
    }

    /**
     * Distance from any given coordinate to the coordinate provided
     *
     * @param c the provided coordinate
     * @return
     */
    @Override
    public int distanceTo(Coordinate c) {
        //differences between given coordinate(c) and current coordinate(this)
        int xDiff = getX() - c.getX();
        int yDiff = getY() - c.getY();

        //If the difference of x and y are equal return the maximum value
        return (Integer.signum(xDiff) == Integer.signum(yDiff)) ? Math.abs(xDiff + yDiff)
                : Math.max(Math.abs(xDiff), Math.abs(yDiff));
    }

    /**
     * @return the neighbors of a given coordinate
     */
    @Override
    public ArrayList<Coordinate> getNeighbors() {
        //list of neighbors
        ArrayList<Coordinate> neighbors = new ArrayList<Coordinate>();

        //helps get the values for each of the neighbors of any given point
        ArrayList<Point> modifiersForHex =
                new ArrayList<Point>(Arrays.asList(
                        new Point(0, 1),
                        new Point(0, -1),
                        new Point(-1, 1),
                        new Point(1, 0),
                        new Point(-1, 0),
                        new Point(1, -1)));

        //loop through neighboring coordinates based on point values
        for (Point p : modifiersForHex) {
            int currentPoint = modifiersForHex.indexOf(p);

            //get values to add to x and y of current coordinate we are checking for neighbors
            int xMod = (int) modifiersForHex.get(currentPoint).getX();
            int yMod = (int) modifiersForHex.get(currentPoint).getY();

            //make new neighbor and add to list of neighbors
            neighbors.add(HexCoord.newCoordinate(getX() + xMod, getY() + yMod));
        }

        return neighbors;
    }


    /**
     * Checks that one coordinate is equal to the other
     *
     * @param obj
     * @return true if same, false if not
     */
    @Override
    public boolean equals(Object obj) {
        //return true if same
        if (this == obj) {
            return true;
        }
        //conditional to check if they are the same if first check is unsuccessful
        if (obj instanceof HexCoord) {
            HexCoord other = (HexCoord) obj;
            //if x and y are same return true, otherwise false
            return getX() == other.getX() && getY() == other.getY();
        }
        return false;
    }

    /**
     * Replace Hash code for Coordinate
     * (done so we can create new coordinates and use them to reference the piecetype map without error)
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


}
