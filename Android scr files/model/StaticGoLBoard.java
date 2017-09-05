package Model;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by MartinStromOlsen on 21/03/2017.
 */

/**
 * Statical implementation of <code>GoLBoard</code>. Does not expand when cells are in the outer
 * bounds.
 */
public class StaticGoLBoard extends GoLBoard implements Serializable {
    private byte[][] boardArray;
    private byte[][] neighBoard;
    private float cellSizeX;
    private float cellSizeY;

    private final int zeroRow = 2;
    private final int zeroColumn = 2;

    private int numColumns;
    private int numRows;

    private byte[][] qrArray;

    private boolean fitZoom = true;

    private int cellSize;

    /**
     * Method drawing the cells (rectangles) on the <code>canvas</code>. The variable
     * <code>cellSizeX</code> is determined based on the width of the <code>canvas</code> and the
     * width of the <code>boardArray</code> (main board) to ensure quadratic rectangles if
     * <code>fitZoom</code> is true. If <code>fitZoom</code> is false, the variable
     * <code>cellSize</code>, determined by a SeekBar in <code>ShowGameActivity.java</code> and the
     * center of the board is placed near the center of the <code>canvas</code>.
     * @param canvas the canvas from <code>GameViewer</code>
     * @param paint paint configured in <code>GameViewer</code>
     */
    @Override
    public void draw(Canvas canvas, Paint paint)
    {

        cellSizeX = canvas.getWidth()/(boardArray.length - zeroColumn);

        //To attain non-quadratic cells, use cellSizeY in parameter 2 and 4 in drawRect
        //cellSizeY = canvas.getHeight()/(boardArray[0].length - zeroRow);
        if(fitZoom)
        {
            for(int row = 1; row < boardArray.length - 1 ; row++)
            {
                for(int column = 1; column < boardArray[0].length - 1; column++)
                {
                    if(boardArray[row][column] == 1)
                    {
                        canvas.drawRect((column-1)*cellSizeX, (row-1)*cellSizeX, (column-1)*cellSizeX + cellSizeX, (row-1)*cellSizeX + cellSizeX, paint );
                    }
                }
            }
        }
        else
        {
            int placementColumn = canvas.getWidth()/2 - (boardArray[0].length/2)*cellSize;
            int placementRow = canvas.getHeight()/2 - (boardArray.length/2)*cellSize;

            for(int row = 1; row < boardArray.length - 1; row++)
            {
                for(int column = 1; column < boardArray[0].length - 1; column++)
                {
                    if(boardArray[row][column] == 1)
                    {
                        canvas.drawRect((column)*cellSize+placementColumn, (row)*cellSize+placementRow, (1+column)*cellSize+placementColumn, (1+row)*cellSize+placementRow, paint );
                    }
                }
            }
        }


    }

    /**
     * Constructor that creates an empty board of the size given by the parameters. The
     * <code>boardArray</code> is created with surrounding 0s in order to have a constant size of
     * the board. The constructor also initialises the standard rule set through
     * <code>Rule.parseRuleString</code>, creates an empty <code>neighBoard</code> to represent the
     * number of neighbors of each cell as well as updating this <code>neighBoard</code> according
     * to the main <code>boardArray</code>.
     * @param row number of rows, exclusive of the surrounding 0-rows
     * @param columns number of columns, exclusive of the surrounding 0-columns
     * @see #createNeighBoard()
     * @see #updateNeighbourBoard()
     * @see #boardArray
     * @see #neighBoard
     */
    public StaticGoLBoard(int row, int columns)
    {
        this.numRows = row;
        this.numColumns = columns;
        boardArray = new byte[numRows + zeroRow][numColumns + zeroColumn];

        for (int x = 1; x < boardArray.length - 1; x++)
        {
            for (int y = 1; y < boardArray[0].length - 1; y++)
            {
                boardArray[x][y] = 0;
            }
        }

        //Sets the rule set
        Rule.parseRuleString("B3/S23");
        createNeighBoard();
        updateNeighbourBoard();
    }

    /**
     * Constructor that takes a QR-string as a parameter and uses this to create a matrix that
     * represents the QR-code and then places a <code>byte[][]</code> representation of this QR-code
     * as the <code>boardArray</code>. Standard rule set is set through
     * <code>Rule.parseRuleString</code>, <code>neighBoard</code> is created and updated
     * on instantiation of the <code>StaticGBoard</code>-object.
     * @param QR the String of text to be converted to a QR-code
     * @see #createNeighBoard()
     * @see #updateNeighbourBoard()
     */
    public StaticGoLBoard(String QR)
    {
        parseQRString(QR);
        Rule.parseRuleString("B3/S23");
        createNeighBoard();
        updateNeighbourBoard();
    }

    /**
     * Constructor taking a <code>byte[][]</code> as the only parameter and uses this as the
     * <code>boardArray</code>. Standard rule set is set through <code>Rule.parseRuleString</code>,
     * <code>neighBoard</code> is created and updated on instantiation of the
     * <code>StaticGBoard</code>-object.
     * @param byteArray the <code>byte[][]</code> to place in the <code>boardArray</code>
     * @see #placeArrayInBoard(byte[][])
     * @see #createNeighBoard()
     * @see #updateNeighbourBoard()
     */
    public StaticGoLBoard(byte[][] byteArray) {
        placeArrayInBoard(byteArray);
        Rule.parseRuleString("B3/S23");
        createNeighBoard();
        updateNeighbourBoard();
    }

    /**
     * Creates a new <code>boardArray</code> with 0-rows and 0-columns and places the content of the
     * <code>byte[][]</code> given as a parameter in the <code>boardArray</code>.
     * @param byteArray the <code>byte[][]</code> to place in the main board
     * @see #boardArray
     */
    @Override
    protected void placeArrayInBoard(byte[][] byteArray) {
        boardArray = new byte[byteArray.length+zeroRow][byteArray[0].length+zeroColumn];

        for (int i = 1; i < boardArray.length - 1; i++)
        {
            for (int j = 1; j < boardArray[0].length - 1; j++)
            {
                boardArray[i][j] = byteArray[i-1][j-1];
            }
        }
    }

    /**
     * Converts the given parameter to a <code>QRCode</code> and then retrieves the
     * <code>byte[][]</code> representing the QR-code. This <code>byte[][]</code> is then placed in
     * the <code>boardArray</code> of the <code>StaticGBoard</code>. If a
     * <code>WriterException</code> is thrown an empty <code>byte[][]</code> passed as a parameter
     * in <code>placeArrayInBoard</code>.
     * @param QR the <code>String</code> to parse into an array for StaticGBoard or into an
     *           ArrayList for DynamicGBoard
     * @see #boardArray
     * @see #placeArrayInBoard(byte[][])
     */
    @Override
    public void parseQRString(String QR)
    {
        QRCode qrCode = new QRCode();
        try
        {
            qrCode = Encoder.encode(QR, ErrorCorrectionLevel.L);
            qrArray = qrCode.getMatrix().getArray();
        }
        catch (WriterException we)
        {
            qrArray = new byte[10][10];
        }

        //qrArray = qrCode.getMatrix().getArray();
        placeArrayInBoard(qrArray);
    }

    /**
     * Creates an empty <code>byte[][]</code> representing the number of neighbors to each cell
     * based on the size of the <code>boardArray</code>.
     * @see #boardArray
     */
    @Override
    protected void createNeighBoard() {

        neighBoard = new byte[boardArray.length][boardArray[0].length];
    }

    /**
     * The method first calls <code>clearNeighBoard</code> in order to reset the neighbor board and
     * then iterates through the <code>byte[][] </code> representing the board from index 1 to index
     * ".length - 1". If the main board contains a live cell in the specified coordinates, a double
     * for-loop adds increases the neighbor-count of that cell with 1 by increasing its
     * corresponding neighbor cells with 1. Lastly, 1 is subtracted from the neighbor-count of the
     * current cell in order to adjust for the double for-loops' addition of one neighbor cell too
     * much (because it adds a neighbor cell in the space for the current cell in the double
     * for-loop).
     * @see #clearNeighBoard()
     * @see #boardArray
     * @see #neighBoard
     */
    @Override
    protected void updateNeighbourBoard()
    {
        clearNeighBoard();
        for (int x = 1; x < boardArray.length - 1; x++)
        {
            for (int y = 1; y < boardArray[0].length - 1; y++)
            {
                if (boardArray[x][y] == 1)
                {
                    for (int m = -1; m < 2; m++)
                    {
                        for (int n = -1; n < 2; n++)
                        {
                            neighBoard[x + m][y + n]++;
                        }
                    }
                    neighBoard[x][y]--;
                }
            }
        }

    }

    /**
     * Clears the <code>neighBoard</code> by assigning each coordinate-pair in the
     * <code>neighBoard</code> the value 0.
     * @see #neighBoard
     */
    protected void clearNeighBoard()
    {
        for (int i = 0; i < neighBoard.length; i++)
        {
            for (int j = 0; j < neighBoard[0].length; j++)
            {
                neighBoard[i][j] = 0;
            }
        }
    }

    /**
     * Iterates through the <code>boardArray</code> with a double for-loop. The state of each cell
     * in <code>boardArray</code> is then determined by method <code>Rule.rleRules</code> that take
     * the current cell state (<code>boardArray[i][j]</code>) and the number of neighbors of the
     * current cell (<code>neighBoard[i][j]</code>) as parameters. The <code>neighBoard</code> is
     * then updated to reflect the changes in <code>boardArray</code>.
     * @see #updateNeighbourBoard() updates the <code>neighBoard</code> after changing the state of
     * cells in <code>boardArray</code>
     * @see #boardArray
     * @see #neighBoard
     */
    @Override
    public void setNextGeneration()
    {
        for (int i = 1; i < boardArray.length-1; i++)
        {
            for (int j = 1; j < boardArray[0].length-1; j++)
            {
                boardArray[i][j] = Rule.rleRules(boardArray[i][j], neighBoard[i][j]);
            }
        }
        updateNeighbourBoard();
    }

    /**
     * Assigns a new value to variable <code>cellSize</code>.
     * @param newSize the new size
     */
    @Override
    public void setCellSize(int newSize) {
        cellSize = newSize;
    }

    /**
     * Determines if <code>fitZoom</code> should be active.
     * @param newValue the new boolean determining if fitZoom should be active (true) or if manual
     *                 zoom is enabled (false)
     */
    @Override
    public void setFitZoom(boolean newValue) {
        fitZoom = newValue;
    }

}
