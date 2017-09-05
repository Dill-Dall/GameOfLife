/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package utilities;

import javafx.scene.input.MouseEvent;
import model.CanvasInfo;

/**
 * DragHandler handles dragging the view with mouse on the canvas. Controller
 * sends the MousEvent to the methods in the class for further usage.
 *
 * @author T.Dahll
 */
public class DragHandler {

    private final CanvasInfo canvasInfo;
    private boolean isDragging = false;
    private double previousX;
    private double previousY;
    private double dragSpeed = 1d;

    /**
     * Sets the new <code>dragSpeed</code>.
     *
     * @param dragSpeed which speed to move the drawing of the
     * <code>canvas</code>
     */
    public void setDragSpeed(double dragSpeed) {
        this.dragSpeed = dragSpeed;
    }

    /**
     * Constructor that take a <code>CanvasInfo</code> as an argument.
     *
     * @param canvasInfo represents the information about the canvas and
     * placement values
     */
    public DragHandler(CanvasInfo canvasInfo) {
        this.canvasInfo = canvasInfo;
    }

    /**
     * Starts the initial value sampling before the drag finishes.
     *
     * @param me <code>MousEvent</code> from the controller
     */
    public void dragStart(MouseEvent me) {
        if (!isDragging && me.isSecondaryButtonDown()) {
            previousX = me.getScreenX();
            previousY = me.getScreenY();
            isDragging = true;
        }
    }

    /**
     * Method which changes the values for the placement on the Board.
     * Continually uses the canvasInfo.dragPlacement to update new placement
     * values.
     *
     * @see model.CanvasInfo
     * @param me MousEvent from the controller
     * @return <code>boolean</code> if the drawing is currently being moved
     */
    public boolean drag(MouseEvent me) {
        if (!isDragging) {
            return false;
        }

        double deltaX = previousX - me.getScreenX();
        double deltaY = previousY - me.getScreenY();

        // Uses the new values on the placement variables in canvassInfo
        canvasInfo.dragPlacement(deltaX * dragSpeed, deltaY * dragSpeed);
        previousX = me.getScreenX();
        previousY = me.getScreenY();

        return true;
    }

    /**
     * Boolean which determines if the drag has ended. When the dragging has
     * ended the <code>boolean</code> changes to false.
     *
     * @param me MousEvent from the controller
     */
    public void dragEnd(MouseEvent me) {
        if (isDragging && !me.isSecondaryButtonDown()) {
            isDragging = false;
        }
    }
}
