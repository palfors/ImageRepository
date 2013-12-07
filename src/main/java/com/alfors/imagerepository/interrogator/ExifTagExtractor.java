package com.alfors.imagerepository.interrogator;

import com.alfors.imagerepository.ImageRepositoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Extract the date taken from the EXif tags embedded in the image
 */
@Component
public class ExifTagExtractor implements ImageInterrogator {

    private static Logger logger = LogManager.getLogger(ImageInterrogator.class.getName());

    public ExifTagExtractor()
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
        GregorianCalendar dateTaken = null;

        if (file == null)
            throw new ImageRepositoryException("getDateTaken() file is null");

        logger.debug("getDateTaken() file [" + file.getName() + "]");

        // TODO: Implement

        return dateTaken;
    }


}
