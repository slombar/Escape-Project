package escape.board.piece;

import escape.required.EscapePiece;
import escape.required.EscapePiece.*;
import escape.util.PieceAttribute;

import java.util.HashMap;

public class PieceType {
    //vars
    private MovementPattern movementPattern;
    private HashMap<PieceAttributeID, PieceAttribute> attributes;

    /**
     * Constructor
     * @param pattern the movement pattern of a piece type
     * @param attributes the hashmap of attributes for a piece type
     */
    public PieceType(MovementPattern pattern, HashMap<PieceAttributeID, PieceAttribute> attributes) {
        this.movementPattern = pattern;
        this.attributes = attributes;
    }

    /**
     * @return attributes of the given piece type
     */
    public HashMap<PieceAttributeID, PieceAttribute> getAttributes() {
        return attributes;
    }

    /**
     * @return movement pattern of the given piece type
     */
    public MovementPattern getMovementPattern() {
        return movementPattern;
    }
}
