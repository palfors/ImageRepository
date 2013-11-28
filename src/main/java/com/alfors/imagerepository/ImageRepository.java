package com.alfors.imagerepository;

import com.alfors.application.ApplicationArguments;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
    @Autowired
    ImageInterrogator imageInterrogator;

    public static void main(String[] args)
    {
        logger.info("ImageRepository start time: " + new Date(System.currentTimeMillis()));
        ImageRepository imageRepository = new ImageRepository(args);

        try {
            imageRepository.organizeImages("/Users/tkmal32/data/2013/personal","/Users/tkmal32/temp/imageManager", true);
        } catch (ImageRepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
            throws ImageRepositoryException
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
            throws ImageRepositoryException
    {
        String destYearDirName = null;
        File destYearDir;
        String destDirName = null;
        File destDir;
        GregorianCalendar dateTaken = null;

        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        imageInterrogator = (ImageInterrogator) context.getBean("imageInterrogator");

        for (final File fileEntry : sourceFolder.listFiles()) {
            if (fileEntry.isDirectory() && recursive) {
                organizeImages(fileEntry, destinationDir, recursive);
            }
            else {
                if (isSupportedImageFile(fileEntry))
                {
                    try
                    {
                        dateTaken = imageInterrogator.getDateTaken(fileEntry);

                        logger.debug("organizeImage() " + fileEntry.getName() + " date taken [" + dateTaken + "]");

                        if (dateTaken != null)
                        {
                            // check if YEAR destination directory exists
                            destYearDirName = String.format("%02d", dateTaken.get(GregorianCalendar.YEAR));
                            destYearDir = new File(destinationDir + "/" + destYearDirName);
                            if (!destYearDir.exists())
                            {
                                // create the directory
                                destYearDir.mkdir();
                            }

                            // check if destination directory exists
                            destDirName = new StringBuffer().append(
                                    String.format("%02d", dateTaken.get(GregorianCalendar.YEAR))).append(
                                    String.format("%02d", dateTaken.get(GregorianCalendar.MONTH))).append(
                                    String.format("%02d", dateTaken.get(GregorianCalendar.DAY_OF_MONTH))).toString();
                            destDir = new File(destinationDir + "/" + destYearDirName + "/" + destDirName);
                            if (!destDir.exists())
                            {
                                // create the directory
                                destDir.mkdir();
                            }

                            // copy the file to the destination directory (if a file with that name does not already exist)
                            if (!fileExists(destDir, fileEntry.getName()))
                            {
                                try {
                                    logger.debug("organizeImage() copying file [" + destDir.getPath() + "/" + fileEntry.getName() + "] to [" + destDir.getPath() + "]");
                                    FileUtils.copyFile(fileEntry, new File(destDir.getPath() + "/" + fileEntry.getName()), true);
                                } catch (IOException e) {
                                    System.out.println("organizeImage() Unable to copy [" + fileEntry + "].  Error [" + e.getMessage() + "]");
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                logger.info("organizeImage() File with name [" + fileEntry.getName() + "] already exists in destination directory [" + destDir.getName() + "].  Skipping...");
                            }
                        }
                        else
                            logger.warn("organizeImage() Unable to determine date taken for image [" + fileEntry.getName() + "].  Skipping...");
                    }
                    catch (ImageRepositoryException e)
                    {
                        logger.error("organizeImage() Unable to copy image [" + fileEntry + "].  Error [" + e.getMessage() + "]");
                        // move on to the next image
                    }
                }
                else
                    logger.debug("organizeImage() File [" + fileEntry + "] is not a supported image type.  Ignoring...");
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

    private boolean isSupportedImageFile(File file)
            throws ImageRepositoryException
    {
        if (file == null)
            throw new ImageRepositoryException("isSupportedImageFile(): Missing image file");

        boolean isImage = false;

        // get the file extension
        Pattern pattern = Pattern.compile("\\.[a-zA-Z]{3}$");
        Matcher matcher = pattern.matcher(file.getName());
        if (matcher.find()) {
            String ext = matcher.group();

            // strip off the leading .
            isImage = ImageFileTypes.isSupported(ext.substring(1));
        }
        else
            logger.debug("-- unable determine extension in file [" + file.getName() +
                    "].  Assuming unsupported file type...");


        return isImage;
    }

}
