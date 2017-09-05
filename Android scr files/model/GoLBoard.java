package Model;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by MartinStromOlsen on 31/03/2017.
 */

/**
 * Abstract superclass for <code>StaticGoLBoard</code> and <code>DynamicGoLBoard</code>.
 */
public abstract class GoLBoard implements Serializable {

    /**
     * Method used to parse a given <code>String</code> when a constructor using a
     * <code>String</code> as a parameter is used.
     * @param QR the <code>String</code> to parse into an array for StaticGBoard or into an
     *           ArrayList for DynamicGBoard
     */
    public abstract void parseQRString(String QR);

    /**
     * Creates an empty <code>byte[][]</code> (StaticGBoard) or an empty
     * <code>ArrayList<ArrayList<Byte>></code> (DynamicGBoard). The board containing the number of
     * neighbors is equal in size to the main <code>byte[][]</code> or
     * <code>ArrayList<ArrayList<Byte></code>.
     */
    protected abstract void createNeighBoard();

    /**
     *Updates the board containing neighbours based on the main board.
     */
    protected abstract void updateNeighbourBoard();

    /**
     * Drawing rectangles representing the cells on the canvas from <code>GameViewer</code>. If
     * <code>boolean</code> "fitZoom" is true, the <code>draw</code>-method will fit the size of the
     * cells to draw a quadratic board.
     * @param canvas the canvas from <code>GameViewer</code>
     * @param paint paint configured in <code>GameViewer</code>
     */
    public abstract void draw(Canvas canvas, Paint paint);

    /**
     * Iterates through the <code>byte[][]</code> (StaticGBoard) or
     * <code>ArrayList<ArrayList<Byte></code> (DynamicGBoard) and applies the static method
     * <code>rleRules</code> of class Rule to determine if the cell should change state based on
     * their current value and their number of neighbors.
     */
    public abstract void setNextGeneration();

    /**
     * Assigns a new cell size based on the parameter <code>newSize</code>.
     * @param newSize the new size
     */
    public abstract void setCellSize(int newSize);

    /**
     * Changes the value of the variable <code>fitZoom</code> from true (default) to false. This
     * occurs if the cell size is set manually.
     * @param newValue the new boolean determining if fitZoom should be active (true) or if manual
     *                 zoom is enabled (false)
     */
    public abstract void setFitZoom(boolean newValue);

    /**
     * Places a <code>byte[][]</code> in the main board of StaticGBoard or DynamicGBoard.
     * @param byteArray the <code>byte[][]</code> to place in the main board
     */
    protected abstract void placeArrayInBoard(byte[][] byteArray);

}
