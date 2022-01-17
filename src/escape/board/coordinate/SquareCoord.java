package escape.board.coordinate;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SquareCoord implements Coordinate {

    //vars
    private final int x,y;

    /**
     * Constructor for a square coordinate
     * @param x coordinate location on the x axis
     * @param y coordinate location on the y axis
     */
    public SquareCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x value for a given coordinate
     * @return x
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Get the y value for a given coordinate
     * @return
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Creates a new coordinate at the x and y provided
     * @param x
     * @param y
     * @return
     */
    public static SquareCoord newCoordinate(int x, int y){
        return new SquareCoord(x,y);
    }

    /**
     * Get the distance from any given coordinate to the provided coordinate
     * @param coordinate the provided coordinate
     * @return
     */
    public int distanceTo(Coordinate coordinate){
        return Math.max(Math.abs(getX() - coordinate.getX()), Math.abs(getY() - coordinate.getY()));
    }

    /**
     *
     * @return the neighbors of a given coordinate
     */
    @Override
    public ArrayList<Coordinate> getNeighbors(){
        //list of neighbors
        ArrayList<Coordinate> neighbors = new ArrayList<Coordinate>();

        //helps get the values for each of the neighbors of any given point
        ArrayList<Point> neighboringPoints = new ArrayList<Point>
                (Arrays.asList(
                        new Point(1,0),
                        new Point(-1,0),
                        new Point(0,1),
                        new Point(0,-1),
                        new Point(1,-1),
                        new Point(1,1),
                        new Point(-1,-1),
                        new Point(-1,1)));

        //loop through neighboring coordinates based on point values
        for(Point p : neighboringPoints) {
            int currentPoint = neighboringPoints.indexOf(p);

            //get values to add to x and y of current coordinate we are checking for neighbors
            int xMod = (int) neighboringPoints.get(currentPoint).getX();
            int yMod = (int) neighboringPoints.get(currentPoint).getY();

            //make new neighbor and add to list of neighbors
            neighbors.add(SquareCoord.newCoordinate(x+xMod,y+yMod));
        }
        return neighbors;
    }

    /**
     * Checks that one coordinate is equal to the other
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
        if (obj instanceof SquareCoord) {
            SquareCoord other = (SquareCoord) obj;
            //if x and y are same return true, otherwise false
            return getX() == other.getX() && getY() == other.getY();
        }
        return false;
    }

    /**
     * Replace Hash code for Coordinate
     * (done so we can create new coordinates and use them to reference the piecetype map without error)
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
