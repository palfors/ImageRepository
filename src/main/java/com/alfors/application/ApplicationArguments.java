package com.alfors.application;

import com.sun.jdi.connect.Connector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Peter Alfors
 * Date: 11/15/12
 */
public class ApplicationArguments 
{
    private HashMap<String, ApplicationArgument> argumentHashMap = null;

    /**
     * Default Constructor
     */
    public ApplicationArguments()
    {
    }

    /**
     * Constructor
     *
     * @param arguments The array of arguments
     *
     * Note that the The default delimeter will be used
     */
    public ApplicationArguments(String[] arguments)
    {
        this(arguments, ApplicationArgument.DEFAULT_DELIMETER);
    }

    /**
     * Constructor
     *
     * @param arguments The array of arguments
     * @param delimeter The string used to deliniate between the name and the value of the argument.
     *                  For example, name=value
     */
    public ApplicationArguments(String[] arguments, String delimeter) 
    {
        parseArguments(arguments, delimeter);        
    }

    /**
     * parse the provided array of application arguments into the key/value (name/value) pairs.
     * The default delimeter will be used.
     *
     * @param arguments The array of arguments to parse
     */
    public void parseArguments(String[] arguments)
    {
        parseArguments(arguments, ApplicationArgument.DEFAULT_DELIMETER);
    }

    /**
     * parse the provided array of application arguments into the key/value (name/value) pairs.
     *
     * @param arguments The array of arguments to parse
     * @param delimeter The string used to deliniate between the name and the value of the argument.
     *                  For example, name=value
     */
    private void parseArguments(String[] arguments, String delimeter)
    {
        if (arguments != null && arguments.length > 0)
        {
            argumentHashMap = new HashMap<String, ApplicationArgument>(arguments.length);

            ApplicationArgument applicationArgument = null;
            for (String arg : arguments)
            {
                applicationArgument = new ApplicationArgument(delimeter);
                applicationArgument.parse(arg);

                argumentHashMap.put(applicationArgument.getName(), applicationArgument);
            }
        }
        else
        {
            // default to empty map
            argumentHashMap = new HashMap<String, ApplicationArgument>();
        }
    }

    /**
     * Retrieve the argument with the specified name
     *
     * @param argumentName  The name of the argument to retrieve
     *
     * @return  The argument for the specified name
     */
    public ApplicationArgument getArgument(String argumentName)
    {
        ApplicationArgument applicationArgument = null;
        if (argumentHashMap != null)
        {
            applicationArgument = argumentHashMap.get(argumentName);
        }

        return applicationArgument;
    }

    /**
     * Return the value of the specific argument.
     * If the argument does not exist, <null> is returned
     *
     * @param argumentName  The name of the argument
     *
     * @return  The argument value or <null> if the argument does not exist
     */
    public String getArgumentValue(String argumentName)
    {
        return getArgumentValue(argumentName, null);
    }

    /**
     * Return the value of the specific argument.
     * If the argument does not exist, the specified default value is returned
     *
     * @param argumentName  The name of the argument
     * @param defaultValue  The value to return if the argument does not exist
     *
     * @return The argument value or provided default value if the argument does not exist
     */
    public String getArgumentValue(String argumentName, String defaultValue)
    {
        ApplicationArgument argument = getArgument(argumentName);

        return (argument != null) ? argument.getValue() : defaultValue;

    }

    /**
     * Log the argument contents
     */
    public void log() 
    {
        if (argumentHashMap != null && argumentHashMap.size() > 0)
        {
            Collection<ApplicationArgument> arguments = argumentHashMap.values();
            Iterator iterator = arguments.iterator();
            ApplicationArgument applicationArgument = null;
            while (iterator.hasNext())
            {
                applicationArgument = (ApplicationArgument) iterator.next();
                applicationArgument.log();
            }
        }
    }
    
}
