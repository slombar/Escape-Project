package escape.board.coordinate;

import java.util.ArrayList;

public interface Coordinate extends escape.required.Coordinate {
    /**
     * Get the distance to a coordinate from any given coordinate
     * @param coord
     * @return
     */
    public int distanceTo(Coordinate coord);

    /**
     * Get the x of a given coordinate
     * @return
     */
    public int getX();

    /**
     * Get the y of a given coordinate
     * @return
     */
    public int getY();

    /**
     * Get the neighbors of a given coordinate
     * @return
     */
    public ArrayList<Coordinate> getNeighbors();

    /**
     * Check if two coordinates are equal
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object);

}
