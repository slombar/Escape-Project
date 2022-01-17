package escape.movement;

import escape.board.Board;
import escape.board.coordinate.Coordinate;
import escape.required.EscapePiece;
import escape.required.EscapePiece.*;

public class MovementRule {
    private EscapePiece.MovementPattern pattern;
    private Board board;

    /**
     * Constructor
     * @param pattern the movementpattern of a piece
     * @param board the current board
     */
    public MovementRule(MovementPattern pattern, Board board) {
        this.pattern = pattern;
        this.board = board;
    }

    /**
     * Check if the given coordinates can be reached with a valid move
     * @param current the coordinate we are moving from
     * @param neighbor the neighbor we want to move to
     * @param from
     * @param to
     * @return true/false if move is valid/invalid
     */
    public boolean isValidMoveType(Coordinate current, Coordinate neighbor, Coordinate from, Coordinate to) {
        boolean result = false;
        switch(pattern) {
            case OMNI:
                result = board.isOmni(current, neighbor);
                break;
            case ORTHOGONAL:
                result = board.isOrthogonal(current, neighbor);
                break;
            case DIAGONAL:
                result = board.isDiagonal(current, neighbor);
                break;
            case LINEAR:
                result = board.isLinear(neighbor, from) && board.isLinear(neighbor, to);
                break;
        }
        return result;
    }


}