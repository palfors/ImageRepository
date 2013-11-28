package com.alfors.imagerepository;

/**
 * Parent application exception
 */
public class ImageRepositoryException extends Exception {

    public ImageRepositoryException(String message)
    {
        super(message);
    }

    public ImageRepositoryException(String message, Throwable t)
    {
        super(message, t);
    }
}
