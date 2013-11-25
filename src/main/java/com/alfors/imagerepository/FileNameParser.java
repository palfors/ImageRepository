package com.alfors.imagerepository;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: tkmal32
 * Date: 11/24/13
 * Time: 9:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileNameParser implements ImageInterogator {

    String fileName = null;

    public FileNameParser(String file_name)
    {
        fileName = file_name;
    }

    public GregorianCalendar getDateTaken() throws ImageRepositoryException
    {
        GregorianCalendar date = null;

        if (fileName == null || fileName.trim().length() == 0)
            throw new ImageRepositoryException("Invalid file name [" + fileName + "]");

        if (fileName.contains("_") && fileName.indexOf("_") == 8)
        {
            String dateString = fileName.substring(0, fileName.indexOf("_"));
            int year = Integer.parseInt(dateString.substring(0,4));
            int month = Integer.parseInt(dateString.substring(4,6));
            int day = Integer.parseInt(dateString.substring(6));

            date = new GregorianCalendar(year, month, day);
        }
        else
        {
            // TODO: might be different formats where the date is embedded in the name in a different location
        }

        return date;
    }
}
