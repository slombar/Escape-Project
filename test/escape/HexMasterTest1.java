/*******************************************************************************
 * This files was developed for CS4533: Techniques of Programming Language Translation
 * and/or CS544: Compiler Construction
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020-21 Gary F. Pollice
 *******************************************************************************/

package escape;

import static escape.required.EscapePiece.PieceName.*;
import static escape.required.Player.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.Arguments;
import escape.BaseEscapeTestMaster;

/**
 * Description
 */
class HexMasterTest1 extends BaseEscapeTestMaster
{
    @BeforeAll
    static void classSetup()
    {
        configFile = "config/egc/R1Hex1.egc";
        currentTests = "Master Hex Game Test 1";
        initialPieceTestValue = 2;
        basicOneMoveTestValue = 3;
        harderOneMoveTestValue = 3;
        multiMoveTestValue = 3;
    }
    
    static Stream<Arguments> initialPiecesProvider()
    {
        return Stream.of(
            arguments(3, -4, HORSE, PLAYER1),   arguments(-1, -4, HORSE, PLAYER1),
            arguments(4, -3, BIRD, PLAYER1),    arguments(1, -3, SNAIL, PLAYER1),
            arguments(2, -2, DOG, PLAYER1),     arguments(-1, -2, FROG, PLAYER1),
            arguments(-3, -1, DOG, PLAYER1),    arguments(1, 0, FROG, PLAYER1),
            arguments(-2, 0, BIRD, PLAYER1),
            
            arguments(4, -1, HORSE, PLAYER2),   arguments(2, 0, FROG, PLAYER2),
            arguments(3, 1, DOG, PLAYER2),      arguments(0, 2, BIRD, PLAYER2),
            arguments(1, 3, BIRD, PLAYER2),     arguments(-3, 3, DOG, PLAYER2),
            arguments(-6, 3, HORSE, PLAYER2),   arguments(-1, 4, SNAIL, PLAYER2)
        );
            
    }
    
    static Stream<Arguments> basicOneMoveProvider()
    {
        return Stream.of(
            // valid moves
            arguments("Dog N", 2, -2, 4, -2, true),
            arguments("Horse NE", 3, -4, 3, -1, true),
            arguments("Frog SE", -1, -2, -3, 0, true),
            arguments("Snail S",1, -3, 0, -3, true),
            arguments("Bird SW", -2, 0, -2, -2, true),
            arguments("Dog NW", 2, -2, 3, -3, true),
            
            // invalid moves
            arguments("Dog exceeds limit", 2, -2, 7, -2, false),
            arguments("No pathavailable", 4, -3, 0, 1, false),
            arguments("Move 0 distance", 4, -3, 4, -3, false)
        );
    }
    
    static Stream<Arguments> harderOneMoveProvider()
    {
        return Stream.of(
            // no hard ones now
            arguments("Dog N", 2, -2, 4, -2, true)
        );
    }
    
    static Stream<Arguments> multiMoveProvider()
    {
        return Stream.of(
            arguments("both sides move", true, false,
                ml(1, 0, 0, 1, 0, 2, 0, 3)),
            arguments("move wrong player then correct", true, false,
                ml(1, 0, 0, 1, 0, 1, 1, 0, 0, 2, 0, 3)),
            arguments("Player 2 wins", true, true,
                ml(1, 0, 0, 1, 2, 0, 1, 1)),
            arguments("Move after game over", false, false,
                ml(1, 0, 0, 1, 2, 0, 2, 1, 2, -2, 2, -3))
        );
    }
}
