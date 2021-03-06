/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.language.connector;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.opengamma.util.test.TestGroup;

/**
 * Simulates the Main class being loaded up within the JVM service wrapper. This is equivalent to JVMTest.cpp/TestStartStop
 */
@Test(groups = TestGroup.INTEGRATION)
public class MainTest {

  @Test
  public void testStartStop() {
    assertNull(Main.svcStart());
    assertTrue(Main.svcAccept("TestUser", "Foo", "Bar", "test", false));
    assertTrue(Main.svcAccept("TestUser", "Foo", "Bar", "test", true));
    assertTrue(Main.svcStop());
  }

}
