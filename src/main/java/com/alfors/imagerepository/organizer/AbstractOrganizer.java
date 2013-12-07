package com.alfors.imagerepository.organizer;

import com.alfors.imagerepository.ImageRepositoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: tkmal32
 * Date: 12/6/13
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOrganizer implements ImageOrganizer {

    protected final String DUPLICATE_FOLDERNAME = "duplicates";
    private static Logger logger = LogManager.getLogger(AbstractOrganizer.class.getName());

    /**
     * Check if a file with the specified name already exists in the provided directory
     *
     * TODO: cache the directory and its file names to reduce file system lookups
     * TODO: better error handling other than Exception
     *
     * @param directory
     * @param fileName
     *
     * @return true if a file with the specified name already exists in the provided directory
     *
     * @throws ImageRepositoryException
     */
    protected boolean fileExists(File directory, String fileName)
            throws ImageRepositoryException
    {
        logger.debug("fileExists() checking directory [" + directory + "] for existing file [" + fileName + "]");
        boolean exists = false;
        if (directory == null || !directory.isDirectory())
        {
            throw new ImageRepositoryException("-- Invalid directory [" + directory + "]");
        }
        else
        {
            for (final File file : directory.listFiles()) {
                if (!file.isDirectory()) {
                    if (fileName.equalsIgnoreCase(file.getName()))
                    {
                        exists = true;
                        logger.debug("-- [" + fileName + "] already exists");
                        break;
                    }
                }
            }
        }
        return exists;
    }


    /**
     * Move the provided file to a "duplicates" directory in the same level
     * The directory will be created if necessary
     *
     * @param fileToMove
     */
    protected void moveToDuplicateFolder(File fileToMove)
    {
        String imageDir = fileToMove.getParentFile().getPath();
        // check for existing 'duplicate' folder

        File dupDir = new File(imageDir + "/" + DUPLICATE_FOLDERNAME);
        if (!dupDir.exists())
        {
            // create the directory
            logger.info("moveToDuplicateFolder() creating duplicate directory [{}]", dupDir);
            dupDir.mkdir();
        }

        // move the file into the new directory
        File dupFile = new File(dupDir.getPath() + "/" + fileToMove.getName());
        if (!dupFile.exists())
        {
            logger.debug("moveToDuplicateFolder() moving duplicate image [{}] to [{}]", fileToMove, dupDir);
            fileToMove.renameTo(dupFile);
        }
    }

}
