/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model;

/**
 * A class to capture exceptions related to the format of patterns. This
 * exception is thrown when a RLE-file is missing information or contains
 * conflicting information such as a pattern containing a larger number of
 * rows/columns than the limits defined by the bounding box of the pattern.
 */
public class PatternFormatException extends Exception {

    /**
     * Constructor taking a String as parameter. Attaches a <code>message</code>
     * with the exception, making it possible to display a possible reason for
     * the exception.
     *
     * @param message the message to attach to the exception
     */
    public PatternFormatException(String message) {
        super(message);
    }
}
