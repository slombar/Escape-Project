package escape.game;

import com.sun.xml.bind.v2.runtime.BinderImpl;
import escape.board.Board;
import escape.board.BoardFactory;
import escape.board.piece.PieceType;
import escape.exception.EscapeException;
import escape.required.Coordinate.*;
import escape.required.EscapePiece.*;
import escape.util.*;
import escape.required.Rule.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagerFactory implements Factory {
    //vars
    private static EscapeGameInitializer initializer;

    /**
     * Constructor for the GameManagerFactory class
     * @param gameInitializer
     */
    public GameManagerFactory(EscapeGameInitializer gameInitializer) {
        this.initializer = gameInitializer;
    }

    /**
     * Make the game manager factory
     * @return the game manager associated with this factory
     */
    @Override
    public Object make() {
        Board b = makeBoard();
        GameManager m = new GameManager(b, initializePieceTypes(b, initializer.getPieceTypes()), setRules(initializer.getRules()));
        return m;
    }

    /**
     * Make the board
     * @return the board we made
     */
    public static Board makeBoard() {
        Factory<Board> boardFactory = new BoardFactory(initializer);
        return boardFactory.make();
    }

    /**
     * Initialize the piece type array for the board
     * @param b the board
     * @param pieceTypeInitializers
     * @return
     */
    public static HashMap<PieceName, PieceType> initializePieceTypes(Board b, PieceTypeDescriptor... pieceTypeInitializers) {
        HashMap<PieceName, PieceType> pieceTypeMap = new HashMap<PieceName, PieceType>();

        //ensure that there is at least one piece type initializer
        if(pieceTypeInitializers != null) {

            //check for movement pattern and name in each piecetypedescriptor
            for(PieceTypeDescriptor pti : pieceTypeInitializers) {
                if (pti.getMovementPattern() == null) {
                    Exception e = new Exception();
                    throw new EscapeException("No movement pattern", e);
                }
                if(pti.getPieceName() == null) {
                    Exception e = new Exception();
                    throw new EscapeException("No name", e);
                }

                HashMap<PieceAttributeID, PieceAttribute> attributeMap = new HashMap<PieceAttributeID, PieceAttribute>();

                if((pti.getMovementPattern() != MovementPattern.ORTHOGONAL && pti.getMovementPattern() != MovementPattern.DIAGONAL && initializer.getCoordinateType() == CoordinateType.HEX)
                        || (initializer.getCoordinateType() == CoordinateType.SQUARE)) {
                    if (pti.getAttributes() != null) {
                        for (PieceAttribute attr : pti.getAttributes()) {
                            attributeMap.put(attr.getId(), attr);
                        }
                    }
                } else {
                    throw new EscapeException("Movement pattern incorrect with given coordinate type.");
                }

                if ((attributeMap.get(PieceAttributeID.DISTANCE) == null
                        && attributeMap.get(PieceAttributeID.FLY) == null)
                        || (attributeMap.get(PieceAttributeID.DISTANCE) != null && attributeMap.get(PieceAttributeID.FLY) != null)) {
                    throw new EscapeException("A Piece must have either the distance or the fly attribute, not none or both.");
                }
                if ((attributeMap.get(PieceAttributeID.DISTANCE) != null
                        && attributeMap.get(PieceAttributeID.DISTANCE).getValue() < 0)
                        || (attributeMap.get(PieceAttributeID.FLY) != null
                        && attributeMap.get(PieceAttributeID.FLY).getValue() < 0)) {
                    throw new EscapeException("Distance and Fly values can't be less than zero.");
                }

                MovementPattern pattern = pti.getMovementPattern();
                PieceType type = new PieceType(pattern, attributeMap);
                pieceTypeMap.put(pti.getPieceName(), type);
            }
       //error: No piece rules
        } else {
            throw new EscapeException("No piece rules.");
        }

        //initialize location values in list
        LocationInitializer[] locationList = initializer.getLocationInitializers();

        //ensure there is at least one location before looping through and assigning them
        if (locationList.length != 0) {
            //check that there is a type for each location
            for (int i = 0; i < locationList.length; i++) {
                if (locationList[i].pieceName != null && !pieceTypeMap.containsKey(locationList[i].pieceName)) {
                    throw new EscapeException("Cannot be initialized without a type");
                }
            }
        }
        return pieceTypeMap;
    }

    /**
     * set the rules for the current game
     * @param rules
     * @return
     */
    public static HashMap<RuleID, RuleDescriptor> setRules(RuleDescriptor ... rules) {
        //new hashmap of rules with descriptor
        HashMap<RuleID, RuleDescriptor> theRules = new HashMap<RuleID, RuleDescriptor>();
        //rule ids
        ArrayList<RuleID> ids = new ArrayList<RuleID>();
        //add to the rules if not null
        if(rules != null) {
            //for each descriptor, add their rule to the rules
            for(RuleDescriptor r : rules) {
                theRules.put(r.ruleId, r);
                ids.add(r.ruleId);
            }
            //check for rule conflict
            if(ids.contains(RuleID.REMOVE) && ids.contains(RuleID.POINT_CONFLICT)) {
                throw new EscapeException("Cannot have both remove and point conflict.");
            }
        }
        return theRules;
    }
}

