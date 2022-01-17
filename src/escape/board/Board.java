package escape.board;

import escape.board.coordinate.Coordinate;
import escape.board.piece.Piece;
import escape.required.Coordinate.CoordinateType;
import escape.required.LocationType;

import java.util.function.BiPredicate;

public interface Board<C extends Coordinate> {

    /**
     * Place a piece at the location specified by the coordinate
     * @param piece the piece
     * @param coordinate the coordinate
     */
    void placePieceAt(Piece piece, C coordinate);

    /**
     * Get the piece at the current location provided by the coordinate
     * @param coordinate the coordinate
     * @return
     */
    Piece getPieceAt(C coordinate);

    /**
     * Remove the piece at the given coordinate
     * @param coordinate coordinate to remove piece at
     */
    void removePiece(Coordinate coordinate);

    /**
     * Set the location type of the given coordinate on the board
     * @param coordinate the coordinate to set the location type
     * @param locationType the location type
     */
    void setLocationType(Coordinate coordinate, LocationType locationType);

    /**
     * Return the location type at the given coordinate
     * @param coordinate the coordinate
     * @return the location type at c
     */
    LocationType getLocationTypeAt(Coordinate coordinate);

    /**
     * Get the type of coordinate for the board
     * @return Coordinate Type of the board
     */
    CoordinateType getCoordinateType();

    /**
     * Check if the current coordiante is valid for a given board config.
     * @param coordinate the coordinate
     * @return true if coordinate is valid, false otherwise
     */
    boolean isCoordinateValid(Coordinate coordinate);

    /**
     * Determines if two coordinates can be reached with omni movement pattern
     * @param coordinateA 1st coordinate
     * @param coordinateB 2nd coordinate
     * @return true if so, false if not
     */
    default boolean isOmni(Coordinate coordinateA, Coordinate coordinateB) {
        BiPredicate<Coordinate, Coordinate> isOmni = (cA, cB) -> !(cA.equals(cB));
        return isOmni.test(coordinateA, coordinateB);
    }

    /**
     * Determines if two coordinates can be reached orthogonally
     * @param coord1 the first coordinate
     * @param coord2 the second coordinate
     * @return true if so, false if not
     */
    default boolean isOrthogonal(Coordinate coord1, Coordinate coord2) {
        BiPredicate<Coordinate, Coordinate> isOrthogonal = (c1, c2) -> c1.getX() == c2.getX() || c1.getY() == c2.getY();
        return isOrthogonal.test(coord1, coord2);
    }


    /**
     * Determines if two coordinates can be reached diagonally
     * @param coord1 the first coordinate
     * @param coord2 the second coordinate
     * @return true if so, false if not
     */
    default boolean isDiagonal(Coordinate coord1, Coordinate coord2) {
        BiPredicate<Coordinate, Coordinate> isDiagonal = (c1, c2) -> Math.abs(c1.getX() - c2.getX()) == Math.abs(c1.getY() - c2.getY());
        return isDiagonal.test(coord1, coord2);
    }

    /**
     * Determines if two coordinates can be reached with linear movement pattern
     * @param coord1 the first coordinate
     * @param coord2 the second coordinate
     * @return true if so, false if not
     */
    boolean isLinear(Coordinate coord1, Coordinate coord2);

}
