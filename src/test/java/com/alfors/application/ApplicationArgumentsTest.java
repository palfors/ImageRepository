package com.alfors.application;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: tkmal32
 * Date: 11/16/12
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationArgumentsTest 
{
    // cut (Class Under Test)
    private ApplicationArguments cut;

    private String[] defaultDelimeterArguments = { "arg1=val1","arg2=val2" };
    private String[] colonDelimeterArguments = { "arg1:val1","arg2:val2" };

    @Before
    public void setUp() throws Exception {
        //nothing to do
    }

    @Test
    public void testParseArgumentsNoDelimeterConstructor() throws Exception {
        cut = new ApplicationArguments(defaultDelimeterArguments);

        cut.log();

        assertNotNull("Argument arg1 not set", cut.getArgument("arg1"));
        assertTrue("Argument arg1 name not set", cut.getArgument("arg1").getName().equals("arg1"));
        assertTrue("Argument arg1 value not set", cut.getArgument("arg1").getValue().equals("val1"));

        assertNotNull("Argument arg2 not set", cut.getArgument("arg2"));
        assertTrue("Argument arg2 name not set", cut.getArgument("arg2").getName().equals("arg2"));
        assertTrue("Argument arg2 value not set", cut.getArgument("arg2").getValue().equals("val2"));
    }

    @Test
    public void testParseArgumentsDelimeterConstructor() throws Exception {
        cut = new ApplicationArguments(colonDelimeterArguments, ":");

        cut.log();

        assertNotNull("Argument arg1 not set", cut.getArgument("arg1"));
        assertTrue("Argument arg1 name not set", cut.getArgument("arg1").getName().equals("arg1"));
        assertTrue("Argument arg1 value not set", cut.getArgument("arg1").getValue().equals("val1"));

        assertNotNull("Argument arg2 not set", cut.getArgument("arg2"));
        assertTrue("Argument arg2 name not set", cut.getArgument("arg2").getName().equals("arg2"));
        assertTrue("Argument arg2 value not set", cut.getArgument("arg2").getValue().equals("val2"));
    }

    @Test
    public void testParseArgumentsNullDelimeterConstructor() throws Exception {
        cut = new ApplicationArguments(colonDelimeterArguments, null);

        cut.log();

        assertNotNull("Argument arg1 not set", cut.getArgument("arg1:val1"));
        assertTrue("Argument arg1 name not set", cut.getArgument("arg1:val1").getName().equals("arg1:val1"));
        assertTrue("Argument arg1 value not set", cut.getArgument("arg1:val1").getValue().equals("arg1:val1"));

        assertNotNull("Argument arg2 not set", cut.getArgument("arg2:val2"));
        assertTrue("Argument arg2 name not set", cut.getArgument("arg2:val2").getName().equals("arg2:val2"));
        assertTrue("Argument arg2 value not set", cut.getArgument("arg2:val2").getValue().equals("arg2:val2"));
    }

}
