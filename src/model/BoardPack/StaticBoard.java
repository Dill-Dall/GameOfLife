/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model.BoardPack;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import model.CanvasInfo;

/**
 * The StaticBoard was the early version which uses a byte[][] array for the
 * <code>boardArray</code> variable. That means it is not meant to expand the
 * board. Now it is only still used in the Gif.java to create .gif files.
 *
 * @version 1.0
 * @author T.Dahll, M.Olsen
 */
public class StaticBoard extends Board {

    private final int zeroRow = 2;
    private final int zeroColumn = 2;
    private byte[][] boardArray;
    private static byte[][] patternArray;

    //-------------------------CONSTRUCTORS-----------------------------------//
    /**
     * Board constructors. Which uses set values, or rows and colums parameters
     * or copies and converts a array consisting of 0s and 1s; Handles outer
     * column and outer row of the arrays by adding two extra rows and columns
     * consisting of 0s.
     */
    public StaticBoard() {
        this(10, 10);
    }

    /**
     * Constructor that takes the dimensions as parameters. Initializes a
     * <code>boardArray</code> which is filled with 0s.
     *
     * @param row desired how many cells on top of each other in the Board +
     * <code>zeroRow</code>
     * @param columns how many cells side by side of each other in the Board +
     * <code>zeroColumn</code>
     */
    public StaticBoard(int row, int columns) {
        this.numRows = row;
        this.numColumns = columns;
        boardArray = new byte[numRows + zeroRow][numColumns + zeroColumn];

        for (int x = 1; x < boardArray.length - 1; x++) {
            for (int y = 1; y < boardArray[0].length - 1; y++) {
                boardArray[x][y] = 0;
            }
        }
    }

    /**
     * Constructor that creates a <code>StaticBoard</code> with dimensions that
     * are related to the number of available processors.
     *
     * @param numOfProcessors the computer's number of processors.
     */
    public StaticBoard(int numOfProcessors) {
        this(numOfProcessors * 2, numOfProcessors * 2);
    }

    /**
     * Board constructor which formats a board to use a byte array.
     *
     * @param board takes a array of 0s and 1s, and converts it into a Board
     * object
     */
    public StaticBoard(byte[][] board) {
        this.numRows = board.length;
        this.numColumns = board[0].length;

        boardArray = new byte[numRows][numColumns];

        for (int x = 0; x < boardArray.length; x++) {
            for (int y = 0; y < boardArray[0].length; y++) {
                boardArray[x][y] = board[x][y];
            }
        }
    }

//----------------------------------BOUNDING LOGIC -----------------------//
    /**
     * Bounding box pattern represented as a <code>String</code> of 1s and 0s.
     *
     * @return a <code>String</code> representation of bound array
     */
    @Override
    public String getBoundingBoxPattern() {

        byte[][] board = this.getBoardArray();
        if (this.boardArray.length == 0) {
            return "";
        }
        int[] boundingBox = getBoundingBox();
        String str = "";
        for (int i = boundingBox[0]; i <= boundingBox[1]; i++) {
            for (int j = boundingBox[2]; j <= boundingBox[3]; j++) {
                if (board[i][j] == 1) {
                    str = str + "1";
                } else {
                    str = str + "0";
                }
            }
        }
        return str;
    }

    /**
     * Collects the outer sizes of an 2D array where there are active elements.
     * So that an array with large white spaces can be compressed to fit exactly
     * to the active elements. boundingBox[4] // minrow maxrow mincolumn
     * maxcolumn
     *
     * @return an array witch describes where the active rows and columns start
     * and end.
     */
    @Override
    public int[] getBoundingBox() {
        byte[][] board = this.getBoardArray();
        int[] boundingBox = new int[4]; // minrow maxrow mincolumn maxcolumn
        boundingBox[0] = board.length;
        boundingBox[1] = 0;
        boundingBox[2] = board[0].length;
        boundingBox[3] = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    continue;
                }
                if (i < boundingBox[0]) {
                    boundingBox[0] = i;
                }
                if (i > boundingBox[1]) {
                    boundingBox[1] = i;
                }
                if (j < boundingBox[2]) {
                    boundingBox[2] = j;
                }
                if (j > boundingBox[3]) {
                    boundingBox[3] = j;
                }

            }
        }

        return boundingBox;
    }

    /**
     * Uses the bounding box, <code>bounding</code> to create a new 2D array 
     * where the size of the array, is determined by the bounding box.
     *
     * @param array to compress
     * @param bounding outer bounds which decides compression.
     * @see Board#getBoundingBox()
     */
    public StaticBoard(byte[][] array, int[] bounding) {

        if (bounding[1] - bounding[0] < 0) {
            boardArray = array;
            return;
        }

        numRows = bounding[1] - bounding[0] + 1;
        numColumns = bounding[3] - bounding[2] + 1;

        boardArray = new byte[numRows + 4][numColumns + 4];

        //Bounding describes outer framework of the array, where there are active 1's
        for (int x = bounding[0], i = 0; x <= bounding[1]; x++, i++) {
            for (int y = bounding[2], j = 0; y <= bounding[3]; y++, j++) {
                boardArray[i + 2][j + 2] = array[x][y];
            }
        }
    }

    //----------------------------------DRAW LOGIC-------------------------------//
    /**
     * Draws the <code>boardArray</code> onto the canvas. Places the drawing at
     * the center of the canvas.
     *
     * @param gc - the <code>GraphicsContext</code> object from the controller
     * @param cInfo <code>canvasInfo</code> of the canvas
     * @see controller.GoLFXController
     */
    @Override
    public void draw(GraphicsContext gc, CanvasInfo cInfo) {
        double cellSize = cInfo.getCellSize();
        cInfo.centerPlacement(boardArray[0].length, boardArray.length);
        double placementX = cInfo.getPlacementX();
        double placementY = cInfo.getPlacementY();
        //Background
        gc.setFill(backGroundColor);

        gc.fillRect(placementX + cellSize, placementY + cellSize, cellSize * (getBoardArray()[0].length - 2),
                cellSize * (getBoardArray().length - 2));

        //gridcolor
        gc.setStroke(gridColor);
        gc.setFill(cellColor);

        //Avoids drawing the outer arrays
        //Places cells acording to boardArray
        for (int x = 1; x < boardArray.length - 1; x++) {
            for (int y = 1; y < boardArray[0].length - 1; y++) {
                if (getBoardArray()[x][y] == 1) {
                    gc.fillRect(y * cellSize + placementX, x * cellSize
                            + placementY, cellSize, cellSize);
                    aliveCells += 1;
                }
                if (grid) {
                    gc.strokeRect(y * cellSize + placementX, x * cellSize
                            + placementY, cellSize, cellSize);
                }
            }
        }
    }

    /**
     * Draw method that do not position the drawing centered on the canvas Used
     * for the drawing on the strip canvas in editorController
     *
     * @param gc GraphicsContext of the <code>strip canvas</code>
     * @param cInfo canvasInfo of the <code>strip canvas</code>
     */
    public void drawStr(GraphicsContext gc, CanvasInfo cInfo) {
        double cellSize = cInfo.getCellSize();
        //gridcolor
        gc.setStroke(gridColor);
        gc.setFill(cellColor);

        //Avoids drawing the outer arrays
        //Places cells acording to boardArray
        for (int x = 1; x < boardArray.length - 1; x++) {
            for (int y = 1; y < boardArray[0].length - 1; y++) {
                if (boardArray[x][y] == 1) {
                    gc.fillRect(y * cellSize, x * cellSize,
                            cellSize, cellSize);
                    aliveCells += 1;
                } else {
                    gc.clearRect(y * cellSize, x * cellSize,
                            cellSize, cellSize);
                }
            }
        }
    }

    
    @Override
    public void setCellValue(int cellX, int cellY, boolean erase, int Maxsize) {
        if ((cellY < boardArray[0].length && cellY > -1)
                && (cellX < boardArray.length && cellX > -1)) {

            if (erase) {
                boardArray[cellX][cellY] = 0;
            } else {
                boardArray[cellX][cellY] = 1;

            }
        }
    }

//---------------------------------GENERATION LOGIC --------------------------//
    @Override
    public void setNewGenValues() {
        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[0].length; j++) {
                int cellstate = boardArray[i][j];
                int numNeighbours = neighBoard[i][j];
                byte newVal = Rule.rleRules(cellstate, numNeighbours);
                boardArray[i][j] = (byte) newVal;
            }
        }
    }

    @Override
    public void createNeighBoard() {
        neighBoard = new byte[boardArray.length][boardArray[0].length];
    }

    @Override
    public void countNeighbhours(int row, int col) {
        for (byte r = -1; r < 2; r++) {
            for (byte c = -1; c < 2; c++) {
                int rRow = r + row;
                int cCol = c + col;
                if (c != 0) {
                    neighBoard[rRow][cCol]++;
                }
                if (r != 0 && c == 0) {
                    neighBoard[rRow][cCol]++;
                }
            }
        }
    }

    @Override
    public void updateNeighbourBoard() {
        for (int x = 1; x < boardArray.length - 1; x++) {
            for (int y = 1; y < boardArray[0].length - 1; y++) {

                if (boardArray[x][y] == 1) {
                    for (int m = -1; m < 2; m++) {
                        for (int n = -1; n < 2; n++) {
                            neighBoard[x + m][y + n]++;
                        }
                    }

                    neighBoard[x][y]--;
                }
            }
        }

    }

//---------------------------------GENERAL LOGIC ------------------------------//
    /**
     * Receives an <code>ArrayList</code> as an argument and then creates a
     * <code>byte[][]</code> from the <code>ArrayList</code> which is then
     * returned to the caller.
     * 
     * @param listArray the <code>ArrayList</code> to create a
     * <code>byte[][]</code> from
     * @return a <code>byte[][]</code> representation of the 
     * <code>ArrayList</code> received as an argument
     */
    public static byte[][] listToArray(ArrayList<ArrayList<Byte>> listArray) {
        int rowSize = listArray.size();
        int colSize = listArray.get(0).size();
        byte[][] Arry = new byte[rowSize][colSize];
        for (int i = 0; i < rowSize; i++) {

            for (int j = 0; j < colSize; j++) {

                Arry[i][j] = listArray.get(i).get(j);
            }
        }
        return Arry;
    }

    @Override
    public void addArrayToBoard(byte[][] board) {
        int placementy = (boardArray.length - board.length) / 2;
        int placementx = (boardArray[0].length - board[0].length) / 2;

        int z = 0;
        int w = 0;
        for (int y = placementy; y < placementy + board.length; y++, z++) {
            w = 0;
            for (int x = placementx; x < placementx + board[0].length; x++, w++) {
                boardArray[y][x] = board[z][w];
            }
        }
    }

    /**
     * Temp method to check output versus drawing
     *
     * @param array the array to represent as String
     */
    public static void StringRepresentation(byte[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(array[i][j] + " ");
            }

        }
    }

    /**
     * Creates a <code>String</code>-representation of the
     * <code>boardArray</code>.
     *
     * @return the <code>String</code>-representation of the board
     */
    @Override
    public String toString() {
        String a = "";

        for (int i = 1; i < boardArray.length - 1; i++) {
            for (int j = 1; j < boardArray[0].length - 1; j++) {

                a += boardArray[i][j];

            }

            a += "\n";
        }
        return a;
    }

    @Override
    public int getSumOfAlive() {

        int sumOfAlive = 0;
        int x = 0;

        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[0].length; j++) {

                if (boardArray[i][j] == 1) {
                    x = i + j;
                    sumOfAlive += x;
                }
            }
        }
        return sumOfAlive;
    }

    /**
     * Gets the number of alive cells in the current <code>boardArray</code>.
     *
     * @return the number of alive cells
     */
    @Deprecated
    public int getAC_label() {
        aliveCells = 0;
        for (int x = 0; x < boardArray.length; x++) {
            for (int y = 0; y < boardArray[0].length; y++) {
                if (boardArray[x][y] == 1) {
                    aliveCells++;
                }
            }
        }
        return aliveCells;
    }

//---------------------------------SETTERS AND GETTERS --------------------------//
    /**
     * Sets the provided <code>boardArray</code> to the
     * <code>StaticBoard</code>.
     *
     * @param boardArray the <code>boardArray</code> to set
     */
    public void setBoardArray(byte[][] boardArray) {
        this.boardArray = boardArray;
    }

    /**
     * Gets the current <code>boardArray</code>.
     *
     * @return the current <code>boardArray</code>
     */
    public byte[][] getBoardArray() {
        return boardArray;
    }

    /**
     * Gets the current <code>patternArray</code>.
     *
     * @return the current <code>patternArray</code>
     * @see #patternArray
     */
    public static byte[][] getPatternArray() {
        return patternArray;
    }

    /**
     * Sets a <code>patternArray</code>.
     *
     * @param aPatternArray the <code>patternArray</code> to set
     */
    public static void setPatternArray(byte[][] aPatternArray) {
        patternArray = aPatternArray;
    }

    /**
     * Sets the specific value in an element of the <code>boardArray</code>.
     *
     * @param state the value to set
     * @param row the row of the element
     * @param column the column of the element
     */
    public void setBoardArrayElement(byte state, int row, int column) {
        boardArray[row][column] = state;
    }

    /**
     * Gets the height of the main board.
     *
     * @return the height of the main board
     */
    @Override
    public int getHeight() {
        return boardArray.length + 2;
    }

    /**
     * Gets the width of the main board.
     *
     * @return the width of the main board
     */
    @Override
    public int getWidth() {
        return boardArray[0].length + 2;
    }

    //------------UNIMPLEMENTED STATICBOARD METHODS--------------------------//
    @Override
    public boolean topExist() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setXmove(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawTopBoard(GraphicsContext gc, CanvasInfo cInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setYmove(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rotateTopBoard(boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[][] getTopBoard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTopBoard(byte[][] rleArray) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void autoFit(byte[][] topBoard) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sizeTest() {
        //throw new UnsupportedOperationException("Not supported yet."); 
    }

}
