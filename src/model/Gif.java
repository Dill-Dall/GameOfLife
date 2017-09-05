/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model;

import java.awt.Color;
import java.io.IOException;
import javafx.scene.control.Alert;
import lieng.GIFWriter;
import model.BoardPack.StaticBoard;

/**
 * Converts a <code>byte[][]</code>-array into a gif file. Uses a recursive '
 * <code>createGif</code>
 * method to run <code>createGif</code> and create each pattern iteration into 
 * a new gif frame.
 *
 * @see #createGif(byte[][], lieng.GIFWriter) 
 * @see #boardToGif(model.BoardPack.StaticBoard, int) 
 * @see GIFWriter by Henrik Lieng.
 * @author T.Dahll
 */
public class Gif {
    // data related to the GIF image file

    private String path = "";
    private int width = 640;
    private int height = 640;
    private int timePerMilliSecond = 200;
    int cellSize = 14;
    private GIFWriter writer;
    private Color cellColor = Color.black;
    private Color backGroundColor = Color.WHITE;

    public void createGif(byte[][] array, GIFWriter writer) {
        // Decide if size is dependant on height or width.
        if (array[0].length > array.length) {
            cellSize = height / array[0].length;
        } else {
            cellSize = width / array.length;
        }
        //centers the board int the mdle of the gif frame.
        int placementX = (getHeight() / 2) - ((cellSize * array.length) / 2);
        int placementY = (getWidth() / 2) - ((cellSize * array[0].length) / 2);

        //Fills the background with background color.
        writer.fillRect(0, getWidth() - 1, 0, getHeight() - 1, backGroundColor);
        try {
            for (int x = 1; x < array.length - 1; x++) {
                for (int y = 1; y < array[0].length - 1; y++) {
                    if (array[x][y] == 1) {
                        writer.fillRect(y * cellSize + placementY, y * cellSize + cellSize + placementY,
                                x * cellSize + placementX, x * cellSize + cellSize + placementX, cellColor);
                    }
                }
            }
            //Adds the frame to the .gif.
            writer.insertAndProceed();

        } catch (IOException e) {
            ErrorMassage("saveGif");
        }
    }

    /**
     * Displaying the <code>from</code> that is provided as an argument in in a
     * dialog box.
     *
     * @param from the <code>String</code> to display in the dialog box
     */
    public void ErrorMassage(String from) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);

        if (from.equals("saveGif")) {
            alert.setContentText("Problem writing board to a Gif file. Please try choosing file again.");
        }
        alert.showAndWait();
    }

    /**
     * Recursive method which calls the <code>createGif</code> method repeatedly
     * for <code>int counter</code> number of times. Each method call add a new
     * frame to the .gif file.
     *
     * @param board StaticBoard which is iterated through and made into frames
     * for the .gif.
     * @param counter number of iterations for the Board and the number of .gif
     * frames.
     * @throws IOException a failed input or output operation has occured.
     */
    public void boardToGif(StaticBoard board,
            int counter) throws IOException {

        if (counter == 0) {
            writer.close();
            return;
        }
        createGif(board.getBoardArray(), writer);
        //Centers the board araound alive elements.
        board = new StaticBoard(board.getBoardArray(), board.getBoundingBox());
        board.setNextGeneration();
        counter--;
        boardToGif(board, counter);
    }

    /**
     * Sets the <code>path</code>. The <code>path</code> is provided as an
     * argument and used with the <code>writer</code>.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
        try {
            writer = new GIFWriter(width, height, path, timePerMilliSecond);
        } catch (IOException e) {
            ErrorMassage("saveGif");
        }

    }

    /**
     * Sets the <code>timePerMilliSecond</code>.
     *
     * @param timePerMilliSecond the duration for each "frame"
     */
    public void setTimePerMilliSecond(int timePerMilliSecond) {
        this.timePerMilliSecond = timePerMilliSecond;
    }

    /**
     * Sets the <code>cellColor</code>.
     *
     * @param cellColor the new color to set
     */
    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
    }

    /**
     * Sets the <code>backGroundColor</code>.
     *
     * @param backGroundColor the new background color to set
     */
    public void setBackGroundClr(Color backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    /**
     * Gets the current <code>path</code>. The path is used by the
     * <code>writer</code>.
     *
     * @return the current path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the <code>width</code>.
     *
     * @return the current width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets a <code>width</code>. This assigns the <code>width</code> provided
     * as an argument to the variable <code>width</code>.
     *
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the <code>height</code>.
     *
     * @return the current height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets a <code>height</code>.
     *
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
