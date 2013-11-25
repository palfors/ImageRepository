package com.alfors.imagerepository;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: tkmal32
 * Date: 11/24/13
 * Time: 9:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ImageInterogator {

    public GregorianCalendar getDateTaken() throws ImageRepositoryException;

}
