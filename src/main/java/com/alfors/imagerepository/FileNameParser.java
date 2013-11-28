package com.alfors.imagerepository;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: tkmal32
 * Date: 11/24/13
 * Time: 9:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileNameParser implements ImageInterogator {

    private String fileName = null;
    private List<String> name_formats = null;


    public FileNameParser(String file_name)
    {
        fileName = file_name;
    }

    public GregorianCalendar getDateTaken() throws ImageRepositoryException
    {
        GregorianCalendar date = null;

        if (fileName == null || fileName.trim().length() == 0)
            throw new ImageRepositoryException("Invalid file name [" + fileName + "]");

        date = findDateInFileName(fileName);
        if (date == null)
        {
            throw new ImageRepositoryException("Unable to determine date from file name [" + fileName + "]");
        }

        return date;
    }

    private GregorianCalendar findDateInFileName(String fileName) throws ImageRepositoryException {
        GregorianCalendar date = null;

        // interrogate the file name looking for 8 digits that would represent a date
        // assume that it will either be the start of the file name or be wrapped with underscores (_)
        if (name_formats == null)
            loadNameFormats();

        System.out.println("findDateInFileName: fileName: " + fileName);
        // cycle through the provided formats
        for (String format : name_formats)
        {
            Pattern pattern = Pattern.compile(format);
            Matcher matcher = pattern.matcher(fileName);

            System.out.println("- format: " + format);
            // will use the first occurrence only
            if (matcher.find()) {
                System.out.println("--- Found it!");
                System.out.println("--- Start index: " + matcher.start());
                System.out.println("--- End index: " + matcher.end() + " ");
                System.out.println("--- Group: " + matcher.group());
            }

        }

        if (fileName.contains("_") && fileName.indexOf("_") == 8)
        {
            String dateString = fileName.substring(0, fileName.indexOf("_"));
            int year = Integer.parseInt(dateString.substring(0,4));
            int month = Integer.parseInt(dateString.substring(4,6));
            int day = Integer.parseInt(dateString.substring(6));

            date = new GregorianCalendar(year, month, day);
        }

        return date;
    }

    /**
     * Load the formats from the properties file
     *
     * @throws ImageRepositoryException
     */
    private void loadNameFormats()
            throws ImageRepositoryException
    {
        try {
            InputStream inputStream =
                    FileNameParser.class.getResourceAsStream("/filenameparser.properties");

            if (inputStream == null)
                throw new ImageRepositoryException("Unable to load filenameparser.properties");

            name_formats = new ArrayList<String>();

            // in case some file names have both so sorting on key name to define an order to apply
            Properties properties = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            properties.load(inputStream);

            String format = null;
            Enumeration<Object> keys = properties.keys();

            while (keys.hasMoreElements())
            {
                format = properties.getProperty((String) keys.nextElement());
                System.out.println("format: " + format);
                name_formats.add(format);
            }


        } catch (IOException e) {
            throw new ImageRepositoryException(
                    "Unable to load formats from properties file [" + e.getMessage() + "]", e);
        }
    }

}
