package escape.game;

import escape.EscapeGameManager;
import escape.board.Board;
import escape.board.coordinate.HexCoord;
import escape.board.coordinate.SquareCoord;
import escape.board.piece.Piece;
import escape.board.piece.PieceType;
import escape.exception.EscapeException;
import escape.movement.AStar;
import escape.required.*;
import escape.required.EscapePiece.*;
import escape.required.Rule.*;

import java.util.ArrayList;
import java.util.HashMap;

import escape.board.coordinate.Coordinate;
import escape.util.RuleDescriptor;

public class GameManager implements EscapeGameManager<Coordinate> {

    //vars
    private Board board;
    private HashMap<PieceName, PieceType> pieceTypes;
    private HashMap<RuleID, RuleDescriptor> rules;
    private GameObserver observer;
    private Player current;
    private Piece last;
    private Player winner;
    private int[] scores;
    private boolean firstMove = true;
    private HashMap<Piece, Integer> pieceValueMap;
    private boolean gameOver;
    private int turns;


    /**
     * Constructor for Game Manager class
     *
     * @param b     board
     * @param types hashmap of piece names and types (movement pattern and attributes)
     * @param rules hashmap of rules with rule ids
     */
    public GameManager(Board b, HashMap<PieceName, PieceType> types, HashMap<RuleID, RuleDescriptor> rules) {
        this.board = b;
        this.pieceTypes = types;
        this.rules = rules;
        this.scores = new int[3];
        this.firstMove = true;
        this.pieceValueMap = new HashMap<Piece, Integer>();
        this.gameOver = false;
        turns = 0;
    }

    /**
     * @return the current board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return the piece types in a hashmap of <PieceName, PieceType>
     */
    public HashMap<PieceName, PieceType> getTypeMap() {
        return pieceTypes;
    }

    /**
     * @param coordinate the location to probe
     * @return the piece at the given coordinate or null if there is not a piece
     */
    @Override
    public Piece getPieceAt(Coordinate coordinate) {
        if (coordinate == null || !board.isCoordinateValid(coordinate)) {
            return null;
        }

        return board.getPieceAt(coordinate);
    }

    /**
     * @return the winning player
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * @return the scores for each player
     */
    public int[] getScores() {
        return scores;
    }

    /**
     * @return true if game is over, false if game is still going
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * make a new coordinate
     *
     * @param x the x component
     * @param y the y component
     * @return your coordinate if valid, null if not
     */
    @Override
    public Coordinate makeCoordinate(int x, int y) {
        Coordinate c = null;

        //determine which board type
        switch (board.getCoordinateType()) {
            case HEX:
                //make hex coord
                c = HexCoord.newCoordinate(x, y);
                break;
            case SQUARE:
                //make square coord
                c = SquareCoord.newCoordinate(x, y);
                break;
        }
        return (board.isCoordinateValid(c)) ? c : null;
    }

    /**
     * Add an observer to game manager
     *
     * @param o, the observer to add
     * @return
     */
    @Override
    public GameObserver addObserver(GameObserver o) {
        this.observer =  o;
        return observer;
    }

    /**
     * Remove an observer from game manager
     *
     * @param observer, the observer to remove
     * @return
     */
    @Override
    public GameObserver removeObserver(GameObserver observer) {
        this.observer = null;
        return observer;
    }

    /**
     * Notifies the observer with a message
     *
     * @param s the message
     */
    public void notifyObserver(String s) {
        observer.notify(s);
    }

    /**
     * Notify observer with error thrown
     * @param s
     * @param cause
     */
    public void notifyObserver(String s, Throwable cause) {
        observer.notify(s,cause);
    }

    /**
     * Move a piece on the given from coordinate to the given to coordinate
     *
     * @param from location of the piece you want to move via a coordinate
     * @param to   location you want to move the piece to via a coordinate
     * @return true if the move was performed, false otherwise
     */
    @Override
    public boolean move(Coordinate from, Coordinate to) {
        //Check that game is not over yet
        if (gameOver) {
            if (scores[1] == scores[2]) {
                //tie
                Throwable t = new EscapeException("Game over in a draw");
                notifyObserver("Game Over");
            } else {
                //player x wins
                Throwable t = new EscapeException(winner.name() + " wins");
                notifyObserver( winner.name() + " wins");
            }
            //move cannot be performed because game is over
            return false;
        }

        //return value
        boolean answer;
        //check if the board from and to coordinates are valid, and that there is a piece at the given from coordinate
        boolean valid = board.isCoordinateValid(from) && board.isCoordinateValid(to) && board.getPieceAt(from) != null;

        //ensure that the piece is valid and its piece type is not null
        if (valid) {
            valid = valid && (pieceTypes.get(board.getPieceAt(from).getName()) != null);
        } else {
            return false;
        }

        //coordinates check out, move on
        if (valid) {
            //set point values of given coordinates
            setPointValues(from, to);

            //check if first move is p1
            Piece p = board.getPieceAt(from);
            Player player = p.getPlayer();
            PieceName name = p.getName();

            //p2 tries to start game
            if (firstMove && !player.equals(Player.PLAYER1)) {
                notifyObserver("Player 1 goes now.");
                return false;
            }
            current = player;
            //p1 tries to go when it is p2's turn or vice versa
            if (last != null && current.equals(last.getPlayer())) {
                notifyObserver("The other player must move before you do.");
                return false;
            }

            //if the piece has fly attribute, use fly to determine if move is valid, otherwise use distance
            answer = (pieceTypes.get(name).getAttributes().get(PieceAttributeID.FLY) != null) ? fly(from, to, p) : distance(from, to, p);

            //if we're on the first move and the move is valid.
            if (answer && firstMove) {
                firstMove = false;
            }
        } else {
            Throwable t = new EscapeException("No piece type associated with coordinate.");
            notifyObserver("No piece type associated with coordinate.", t);
            return false;
        }

        //check if the player's move made their piece escape
        //and that the answer it is a valid move
        if (board.getLocationTypeAt(to) == LocationType.EXIT && answer) {
            //store number for player
            int playerNum = 0;

            //figure out who's piece it was
            if(last.getPlayer().equals(Player.PLAYER1)){
                playerNum = 1;
            }else{
                playerNum = 2;
            }
            //add the piece's value to the player's score
            int newScore = scores[playerNum] + pieceValueMap.get(last);
            scores[playerNum] = newScore;
        }
        //determine which player has a higher score
        //if p2 is higher, assign their score as highest num, otherwise p1 score is highest num even if equal
        Player higher;
        int higherNum = 0;
        if (scores[1] < scores[2]) {
            higher = Player.PLAYER2;
            higherNum = scores[2];
        } else {
            //default higher if same = player 1
            higher = Player.PLAYER1;
            higherNum = scores[1];
        }

        //if the turn limit or score limit is reached, properly end game
        if ((rules.get(RuleID.TURN_LIMIT) != null && turns >= rules.get(RuleID.TURN_LIMIT).ruleValue)
                || (rules.get(RuleID.SCORE) != null && higherNum >= rules.get(RuleID.SCORE).ruleValue)) {

            //if scores aren't equal, p1 or p2 wins
            if (scores[1] != scores[2]) {
                winner = higher;
                Throwable t = new EscapeException(higher.name() + " wins");
                notifyObserver(higher.name() + " wins");

            } else {
                //tie
                Throwable t = new EscapeException("Game over in a draw");
                notifyObserver("Game over in a draw");

            }
            gameOver = true;

        }
        //determine if last move won the game and the winner is null
        if(gameOver && winner == null){
            winner = last.getPlayer();
            return false;
        }
            return answer;

    }

    /**
     * Get distance between two coordinates based off piece distance/fly value
     *
     * @param from
     * @param to
     * @param p
     * @return
     */
    public boolean distance(Coordinate from, Coordinate to, Piece p) {
        AStar pathFinder = new AStar(p.getName(), board, pieceTypes);
        //find a path from ''from' to 'to'
        ArrayList<Coordinate> path = pathFinder.findPath(from, to);
        //check if path is null
        if (path != null) {
            //target set to first path item
            Coordinate target = path.get(0);
            //if the piece at the first item in the path is null
            if (board.getPieceAt(target) == null) {
                //remove 'from' from board
                board.removePiece(from);
                //place piece at new location (complete a move)
                board.placePieceAt(p, to);
                //set last moved piece
                last = p;
                //add a turn
                turns++;
                return true;
            }
            //if there is a piece at a target location, combat is initiated
            else if (board.getPieceAt(target).getPlayer() != p.getPlayer()) {
                //complete combat
                boolean success = combat(from, to);
                //if success, the piece successfully was placed on the board
                if (success) {
                    last = p;
                    turns++;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Assign the point values to each coordinate
     *
     * @param c1
     * @param c2
     */
    public void setPointValues(Coordinate c1, Coordinate c2) {
        //list of coordinates
        ArrayList<Coordinate> list = new ArrayList<Coordinate>();
        list.add(c1);
        //check if the piece at the given coordinate is there
        if (board.getPieceAt(c2) != null) {
            //if it is, add to list
            list.add(c2);
        }
        //for each coordinate in the list
        for (Coordinate c : list) {
            //get the piece at the current coordinate
            Piece p = board.getPieceAt(c);
            //check if the piece has a value
            if (pieceValueMap.get(p) == null) {
                //check if the piece has an attribute associated with it
                if (pieceTypes.get(p.getName()).getAttributes().get(PieceAttributeID.VALUE) == null) {
                    //if not, add a value of 1 as default
                    pieceValueMap.put(p, 1);
                } else {
                    //assign the proper value to the piece map
                    pieceValueMap.put(p, pieceTypes.get(p.getName()).getAttributes().get(PieceAttributeID.VALUE).getValue());
                }
            }
        }
    }

    /**
     * Complete combat between two pieces when one piece is moved onto the other's spot on the board
     *
     * @param from
     * @param to
     * @return true, will change when other rules are introduced.
     */
    public boolean combat(Coordinate from, Coordinate to) {
        //assign pieces and values
        Piece fromPiece = board.getPieceAt(from);
        Piece toPiece = board.getPieceAt(to);

        //set from and to values
        int fromValue = pieceValueMap.get(fromPiece);
        int toValue = pieceValueMap.get(toPiece);

        //if the remove rule is present, combat is possible
        if (rules.containsKey(RuleID.REMOVE)) {
            //remove both pieces and place from piece at to location
            board.removePiece(to);
            board.removePiece(from);
            board.placePieceAt(fromPiece, to);
            //return true, we have succeeded in combat
            return true;
        //check for point conflict rule, combat is possible
        } else if (rules.containsKey(RuleID.POINT_CONFLICT)) {
            //compare to and from values for pieces,
            // if to > from then remove from piece
            // if from < to remove to piece
            if (fromValue > toValue) {
                //add to piece value map, the piece we are coming from and its value subtracted by the value of the piece it is attacking
                //this updates its "health" for future combat
                pieceValueMap.put(fromPiece, fromValue - toValue);
                //remove to and from pieces
                board.removePiece(from);
                board.removePiece(to);
                //add from piece into to location
                board.placePieceAt(fromPiece, to);
            } else if (fromValue < toValue) {
                //add to piece to hashmap, update health as we do above
                pieceValueMap.put(toPiece, toValue - fromValue);
                board.removePiece(from);
            } else {
                //the values are equal, remove both pieces
                board.removePiece(to);
                board.removePiece(from);
            }
            return true;
        }
        return false;
    }

    /**
     * Special move case for fly
     * Moves a piece with the fly attribute
     *
     * @param from the start
     * @param to   the end
     * @param p    the piece to move
     * @return true if movable, false if not
     */
    public boolean fly(Coordinate from, Coordinate to, Piece p) {
        //get piece info
        PieceName name = p.getName();
        Player player = p.getPlayer();
        MovementPattern pattern = pieceTypes.get(name).getMovementPattern();
        //if we have not reached the destination
        if (!from.equals(to)) {
            //if the location we are moving to is not a block and the piece on it is null (no piece on that location)
            // OR the piece at the given location is not null and it belongs to the other player
            //otherwise, just return false. impossible move.
            if ((board.getPieceAt(to) == null && board.getLocationTypeAt(to) != LocationType.BLOCK) || (board.getPieceAt(to) != null && board.getPieceAt(to).getPlayer() != player)) {
                //if the piece has an omni movement pattern or orthogonal or diagonal
                if (pattern == MovementPattern.OMNI || pattern == MovementPattern.ORTHOGONAL || pattern == MovementPattern.DIAGONAL) {

                    //if the distance between from and to is less than the distance value for the current piece
                    if (from.distanceTo(to) <= pieceTypes.get(name).getAttributes().get(PieceAttributeID.FLY).getValue()) {
                        //if the piece at the to location is null
                        if (board.getPieceAt(to) == null) {
                            //remove the from piece and place it at that location
                            board.removePiece(from);
                            board.placePieceAt(p, to);
                            //set variables accordingly
                            last = p;
                            turns++;
                            //return the successful move
                            return true;
                        //if the piece at our destined location is not null (combat)
                        } else {
                            //perform combat action
                            boolean success = combat(from, to);
                            //if we succeeded with combat, return true. otherwise, return false because move was unsuccessful
                            if (success) {
                                last = p;
                                turns++;
                                return true;
                            }
                            //let observer know combat is not possible
                            notifyObserver("Combat impossible.");
                        }
                    }
                } else {
                    //if the from and to locations are linear and the distance between them is less than the distance value for the from piece
                    if (board.isLinear(from, to) && from.distanceTo(to) <= pieceTypes.get(name).getAttributes().get(PieceAttributeID.FLY).getValue()) {
                        //if there is no piece at the to location
                        if (board.getPieceAt(to) == null) {
                            //remove from piece from board, place at the to location
                            board.removePiece(from);
                            board.placePieceAt(p, to);
                            //update variables correspondingly
                            last = p;
                            turns++;
                            //move success, return true
                            return true;
                        } else {
                            //perform combat between current piece and piece at given destination
                            boolean success = combat(from, to);
                            //if combat was successful, perform updates accordingly and return true
                            if (success) {
                                last = p;
                                turns++;
                                return true;
                            }
                            //notify observer
                            notifyObserver("Combat impossible.");
                        }
                    }
                }
            }
        }
        return false;
    }


}
