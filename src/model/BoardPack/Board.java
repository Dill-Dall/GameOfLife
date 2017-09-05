/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model.BoardPack;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.CanvasInfo;

/**
 * The Board class uses and creates byte arrays of 1's and 0's (boardArray).
 * Then a neighBour array is created (neighBoard) from the boardArray which
 * counts how many 1's each cell has as a neighbor. Then the method
 * nextGeneration can be run, which sends boardArray and the neighBoard array
 * through the Rules.rleRules() method from the rule class, which changes each 1
 * and 0 to a different value according to the applyRuleset in the Rule class.
 * Then the boardArray is run through the draw method to be placed on the canvas
 * of the FXML-file.
 *
 * @author T.Dahll, M.S.Olsen
 * @see model.BoardPack.Rule
 * @see model.BoardPack.DynamicBoard
 * @see model.BoardPack.StaticBoard
 */
public abstract class Board {

    protected static Color cellColor = Color.BLACK;
    protected static Color gridColor = Color.BLACK;
    protected static Color backGroundColor = Color.WHITE;
    protected static boolean grid = false;
    protected byte[][] neighBoard;
    protected int iterations = 0;
    protected static int aliveCells = 0;
    protected int numColumns, numRows;
    protected boolean optimaliser = false;

    //-------------------------Generation Logic-------------------------------//
    /**
     * Generation logic consists mainly of three parts(methods). Firstly the
     * <code>updateNeighBoard</code> which checks the BoardArray for alive cells
     * and then for each alive cells uses the <code>countNeighBhours</code>
     * method to add +1 to each of neighbouring cells in the
     * <code>neighBoard</code>, <code>byte[][]</code>. Then then the
     * <code>setNewGenValues</code>() method checks if cells are alive or dead
     * and passes the status and its neighbour value to Rule.rleRules and gets
     * back the new status of the cells. Which is now the new values of the
     * BoardArray.
     *
     * @see #setNewGenValues()
     * @see #countNeighbhours(int row, int col)
     * @see #neighBoard
     * @see Rule#rleRules(int cellState, int numOfNeighbhours)
     */
    public void setNextGeneration() {
        beforeSetGeneration();
        updateNeighbourBoard();
        setNewGenValues();
    }

    /**
     * Prepares the Board for the generation logic. This is done by: resetting
     * the <code>aliveCells</code>, creating an empty board to contain the
     * neighbors all the cells with <code>createNeighBoard</code> and then
     * increasing the number of <code>iterations</code>.
     *
     * @see #createNeighBoard()
     */
    public void beforeSetGeneration() {
        aliveCells = 0;
        createNeighBoard();
        iterations++;
    }

    /**
     * Creates an array containing the number of neighbors for each cell. The
     * array containing information about the number of neighbors is used to
     * calculate the next generation of the main board
     * (<code>boardArray</code>).
     */
    public abstract void updateNeighbourBoard();

    /**
     * Changes the current <code>boardArray</code> to its nextGenaration by
     * placing new values into the <code>boardArray</code>. Comparing the
     * <code>neighBoard</code> with its corresponding <code>boarArray</code>
     * cell state. Then using the Rules.rleRules() to determine the new state of
     * the cells.
     *
     * @see Rule#rleRules(int, int)
     */
    public abstract void setNewGenValues();

    /**
     * Adds +1 to all cells in the neighboring cell of an alive cell. This in
     * the end determines how many neighboring alive cell each cell has.
     * 
     * @param row index
     * @param col index
     */
    public abstract void countNeighbhours(int row, int col);

    /**
     * Gets the <code>byte[][]</code> containing the number of neighbors to all
     * the cells.
     *
     * @return neighBoard the <code>byte[][]</code> with the number of neighbors
     * of the corresponding cells
     */
    public byte[][] getNeighBoard() {
        return neighBoard;
    }

    /**
     * Creates a new blank <code>neighBoard</code> with the same height and 
     * width as the main board. This is later updated with the number of 
     * neighbors to each cell.
     */
    public abstract void createNeighBoard();

    /**
     * Sets the value for the <code>optimaliser</code>.
     *
     * @param optimaliser the <code>boolean</code> to assign (<code>true</code>
     * if active)
     */
    public void setOptimaliser(boolean optimaliser) {
        this.optimaliser = optimaliser;
    }

    ;
    
     /**
     * Checks if dimensions of the <code>boardArray</code> exceeds a certain 
     * limit. If the provided limit (in this case, 2000) is exceeded, the outer 
     * rows and columns are set to 0 to limit the dimensions of the 
     * <code>boardArray</code> to the given constant (in this case, 2000).
     * 
     * @see DynamicBoard#boardArray
     */
    public abstract void sizeTest();
    //----------------------------Rows and Columns----------------------------//

    /**
     * Gets the number of rows in the main board.
     *
     * @return the number of rows in the <code>boardArray</code>
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Sets the number of rows in the main board.
     *
     * @param aNumRows the number of rows to set
     */
    public void setNumRows(int aNumRows) {
        numRows = aNumRows;
    }

    /**
     * Gets the number of columns in the main board.
     *
     * @return the number of columns in the <code>boardArray</code>
     */
    public int getNumColumns() {
        return numColumns;
    }

    /**
     * Sets the number of columns in the main board.
     *
     * @param aNumColumns the number of columns to set
     */
    public void setNumColumns(int aNumColumns) {
        numColumns = aNumColumns;
    }

    //--------------------------------Alive and Iterated----------------------//
    /**
     * Sums the indices of the cells that are alive (==1). The sum of these
     * indices is returned to the caller.
     *
     * @return the sum of indices of alive cells
     */
    public abstract int getSumOfAlive();

    /**
     * Gets the number of alive cells. This value is stored in the variable
     * <code>aliveCells</code>.
     *
     * @return the number of alive cells
     */
    public int getAliveCells() {
        return aliveCells;
    }

    /**
     * Resets the "counter" for alive cells.
     *
     * @see #aliveCells
     */
    public void reset_aliveCells() {
        aliveCells = 0;
    }

    /**
     * Sets the value of <code>aliveCells</code>.
     *
     * @param aAliveCells the number of alive cells
     */
    protected static void setAliveCells(int aAliveCells) {
        aliveCells = aAliveCells;
    }

    /**
     * Resets the "counter" for the number of iterations performed.
     *
     * @see #iterations
     */
    public void reset_iterations() {
        iterations = 0;
    }

    /**
     * Gets the number of iterations performed. This value is stored in the
     * variable <code>iterations</code>.
     *
     * @return the number of iterations performed
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Sets the number of <code>iterations</code>.
     *
     * @param aIterations the number of iterations to set
     */
    public void setIterations(int aIterations) {
        this.iterations = aIterations;
    }

    //-----------------------------DRAW LOGIC---------------------------------//
    /**
     * Draws the <code>boardArray</code> onto the canvas and places the drawing
     * at the center of the canvas.
     *
     * @param gc - receives the GraphicsContext object from the controller
     * @param cInfo CanvasInfo
     */
    public abstract void draw(GraphicsContext gc, CanvasInfo cInfo);

    /**
     * Gets the current <code>backGroundColor</code>.
     *
     * @return the background color
     */
    public static Color getBackGroundColor() {
        return backGroundColor;
    }

    /**
     * Sets the background color of the canvas.
     *
     * @param aBackGroundColor the background color to set
     */
    public void setBackGroundColor(Color aBackGroundColor) {
        Board.backGroundColor = aBackGroundColor;
    }

    /**
     * Gets the current color of the grid.
     *
     * @return the current grid color
     */
    public static Color getGridColor() {
        return gridColor;
    }

    /**
     * Sets the color of the cells to the provided cell color,
     * <code>value</code>.
     *
     * @param value the new color of the cells
     */
    public static void setCellColor(Color value) {
        cellColor = value;
    }

    /**
     * Gets the current <code>cellColor</code>.
     *
     * @return the current cell color
     */
    public static Color getCellColor() {
        return cellColor;
    }

    /**
     * Sets a grid color. The provided <code>aGridColor</code> is assigned to
     * the variable <code>gridColor</code>.
     *
     * @param aGridColor the grid color to set
     */
    public static void setGridColor(Color aGridColor) {
        gridColor = aGridColor;
    }

    /**
     * Returns true if <code>grid</code> is activated and to be drawn.
     *
     * @return the <code>true</code> if <code>grid</code> is active
     */
    protected boolean isGrid() {
        return grid;
    }

    /**
     * Sets a <code>boolean</code> to the variable <code>grid</code>.
     *
     * @param aGrid a <code>boolean</code> deciding if a grid should be drawn
     */
    public void setGrid(boolean aGrid) {
        grid = aGrid;
    }

    //------------------------------------------------------------------------//
    /**
     * Adds a new <code>byte[][]</code> to the board. The <code>array</code> is 
     * added onto the existing <code>boardArray</code>.
     * 
     * @param array the <code>byte[][]</code> to add
     */
    public abstract void addArrayToBoard(byte[][] array);

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
    public abstract void setCellValue(int row, int col, boolean erase, int maxSize);

    /**
     * Used to "compress" the board to fit to where there are alive cells.
     *
     * @return an array which describes outer edges of the board where there a
     * alive cells.
     */
    public abstract int[] getBoundingBox();

    /**
     * Gets the height of the <code>boardArray</code>.
     *
     * @return height of <code>boardArray</code>
     */
    public abstract int getHeight();

    /**
     * Gets the width of the <code>boardArray</code>.
     *
     * @return width of <code>boardArray</code>
     */
    public abstract int getWidth();

    /**
     * Extracts the pattern in the bounding box and returns a
     * <code>String</code>-representation of it.
     *
     * @return a <code>String</code> with the the "compressed board"
     */
    public abstract String getBoundingBoxPattern();

    //-------------------MOVE PATTERN LOGIC---------------------------------//
    /*
    01.05.17
    As of now these methods are only implemented on the main controller and with 
    the DynamicBoard sub-class.
     */
    /**
     * Determines if there exists a pattern/board to place onto the existing
     * <code>boardArray</code>.
     *
     * @return a boolean which tells if the <code>topBoard</code> exist
     */
    public abstract boolean topExist();

    /**
     * Determines the movement of the topBoard along the X-axis.
     *
     * @param i sets which direction and amount the board is set to move
     */
    public abstract void setXmove(int i);

    /**
     * Determines the movement of the topBoard along the Y-axis.
     *
     * @param i sets which direction and amount the board is set to move
     */
    public abstract void setYmove(int i);

    /**
     * The <code>topBoard</code> is drawn in green and is drawn again for each
     * movement until it is placed onto the <code>boardArray</code>.
     *
     * @param gc <code>GraphicContext</code> of <code>canvas</code>
     * @param cInfo <code>canvasInfo</code> of <code>canvas</code>
     * @see #addArrayToBoard(byte[][])
     */
    public abstract void drawTopBoard(GraphicsContext gc, CanvasInfo cInfo);

    /**
     * Rotates the board to be placed onto the <code>boardArray</code> around
     * its center.
     *
     * @param left boolean determines if the rotation is to the left,
     * <code>true</code> or to the right, <code>false</code> right
     */
    public abstract void rotateTopBoard(boolean left);

    /**
     * Gets the current <code>topBoard</code>.
     *
     * @return the <code>topBoard</code> to be placed
     */
    public abstract byte[][] getTopBoard();

    /**
     * Adds another board on top of the existing <code>boardArray</code>.
     *
     * @param topBoard <code>byte[][]</code>-array containing the new board
     */
    public abstract void setTopBoard(byte[][] topBoard);

    /**
     * Makes sure that the <code>boardArray</code> is expanded to fit the
     * provided <code>topBoard</code>.
     *
     * @param topBoard new array to place onto the <code> boardArray</code>
     */
    public abstract void autoFit(byte[][] topBoard);
//----------------------------------------------------------------------------//

}
