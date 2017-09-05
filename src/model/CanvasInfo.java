/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model;

import javafx.scene.canvas.Canvas;

/**
 * Class containing information and logic about the canvas, placement of
 * elements and logic related to positioning the board on the canvas.
 *
 * @author T.Dahll, M.S.Olsen
 */
public final class CanvasInfo {

    private double canvasWidth;
    private double canvasheight;
    private double cellSize;
    private double placementX;
    private double placementY;
    private final Canvas canvas;

    /**
     * Constructor using <code>canvas</code> and <code>cellSize</code> as
     * arguments. Assigns the same dimensions to the class <code>canvas</code>
     * as the <code>canvas</code> provided as the argument in the constructor.
     * The class <code>cellSize</code> is also assigned the same value as the
     * <code>cellSize</code> provided as an argument in the constructor.
     *
     * @param canvas the <code>canvas</code> provided as argument
     * @param cellSize the <code>cellSize</code> provided as argument
     */
    public CanvasInfo(Canvas canvas, double cellSize) {
        this.canvas = canvas;
        this.cellSize = cellSize;
        setCanvasWidth(canvas.getWidth());
        setCanvasheight(canvas.getHeight());
    }

    /**
     * Gets the width of the current <code>canvas</code>. The width is returned
     * in type <code>double</code>.
     *
     * @return the width of the current <code>canvas</code>
     */
    public double getCanvasWidth() {
        return canvas.getWidth();
    }

    /**
     * Sets the width of the current <code>canvas</code>. Using the provided
     * <code>canvasWidth</code> to set the width of the <code>canvas</code>.
     *
     * @param canvasWidth the new width
     */
    public void setCanvasWidth(double canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    /**
     * Gets the height of the current <code>canvas</code>. The height is
     * returned in type <code>double</code>.
     *
     * @return the height of the current <code>canvas</code>
     */
    public double getCanvasheight() {
        return canvas.getHeight();
    }

    /**
     * Set the height for the <code>canvas</code>.
     *
     * @param canvasheight the height to be assigned to the <code>canvas</code>
     */
    public void setCanvasheight(double canvasheight) {
        this.canvasheight = canvasheight;
    }

    /**
     * Gets the current <code>cellSize</code>.
     *
     * @return the current size of the cell
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     * Sets the size of the cells. The <code>cellSize</code> is assigned the new
     * value provided as an argument.
     *
     * @param cellSize the new cell size
     */
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Gets the <code>placementX</code>. Represents the length along the X-axis
     * to align the drawing of the board by.
     *
     * @return the <code>placementX</code>
     */
    public double getPlacementX() {
        return placementX;
    }

    /**
     * Gets the <code>placementY</code>. Represents the length along the Y-axis
     * to align the drawing of the board by.
     *
     * @return the <code>placementY</code>
     */
    public double getPlacementY() {
        return placementY;
    }

    /**
     * Determines the <code>placementX</code>. The variable
     * <code>placementX</code> is used in order to place the drawing of the
     * board on the <code>canvas</code> according to added columns.
     *
     * @param placeColVar the number of added columns
     */
    public void setPlacementX(int placeColVar) {
        placementX -= (placeColVar * cellSize);
    }

    /**
     * Determines the <code>placementY</code>. The variable
     * <code>placementY</code> is used in order to place the drawing of the
     * board on the <code>canvas</code> according to added rows.
     *
     * @param placeRowVar the number of added rows
     */
    public void setPlacementY(int placeRowVar) {
        placementY -= (placeRowVar * cellSize);
    }

    /**
     * Assigns values to <code>placementX</code> and <code>placementY</code>
     * based on the width/height of the <code>canvas</code>, the
     * <code>cellSize</code>, the <code>boardWidth</code> and
     * <code>boardHeight</code>. The values given as arguments to this method is
     * the dimensions of the board and is used in order to center the drawing on
     * the <code>canvas</code>.
     *
     * @param boardWidth the width of the current board
     * @param boardHeight the height of the current board
     */
    public void centerPlacement(int boardWidth, int boardHeight) {
        placementX = (canvasWidth / 2) - ((cellSize * boardWidth / 2));
        placementY = (canvasheight / 2) - ((cellSize * boardHeight / 2));
    }

    /**
     * Alters the variables <code>placementX</code> and <code>placementY</code>.
     * This is done when the board is "dragged" in order to change the location
     * on the <code>canvas</code>.
     *
     * @param x the amount of movement along the X-axis
     * @param y the amount of movement along the Y-axis
     */
    public void dragPlacement(double x, double y) {
        placementX -= x;
        placementY -= y;
    }
}
