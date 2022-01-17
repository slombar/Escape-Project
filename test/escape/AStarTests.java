package escape;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import escape.board.piece.Piece;
import escape.game.GameManager;
import escape.movement.AStar;
import escape.required.EscapePiece.*;
import escape.required.Player;
import org.junit.jupiter.api.Test;

import escape.board.coordinate.Coordinate;


public class AStarTests {

    @Test
    void SquarePath1() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();
        Piece p = new Piece(Player.PLAYER1, PieceName.FROG);

        egm.getBoard().placePieceAt(p, egm.makeCoordinate(1, 1));
        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());

        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(1, 1), egm.makeCoordinate(2, 2));
        assertNotNull(foundPath);
    }

    @Test
    void SquarePath2() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.FROG);

        egm.getBoard().placePieceAt(p, egm.makeCoordinate(1, 2));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());

        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(1, 2), egm.makeCoordinate(6, 7));
        assertNull(foundPath);
    }

    @Test
    void SquarePath3() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.FROG);

        egm.getBoard().placePieceAt(p, egm.makeCoordinate(8, 1));
        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.HORSE), egm.makeCoordinate(5, 4));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(8, 1), egm.makeCoordinate(4, 5));
        assertNotNull(foundPath);
    }


    @Test
    void SquarePath4() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();
        Piece p = new Piece(Player.PLAYER1, PieceName.HORSE);

        egm.getBoard().placePieceAt(p, egm.makeCoordinate(1, 6));
        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());

        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(1, 6), egm.makeCoordinate(6, 6));
        assertNotNull(foundPath);
    }

    @Test
    void SquarePath5() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.HORSE);
        egm.getBoard().placePieceAt(p, egm.makeCoordinate(10,10));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(10,10), egm.makeCoordinate(1,1));
        assertNull(foundPath);
    }

    @Test
    void SquarePath6() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();
        Piece p = new Piece(Player.PLAYER1, PieceName.BIRD);
        Coordinate c = egm.makeCoordinate(3,6);

        egm.getBoard().placePieceAt(p, c);

        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(4, 6));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());

        ArrayList<Coordinate> foundPath = pathFinder.findPath(c, egm.makeCoordinate(5, 6));

        assertNull(foundPath);
    }

    @Test
    void SquarePath7() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.DOG);
        egm.getBoard().placePieceAt(p, egm.makeCoordinate(5, 1));

        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(5, 5));
        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());

        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(5, 1), egm.makeCoordinate(5, 8));
        assertNull(foundPath);
    }

    @Test
    void SquarePath8() throws Exception{
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testSquare.egc");
        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.HORSE);

        egm.getBoard().placePieceAt(p, egm.makeCoordinate(4, 4));
        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(8, 7));
        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(7, 8));
        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(9, 8));
        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(8, 9));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(4, 4), egm.makeCoordinate(8, 8));
        assertNotNull(foundPath);
    }

    @Test
    void HexPath1() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testHex.egc");
        GameManager egm = (GameManager) builder.makeGameManager();
        Piece p = new Piece(Player.PLAYER1, PieceName.FROG);

        egm.getBoard().placePieceAt(p, egm.makeCoordinate(0, 0));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(0, 0), egm.makeCoordinate(21, 21));
        assertNull(foundPath);
    }

    @Test
    void HexPath2() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testHex.egc");
        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.FROG);
        egm.getBoard().placePieceAt(p, egm.makeCoordinate(0, -3));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());

        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(0, -3), egm.makeCoordinate(0, 4));
        assertNotNull(foundPath);
    }

    @Test
    void HexPath3() throws Exception{
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testHex.egc");

        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.HORSE);

        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(1, 0));
        egm.getBoard().placePieceAt(p, egm.makeCoordinate(99, 0));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(99, 0), egm.makeCoordinate(0, 0));
        assertNotNull(foundPath);
    }

    @Test
    void HexPath4() throws Exception{
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testHex.egc");
        GameManager egm = (GameManager) builder.makeGameManager();
        Piece p = new Piece(Player.PLAYER1, PieceName.FROG);

        egm.getBoard().placePieceAt(new Piece(Player.PLAYER2, PieceName.FROG), egm.makeCoordinate(1, 0));
        egm.getBoard().placePieceAt(p, egm.makeCoordinate(7, 0));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(7, 0), egm.makeCoordinate(0, 0));
        assertNotNull(foundPath);
    }
    
    @Test
    void HexPath5() throws Exception{
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testHex.egc");
        GameManager egm = (GameManager) builder.makeGameManager();
        Piece p = new Piece(Player.PLAYER1, PieceName.SNAIL);

        egm.getBoard().placePieceAt(p, egm.makeCoordinate(14, 13));
        egm.getBoard().placePieceAt(new Piece(Player.PLAYER1,  PieceName.FROG), egm.makeCoordinate(0, 1));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(14, 13), egm.makeCoordinate(0, 0));
        assertNull(foundPath);
    }

    @Test
    void HexSameCoordinate() throws Exception {
        EscapeGameBuilder builder = new EscapeGameBuilder("config/egc/testHex.egc");
        GameManager egm = (GameManager) builder.makeGameManager();

        Piece p = new Piece(Player.PLAYER1, PieceName.HORSE);
        egm.getBoard().placePieceAt(p, egm.makeCoordinate(0, 2));

        AStar pathFinder = new AStar(p.getName(), egm.getBoard(), egm.getTypeMap());
        ArrayList<Coordinate> foundPath = pathFinder.findPath(egm.makeCoordinate(0, 2), egm.makeCoordinate(0, 2));
        assertNull(foundPath);
    }

}