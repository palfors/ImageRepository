package com.alfors.imagerepository;

import com.alfors.application.ApplicationArguments;
//import com.drew.imaging.ImageMetadataReader;
//import com.drew.imaging.ImageProcessingException;
//import com.drew.metadata.Directory;
//import com.drew.metadata.Metadata;
//import com.drew.metadata.Tag;
//import com.drew.metadata.exif.ExifSubIFDDirectory;
//import com.drew.metadata.jpeg.JpegDirectory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Peter Alfors
 * Date: 10/22/12
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

        //imageRepository.findImages();
        //imageRepository.testPassByValue();
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

    public void findImages()
    {
        logger.debug("ImageRepository:findImages!");

        // get the source path
        path = applicationArguments.getArgumentValue(ARG_SOURCE_PATH);

        path = "test_path";
        logger.info("ImageRepository: searching path: {}", path);
    }

    public void organizeImages(String sourceDir, String destinationDir, boolean recursive)
    {
        // for now, only process images with the name starting with a the date
        File srcDir = new File(sourceDir);
        if (srcDir != null && srcDir.isDirectory())
        {
            System.out.println("Found images:");
            organizeImages(srcDir, destinationDir, recursive);
        }
        else
        {
            System.out.println("organizeImages: invalid sourceDir [" + sourceDir + "]");
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

                    System.out.println(name + "[" + year + "][" + String.format("%02d", month) + "][" + String.format("%02d", day) + "]");

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
                                //System.out.println("copying file [" + destDir.getPath() + "/" + fileEntry.getName() + "] to [" + destDir.getPath() + "]");
                                FileUtils.copyFile(fileEntry, new File(destDir.getPath() + "/" + fileEntry.getName()), true);
                            } catch (IOException e) {
                                System.out.println("Unable to copy [" + fileEntry + "].  Error [" + e.getMessage() + "]");
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                        else
                        {
                            System.out.println("File with name [" + fileEntry.getName() + "] already exists in destination directory [" + destDir.getName() + "].  Skipping...");
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("Unable to copy [" + fileEntry + "].  Error [" + e.getMessage() + "]");
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
     * @return
     * @throws Exception
     */
    private boolean fileExists(File directory, String fileName)
        throws Exception
    {
        System.out.println("fileExists() checking directory [" + directory + "] for existing file [" + fileName + "]");
        boolean exists = false;
        if (directory == null || !directory.isDirectory())
        {
            throw new Exception("-- Invalid directory [" + directory + "]");
        }
        else
        {
            for (final File file : directory.listFiles()) {
                System.out.println("-- comparing [" + fileName + "] to file: [" + file.getName() + "]");
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

//    public void organizeImages()
//    {
//        // cycle through all images found in the source directory and move then to the destination directory
//        // under a folder structure defined by the create date (date taken)
//        File jpegFile = new File("/Users/tkmal32/data/2013/personal/PeteAlfors.jpg");
//        try {
//            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
//            System.out.println("gradient.jpg:");
//            for (Directory directory : metadata.getDirectories()) {
//                for (Tag tag : directory.getTags()) {
//                    System.out.println(tag);
//                }
//            }
//            // obtain the Exif directory
//            ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
//
//            if (directory != null)
//            {
//                // query the tag's value
//                Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
//                System.out.println("Original Date: " + date);
//            }
//            else
//            {
//                System.out.println("No ExifSubIFDDirectory");
//            }
//
//            Iterable<Directory> iter = metadata.getDirectories();
//            for (Directory dir : iter)
//            {
//                System.out.println("Directory: " + dir);
//            }
//        } catch (ImageProcessingException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//    }

    private void testPassByValue()
    {
        // primitive
        
        // object
        System.out.println("testPassByValue(): TEST 1");
        MyInnerClass object1 = new MyInnerClass("bob");
        System.out.println("testPassByValue(): initial object [" + object1 + "]");
        MyInnerClass object2 = testPassByValueNewClass(object1);
        
        System.out.println("testPassByValue(): passed object [" + object1 + "] returned object [" + object2 + "]");

        System.out.println("testPassByValue(): TEST 2");
        object1 = new MyInnerClass("bob");
        System.out.println("testPassByValue(): initial object [" + object1 + "]");
        object2 = testPassByValueUpdateClass(object1);

        System.out.println("testPassByValue(): object1 [" + object1 + "] object2 [" + object2 + "]");
    }
    
    private MyInnerClass testPassByValueNewClass(MyInnerClass object)
    {
        System.out.println("testPassByValueNewClass() initial object [" + object + "]");
        object = new MyInnerClass("john");

        System.out.println("testPassByValueNewClass() created and returning object [" + object + "]");
        return object;
    }

    private MyInnerClass testPassByValueUpdateClass(MyInnerClass object)
    {
        System.out.println("testPassByValueUpdateClass() initial object [" + object + "]");
        object.setName("tom");

        System.out.println("testPassByValueUpdateClass() updated and returning object [" + object + "]");
        return object;
    }
    
    protected class MyInnerClass
    {
        String _name = null;
        
        public MyInnerClass(String name)
        {
            setName(name);
        }

        public String getName()
        {
            return _name;
        }

        public void setName(String name)
        {
            _name = name;
        }
        
        public String toString()
        {
            return getName();
        }
    }
    
}
