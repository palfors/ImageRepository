package com.alfors.imagerepository.organizer;

import com.alfors.imagerepository.ImageRepositoryException;

import java.io.File;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: tkmal32
 * Date: 12/6/13
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ImageOrganizer {

    /**
     * Organize the file (ie: move/copy) based on the provided date
     *
     * @param imageFile
     * @param destinationDir
     * @param imageDate
     *
     * @throws ImageRepositoryException
     */
    public void organize(final File imageFile, File destinationDir, GregorianCalendar imageDate)
            throws ImageRepositoryException;

}
