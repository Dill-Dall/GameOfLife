/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package utilities;

import java.io.Serializable;

/**
 * A class with the purpose of having an object with two variables. Used to
 * store information about "active" cells, that is, cells having cell state 1 or
 * cells that have neighbors.
 *
 * @see model.BoardPack.DynamicBoard where IndexPairs are used to index the
 * address of cells
 * 
 * @author T.Dahll
 */
public class IndexPair implements Serializable {

    /**
     * The row-value for the cell.
     */
    private final int row;

    /**
     * The column-value for the cell.
     */
    private final int col;

    /**
     * Constructor taking the <code>row</code> and the <code>col</code> as
     * arguments. This represents the coordinates of the cell.
     *
     * @param row the row value of the cell
     * @param col the column value of the cell
     */
    public IndexPair(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the <code>row</code> for the <code>indexPair</code>.
     *
     * @return the row-value for the cell
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the <code>col</code> for the <code>indexPair</code>.
     *
     * @return the column-value for the cell
     */
    public int getCol() {
        return col;
    }

    /**
     * A <code>String</code>-representation of the <code>indexPair</code>.
     *
     * @return the <code>String</code> that represents the
     * <code>indexPair</code>
     */
    @Override
    public String toString() {
        return "row " + row + " col " + col;
    }
}
