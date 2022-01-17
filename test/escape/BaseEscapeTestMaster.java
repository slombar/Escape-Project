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

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import escape.required.*;
import escape.required.EscapePiece.PieceName;

/**
 * Description
 */
public abstract class BaseEscapeTestMaster
{

	protected static EscapeGameManager manager;
	protected static String configFile;
	protected static String currentTests;
	
	// Deduction amounts
	protected static int initialPieceTestValue;
	protected static int basicOneMoveTestValue;
	protected static int harderOneMoveTestValue;
	protected static int multiMoveTestValue;
	protected static int extraCredit;
	
	// Deductions
	protected static int initialPieceDeductions;
	protected static int basicOneMoveDeductions;
	protected static int harderOneMoveDeductions;
	protected static int multiMoveDeductions;
	protected static int extraCreditAdditions;
	
	@BeforeAll
	static void initialize()
	{
		initialPieceDeductions = 0;
		basicOneMoveDeductions = 0;
		harderOneMoveDeductions = 0;
		multiMoveDeductions = 0;
		extraCreditAdditions = 4;
	}
	
	@AfterAll
	static void reportResults()
	{
	    if (initialPieceDeductions > 5) initialPieceDeductions = 5;
		int total = -(initialPieceDeductions + basicOneMoveDeductions 
			+ harderOneMoveDeductions + multiMoveDeductions) + extraCreditAdditions;
		System.out.println("\n\n-------------- Deductions for " + currentTests 
			+ " --------------\n");
		System.out.println("\tInitial setup tests:    " + -initialPieceDeductions);
		System.out.println("\tBasic one move tests:   " + -basicOneMoveDeductions);
		System.out.println("\tHarder one move tests:  " + -harderOneMoveDeductions);
		System.out.println("\tMulti-move tests:       " + -multiMoveDeductions);
		System.out.println("\tExtra credit:           " + extraCreditAdditions);
		System.out.println("\t                       ----");
		System.out.println("\t                 TOTAL: " + total);
	}
	
	@BeforeEach
	void loadGame() throws Exception
	{
		EscapeGameBuilder egb = new EscapeGameBuilder(configFile);
		manager = egb.makeGameManager();
		assertNotNull(manager);
	}
	
	// Shared tests
	@ParameterizedTest
	@MethodSource("initialPiecesProvider")
	void initialPiecesTest(int x1, int y1, PieceName type, Player player)
	{
		// Maximum: -5
		initialPieceDeductions += initialPieceTestValue;
		Coordinate c = manager.makeCoordinate(x1, y1);
		EscapePiece p = manager.getPieceAt(c);
		assertNotNull(p);
		assertEquals(p.getName(), type);
		assertEquals(p.getPlayer(), player);
		initialPieceDeductions -= initialPieceTestValue;
	}
	
	@ParameterizedTest
	@MethodSource("basicOneMoveProvider")
	void basicOneMove(String testName, int x1, int y1, int x2, int y2, boolean expected)
	{
		basicOneMoveDeductions += basicOneMoveTestValue;
		oneMoveHelper(x1, y1, x2, y2, expected);
		basicOneMoveDeductions -= basicOneMoveTestValue;
	}
	
	@ParameterizedTest
	@MethodSource("harderOneMoveProvider")
	void harderOneMove(String testName, int x1, int y1, int x2, int y2, boolean expected)
	{
		harderOneMoveDeductions += harderOneMoveTestValue;
		oneMoveHelper(x1, y1, x2, y2, expected);
		harderOneMoveDeductions -= harderOneMoveTestValue;
	}
	
	@ParameterizedTest
	@MethodSource("multiMoveProvider")
	void multiMoveTest(String name, boolean expected, boolean exit, List<Coordinate>coords)
	{
		multiMoveDeductions += multiMoveTestValue;
		Coordinate c1 = coords.remove(0);
		Coordinate c2 = coords.remove(0);
		EscapePiece p1 = manager.getPieceAt(c1);
		while (!coords.isEmpty())
		{
			manager.move(c1, c2);
			c1 = coords.remove(0);
			c2 = coords.remove(0);
		}
		assertEquals(expected, manager.move(c1,  c2));
		EscapePiece p2 = manager.getPieceAt(c2);
		if (exit) {
			assertNull(p2);
		}
		multiMoveDeductions -= multiMoveTestValue;
	}

	// Helpers
	// Make a list
	protected static List<Coordinate> ml(int... ints)
	{
		int i = 0;
		List<Coordinate> coords = new LinkedList<Coordinate>();
		while (i < ints.length) {
			coords.add(manager.makeCoordinate(ints[i++], ints[i++]));
		}
		return coords;
	}
	
	void oneMoveHelper(int x1, int y1, int x2, int y2, boolean expected)
	{
		Coordinate c1 = manager.makeCoordinate(x1, y1);
		Coordinate c2 = manager.makeCoordinate(x2, y2);
		EscapePiece p = manager.getPieceAt(c1);
		assertEquals(expected, manager.move(c1,  c2));
	}
}
