package escape.board;

import escape.board.coordinate.Coordinate;
import escape.board.coordinate.HexCoord;
import escape.board.coordinate.SquareCoord;
import escape.board.piece.Piece;
import escape.required.Coordinate.CoordinateType;
import escape.required.LocationType;
import escape.util.EscapeGameInitializer;
import escape.util.Factory;
import escape.util.LocationInitializer;

public class BoardFactory implements Factory {

    //vars
    private static EscapeGameInitializer egi;
    private static CoordinateType ct;

    /**
     * Constructor for the Board factory class
     * @param i
     */
    public BoardFactory(EscapeGameInitializer i) {
        this.egi = i;
        ct = egi.getCoordinateType();
    }


    /**
     * Make the board depending on the coordinate system
     * @return
     */
    @Override
    public Object make() {
        Board board = null;

        switch (ct) {
            //create hex board
            case HEX:
                board = new HexBoard(egi.getxMax(), egi.getyMax(), ct);
                break;
            //create square board
            case SQUARE:
                board = new SquareBoard(egi.getxMax(), egi.getyMax(), ct);
                break;
        }

        //init board
        initializeBoard(board, egi.getLocationInitializers());
        return board;
    }

    /**
     * Initialize the board
     * @param board the board
     * @param initializers the LocationInitializer
     */
    public static void initializeBoard(Board board, LocationInitializer... initializers) {
        Coordinate coordinate = null;

        //if we have initializers, then we will set them
        if(initializers.length != 0) {

            //for each initializer, we check our coordinate type
            for (LocationInitializer li : initializers) {
                switch(ct) {
                    case HEX:
                        coordinate =  HexCoord.newCoordinate(li.x, li.y);
                        break;
                    case SQUARE:
                        coordinate =  SquareCoord.newCoordinate(li.x, li.y);
                        break;
                }

                //check if coordinate is valid for board
                if(!board.isCoordinateValid(coordinate)) {
                    continue;
                }

                //if location is clear, then set it on the board
                if (li.locationType != null && li.locationType != LocationType.CLEAR) {
                    board.setLocationType(coordinate, li.locationType);
                }
                //if the piece on the current location isn't null, place it on the board correspondingly
                if (li.pieceName != null) {
                    board.placePieceAt(new Piece(li.player, li.pieceName), coordinate);
                }
            }
        }
    }

}
