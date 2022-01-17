/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020 Gary F. Pollice
 *******************************************************************************/

package escape;

import static escape.required.EscapePiece.PieceName.*;
import static escape.required.Player.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;
import escape.BaseEscapeTestMaster;

/**
 * Test cases or Beta square board games
 */
public class SquareMasterTest1 extends BaseEscapeTestMaster
{
    @BeforeAll
    static void classSetup()
    {
        configFile = "config/egc/R1Square1.egc";
        currentTests = "Master Square Game Test 1";
        initialPieceTestValue = 2;
        basicOneMoveTestValue = 3;
        harderOneMoveTestValue = 3;
        multiMoveTestValue = 3;
    }
    
    static Stream<Arguments> initialPiecesProvider()
    {
        return Stream.of(
            arguments(5, 5, HORSE, PLAYER1),    arguments(5, 8, DOG, PLAYER1),
            arguments(5, 13, DOG, PLAYER1),     arguments(7, 6, HORSE, PLAYER1),
            arguments(8, 7, BIRD, PLAYER1),     arguments(9, 5, FROG, PLAYER1),
            arguments(9, 8, FROG, PLAYER1),     arguments(9, 13, SNAIL, PLAYER1),
            arguments(11, 11, BIRD, PLAYER1),   arguments(11, 12, DOG, PLAYER1),
            
            arguments(10, 10, BIRD, PLAYER2),   arguments(10, 11, BIRD, PLAYER2),
            arguments(10, 12, BIRD, PLAYER2),   arguments(11, 5, BIRD, PLAYER2),
            arguments(11, 8, BIRD, PLAYER2),   arguments(11, 10, BIRD, PLAYER2),
            arguments(11, 15, BIRD, PLAYER2),   arguments(12, 6, BIRD, PLAYER2),
            arguments(12, 10, BIRD, PLAYER2),   arguments(12, 12, BIRD, PLAYER2)
        );
            
    }
    
    static Stream<Arguments> basicOneMoveProvider()
    {
        return Stream.of(
            // valid moves
            arguments("Frog W", 9, 5, 9, 4, true),
            arguments("Frog NW", 9, 5, 10, 4, true),
            arguments("Bird N", 8, 7, 10, 7, true),
            arguments("Horse NE", 5, 5, 8, 8, true),
            arguments("Dog E", 5, 8, 5, 12, true),
            arguments("Snail SE", 9, 13, 8, 14, true),
            arguments("Bird S", 8, 7, 5, 7, true),
            arguments("Horse SW", 7, 6, 4, 3, true),
            arguments("Bird to Exit", 8, 7, 8, 10, true),
            
            // invalid moves
            arguments("Bird too far", 11, 11, 9, 11, false),
            arguments("No piece on source", 9, 6, 9, 7, false),
            arguments("Frog exceeds limit", 9, 5, 5, 4, false),
            arguments("Move wrong player", 11, 5, 11, 4, false),
            arguments("Move on top of own piece", 8, 7, 9, 8, false),
            arguments("Move on top of opponent", 9, 5, 11, 5, false),
            arguments("Move 0 distance", 5, 13, 5, 13, false)
        );
    }
    
    static Stream<Arguments> harderOneMoveProvider()
    {
        return Stream.of(
            // valid moves
            arguments("Dog non-linear", 11, 12, 11, 9, true),
            
            // invalid moves
            arguments("Dog too far", 11, 12, 10, 9, false)
        );
    }
    
    static Stream<Arguments> multiMoveProvider()
    {
        return Stream.of(
            arguments("both sides move", true, false,
                ml(9, 8, 8, 8, 11, 5, 12, 5)),
            arguments("move wrong player then correct", true, false,
                ml(9, 8, 8, 8, 8, 8, 9, 8, 11, 5, 12, 5)),
            arguments("Player 1 wins", true, true,
                ml(8, 7, 8, 11, 10, 10, 11, 9, 9, 8, 8, 10)),
            arguments("Move after game over", false, false,
                ml(8, 7, 8, 11, 10, 10, 11, 9, 9, 8, 8, 10, 10, 11, 9, 11))
        );
    }
}
