package com.alfors.application;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Peter Alfors
 * Date: 11/16/12
 */
public class ApplicationArgumentTest
{

    // cut (Class Under Test)
    private ApplicationArgument cut;

    @Before
    public void setUp() throws Exception {
        //nothing to do
    }

    @Test
    public void testParseArgumentDefaultDelimeter() throws Exception {
        cut = new ApplicationArgument();
        cut.parse("arg1=val1");

        cut.log();

        assertTrue("Name not set", cut.getName().equals("arg1"));
        assertTrue("Value not set", cut.getValue().equals("val1"));
    }

    @Test
    public void testParseArgumentDelimeter() throws Exception {
        cut = new ApplicationArgument(":");
        cut.parse("arg1:val1");

        cut.log();

        assertTrue("Name not set", cut.getName().equals("arg1"));
        assertTrue("Value not set", cut.getValue().equals("val1"));
    }

    @Test
    public void testParseArgumentNoDelimeter() throws Exception {
        cut = new ApplicationArgument();
        cut.parse("arg1:val1", null);

        cut.log();

        assertTrue("Name not set", cut.getName().equals("arg1:val1"));
        assertTrue("Value not set", cut.getValue().equals("arg1:val1"));
    }

    @Test
    public void testParseArgumentNullDelimeter() throws Exception {
        cut = new ApplicationArgument(null);
        cut.parse("arg1:val1");

        cut.log();

        assertTrue("Name not set", cut.getName().equals("arg1:val1"));
        assertTrue("Value not set", cut.getValue().equals("arg1:val1"));
    }

}
