/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model.BoardPack;

import static model.BoardPack.Board.aliveCells;

/**
 * A class that contains concurrent version of methods used to set the next
 * generation. These methods are adapted in order to utilize concurrency by
 * dividing the main board and the board containing information about the
 * neighbors into partitions of the original board.
 * 
 * @author M.S.Olsen, T.Dahll
 */
public class ConcurrentGeneration {

    DynamicBoard board;
    
    /**
     * A constructor that takes a <code>DynamicBoard</code> as an argument.
     *
     * @param board the board to place
     */
    public ConcurrentGeneration(DynamicBoard board) {
        this.board = board;
    }

    /**
     * synchronised version of <code>updateConcurrentNeighbourBoard</code>.
     * Concurrent threads may access the same element in the
     * <code>boardArray</code> when counting neighbours with the
     * <code>countNeighbours</code>-method if the board is sufficiently small.
     *
     * @param colWidth the width of the column
     * @param colPlace the start index
     * @see #countConcurrentNeighbhours(int, int)
     */
    public void updateConcurrentNeighbourBoard(int colWidth, int colPlace) {

        for (int row = 0; row < board.getBoardArray().size(); row++) {
            for (int col = colPlace; col < colPlace + colWidth; col++) {
                if (board.getBoardArray().get(row).get(col) == 1) {

                    countConcurrentNeighbhours(row, col);
                }
            }
        }

    }

    /**
     * Sets the next generation in a selected partition. The partition is
     * defined by the (base) <code>colPlace</code> and the (offset)
     * <code>colWidth</code>. The new cell state is determined by the
     * <code>Rule.rleRules</code> based on the previous cell state and the
     * number of neighbors.
     *
     * @param colPlace the starting index of the partition
     * @param colWidth the offset to iterate over
     */
    public void setNextGenerationConcurrent(int colPlace, int colWidth) {
        int aliveCellsAppend = 0;
        for (int i = 0; i < board.getBoardArray().size(); i++) {
            for (int j = colPlace; j < colPlace + colWidth; j++) {
                int cellState = board.getBoardArray().get(i).get(j);
                int numNeighbours = board.getNeighBoard()[i][j];
                if (numNeighbours != 0 || cellState != 0) {
                    byte newVal = Rule.rleRules(cellState, numNeighbours);
                    board.getBoardArray().get(i).set(j, newVal);

                    if (newVal == 1) {
                        aliveCellsAppend++;
                    }
                }
            }
        }
        synchronized (this) {
            aliveCells += aliveCellsAppend;
        }
    }

    /**
     * Counts the neighbors of the provided cell. The <code>row</code> and
     * <code>col</code> of the cell are provided as arguments and used in order
     * to update the number of neighbors.
     *
     * @param row the row-coordinate of the cell
     * @param col the column-coordinate of the cell
     */
    public void countConcurrentNeighbhours(int row, int col) {
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                int rRow = r + row;
                int cCol = c + col;

                if (c != 0 && cCol != -1) {
                    board.getNeighBoard()[rRow][cCol]++;
                }

                if (r != 0 && c == 0) {
                    board.getNeighBoard()[rRow][cCol]++;
                }
            }
        }
    }
}
