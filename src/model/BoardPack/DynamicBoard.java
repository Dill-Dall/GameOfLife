/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model.BoardPack;

import utilities.IndexPair;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.CanvasInfo;

/**
 * DynamicBoard uses the expandable <code>boardArray</code> as the board. The
 * DynamicBoard-class differs from the StaticBoard-class in the way cells
 * located at the row-borders and at the column-borders are processed. Instead
 * of having a zero-row and a zero-column wrapped around the board, the
 * DynamicBoard expands dynamically by adding rows and columns in the needed
 * direction.
 *
 * @author T.Dahll, M.S.Olsen
 * @see model.BoardPack.Board
 */
public class DynamicBoard extends Board implements Serializable {

    /**
     * The <code>ArrayList</code> containing the main board of the game.
     */
    private ArrayList<ArrayList<Byte>> boardArray;

    /**
     * The <code>ArrayList</code> containing the pattern of the game.
     */
    private static ArrayList<ArrayList<Byte>> patternArray;

    //--------------------------CONSTRUCTORS--------------------------------//
    /**
     * Board constructors. Which uses set values, or rows and colums parameters
     * or copies and converts a array consisting of 0's and 1's; Handles outer
     * column and outer row of the arrays by adding two extra rows and columns
     * consisting of 0's.
     */
    public DynamicBoard() {
        this(20, 20);
    }

    /**
     * Makes sure that the initial dimensions of the board is large enough to
     * have synchronized threads doing they separate columns at the same time.
     *
     * @param numOfProcessors the computer's number of processors.
     */
    public DynamicBoard(int numOfProcessors) {
        this(numOfProcessors * 2, numOfProcessors * 2);
    }

    /**
     * Constructor that takes the number of rows <code>row</code> and the number
     * of columns <code>columns</code> as parameters. Initializes an empty board
     * (filled with zeros).
     *
     * @param row the number of rows in the board
     * @param columns the number of columns in the board
     * @see #create2Darray(int, int)
     * @see #createNeighBoard()
     */
    public DynamicBoard(int row, int columns) {
        this.numRows = row;
        this.numColumns = columns;
        boardArray = create2Darray(row, columns);
    }

    /**
     * Board constructor which accepts another <code>DynamicBoard</code> or a 
     * <code>StaticBoard</code>as an argument. This clones the 
     * <code>boardArray</code> belonging to the <code>DynamicBoard</code> used 
     * as an argument. Or creates the <code>boardArray</code> from the 
     * <code>StaticBoard</code> <code>byte [][]</code>.
     *
     * @param board the board to copy
     */
    public DynamicBoard(Board board) {
        this.numRows = board.getHeight();
        this.numColumns = board.getWidth();
        boardArray = (ArrayList<ArrayList<Byte>>) ((DynamicBoard) board).boardArray.clone();
    }

    //-----------------------BOUNDING LOCIC----------------------------------//
    /**
     * Bounding box-pattern represented as a string of 1's and 0's. Extracts the
     * pattern in the bounding box formatted as a <code>String</code>.
     *
     * Reference "oppgavesett 5".
     * 
     * @return the <code>String</code>-representation of the bounding box
     */
    @Override
    @Deprecated
    public String getBoundingBoxPattern() {
        if (boardArray.isEmpty()) {
            return "";
        }
        int[] boundingBox = getBoundingBox();
        String boundingArray = "";
        for (int i = boundingBox[0]; i <= boundingBox[1]; i++) {
            for (int j = boundingBox[2]; j <= boundingBox[3]; j++) {
                if (boardArray.get(i).get(j) == 1) {
                    boundingArray = boundingArray + "1";
                } else {
                    boundingArray = boundingArray + "0";
                }
            }
        }
        return boundingArray;
    }

    /**
     * The <code>getBoundingBox</code> is a method which collects the outer
     * sizes of an 2D array where there are active elements. So that an array
     * with large white spaces can be compressed to fit exactly to the active
     * elements. boundingBox[4] // minrow maxrow mincolumn maxcolumn
     *
     * Reference "oppgavesett 5".
     * 
     * @return an array witch describes where the active rows and columns start
     * and end.
     */
    @Override
    public int[] getBoundingBox() {
        int[] boundingBox = new int[4]; // minrow maxrow mincolumn maxcolumn
        boundingBox[0] = boardArray.size();
        boundingBox[1] = 0;
        boundingBox[2] = boardArray.get(0).size();
        boundingBox[3] = 0;
        for (int i = 0; i < boardArray.size(); i++) {
            for (int j = 0; j < boardArray.get(0).size(); j++) {
                if (boardArray.get(i).get(j) == 0) {
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
     * Uses the boundingBox to create a new 2D-array. The size of the array is
     * determined by the bounding box, <code>bounding</code> and placed in the
     * <code>boardArray</code>.
     *
     * Reference: assignment text.
     *
     * @param array the <code>ArrayListArrayList(byte)</code> containing the
     * pattern
     * @param bounding the <code>int[]</code> containing the coordinates for the
     * bounding box
     * @see Board#getBoundingBox()
     * 
     */
    public DynamicBoard(ArrayList<ArrayList<Byte>> array, int[] bounding) {
        if (bounding[1] - bounding[0] < 0) {
            boardArray = array;
            return;
        }

        numRows = bounding[1] - bounding[0] + 1;
        numColumns = bounding[3] - bounding[2] + 1;
        boardArray = create2Darray(numRows + 4, numColumns + 4);

        //Bounding describes outer framework, where there are active 1s
        for (int x = bounding[0], i = 0; x <= bounding[1]; x++, i++) {
            for (int y = bounding[2], j = 0; y <= bounding[3]; y++, j++) {
                boardArray.get(i + 2).set(j + 2, array.get(x).get(y));
            }
        }
    }

    /**
     * Gets the <code>ArrayList</code> containing the <code>boundArray</code>.
     * The returned <code>boundArray</code> gets values from the
     * <code>this.boardArray</code>. The <code>numRows</code> and
     * <code>numColumns</code> is extracted from <code>bounding</code>.
     *
     * @return an <code>ArrayList</code> of the Board where the are no null
     * borders
     * @see #create2Darray(int, int)
     */
    public ArrayList<ArrayList<Byte>> getBoundArray() {
        int[] bounding = getBoundingBox();
        numRows = bounding[1] - bounding[0] + 1;
        numColumns = bounding[3] - bounding[2] + 1;
        ArrayList<ArrayList<Byte>> boundArray = create2Darray(numRows, numColumns);

        //Bounding describes outer framework, where there are active 1s
        for (int x = bounding[0], i = 0; x <= bounding[1]; x++, i++) {
            for (int y = bounding[2], j = 0; y <= bounding[3]; y++, j++) {
                boundArray.get(i).set(j, this.boardArray.get(x).get(y));
            }
        }
        return boundArray;
    }

    /**
     * Adds a <code>byte[][]</code> to the boardArray at the position decided by
     * the placement variables. The placement variables, <code>placementy</code>
     * and <code>placementx</code> are determined by the placement from the user
     * (<code>ymove</code> and <code>xmove</code>) and the
     * <code>boardArray</code> relative to the <code>array</code> that is to be
     * placed onto the <code>boardArray</code>. If the <code>array</code> is
     * placed outside the boundaries of the <code>boardArray</code>, rows and
     * columns are added to compensate for this.
     *
     * @param array the <code>byte[][]</code> to be placed onto the
     * <code>boardArray</code>
     * @see #addRows(int, boolean)
     * @see #addColumns(int, boolean)
     * @see #boardArray the existing board
     * @see #topExist changed to false if it previously existed a
     * <code>topBoard</code> to be placed
     */
    @Override
    public void addArrayToBoard(byte[][] array) {
        optimaliser = false;
        if (boardArray.size() <= array.length || boardArray.get(0).size() <= array[0].length) {
            while (boardArray.size() <= array.length) {
                addRows(1, false);
                addRows(1, true);
            }
            while (boardArray.get(0).size() <= array[0].length) {
                addColumns(1, false);
                addColumns(1, true);
            }
        }

        int placementy = ymove + (boardArray.size() - array.length) / 2;
        int placementx = xmove + (boardArray.get(0).size() - array[0].length) / 2;

        if (placementy + array.length > boardArray.size()) {
            addRows(placementy + array.length - boardArray.size() + 1, false);
        }
        if (placementy < 0) {
            addRows(Math.abs(placementy), true);
            placementy = 1;
        }

        if (placementx + array[0].length > boardArray.get(0).size()) {
            addColumns(placementx + array[0].length - boardArray.get(0).size() + 1, false);
        }
        if (placementx < 0) {
            addColumns(Math.abs(placementx), true);
            placementx = 1;
        }

        for (int y = placementy, z = 0; y < placementy + array.length; y++, z++) {

            for (int x = placementx, w = 0; x < placementx + array[0].length; x++, w++) {
                //Adds 1's to the Board
                if (array[z][w] == 1) {
                    boardArray.get(y).set(x, array[z][w]);
                }
            }
        }

        if (topExist()) {
            topExist = false;
            xmove = 0;
            ymove = 0;
        }
    }

    //-------------------------EXPANDING LOGIC ----------------------------- //
    
    // adRow and adCol are used by the indexPairs in the index ArrayList, to adjust 
    //the indexses when the Board is expanded.
    private int adRow;
    private int adCol;

    /**
     * Adds rows and columns to the board as it grows bigger. Creates a
     * <code>num</code> number of rows, create them of the same length as the
     * <code>boardList.get(0)</code> and places the rows either at index 0 (if
     * <code>top</code> is true) or at the end of the <code>boardList</code> if
     * <code>top</code> is false.
     *
     * @see #addRows(int, boolean) - adds <code>int</code> number of rows at the
     * top of the <code>boardArray</code> if the <code>boolean</code> is true
     * @see #addColumns(int, boolean) - adds <code>int</code> number of columns
     * at the left side of the <code>boardArray</code> if the
     * <code>boolean</code> is true
     * @see #adRow - adjusts the index address for the rows according to how the
     * indexes have been changed when rows have been added
     * @see #adCol - adjusts the index address for the columns according to how
     * the indexes have been changed when columns have been added
     */
    public void addRowsandColumns() {
        
        boolean top = true;

        //Check if outer rows got 1, and add extra rows if so.
        if (boardArray.get(0).contains((byte) 1)) {
            addRows(20, top);
            adRow += 20;
        }

        if (boardArray.get(boardArray.size() - 1).contains((byte) 1)) {
            top = false;
            addRows(20, top);
        }

        //Check if outer colums contains a 1, add extra if so
        boolean checkColL = true;
        boolean checkColR = true;
        boolean left = true;

        for (int row = 0; row < boardArray.size() - 1; row++) {
            if (boardArray.get(row).get(0) == 1 && checkColL) {
                addColumns(20, left);
                checkColL = false;
                adCol += 20;
            }
            //TODO boardArray.get(row).get(boardArray.get(0).size() - adCol-1) == 1 && checkColR
            if (boardArray.get(row).get(boardArray.get(0).size() - 1) == 1 && checkColR) {

                left = false;
                addColumns(20, left);
                checkColR = false;
            }

            if (checkColL == false & checkColR == false) {
                break;
            }
        }
    }

    /**
     * Variable used to adjust placement of board along the X-axis.
     */
    int placeColVar;

    /**
     * Add columns that expands the <code>boardArray</code> horizontally. The
     * <code>addColumns</code> determines how many columns that are to be added
     * and the variables, <code>left</code> determines the side of the
     * <code>boardArray</code> where the columns should be added. The variable,
     * <code>placeColVar</code>, is updated according to the number of columns
     * added to the left side of the <code>boardArray</code>. The variable
     * <code>placeColVar</code> is then used centering the drawing of the
     * <code>boardArray</code>
     *
     * @param addColumns the number of columns to add
     * @param left the side to add the columns
     */
    public void addColumns(int addColumns, boolean left) {
        placeColVar = 0;
        for (int adder = 0; adder < addColumns; adder++) {
            if (left) {
                placeColVar++;
            };
            for (int row = 0; row < boardArray.size(); row++) {
                if (left) {

                    boardArray.get(row).add(0, (byte) 0);

                } else {
                    boardArray.get(row).add((byte) 0);
                }
            }
        }
    }

    /**
     * Variable used to adjust placement of board along the Y-axis.
     */
    int placeRowVar;

    /**
     * Creates <code>addRows</code> number of rows, create them of the same
     * length as the <code>boardArray</code>. The places the row either at index
     * 0 (if <code>top</code> is true) or at the end of the
     * <code>boardArray</code> if <code>top</code> is false. The variable,
     * <code>placeRowVar</code>, is updated with the number of rows that have
     * been added to the top of the <code>boardArray</code> and used to
     * calculate the placement of the drawing of the <code>boardArray</code> in
     * method <code>draw</code>.
     *
     * @param addRows the number of rows to add
     * @param top the side to add the rows
     */
    public void addRows(int addRows, boolean top) {
        placeRowVar = 0;
        for (int j = 0; j < addRows; j++) {
            ArrayList<Byte> newRow = new ArrayList<>();
            for (Byte get : boardArray.get(0)) {
                newRow.add((byte) 0);
            }
            if (top) {
                placeRowVar++;
                boardArray.add(0, newRow);
            } else {
                boardArray.add(newRow);
            }
        }
    }

    /**
     * Fits the <code>boardArray</code> according to <code>array</code> provided
     * as an argument. This is done by calling methods <code>addRows</code> and
     * <code>addColumns</code> which expands the <code>boardArray</code> by 5 at
     * the bottom, right side of the <code>boardArray</code>.
     *
     * @param array the <code>byte[][]</code> to place onto the
     * <code>boardArray</code>
     * @see #addRows(int, boolean)
     * @see #addColumns(int, boolean)
     */
    @Override
    public void autoFit(byte[][] array) {
        if (boardArray.size() < array.length || boardArray.get(0).size() < array.length) {
            while (boardArray.size() < array.length) {
                addRows(5, false);
            }
            while (boardArray.get(0).size() < array[0].length) {
                addColumns(5, false);
            }
        }
    }

    //------------------GENERATION LOGIC--------------------------------------//
    /**
     * In <code>DynamicBoard</code> and with only one thread active this process
     * has been optimized. When there are less alive cells than dead. Here we
     * have used an <code>ArrayList</code> of <code>indexPair</code>, which
     * stores the placement of active cells, meaning alive cells and those with
     * neighbors. Combined make up a list of the cells which are relevant in the
     * logic, and therefore we avoid iterating through irrelevant cells. The
     * effect of this can be seen on the turingmachine.rle where 4 threads give
     * a speed of 15 and the optimized gives 24.
     */
    
    /**
     * The <code>ArrayList</code> used by the optimizing-function. This is used 
     * in order to store information about "active" cells, that is, cells that 
     * are alive and/or have neighbors.
     *
     * @see #optimaliser
     * @see #updateNeighbourBoard()
     * @see #countNeighbhours(int, int)
     */
    private ArrayList<IndexPair> index;

    /**
     * Operations to be performed before setting the next generation. Variables
     * <code>aliveCells</code>, <code>adRow</code> and <code>adCol</code> is set
     * to 0 before processing the next generation. Method
     * <code>addRowsandColumns</code> is called in order to expand the
     * <code>boardArray</code> if necessary and the board containing the number
     * of neighbors is prepared for the next generation. The variable
     * <code>iterations</code> is incremented by 1 to show the number of
     * generations.
     *
     * @see #aliveCells
     * @see #adRow
     * @see #adCol
     * @see #addRowsandColumns()
     * @see #createNeighBoard()
     * @see #iterations
     */
    @Override
    public void beforeSetGeneration() {
        aliveCells = 0;
        adRow = 0;
        adCol = 0;
        addRowsandColumns();
        createNeighBoard();
        iterations++;
    }

    /**
     * Iterates over the <code>boardArray</code> in order to update the
     * <code>neighBoard</code>. If the <code>optimaliser</code> is set to
     * <code>true</code>, a <code>tempPair</code> is created in order to contain
     * information about the cells in <code>boardArray</code> which are alive
     * (have value 1). The <code>index</code> is updated with the coordinates of
     * the cells that are alive. The <code>aliveCells</code> is incremented by 1
     * in order to display the number of alive cells.
     *
     * If the <code>optimaliser</code> has value <code>false</code>, the entire
     * <code>boardArray</code> is iterated in order to detect the alive cells.
     *
     * Regardless of the <code>optimaliser</code>-value, the
     * <code>countNeighbhours</code> method is called in order to count the
     * number of neighbors of the cell at coordinates (row, column).
     *
     * @see #optimaliser
     * @see #index
     * @see #boardArray
     * @see #aliveCells
     * @see #countNeighbhours(int, int)
     */
    @Override
    public void updateNeighbourBoard() {
        if (optimaliser) {
            ArrayList<IndexPair> tempPair = new ArrayList<>(index);
            index.clear();

            for (int n = 0; n < tempPair.size(); n++) {
                int row = tempPair.get(n).getRow() + adRow;
                int col = tempPair.get(n).getCol() + adCol;

                if (boardArray.get(row).get(col) == 1) {
                    index.add(new IndexPair(row, col));
                    countNeighbhours(row, col);
                }
            }
        } else {
            index = new ArrayList<>();

            for (int row = 1; row < boardArray.size() - 1; row++) {
                for (int col = 1; col < boardArray.get(0).size() - 1; col++) {
                    if (boardArray.get(row).get(col) == 1) {
                        index.add(new IndexPair(row, col));
                        countNeighbhours(row, col);
                    }
                }
            }

        }
    }

    /**
     * Creates an empty <code>byte[][]</code> that is assigned to
     * <code>neighBoard</code>. The <code>neighBoard</code> has the same
     * dimensions as the <code>boardArray</code>.
     *
     * @see #neighBoard
     * @see #boardArray
     */
    @Override
    public void createNeighBoard() {
        neighBoard = new byte[boardArray.size()][boardArray.get(0).size()];
    }

    /**
     * Counts the number of neighbors for the cell at coordinate (row, column).
     * The surrounding cells of the cell given by (row, column) are iterated in
     * order to update the number of neighbors that are related to the the
     * current cell with the corresponding cell in <code>neighBoard</code>. The
     * cells that have a cell state of 0 and have neighbors are added to the
     * <code>index</code> with their coordinates (row, column).
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @see #neighBoard
     * @see #boardArray
     * @see #index
     */
    @Override
    public void countNeighbhours(int row, int col) {
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                int rRow = r + row;
                int cCol = c + col;

                if (c != 0) {
                    neighBoard[rRow][cCol]++;
                    if (neighBoard[rRow][cCol] == 1 && boardArray.get(rRow).get(cCol) == 0) {
                        index.add(new IndexPair(rRow, cCol));
                    }
                }

                if (r != 0 && c == 0) {
                    neighBoard[rRow][cCol]++;
                    if (neighBoard[rRow][cCol] == 1 && boardArray.get(rRow).get(cCol) == 0) {
                        index.add(new IndexPair(rRow, cCol));
                    }
                }
            }
        }
    }

    /**
     * Sets the new value of the cells in the <code>boardArray</code>. If the
     * <code>optimaliser</code> is equal to <code>true</code>, the method
     * iterates through <code>index</code> and obtains the <code>row</code> and
     * <code>column</code> from the <code>index</code>, the cell state from the
     * <code>boardArray</code> and the number of neighbors from the
     * <code>neighBoard</code>. The new cell state is determined by the static
     * method in the Rule-class which calculates the new cell state-value. The
     * new cell state-value is placed in the <code>boardArray</code>.
     *
     * If the <code>optimaliser</code> is equal to <code>false</code>, the
     * method iterates over the <code>boardArray</code> and determines the new
     * cell state-value at all the coordinates in the <code>boardArray</code>.
     *
     * @see #optimaliser
     * @see #index
     * @see #boardArray
     * @see #neighBoard
     */
    @Override
    public void setNewGenValues() {
        if (optimaliser) {
            for (int n = 0; n < index.size(); n++) {
                int row = index.get(n).getRow();
                int col = index.get(n).getCol();
                int cellstate = boardArray.get(row).get(col);
                int numNeighbours = neighBoard[row][col];
                byte newVal = Rule.rleRules(cellstate, numNeighbours);
                boardArray.get(row).set(col, newVal);

                if (newVal == 1) {
                    aliveCells++;
                }
            }
        } else {
            for (int i = 0; i < boardArray.size(); i++) {
                for (int j = 0; j < boardArray.get(0).size(); j++) {
                    int cellstate = boardArray.get(i).get(j);
                    int numNeighbours = neighBoard[i][j];
                    byte newVal = Rule.rleRules(cellstate, numNeighbours);
                    boardArray.get(i).set(j, newVal);

                    if (newVal == 1) {
                        aliveCells++;
                    }
                }
            }
            optimaliser = true;
        }
    }

    //--------------------------DRAW and COORDINATES LOGIC--------------------//
    /**
     * Draws the cells of the <code>DynamicBoard</code>. The placement values is
     * calculated with the <code>placeColVar</code> and the
     * <code>placeRowVar</code> in order to compensate for columns and rows
     * added to the <code>boardArray</code>. The cells are drawn with a
     * <code>cellSize</code> obtained from the <code>cInfo</code>.
     *
     * @param gc the <code>GraphicsContext</code> used to draw
     * @param cInfo the <code>CanvasInfo</code>-object containing information
     * about the canvas
     * @see #placeColVar
     * @see #placeRowVar
     * @see #boardArray
     */
    @Override
    public void draw(GraphicsContext gc, CanvasInfo cInfo) {
        // Background
        gc.setFill(backGroundColor);
        gc.fillRect(0, 0, cInfo.getCanvasWidth(), cInfo.getCanvasheight());

        if (topExist) {
            drawTopBoard(gc, cInfo);
        }
        double cellSize = cInfo.getCellSize();
        //adjusts the screen acording to the added rows and columns
        cInfo.setPlacementX(placeColVar);
        cInfo.setPlacementY(placeRowVar);
        placeRowVar = 0;
        placeColVar = 0;
        double placementX = cInfo.getPlacementX();
        double placementY = cInfo.getPlacementY();

        //gridcolor
        gc.setStroke(gridColor);
        gc.setFill(cellColor);

        //Places cells according to boardArray
        for (int x = 0; x < boardArray.size(); x++) {
            for (int y = 0; y < boardArray.get(0).size(); y++) {
                if (boardArray.get(x).get(y) == 1) {
                    gc.fillRect(y * cellSize + placementX, x * cellSize
                            + placementY, cellSize -0.1, cellSize-0.1);
                    
                }
                if (grid) {
                    gc.strokeRect(y * cellSize + placementX, x * cellSize
                            + placementY, cellSize, cellSize);
                }
            }
        }
    }

    /**
     * The amount of horizontal movement of a <code>byte[][]</code> to place
     * onto the existing <code>boardArray</code>. Used in order to place a
     * <code>byte[][]</code> at the correct coordinates of the
     * <code>boardArray</code>.
     *
     * @see #addArrayToBoard(byte[][])
     */
    private int xmove = 0;

    /**
     * The amount of vertical movement of a <code>byte[][]</code> to place onto
     * the existing <code>boardArray</code>. Used in order to place a
     * <code>byte[][]</code> at the correct coordinates of the
     * <code>boardArray</code>.
     *
     * @see #addArrayToBoard(byte[][])
     */
    private int ymove = 0;

    /**
     * A <code>boolean</code> to determine if there is a <code>topBoard</code>
     * to place.
     */
    private boolean topExist = false;
    
   

    /**
     * Draws the <code>topBoard</code> on the canvas. The <code>topBoard</code>
     * to place onto the <code>boardArray</code> is drawn temporarily over the
     * <code>boardArray</code> before being added onto the
     * <code>boardArray</code>. Adjustments to the placement of the
     * <code>topBoard</code> is calculated so that it is placed according to the
     * existing <code>boardArray</code>.
     *
     * @param gc the <code>GraphicsContext</code> used to draw
     * @param cInfo the <code>CanvasInfo</code>-object containing information
     * about the canvas
     */
    @Override
    public void drawTopBoard(GraphicsContext gc, CanvasInfo cInfo) {
        double placeColinGrid = 0;
        double placeRowingrid = 0;
        double cellSize = cInfo.getCellSize();

        if (topBoard.length % 2 == 0) {
            placeRowingrid -= cellSize / 2;
        }

        if (topBoard[0].length % 2 == 0) {
            placeColinGrid -= cellSize / 2;
        }

        if (boardArray.size() % 2 == 0) {
            placeRowingrid += cellSize / 2;
        }

        if (boardArray.get(0).size() % 2 == 0) {
            placeColinGrid += cellSize / 2;
        }

        if (topBoard[0].length > topBoard.length) {
            placeColinGrid += cellSize * Math.abs(topBoard.length - topBoard[0].length) / 2;
            placeRowingrid -= cellSize * Math.abs(topBoard.length - topBoard[0].length) / 2;
        }

        if (topBoard[0].length < topBoard.length) {
            placeColinGrid -= cellSize * Math.abs(topBoard.length - topBoard[0].length) / 2;
            placeRowingrid += cellSize * Math.abs(topBoard.length - topBoard[0].length) / 2;
        }
        double placementX = (cInfo.getCanvasWidth() / 2) - ((cellSize * topBoard.length / 2))
                + (xmove * cellSize) - placeColinGrid;
        double placementY = (cInfo.getCanvasheight() / 2) - ((cellSize * topBoard[0].length / 2))
                + (ymove * cellSize) - placeRowingrid;

        gc.setFill(Color.GREEN);
        for (int row = 0; row < topBoard.length; row++) {
            for (int col = 0; col < topBoard[0].length; col++) {
                if (topBoard[row][col] == 1) {
                    gc.fillRect(col * cellSize + placementX, row * cellSize
                            + placementY, cellSize, cellSize);
                }
            }
        }
    }

    /**
     * Method that do not position the drawing centered on the canvas. Used for
     * drawing on the horizontal canvas in the editor. Gets the existing
     * <code>gridColor</code> and <code>cellColor</code> and uses these colors
     * to draw the horizontal canvas with the EditorController.
     *
     * @param gc the <code>GraphicsContext</code> to use
     * @param cInfo <code>CanvasInfo</code> object which contains canvas logic
     * @see controller.EditorController - the controller for the editor
     * (EditorController)
     */
    public void drawStr(GraphicsContext gc, CanvasInfo cInfo) {
        double cellSize = cInfo.getCellSize();
        gc.setStroke(gridColor);
        gc.setFill(cellColor);

        //Avoids drawing the outer arrays
        //Places cells acording to boardArray
        for (int x = 1; x < boardArray.size() - 1; x++) {
            for (int y = 1; y < boardArray.get(0).size() - 1; y++) {
                if (boardArray.get(x).get(y) == 1) {
                    gc.fillRect(y * cellSize, x * cellSize, cellSize, cellSize);
                    aliveCells += 1;
                } else {
                    gc.clearRect(y * cellSize, x * cellSize, cellSize, cellSize);
                }
            }
        }
    }
    
    
     /**
     * Alters the cell state of a specific cell, identified by <code>row</code>
     * and <code>col</code>. The <code>erase</code> determines if the alteration
     * should be 0 into 1 (<code>false</code>) or 1 into 0 (<code>true</code>).
     * If <code>row</code> or <code>col</code> is outside of the bounds of the
     * <code>boardArray</code>, rows or columns are added at the side of the
     * <code>boardArray</code> which is selected.
     *
     * @param row the row of the current cell
     * @param col the column of the current cell
     * @param erase erases (1 into 0) if <code>true</code> or draws (0 into 1)
     * if <code>false</code>
     * @param maxSize when the Board will no longer expand on mouseclick.
     */
    @Override
    public void setCellValue(int row, int col, boolean erase, int maxSize) {
        //Checks where row and col indexes are
        if ((col < boardArray.get(0).size() && col > - 1)
                && (row < boardArray.size() && row > - 1)) {
            if (erase) {
                boardArray.get(row).set(col, (byte) 0);
            } else if (boardArray.get(row).get(col) == 0) {
                //Sets new cellState
                boardArray.get(row).set(col, (byte) 1);
                aliveCells++;
            }

        } else {
            //If the indexses are outside of the boardarray's width or height
            if (Math.abs(row) + boardArray.size() < maxSize || Math.abs(col) + 
                    boardArray.get(0).size() < maxSize) {
                if (row >= boardArray.size()) {
                    //Heightens boardArray to fit the new index's placement.
                    addRows(row - boardArray.size() + 1, false);
                    row = boardArray.size() - 1;
                }
                if (row < 0) {
                    addRows(Math.abs(row), true);
                    row = 0;
                }

                if (col >= boardArray.get(0).size()) {
                    //Widens boardArray to fit the new index's placement.
                    addColumns(col - boardArray.get(0).size() + 1, false);
                    col = boardArray.get(0).size() - 1;
                }
                if (col < 0) {
                    addColumns(Math.abs(col), true);
                    col = 0;
                }
                if (boardArray.get(row).get(col) == 0) {
                    //Sets new cellState
                    boardArray.get(row).set(col, (byte) 1);
                    aliveCells++;
                }
            }
        }
        optimaliser = false;
    }

    /**
     * Checks if dimensions of the <code>boardArray</code> exceeds a certain
     * limit. If the provided limit (in this case, 2000) is exceeded, the outer
     * rows and columns are set to 0 to limit the dimensions of the
     * <code>boardArray</code> to the given constant (in this case, 2000).
     *
     * @see #boardArray
     */
    @Override
    public void sizeTest() {
        if (getWidth() >= 2000) {
            for (int i = 0; i < boardArray.size(); i++) {
                boardArray.get(i).set(0, (byte) 0);
                boardArray.get(i).set(boardArray.get(0).size() - 1, (byte) 0);
            }
            if (getHeight() >= 2000) {
                ArrayList<Byte> newRow = new ArrayList<>();
                for (Byte get : boardArray.get(0)) {
                    newRow.add((byte) 0);
                }
                boardArray.set(0, newRow);
                boardArray.set(boardArray.size() - 1, newRow);
            }
        }
    }

    /**
     * Sets the outer borders of the <code>boardArray</code> to 0 if the number
     * of alive cells exceeds a constant limit (set to 500 000 in this case).
     *
     * @see #boardArray
     */
    public void aliveCellsTest() {
        if (aliveCells > 500000) {
            for (int i = 0; i < boardArray.size(); i++) {
                boardArray.get(i).set(0, (byte) 0);
                boardArray.get(i).set(boardArray.get(0).size() - 1, (byte) 0);
            }

            ArrayList<Byte> newRow = new ArrayList<>();
            for (Byte get : boardArray.get(0)) {
                newRow.add((byte) 0);
            }
            boardArray.set(0, newRow);
            boardArray.set(boardArray.size() - 1, newRow);
        }
    }

    //--------------------------MOVE AND DRAG-------------------------------//
    /**
     * The <code>byte[][]</code> representing the pattern to place onto the
     * <code>boardArray</code>.
     */
    private byte[][] topBoard;

    /**
     * Gets the current <code>topBoard</code>.
     *
     * @return the <code>topBoard</code>
     */
    @Override
    public byte[][] getTopBoard() {
        return topBoard;
    }

    /**
     * Sets the given <code>topBoard</code> over the <code>boardArray</code>.
     * The <code>topBoard</code> given as an argument is placed into a temporary
     * <code>byte[][]</code> that is created without the zero-rows and
     * zero-columns of the provided <code>topBoard</code>. The temporary
     * <code>byte[][]</code> is then assigned to the <code>topBoard</code> and
     * then the <code>boardArray</code> is expanded in size in order to fit the
     * <code>topBoard</code> with method <code>autoFit</code>.
     *
     * @param topBoard the <code>byte[][]</code> to set
     */
    @Override
    public void setTopBoard(byte[][] topBoard) {

        byte[][] tempArray = new byte[topBoard.length - 2][topBoard[0].length - 2];

        for (int i = 0; i < tempArray.length; i++) {
            for (int j = 0; j < tempArray[0].length; j++) {
                tempArray[i][j] = topBoard[i + 1][j + 1];
            }
        }

        this.topBoard = tempArray;
        autoFit(tempArray);
        topExist = true;
    }

    /**
     * Determines the placement of the <code>byte[][]</code>. The
     * <code>xmove</code> determines the placement of the <code>byte[][]</code>
     * to place onto the <code>boardArray</code> along the X-axis.
     *
     * @param xmove the amount of movement along the X-axis
     */
    @Override
    public void setXmove(int xmove) {
        this.xmove += xmove;
    }

    /**
     * Determines the placement of the <code>byte[][]</code>. The
     * <code>ymove</code> determines the placement of the <code>byte[][]</code>
     * to place onto the <code>boardArray</code> along the Y-axis.
     *
     * @param ymove the amount of movement along the Y-axis
     */
    @Override
    public void setYmove(int ymove) {
        this.ymove += ymove;
    }

    /**
     * Gets the <code>topExist</code>. Returns <code>true</code> if there is a
     * <code>topBoard</code> to place onto the <code>boardArray</code> and
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> if <code>topBoard</code> exists and
     * <code>false</code> if <code>topBoard</code> does not exist
     */
    @Override
    public boolean topExist() {
        return topExist;
    }

    /**
     * Rotates the <code>topBoard</code>. The direction of the rotation is based
     * on the <code>boolean</code>, <code>left</code> and the rotation is
     * performed by creating a temporary <code>byte[][]</code> of equal size to
     * contain the rotated <code>byte[][]</code>.
     *
     * Source: http://stackoverflow.com/questions/42519/how-do-you-rotate-a-two-dimensional-array
     *
     * @param left the direction of the rotation, <code>true</code> if left
     */
    @Override
    public void rotateTopBoard(boolean left) {

        int width = topBoard.length;
        int height = topBoard[0].length;
        byte[][] rotated = new byte[height][width];

        for (int m = 0; m < height; ++m) {

            for (int n = 0; n < width; ++n) {
                if (left) {
                    //rotate topBoard array left
                    rotated[m][n] = topBoard[n][height - m - 1];
                } else {
                    //rotate topBoard array right
                    rotated[m][n] = topBoard[width - n - 1][m];
                }
            }
        }
        this.topBoard = rotated;
    }

    //--------------------------SETTERS AND GETTERS------------------------//
    /**
     * Sets a new <code>boardArray</code> onto the existing
     * <code>boardArray</code>.
     *
     * @param boardArray the boardArray to set
     * @see #boardArray - the existing <code>boardArray</code>
     */
    public void setBoardArray(ArrayList<ArrayList<Byte>> boardArray) {
        this.boardArray = boardArray;
    }

    /**
     * Gets the current <code>boardArray</code>. The <code>boardArray</code>
     * represents the board of the game, containing the cells.
     *
     * @return the boardArray of the <code>DynamicBoard</code>
     */
    public ArrayList<ArrayList<Byte>> getBoardArray() {
        return boardArray;
    }

    /**
     * Gets the current <code>patternArray</code>.
     *
     * @return the patternArray of the <code>DynamicBoard</code>
     */
    public static ArrayList<ArrayList<Byte>> getPatternArray() {

        return patternArray;
    }

    /**
     * Sets a <code>patternArray</code>. The <code>aPatternArray</code> is
     * assigned to the <code>patternArray</code> of the
     * <code>DynamicBoard</code>.
     *
     * @param aPatternArray the patternArray to set
     */
    public static void setPatternArray(ArrayList<ArrayList<Byte>> aPatternArray) {
        patternArray = aPatternArray;
    }

    /**
     * Sets a specific element of the <code>boardArray</code>. The element with
     * coordinates (<code>row</code>, <code>column</code>) is assigned the state
     * , <code>state</code>.
     *
     * @param state the state to assign to the element
     * @param row the row of the element
     * @param column the column of the element
     */
    public void setBoardArrayElement(byte state, int row, int column) {
        boardArray.get(row).set(column, (byte) state);
    }

    /**
     * Temporary method to check output versus drawing. Prints the
     * <code>boardArray</code> as a <code>String</code>-representation.
     *
     * @param array the <code>ArrayList</code> to print
     */
    public void arrayRep(ArrayList<ArrayList<Byte>> array) {
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < array.get(0).size(); j++) {
                System.out.print(boardArray.get(i).get(j) + " ");
            }
        }
    }

    /**
     * Creates a <code>String</code>-representation of the <code>array</code>
     * given as an argument.
     *
     * @param array the <code>ArrayList</code> to represent
     * @return the <code>String</code>-representation of the
     * <code>ArrayList</code>
     */
    public String arraytoString(ArrayList<ArrayList<Byte>> array) {
        String r = "";
        for (int i = 0; i < array.size(); i++) {
            for (int j = 0; j < array.get(0).size(); j++) {
                r += array.get(i).get(j);
            }
            r += "\n";
        }
        return r;
    }

    /**
     * The overridden <code>toString</code>-method of the
     * <code>DynamicBoard</code>-class. Creates a <code>String</code>
     * -representation of the current <code>boardArray</code>, without newlines.
     *
     * @return the <code>String</code>-representation of the
     * <code>boardArray</code>
     */
    @Override
    public String toString() {
        String sr = "";
        for (int row = 0; row < boardArray.size(); row++) {
            for (int column = 0; column < boardArray.get(0).size(); column++) {
                if (boardArray.get(row).get(column) == 1) {
                    sr = sr.concat("1");
                } else {
                    sr = sr.concat("0");
                }
            }
        }
        return sr;
    }

    @Override
    public int getSumOfAlive() {
        int sum = 0;
        int sumDelta = 0;

        for (int i = 0; i < boardArray.size(); i++) {
            for (int j = 0; j < boardArray.get(0).size(); j++) {
                if (boardArray.get(i).get(j) == 1) {
                    sumDelta = i + j;
                    sum += sumDelta;
                }

            }
        }
        return sum;
    }

    /**
     * Gets the <code>optimaliser</code>. Returns <code>true</code> if the
     * optimization is active.
     *
     * @return the <code>optimaliser</code>-value (<code>true</code> if active)
     */
    public boolean isOptimaliser() {
        return optimaliser;
    }

    /**
     * Gets the height of the <code>boardArray</code>. The height of the
     * <code>boardArray</code> is represented as the size of the
     * <code>boardArray</code>.
     *
     * @return the height of the <code>boardArray</code>
     */
    @Override
    public int getHeight() {
        return boardArray.size();
    }

    /**
     * Gets the width of the <code>boardArray</code>. The width of the
     * <code>boardArray</code> is represented as the size of the row at index 0
     * of the <code>boardArray</code>.
     *
     * @return the width of the <code>boardArray</code>
     */
    @Override
    public int getWidth() {
        return boardArray.get(0).size();
    }

    /**
     * Creates an empty 2d <code>ArrayList</code>. The <code>ArrayList</code>
     * has the dimensions provided by the <code>row</code> and the
     * <code>col</code>.
     *
     * @param row the number of rows
     * @param col the number of threadCol
     * @return the empty <code>ArrayList</code>
     */
    public static ArrayList<ArrayList<Byte>> create2Darray(int row, int col) {
        ArrayList<ArrayList<Byte>> arrayList = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            arrayList.add(new ArrayList<>());
            for (int j = 0; j < col; j++) {
                arrayList.get(i).add(j, (byte) 0);
            }
        }
        return arrayList;
    }

    /**
     * Converts a <code>staticBoard</code> into a <code>DynamicBoard</code>.
     * 
     * @param array to turn into ArrayList
     * @see StaticBoard
     * @return arrayList to use as <code>boardArray</code> field.
     */
    public static ArrayList<ArrayList<Byte>> ArrayListFromByteArray(byte[][] array) {
        ArrayList<ArrayList<Byte>> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            arrayList.add(new ArrayList<>());
            for (int j = 0; j < array[0].length; j++) {
                arrayList.get(i).add(j, array[i][j]);
            }
        }
        return arrayList;
    }
    
}
