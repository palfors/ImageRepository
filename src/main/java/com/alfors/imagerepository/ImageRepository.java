package com.alfors.imagerepository;

import com.alfors.application.ApplicationArguments;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Peter Alfors
 */
public class ImageRepository
{
    ApplicationArguments applicationArguments = null;

    private static final String ARG_SOURCE_PATH = "path";
    private static final String ARG_RECURSIVE = "recursive";

    private static Logger logger = LogManager.getLogger(ImageRepository.class.getName());

    public static void main(String[] args)
    {
        logger.info("ImageRepository start time: " + new Date(System.currentTimeMillis()));
        ImageRepository imageRepository = new ImageRepository(args);

        imageRepository.organizeImages("/Users/tkmal32/data/2013/personal","/Users/tkmal32/temp/imageManager", true);
    }

    public ImageRepository(String[] args)
    {
        parseCommandArgs(args);
    }

    private void parseCommandArgs(String[] args)
    {
        if (args != null && args.length > 0)
        {
            applicationArguments = new ApplicationArguments(args);

            applicationArguments.log();
        }
    }

    public void organizeImages(String sourceDir, String destinationDir, boolean recursive)
    {
        // for now, only process images with the name starting with a the date
        File srcDir = new File(sourceDir);
        if (srcDir != null && srcDir.isDirectory())
        {
            logger.debug("Found images:");
            organizeImages(srcDir, destinationDir, recursive);
        }
        else
        {
            logger.error("organizeImages: invalid sourceDir [" + sourceDir + "]");
        }
    }

    public void organizeImages(final File sourceFolder, String destinationDir, boolean recursive)
    {
        String name = null;
        int year;
        int month;
        int day;
        String destYearDirName = null;
        File destYearDir;
        String destDirName = null;
        File destDir;

        for (final File fileEntry : sourceFolder.listFiles()) {
            if (fileEntry.isDirectory() && recursive) {
                organizeImages(fileEntry, destinationDir, recursive);
            } else {
                name = fileEntry.getName();
                if (name.toLowerCase().contains(".jpg")
                    && name.contains("_")
                    && name.indexOf("_") == 8)
                {
                    name = name.substring(0, name.indexOf("_"));
                    year = Integer.parseInt(name.substring(0,4));
                    month = Integer.parseInt(name.substring(4,6));
                    day = Integer.parseInt(name.substring(6));

                    logger.debug(name + "[" + year + "][" + String.format("%02d", month) + "][" + String.format("%02d", day) + "]");

                    // check if YEAR destination directory exists
                    destYearDirName = String.format("%02d", year);
                    destYearDir = new File(destinationDir + "/" + destYearDirName);
                    if (!destYearDir.exists())
                    {
                        // create the directory
                        destYearDir.mkdir();
                    }

                    // check if destination directory exists
                    destDirName = new StringBuffer().append(
                            String.format("%02d", year)).append(
                            String.format("%02d", month)).append(
                            String.format("%02d", day)).toString();
                    destDir = new File(destinationDir + "/" + destYearDirName + "/" + destDirName);
                    if (!destDir.exists())
                    {
                        // create the directory
                        destDir.mkdir();
                    }

                    try {
                        // copy the file to the destination directory (if a file with that name does not already exist)
                        if (!fileExists(destDir, fileEntry.getName()))
                        {
                            try {
                                logger.debug("copying file [" + destDir.getPath() + "/" + fileEntry.getName() + "] to [" + destDir.getPath() + "]");
                                FileUtils.copyFile(fileEntry, new File(destDir.getPath() + "/" + fileEntry.getName()), true);
                            } catch (IOException e) {
                                System.out.println("Unable to copy [" + fileEntry + "].  Error [" + e.getMessage() + "]");
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            logger.info("File with name [" + fileEntry.getName() + "] already exists in destination directory [" + destDir.getName() + "].  Skipping...");
                        }
                    }
                    catch (ImageRepositoryException e)
                    {
                        logger.error("Unable to copy [" + fileEntry + "].  Error [" + e.getMessage() + "]");
                        //e.printStackTrace();
                    }
                }
            }
        }
    }

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
    private boolean fileExists(File directory, String fileName)
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
                logger.debug("-- comparing [" + fileName + "] to file: [" + file.getName() + "]");
                if (!file.isDirectory()) {
                    if (fileName.equalsIgnoreCase(file.getName()))
                    {
                        exists = true;
                        break;
                    }
                }
            }
        }
        return exists;
    }

}
