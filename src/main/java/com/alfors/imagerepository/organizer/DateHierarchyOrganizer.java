package com.alfors.imagerepository.organizer;

import com.alfors.imagerepository.ImageRepositoryException;
import com.alfors.imagerepository.interrogator.ImageInterrogator;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkmal32
 * Date: 12/6/13
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateHierarchyOrganizer extends AbstractOrganizer
{
    private List<String> destination_hierarchy_formats = null;
    private final String DESTINATION_FORMAT_LEVEL_KEY_PREFIX = "dest.dir.format.level.";
    
    private static Logger logger = LogManager.getLogger(DateHierarchyOrganizer.class.getName());

    /**
     *
     * @param imageFile
     * @param destinationDir
     * @param imageDate
     * @throws ImageRepositoryException
     */
    public void organize(final File imageFile, File destinationDir, GregorianCalendar imageDate)
            throws ImageRepositoryException
    {
        if (imageFile == null)
            throw new ImageRepositoryException("organize() imageFile is required!");
        if (destinationDir == null)
            throw new ImageRepositoryException("organize() destinationDir is required!");
        if (imageDate == null)
            throw new ImageRepositoryException("organize() imageDate is required!");

        // load the destination directory formats (levels)
        // TODO: implement based on the provided formats
//        if (destination_hierarchy_formats == null)
//            loadDestinationFormats();

        // parse the date
        String year = String.format("%04d", imageDate.get(GregorianCalendar.YEAR));
        String month = String.format("%02d", (imageDate.get(GregorianCalendar.MONTH)+1));  // month is 0-based
        String day = String.format("%02d", imageDate.get(GregorianCalendar.DAY_OF_MONTH));

        logger.debug("organize() extracted year [{}] month [{}] day [{}] from file [{}]", year, month, day, imageFile.getPath());
        
        // check if YEAR destination directory exists
        File destYearDir = new File(destinationDir.getPath() + "/" + year);
        logger.debug("organize() destYearDir [{}]", destYearDir);
        if (!destYearDir.exists())
        {
            logger.debug("organize() creating year directory [{}]", destYearDir);
            // create the directory
            destYearDir.mkdir();
        }

        // check if destination directory exists
        // [dest dir]/yyyy/yyyy_MM_dd
        String destDirName = new StringBuilder().append(
                destinationDir).append("/").append(
                year).append("/").append(
                year).append("_").append(
                month).append("_").append(
                day).toString();
        File destDir = new File(destDirName);
        if (!destDir.exists())
        {
            // create the directory
            logger.info("organize() creating date directory [{}]", destDir);
            destDir.mkdir();
        }

        // move the file to the destination directory (if a file with that name does not already exist)
        if (!fileExists(destDir, imageFile.getName()))
        {
            String filePath = imageFile.getPath();
            String newFilePath = destDir.getPath() + "/" + imageFile.getName();
            imageFile.renameTo(new File(newFilePath));
            logger.info("organize() moved file [{}] to [{}]", filePath, newFilePath);
        }
        else
        {
            // move to a duplicated directory so that it is clear why it was not moved and let the user decide to
            // delete them or not
            this.moveToDuplicateFolder(imageFile);
            logger.info("organize() File [{}] already exists in destination directory [{}]. File was moved the duplicates directory instead", imageFile.getPath(), destDir.getName());
        }

    }

    /**
     * Load the formats from the properties file
     *
     * @throws ImageRepositoryException
     */
    private void loadDestinationFormats()
            throws ImageRepositoryException
    {
        try {
            InputStream inputStream =
                    DateHierarchyOrganizer.class.getResourceAsStream("/datehierarchyorganizer.properties");

            if (inputStream == null)
                throw new ImageRepositoryException("loadDestinationFormats() Unable to load datehierarchyorganizer.properties");

            destination_hierarchy_formats = new ArrayList<String>();

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
                if (key.toLowerCase().startsWith(DESTINATION_FORMAT_LEVEL_KEY_PREFIX))
                {
                    format = properties.getProperty(key);
                    logger.info("loadDestinationFormats() adding format level [" + format + "] from key [" + key + "]");

                    destination_hierarchy_formats.add(format);
                }
            }


        } catch (IOException e) {
            throw new ImageRepositoryException(
                    "loadNameFormats() Unable to load formats from properties file [" + e.getMessage() + "]", e);
        }
    }
    
}
