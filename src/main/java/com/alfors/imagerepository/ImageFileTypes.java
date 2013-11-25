package com.alfors.imagerepository;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tkmal32
 * Date: 11/24/13
 * Time: 9:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageFileTypes {

    public static final String JPEG_EXT = "jpg";

    private static final String[] image_types = { JPEG_EXT };

    public boolean isImageExtension(String extenstion)
    {
        boolean isValid = false;
        for (String ext : image_types)
        {
            if (ext.equalsIgnoreCase(extenstion))
            {
                isValid = true;
                break;
            }
        }

        return isValid;
    }
}
