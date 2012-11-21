package com.alfors.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: tkmal32
 * Date: 11/15/12
 * Time: 6:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationArgument 
{
    public static final String DEFAULT_DELIMETER = "=";

    private String name = null;
    private String value = null;
    private String delimeter = DEFAULT_DELIMETER;

    private static Logger logger = LogManager.getLogger("ImageRepository");

    public ApplicationArgument() {
    }

    public ApplicationArgument(String delimeter) {
        this.delimeter = delimeter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    protected void parse(String argument)
    {
        parse(argument, delimeter);
    }

    protected void parse(String argument, String delim)
    {
        if (argument != null)
        {
            if (delim != null && delim.length() > 0) {
                setName(argument.substring(0, argument.indexOf(delim)));
                setValue(argument.substring(argument.indexOf(delim)+1));
            }
            else {
                setName(argument);
                setValue(argument);
            }
        }
    }

    public void log()
    {
        logger.info("Argument name [{}] value [{}]", name, value);
    }

}
