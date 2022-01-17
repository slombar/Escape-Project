package escape.board;

import escape.board.coordinate.Coordinate;
import escape.board.coordinate.SquareCoord;
import escape.board.piece.Piece;
import escape.exception.EscapeException;
import escape.required.LocationType;
import escape.required.Coordinate.CoordinateType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class SquareBoard implements Board{

    //vars
    Map<Coordinate, LocationType> squares;
    Map<Coordinate, Piece> pieces;
    private final int xMax, yMax;
    ArrayList<Coordinate> coordinates;
    CoordinateType ct;

    /**
     * Constructor for the SquareBoard class
     * @param x, the maximum x value
     * @param y, the maximum y value
     * @param ct, the coordinate type
     */
    public SquareBoard(int x, int y, CoordinateType ct) {
        this.xMax = x;
        this.yMax = y;
        this.ct = ct;
        pieces = new HashMap<Coordinate, Piece>();
        coordinates = new ArrayList<Coordinate>();
        squares = new HashMap<Coordinate, LocationType>();
    }

    /**
     *
     * @return the maximum x value of the board
     */
    public int getxMax() {
        return xMax;
    }

    /**
     *
     * @return the maximum y value of the board
     */
    public int getyMax() {
        return yMax;
    }

    /**
     *
     * @return the coordinate type of the board
     */
    @Override
    public CoordinateType getCoordinateType() {
        return ct;
    }


    /**
     *
     * @param coordinate the coordinate
     * @return a piece in the current position given or null if no piece is present
     */
    @Override
    public Piece getPieceAt(Coordinate coordinate) {

        for (Coordinate c: coordinates) {
            if(coordinate.getX() == c.getX() && coordinate.getY() == c.getY()){
                return pieces.get(c);
            }
        }

        return pieces.get(coordinate);
    }

    /**
     * take a piece off of the board at the given coordinate
     * @param coordinate coordinate to remove piece at
     */
    @Override
    public void removePiece(Coordinate coordinate) {
        pieces.remove(coordinate);
        coordinates.remove(coordinate);
    }

    /**
     * Place a piece on the board at the given coordinate
     * @param piece the piece
     * @param coordinate the coordinate
     */
    @Override
    public void placePieceAt(Piece piece, Coordinate coordinate) {
        //check if coordinate is valid
        if(isCoordinateValid(coordinate)) {
            //if on exit location or no piece
            if(squares.get(coordinate) == LocationType.EXIT || piece == null) {
                pieces.remove(coordinate);
                coordinates.remove(coordinate);
            //cannot move onto block location
            }else if(squares.get(coordinate) == LocationType.BLOCK){
                throw new EscapeException("Pieces cannot be on a block location.");
            }
            //otherwise, we are good to place piece
            else {
                pieces.put(coordinate, piece);
                coordinates.add(coordinate);
            }
            //coordinate for piece is not valid for board type
        } else {
            throw new EscapeException("Coordinate invalid.");
        }
    }

    /**
     * Set the location type of a given square on the board based off of the given coordinate
     * @param coordinate the coordinate to set the location type
     * @param locationType the location type
     */
    @Override
    public void setLocationType(Coordinate coordinate, LocationType locationType) {
        squares.put(coordinate,locationType);
    }

    /**
     * Get the location type at a given coordinate
     * @param coordinate the coordinate
     * @return coordinate's location type
     */
    @Override
    public LocationType getLocationTypeAt(Coordinate coordinate) {
        squares.putIfAbsent(coordinate, LocationType.CLEAR);
        return squares.get(coordinate);
    }

    /**
     * Determines if two coordinates are linear
     * @param c1
     * @param c2
     * @return
     */
    @Override
    public boolean isLinear(Coordinate c1, Coordinate c2) {
        return isDiagonal(c1, c2) || isOrthogonal(c1, c2);
    }

    /**
     * Check if a coordinate is valid on the board
     * @param coordinate the coordinate
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isCoordinateValid(Coordinate coordinate) {
        boolean result;
        result = isSquareCoordinateValid.test((SquareCoord) coordinate);
        return result;
    }

    /**
     * Lambda function to check if the given square coordinate is valid for the current board
     * @param c the coordinate to check
     * @return true or false based on validity
     */
    public Predicate<SquareCoord> isSquareCoordinateValid = (c) ->
            (c.getX() <= getxMax() && c.getY() <= getyMax() && c.getX() >= 1 && c.getY() >= 1);
}
