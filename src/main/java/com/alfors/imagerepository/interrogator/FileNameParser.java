package com.alfors.imagerepository.interrogator;

import com.alfors.imagerepository.ImageRepositoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

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
@Component
public class FileNameParser implements ImageInterrogator {

    private List<String> name_formats = null;
    private static Logger logger = LogManager.getLogger(ImageInterrogator.class.getName());

    public FileNameParser()
    {
    }

    /**
     * Determine the date the image was taken
     *
     * @param file  The file to check
     *
     * @return  The date the image was taken, or null if unable to determine the date
     *
     * @throws com.alfors.imagerepository.ImageRepositoryException
     */
    public GregorianCalendar getDateTaken(File file) throws ImageRepositoryException
    {
        if (file == null)
            throw new ImageRepositoryException("getDateTaken() Missing image file");

        String fileName = file.getName();
        if (fileName.trim().length() == 0)
            throw new ImageRepositoryException("getDateTaken() Invalid image file name [" + fileName + "]");

        return findDateInFileName(fileName);
    }

    private GregorianCalendar findDateInFileName(String fileName) throws ImageRepositoryException
    {
        GregorianCalendar date = null;

        logger.debug("findDateInFileName() fileName: " + fileName);

        // interrogate the file name looking for 8 digits that would represent a date
        // assume that it will either be the start of the file name or be wrapped with underscores (_)
        if (name_formats == null)
            loadNameFormats();

        // cycle through the provided formats
        for (String format : name_formats)
        {
            Pattern pattern = Pattern.compile(format);
            Matcher matcher = pattern.matcher(fileName);

            logger.debug("findDateInFileName() format: " + format);
            // will use the first occurrence only
            if (matcher.find()) {
                String dateString = matcher.group();

                // filter out non digits
                pattern = Pattern.compile("\\d{8}");
                matcher = pattern.matcher(dateString);
                if (matcher.find()) {
                    dateString = matcher.group();

                    try
                    {
                        int year = Integer.parseInt(dateString.substring(0,4));
                        int month = Integer.parseInt(dateString.substring(4,6));
                        int day = Integer.parseInt(dateString.substring(6));

                        // note that the month is 0 based
                        date = new GregorianCalendar(year, (month-1), day);

                        logger.debug("findDateInFileName() defined year [{}] month [{}] day [{}]",
                                date.get(GregorianCalendar.YEAR),
                                date.get(GregorianCalendar.MONTH),
                                date.get(GregorianCalendar.DAY_OF_MONTH));
                    }
                    catch (NumberFormatException e)
                    {
                        // if we get to here, then then format (regular expression) defined in the properties
                        // files allowed non-numeric characters in the date portion of the file name
                        // This is an unsupported format, so we will throw an exception because it will fail again
                        throw new ImageRepositoryException("Failed to parse the result [" + dateString +
                                "] of the format filter [" + format +
                                "] into a number.  Check the format regular expression.  " +
                                "The output must be an 8 digit number in the pattern YYYYMMDD");
                    }
                }
                else {
                    throw new ImageRepositoryException(
                            "findDateInFileName() Format [" + format + "] does not provide an 8 digit date");
                }
            }
        }

        if (date == null)
            logger.debug("findDateInFileName() Unable to determine date from file [" + fileName + "]");

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
                throw new ImageRepositoryException("loadNameFormats() Unable to load filenameparser.properties");

            name_formats = new ArrayList<String>();

            // in case some file names have both so sorting on key name to define an order to apply
            Properties properties = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<Object>(super.keySet()));
                }
            };
            properties.load(inputStream);

            String key = null;
            String format = null;
            Enumeration<Object> keys = properties.keys();

            while (keys.hasMoreElements())
            {
                key = (String) keys.nextElement();
                if (key.toLowerCase().startsWith("format."))
                {
                    format = properties.getProperty(key);
                    logger.info("loadNameFormats() adding format [" + format + "] from key [" + key + "]");

                    name_formats.add(format);
                }
            }


        } catch (IOException e) {
            throw new ImageRepositoryException(
                    "loadNameFormats() Unable to load formats from properties file [" + e.getMessage() + "]", e);
        }
    }

}
