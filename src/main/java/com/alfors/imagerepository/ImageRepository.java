package com.alfors.imagerepository;

import com.alfors.application.ApplicationArguments;
import com.alfors.imagerepository.interrogator.ImageInterrogator;
import com.alfors.imagerepository.organizer.ImageOrganizer;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

    private static final String ARG_SOURCE_PATH = "src";
    private static final String ARG_DESTINATION_PATH = "dest";
    private static final String ARG_RECURSIVE = "recursive";

    private static Logger logger = LogManager.getLogger(ImageRepository.class.getName());
    @Autowired
    ImageInterrogator imageInterrogator;
    @Autowired
    ImageOrganizer imageOrganizer;

    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();
        logger.debug("main() start time: " + new Date(start));
        ImageRepository imageRepository = new ImageRepository(args);

        int processCount = 0;

        try {
            String sourceDir = imageRepository.getSourceDir();
            String destinationDir = imageRepository.getDestinationDir();
            boolean searchRecursively = imageRepository.isRecursiveSearch();

            logger.info("main() start time: " + new Date(System.currentTimeMillis()));
            processCount = imageRepository.organize(sourceDir, destinationDir, searchRecursively);
//            imageRepository.organizeImages("/Users/tkmal32/data/2013/personal","/Users/tkmal32/temp/imageManager", true);

        } catch (ImageRepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        long end = System.currentTimeMillis();
        logger.debug("main() end time: " + new Date(end));
        logger.info("main() organization of [" + processCount + "] file(s) took: " + ((end-start)/1000) + " seconds");
    }

    public ImageRepository(String[] args)
    {
        parseCommandArgs(args);
    }

    public String getSourceDir()
    {
        return applicationArguments.getArgumentValue(ARG_SOURCE_PATH);
    }

    public String getDestinationDir()
    {
        return applicationArguments.getArgumentValue(ARG_DESTINATION_PATH);
    }

    public boolean isRecursiveSearch()
    {
        // default to false
        boolean recursive = false;
        String recursiveArg = applicationArguments.getArgumentValue(ARG_RECURSIVE);
        if (recursiveArg != null && recursiveArg.trim().length() > 0)
        {
            recursive = Boolean.parseBoolean(recursiveArg);
        }

        return recursive;
    }

    private void parseCommandArgs(String[] args)
    {
        if (args != null && args.length > 0)
        {
            applicationArguments = new ApplicationArguments(args);

            applicationArguments.log();
        }
    }

    public int organize(String sourceDir, String destinationDir, boolean recursive)
            throws ImageRepositoryException
    {
        if (sourceDir == null || sourceDir.trim().length() == 0)
            throw new ImageRepositoryException("organize() sourceDir is required!");
        if (destinationDir == null || destinationDir.trim().length() == 0)
            throw new ImageRepositoryException("organize() destinationDir is required!");

        int organizedCount = 0;

        File srcDir = new File(sourceDir);
        if (srcDir != null && srcDir.isDirectory())
        {
            File destDir = new File(destinationDir);
            if (destDir != null && destDir.isDirectory())
            {
                organizedCount = organize(srcDir, destDir, recursive);
            }
        }
        else
        {
            logger.error("organize() Invalid sourceDir [" + sourceDir + "]");
        }

        return organizedCount;
    }

    /**
     *
     * @param sourceFolder
     * @param destinationDir
     * @param recursive
     * @return  Count of files processed
     *
     * @throws ImageRepositoryException
     */
    public int organize(final File sourceFolder, final File destinationDir, boolean recursive)
            throws ImageRepositoryException
    {
        if (sourceFolder == null)
            throw new ImageRepositoryException("organizeImages() sourceFolder is required!");
        if (destinationDir == null)
            throw new ImageRepositoryException("organizeImages() destinationDir is required!");

        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        imageInterrogator = (ImageInterrogator) context.getBean("imageInterrogator");
        imageOrganizer = (ImageOrganizer) context.getBean("imageOrganizer");

        GregorianCalendar dateTaken = null;
        int progressCounter = 0;
        int organizedCount = 0;

        File[] files = sourceFolder.listFiles();
        if (files != null)
            logger.info("organize() Directory [" + sourceFolder + "] contains [" + files.length + "] files");

        for (final File file : files) {
            progressCounter++;
            if (file.isDirectory() && recursive) {
                organize(file, destinationDir, recursive);
            }
            else {
                if (isSupportedImageFile(file))
                {
                    try
                    {
                        dateTaken = imageInterrogator.getDateTaken(file);

                        logger.debug("organizeImages() " + file.getName() + " date taken [" + dateTaken + "]");

                        if (dateTaken != null)
                        {
                            imageOrganizer.organize(file, destinationDir, dateTaken);
                            organizedCount++;
                        }
                        else
                            logger.warn("organize() Unable to determine date taken for image [" + file.getName() + "].  Skipping...");
                    }
                    catch (ImageRepositoryException e)
                    {
                        logger.error("organize() Unable to copy image [" + file + "].  Error [" + e.getMessage() + "]");
                        // move on to the next image
                    }
                }
                else
                    logger.debug("organize() File [" + file + "] is not a supported image type.  Ignoring...");
            }

            if ((progressCounter % 100) == 0)
                logger.info("organize() processed [" + progressCounter + "] of [" + files.length + "] files");
        }

        return organizedCount;
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
