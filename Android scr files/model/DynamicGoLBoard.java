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
 * The <code>DynamicGoLBoard</code> represents the expandable implementation of the
 * <code>GoLBoard</code>.
 */
public class DynamicGoLBoard extends GoLBoard implements Serializable {

    private byte[][] qrArray;
    private ArrayList<ArrayList<Byte>> boardList;
    private ArrayList<ArrayList<Byte>> neighBoardList;

    private boolean fitZoom = true;
    private int cellSize;

    /**
     * Constructor taking a <code>String</code> as parameter. The parameter is parsed into an
     * <code>byte[][]</code> and placed in the <code>boardList</code>. The standard rule set is set
     * through <code>Rule.parseRuleString</code>, <code>neighBoardList</code> is created to count
     * the number of neighbors to each cell and the <code>neighBoardList</code> is updated.
     * @param QR the parameter to create the QR-code
     * @see #parseQRString(String)
     * @see #createNeighBoard()
     * @see #updateNeighbourBoard()
     */
    public DynamicGoLBoard(String QR) {
        parseQRString(QR);
        Rule.parseRuleString("B3/S23");
        createNeighBoard();
        updateNeighbourBoard();
    }

    /**
     * Constructor taking a <code>byte[][]</code> as parameter. The parameter is placed onto the
     * <code>boardList</code> and the standard rule set is set through
     * <code>Rule.parseRuleString</code>, <code>neighBoardList</code> is created to count the number
     * of neighbors to each cell and the <code>neighBoardList</code> is updated.
     * @param byteArray the parameter to place onto the <code>boardList</code>
     * @see #placeArrayInBoard(byte[][])
     * @see #createNeighBoard()
     * @see #updateNeighbourBoard()
     */
    public DynamicGoLBoard(byte[][] byteArray) {
        placeArrayInBoard(byteArray);
        Rule.parseRuleString("B3/S23");
        createNeighBoard();
        updateNeighbourBoard();
    }

    /**
     * Converts the given parameter into a <code>QRCode</code> and then extracts the
     * <code>byte[][]</code> representation of the QR-code and places it in the
     * <code>boardList</code>. If a <code>WriterException</code> is thrown, an empty
     * <code>byte[][]</code> passed as a parameter in <code>placeArrayInBoard</code>.
     * @param QR the <code>String</code> to parse into an array for StaticGBoard or into an
     *           ArrayList for DynamicGBoard
     * @see #boardList
     * @see #placeArrayInBoard(byte[][])
     */
    @Override
    public void parseQRString(String QR) {
        QRCode qrCode = new QRCode();
        try
        {
            qrCode = Encoder.encode(QR, ErrorCorrectionLevel.L);
        }
        catch (WriterException we)
        {
            qrCode = null;
            qrArray = new byte[10][10];
        }

        if (qrCode != null) {
            qrArray = qrCode.getMatrix().getArray();
        }

        placeArrayInBoard(qrArray);

    }

    /**
     * Creates a new <code>boardList</code> and places the <code>byte[][]</code> given as a
     * parameter into the <code>boardList</code> by iterating through a double for-loop.
     * @param byteArray the <code>byte[][]</code> to place in the main board
     * @see #boardList
     */
    @Override
    protected void placeArrayInBoard(byte[][] byteArray)
    {
        boardList = new ArrayList<>();

        for (int i = 0; i < byteArray.length; i++)
        {
            boardList.add(new ArrayList<Byte>());

            for (int j = 0; j < byteArray[0].length; j++)
            {
                boardList.get(i).add(j, byteArray[i][j]);
            }
        }
    }

    /**
     * Assigns a new <code>ArrayList</code> to the reference variable <code>neighBoardList</code>
     * in order to account for possible changes in size of the
     * <code>boardList</code>. Ensures that the <code>neighBoardList</code> has the same dimensions
     * as <code>boardList</code>.
     * @see #boardList
     * @see #neighBoardList
     */
    @Override
    protected void createNeighBoard() {
        neighBoardList = new ArrayList<>();

        for (int row = 0; row < boardList.size(); row++) {
            neighBoardList.add(new ArrayList<Byte>());
            for (int column = 0; column < boardList.get(0).size(); column++) {
                neighBoardList.get(row).add(column, (byte)0);
            }
        }
    }

    /**
     * Resets the counters <code>numNeighbors</code> (the current number of neighbors in the cells
     * of the neighbor-board that surrounds the current cell, this is increased if the examined cell
     * == 1) and <code>cellNeighbors</code> (the number of neighbors of the examined cell, which is
     * decreased by 1 to account for the double counting in the for-loop). The method
     * <code>addRowsandColumns</code> is called to check if the
     * <code>ArrayList<ArrayList<Byte></code> representing the main board needs to expand. The
     * method <code>createNeighBoard</code> is then called to replace the previous neighbor-board
     * with a new that fits the (possibly) new dimensions of the main board. To account for
     * situations when the for-loops are in the outer edges, the variables <code>minRow</code>,
     * <code>minColumn</code>, <code>maxRow</code> and <code>maxColumn</code> are checked
     * and altered if necessary, meaning the outer, double for-loop is on some of the edges of the
     * board.
     * @see #addRowsandColumns()
     * @see #createNeighBoard()
     * @see #boardList
     * @see #neighBoardList
     */
    @Override
    protected void updateNeighbourBoard() {

        int numNeighbors = 0;
        int cellNeighbors = 0;
        sizeTest();
        addRowsandColumns();
        createNeighBoard();


        for (int row = 0; row < boardList.size(); row++)
        {
            for (int column = 0; column < boardList.get(0).size(); column++)
            {
                if (boardList.get(row).get(column) == 1)
                {
                    //checks if one of the for-loops have reached an outer edge
                    int minRow = -1;
                    int minColumn = -1;
                    int maxRow = 2;
                    int maxColumn = 2;

                    if(row == 0)
                    {
                        minRow = 0;
                    }

                    if(column == 0)
                    {
                        minColumn = 0;
                    }

                    if(row == boardList.size()-1)
                    {
                        maxRow = 1;
                    }

                    if(column == boardList.get(0).size()-1)
                    {
                        maxColumn = 1;
                    }

                    for (int m = minRow; m < maxRow; m++)
                    {
                        for (int n = minColumn; n < maxColumn; n++)
                        {
                            numNeighbors = neighBoardList.get(row + m).get(column + n);
                            neighBoardList.get(row + m).set(column + n, (byte) ++numNeighbors);
                        }
                    }

                    cellNeighbors = neighBoardList.get(row).get(column);
                    neighBoardList.get(row).set(column, (byte) (cellNeighbors - 1));
                }
            }
        }

    }

    /**
     * Method that expands the <code>boardList</code> if necessary. The method checks the top and
     * bottom row and adds an empty row at the top or at the bottom, respectively through method
     * <code>addRows</code>. The method then proceed to check the left and right column by iterating
     * through the rows of the <code>boardList</code>, checking if the leftmost and rightmost
     * columns contains the value 1 and if so, add a column to the left and/or right respectively.
     * @see #addRows(int, boolean)
     * @see #addColumns(int, boolean)
     */
    private void addRowsandColumns()
    {
        boolean top = true;

        //Check if top row contains 1
        if (boardList.get(0).contains((byte) 1)) {
            addRows(1, top);
        }

        //Check if bottom row contains 1
        if (boardList.get(boardList.size() - 1).contains((byte) 1)) {
            top = false;
            addRows(1, top);
        }

        //Check if outer columns got 1, add extra if so.
        boolean checkColL = true;
        boolean checkColR = true;
        boolean left = true;

        for (int row = 0; row < boardList.size(); row++) {
            if (boardList.get(row).get(0) == 1 && checkColL) {
                addColumns(1, left);
                checkColL = false;
            }

            if (boardList.get(row).get(boardList.get(0).size() - 1) == 1 && checkColR) {
                left = false;
                addColumns(1, left);
                checkColR = false;
            }
        }
    }

    /**
     * Creates <code>num</code> number of rows, create them of the same length as the
     * <code>boardList.get(0)</code> and places the rows either at index 0 (if <code>top</code> is
     * true) or at the end of the <code>boardList</code> if <code>top</code> is false.
     * @param num the number of rows to add
     * @param top the position (top or bottom)
     * @see #boardList
     */
    private void addRows(int num, boolean top)
    {
        for(int i = 0; i < num; i++)
        {
            ArrayList newRow = new ArrayList<Byte>();
            for(int j = 0; j < boardList.get(0).size(); j++)
            {
                newRow.add((byte)0);
            }

            if(top)
            {
                boardList.add(0, newRow);
            } else
            {
                boardList.add(newRow);
            }
        }
    }

    /**
     * Creates a <code>num</code> number of columns.
     * @param num the number of columns
     * @param left the side of the <code>boardList</code> to add the columns
     */
    private void addColumns(int num, boolean left)
    {
        for(int i = 0; i < num; i++)
        {
            for(int rowNumber = 0; rowNumber < boardList.size(); rowNumber++)
            {
                if(left)
                {
                    boardList.get(rowNumber).add(0, (byte)0);
                }
                else
                {
                    boardList.get(rowNumber).add((byte)0);
                }
            }
        }
    }

    /**
     * Using the <code>canvas</code> from method <code>onDraw(Canvas canvas)</code> in class
     * GameViewer. <code>boardList.size()</code> and <code>boardList.get(0).size()</code> are used
     * to determine which variable to use in order to show the entire board at the same time.
     * The method also specifies <code>cellSize</code> in order to attain quadratic cells, this
     * could be changed to utilize the entire canvas.
     * @param canvas the canvas to draw on
     * @param paint the paint used, specified in <code>init()</code>-method of class GameViewer
     */
    @Override
    public void draw(Canvas canvas, Paint paint) {

        if(fitZoom) {

            if (boardList.size() > boardList.get(0).size())
            {
                cellSize = (canvas.getWidth() / (boardList.size()));
            }
            else
            {
                cellSize = (canvas.getWidth() / (boardList.get(0).size()));
            }

            for(int row = 0; row < boardList.size()   ; row++)
            {
                for(int column = 0; column < boardList.get(0).size() ; column++)
                {
                    if(boardList.get(row).get(column) == 1)
                    {
                        canvas.drawRect((column)*cellSize, (row)*cellSize, (column)*cellSize + cellSize, (row)*cellSize + cellSize, paint);
                    }
                }
            }

        } else
        {
            int placementColumn = canvas.getWidth()/2 - (boardList.get(0).size()/2)*(cellSize);
            int placementRow = canvas.getHeight()/2 - (boardList.size()/2)*(cellSize);

            for(int row = 0; row < boardList.size(); row++)
            {
                for(int column = 0; column < boardList.get(0).size() ; column++)
                {
                    if(boardList.get(row).get(column) == 1)
                    {
                        canvas.drawRect((column)*(cellSize)+placementColumn, (row)*(cellSize)+placementRow, (1+column)*(cellSize)+placementColumn, (1+row)*(cellSize)+placementRow, paint );
                    }
                }
            }
        }
    }

    /**
     * Sets the next generation of Game of Life using an <code>ArrayList<ArrayList<Byte>></code> to
     * represent the board. After determining the state of each cell,
     * <code>updateNeighbourBoard</code> is being called in order to update the
     * <code>neighBoardList</code> for the following iteration.
     * @see #updateNeighbourBoard()
     */
    @Override
    public void setNextGeneration()
    {
        for(int row = 0; row < boardList.size(); row++)
        {
            for(int column = 0; column < boardList.get(0).size(); column++)
            {
                int cellState = boardList.get(row).get(column);
                int neighbourCount = neighBoardList.get(row).get(column);
                boardList.get(row).set(column, Rule.rleRules(cellState, neighbourCount));
            }
        }
        updateNeighbourBoard();
    }

    /**
     * Assigns a new value to the variable <code>cellSize</code>.
     * @param newSize the new size
     */
    @Override
    public void setCellSize(int newSize)
    {
       cellSize = newSize;
    }

    /**
     * Enable or disable <code>fitZoom</code>.
     * @param newValue the new boolean determining if fitZoom should be active (true) or if manual
     *                 zoom is enabled (false)
     * @see #fitZoom
     */
    @Override
    public void setFitZoom(boolean newValue)
    {
        fitZoom = newValue;
    }

    /**
     * Gets the current <code>boardList</code>.
     *
     * @return the current <code>boardList</code>
     */
    protected ArrayList getBoardList()
    {
        return boardList;
    }

    /**
     * Checks the size of the <code>boardList</code> to restrict the size of the board at a certain
     * , constant limit (which is 2000 in this case). This prevents the <code>boardList</code> from
     * becoming too large.
     *
     * @see #boardList
     */
    private void sizeTest(){
        if(boardList.get(0).size()> 2000) {
            for (int i = 0; i < boardList.size(); i++) {
                boardList.get(i).set(0, (byte) 0);
                boardList.get(i).set(boardList.get(0).size() - 1, (byte) 0);
            }
            if (boardList.size() > 2000) {
                ArrayList<Byte> newRow = new ArrayList<>();
                for (Byte get : boardList.get(0)) {
                    newRow.add((byte) 0);
                }
                boardList.set(0, newRow);
                boardList.set(boardList.size() - 1, newRow);
            }
        }
    }
}
