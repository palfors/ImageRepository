package com.alfors.imagerepository;

import com.alfors.application.ApplicationArguments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Pete Alfors
 * Date: 10/22/12
 * Time: 10:36 PM
 */
public class ImageRepository
{
    ApplicationArguments applicationArguments = null;

    private static final String ARG_SOURCE_PATH = "path";
    private static final String ARG_RECURSIVE = "recursive";

    private String path = "/";
    private boolean recursive = false;
    private String[] imageTypes = { "jpg", "gif", "png" };

    private static Logger logger = LogManager.getLogger("ImageRepository");

    public static void main(String[] args)
    {
        ImageRepository imageRepository = new ImageRepository(args);

        imageRepository.findImages();
    }

    public ImageRepository(String[] args)
    {
        parseCommandArgs(args);
    }

    private void parseCommandArgs(String[] args)
    {
        if (args != null)
        {
            applicationArguments = new ApplicationArguments(args);

            applicationArguments.log();
        }
    }

    public void findImages()
    {
        logger.debug("ImageRepository:findImages!");

        // get the source path
        path = applicationArguments.getArgumentValue(ARG_SOURCE_PATH);

        path = "test_path";
        logger.info("ImageRepository: searching path: {}", path);
    }

    
}
