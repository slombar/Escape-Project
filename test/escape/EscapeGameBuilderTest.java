/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016 Gary F. Pollice
 *******************************************************************************/

package escape;

import org.junit.jupiter.api.Test;

/**
 * This is a simple test, not really a unit test, to make sure that the
 * EscapeGameBuilder, in the starting code, is actually working.
 * 
 * @version May 30, 2020
 */
class EscapeGameBuilderTest
{

	@Test
	void test() throws Exception
	{
		EscapeGameBuilder egb = new EscapeGameBuilder("config/egc/testHex.egc");
	}

}
