package escape;

import static org.junit.jupiter.api.Assertions.*;
import escape.board.coordinate.Coordinate;
import escape.board.piece.Piece;
import escape.game.GameManager;
import escape.required.EscapePiece;
import escape.required.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import escape.exception.EscapeException;
import escape.required.EscapePiece.*;
import static junit.framework.Assert.assertNotNull;

public class GameTests {


    @Test
    void hexTest() throws Exception {
        EscapeGameBuilder egb = new EscapeGameBuilder("config/egc/testHex.egc");
        EscapeGameManager egm = egb.makeGameManager();
        assertNotNull(egm);
    }

    @Test
    void noPieceRules() throws Exception {
        String s = "No piece rules.";
        EscapeGameBuilder egb = new EscapeGameBuilder("config/egc/testNoPieceRules.egc");
        EscapeException exception = Assertions.assertThrows(EscapeException.class,
                () -> {
                    egb.makeGameManager();
                });
        assertEquals(s, exception.getMessage());
    }

    @Test
    void hexValidCoordinate() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder("config/egc/testHex.egc");
        EscapeGameManager egm = egb.makeGameManager();
        assertNotNull(egm.makeCoordinate(-3, -3));
    }

    @Test
    void squareValid() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder("config/egc/testSquare.egc");
        EscapeGameManager egm = egb.makeGameManager();
        assertNotNull(egm.makeCoordinate(19, 20));
    }

    @Test
    void squareNotValid() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder("config/egc/testSquare.egc");
        EscapeGameManager egm = egb.makeGameManager();

        assertNull(egm.makeCoordinate(21, 1));
    }

    @Test
    void getPieceAtNull() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder("config/egc/testSquare.egc");
        EscapeGameManager egm = egb.makeGameManager();

        assertNull(egm.getPieceAt(egm.makeCoordinate(3, 3)));
    }

    @Test
    void getPieceAtNewCoord() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder("config/egc/testSquare.egc");

        GameManager egm = (GameManager) egb.makeGameManager();
        Coordinate c = egm.makeCoordinate(3, 3);
        Piece p = Piece.makePiece(Player.PLAYER1, EscapePiece.PieceName.FROG);

        egm.getBoard().placePieceAt(p, c);

        Coordinate c2 = egm.makeCoordinate(3, 3);

        Assertions.assertNotNull(egm.getPieceAt(c2));
    }

    @Test
    void placePiece() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager gameManager = (GameManager) egb.makeGameManager();

        Coordinate testCoord = gameManager.makeCoordinate(1, 1);
        Piece testPiece = Piece.makePiece(Player.PLAYER1, EscapePiece.PieceName.DOG);
        Piece actualPiece = null;
        gameManager.getBoard().placePieceAt(testPiece, testCoord);

        actualPiece = gameManager.getPieceAt(testCoord);

        Assertions.assertNotNull(actualPiece);
        assertEquals(testPiece, actualPiece);
    }

    @Test
    void getPieceAtInvalid() throws Exception {
        EscapeGameBuilder egb
                = new EscapeGameBuilder("config/egc/testSquare.egc");
        EscapeGameManager egm = egb.makeGameManager();

        assertNull(egm.getPieceAt(egm.makeCoordinate(30, 30)));
    }

    @Test
    void tenPieceGame() throws Exception {
        EscapeGameBuilder egb = new EscapeGameBuilder("config/egc/onlyExitHex.egc");
        GameManager egm = (GameManager) egb.makeGameManager();
        boolean move = false;
        //p1 always goes first
        boolean p1going = true;

        Piece p1 = new Piece(Player.PLAYER1, PieceName.DOG);
        Piece p2 = new Piece(Player.PLAYER2, PieceName.DOG);

        do {
            if (p1going) {
                p1going = false;
                Coordinate c1 = egm.makeCoordinate(-1, 0);
                //put a piece down at -1,0 for player 1
                egm.getBoard().placePieceAt(p1, c1);
                //move onto the exit
                move = egm.move(c1, egm.makeCoordinate(0, 0));
                if (move) {
                    assertTrue(move);
                    //is the piece at (-1,0) p2? if yes, the piece was not removed from the board.
                    assertNotEquals(p2, egm.getBoard().getPieceAt(c1));
                    //check that the piece is not on 0,0
                    assertNotEquals(p2, egm.getBoard().getPieceAt(egm.makeCoordinate(0, 0)));
                }
            } else {
                p1going = true;
                Coordinate c2 = egm.makeCoordinate(1, 0);
                //put a piece down at 1,0 for player 2
                egm.getBoard().placePieceAt(p2, c2);
                //move onto the exit
                move = egm.move(c2, egm.makeCoordinate(0, 0));
                if (move) {
                    assertTrue(move);
                    //is the piece at (1,0) p2? if yes, the piece was not removed from the board.
                    assertNotEquals(p2, egm.getBoard().getPieceAt(c2));
                    //check that the piece is not on 0,0
                    assertNotEquals(p2, egm.getBoard().getPieceAt(egm.makeCoordinate(0, 0)));
                }
            }
        } while (move);

        assertTrue(egm.getWinner().equals(Player.PLAYER1));
    }

    @Test
    void testAccurateScore() throws Exception {
        EscapeGameBuilder egb = new EscapeGameBuilder("config/egc/onlyExitHex.egc");
        GameManager egm = (GameManager) egb.makeGameManager();
        boolean move;

        Piece p1 = new Piece(Player.PLAYER1, PieceName.BIRD);
        Coordinate c1 = egm.makeCoordinate(5, 5);

        //put a piece down at -1,0 for player 1
        egm.getBoard().placePieceAt(p1, c1);
        //move onto the exit
        move = egm.move(c1, egm.makeCoordinate(0, 0));

        assertTrue(move);

        //a piece was moved onto the exit for p1, the score should reflect that
        assertEquals(1,egm.getScores()[1]);

        Piece p2 = new Piece(Player.PLAYER2, PieceName.BIRD);
        Coordinate c2 = egm.makeCoordinate(5, 5);

        //put a piece down at -1,0 for player 1
        egm.getBoard().placePieceAt(p2, c2);
        //move onto the exit
        move = egm.move(c2, egm.makeCoordinate(0, 0));

        assertTrue(move);

        //a piece was moved onto the exit for p1, the score should reflect that
        assertEquals(1,egm.getScores()[2]);
    }

    @Test
    void tie() throws Exception{
        EscapeGameBuilder egb = new EscapeGameBuilder("config/egc/onlyExitSquare.egc");
        GameManager egm = (GameManager) egb.makeGameManager();
        Piece p1 = new Piece(Player.PLAYER1, PieceName.BIRD);
        Coordinate c1 = egm.makeCoordinate(2, 1);
        egm.getBoard().placePieceAt(p1, c1);

        //#1
        assertTrue(egm.move(c1, egm.makeCoordinate(1, 1)));

        Piece p2 = new Piece(Player.PLAYER2, PieceName.BIRD);
        Coordinate c2 = egm.makeCoordinate(1, 2);
        egm.getBoard().placePieceAt(p2, c2);

        //#2
        assertTrue(egm.move(c2, egm.makeCoordinate(1, 1)));

        
        //at this point, both scores should be 1
        assertTrue(egm.getScores()[1] == 1 && egm.getScores()[2] == 1);

        egm.getBoard().placePieceAt(p1, c1);
        egm.getBoard().placePieceAt(p2, c2);

        //#3
        assertTrue(egm.move(c1, c1= egm.makeCoordinate(4, 1)));

        //#4
        assertTrue(egm.move(c2, c2 = egm.makeCoordinate(3, 5)));

        //#5
        assertFalse(egm.move(c1, c1 = egm.makeCoordinate(4, 5)));

        //#6
        assertFalse(egm.move(c2, egm.makeCoordinate(3, 6)));
        //is game over?
        assertTrue(egm.isGameOver());

    }

}
