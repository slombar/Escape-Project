package escape.board.piece;

import escape.required.EscapePiece;
import escape.required.Player;

public class Piece implements EscapePiece {

    //vars
    private final PieceName name;
    private final Player player;

    /**
     * Constructor for Piece class
     * @param player
     * @param name
     */
    public Piece(Player player, PieceName name) {
        this.player = player;
        this.name = name;
    }

    /**
     * Create a piece with given player and name
     * @param player
     * @param name
     * @return
     */
    public static Piece makePiece(Player player, PieceName name){
        return new Piece(player, name);
    }

    /**
     *
     * @return the name of the current piece
     */
    public PieceName getName() {
        return name;
    }

    /**
     *
     * @return the player who owns the current piece
     */
    public Player getPlayer() {
        return player;
    }


}


