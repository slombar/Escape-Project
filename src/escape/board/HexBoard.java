package escape.board;

import escape.board.coordinate.Coordinate;
import escape.board.coordinate.HexCoord;
import escape.exception.EscapeException;
import escape.required.Coordinate.CoordinateType;
import escape.board.piece.Piece;
import escape.required.LocationType;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.HashMap;
import java.util.Map;

public class HexBoard implements Board {

    //vars
    private final int xMax, yMax;
    Map<Coordinate, LocationType> squares;
    Map<Coordinate, Piece> pieces;
    ArrayList<Coordinate> coordinates;
    CoordinateType ct;

    /**
     * Constructor for the HexBoard class
     * @param x, the maximum x value
     * @param y, the maximum y value
     * @param ct, the coordinate type
     */
    public HexBoard(int x, int y, CoordinateType ct) {
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

        //go through coordinates to determine if coordinate is in hashmap
        for (Coordinate c: coordinates) {
            //if found, return piece. otherwise null.
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
        //remove from hashmap and coordinates
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
        squares.put(coordinate, locationType);
    }

    /**
     * Get the location type at a given coordinate
     * @param coordinate the coordinate
     * @return coordinate's location type
     */
    @Override
    public LocationType getLocationTypeAt(Coordinate coordinate) {
        //add clear location type if not there
        squares.putIfAbsent(coordinate, LocationType.CLEAR);
        return squares.get(coordinate);
    }


    /**
     * Check if a coordinate is valid on the board
     * @param coordinate the coordinate
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isCoordinateValid(Coordinate coordinate) {
        return isCoordinateValid.test((HexCoord) coordinate);
    }
    /**
     * Lambda function to see if the coordinate is valid
     * @param coordinate the coordinate
     * @return true/false if valid/invalid
     */
    public Predicate<HexCoord> isCoordinateValid = (c) ->
            (c.getX() <= getxMax() && c.getX() >= 0 && getyMax() == 0 && getxMax() != 0) ||
                    (c.getY() <= getyMax() && c.getY() >= 0 && getxMax() == 0 && getyMax() != 0) ||
                    (getxMax() == 0 && getyMax() == 0);

    /**
     * Check if two coordinates are linear
     * @param c1
     * @param c2
     */
    @Override
    public boolean isLinear(Coordinate c1, Coordinate c2){
        boolean answer = false;

        //if both x values are equal
        if(c1.getX() == c2.getX()) {
            answer = true;
        }
        //if both y values are equal
        else if(c1.getY() == c2.getY()) {
            answer = true;
        }
        //check for diagonal for hex
        else if(Math.abs(c1.getX()) - Math.abs(c2.getX()) == Math.abs(c1.getY()) - Math.abs(c2.getY())
                && c1.getX() - c2.getX() != c1.getY() - c2.getY()) {
            answer = true;
        }
        //true/false
        return answer;
    }


}
