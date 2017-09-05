/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BoardPack.Board;
import model.BoardPack.ConcurrentGeneration;
import model.BoardPack.DynamicBoard;

/**
 * A class that is handling the usage of threads. The creation of the
 * administrative threads is performed in <code>createSeparateThreads</code>
 * while the threads that handles the logic related to the <code>Board</code>
 * are created in the method <code>createUpdateNeighborThreads</code> and method
 * <code>createSetNextGenThreads</code>.
 *
 * @author M.S.Olsen, T.Dahll
 */
public class WorkHive {

    static List<Thread> ants = new ArrayList<>();
    private static Board board;
    private static ConcurrentGeneration concur;

    /**
     * No-parameter constructor. Initializes a <code>workHive</code>-object.
     */
    public WorkHive() {
    }

    /**
     * Creates the threads that handle updating the board of neighbors. The
     * number of threads created is multiplied by two, creating two times as
     * many threads as there are available processors. The board is divided in
     * size between the threads and each thread is provided with
     * <code>colPlace</code> which corresponds to the base of the index and
     * <code>colWidth</code> which corresponds to the offset of the index. After
     * the threads are created, their <code>start</code>-methods are called
     * through method <code>orderAnts</code> where the threads with an odd
     * numbered index starts first and then their <code>join</code>-methods are
     * called before the same process is performed with the even numbered index
     * threads.
     *
     * @param numberOfThreads the number of available processors
     * @see #orderAnts()
     */
    public static void createUpdateNeighborThreads(int numberOfThreads) {
        ((DynamicBoard) board).beforeSetGeneration();

        int threads = numberOfThreads * 2;

        ants.clear();
        //Splits the asignment for the different threads. Which columns they're each
        //responsible for in the Board.
        for (int i = 1; i <= threads; i++) {
            final int num = i;
            ants.add(new Thread(() -> {

                int width = board.getWidth();
                int colWidth;
                int lastCol;
                int colPlace = 0;
                if (width % threads != 0) {
                    colWidth = Math.floorDiv(width, threads);
                    lastCol = colWidth + width - (colWidth * threads);

                    if (num == threads) {
                        colPlace = colWidth * (num - 1);
                        colWidth = lastCol;
                    } else {
                        colPlace = colWidth * (num - 1);
                    }

                } else {
                    colWidth = width / threads;
                    colPlace = colWidth * (num - 1);
                }

                concur.updateConcurrentNeighbourBoard(colWidth, colPlace);

            }));
        }

        try {
            orderAnts();
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkHive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates the threads that handle setting the next generation for the
     * board. The number of threads created is multiplied by two, creating two
     * times as many threads as there are available processors. The board is
     * divided in size between the threads and each thread is provided with
     * <code>colPlace</code> which corresponds to the base of the index and
     * <code>colWidth</code> which corresponds to the offset of the index. After
     * the threads are created, their <code>start</code>-methods are called
     * through method <code>orderAnts</code> where the threads with an odd
     * numbered index starts first and then their <code>join</code>-methods are
     * called before the same process is performed with the even numbered index
     * threads.
     *
     * @param numberOfThreads the number of available processors
     * @see #orderAnts()
     */
    public static void createSetNextGenThreads(int numberOfThreads) {
        int threads = numberOfThreads * 2;

        ants.clear();
        
        //Splits the asignment for the different threads. Which columns they're each
        //responsible for in the Board.
                for (int i = 1; i <= threads; i++) {
            final int num = i;
            ants.add(new Thread(() -> {

                int width = board.getWidth();
                int colWidth;
                int lastCol;
                int colPlace = 0;
                if (width % threads != 0) {
                    colWidth = Math.floorDiv(width, threads);
                    lastCol = colWidth + width - (colWidth * threads);

                    if (num == threads) {
                        colPlace = colWidth * (num - 1);
                        colWidth = lastCol;
                    } else {
                        colPlace = colWidth * (num - 1);
                    }

                } else {
                    colWidth = width / threads;
                    colPlace = colWidth * (num - 1);
                }

                concur.setNextGenerationConcurrent(colPlace, colWidth);
            }));
        }

        try {
            orderAnts();
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkHive.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Prepares the threads that handles updating the board of neighbors and
     * setting the next generation. The process of creating the threads is
     * performed by two runnable tasks, <code>FirstThreadTask</code> that
     * creates the threads that updates the board of neighbors and the
     * <code>SecondThreadTask</code> that creates the threads that sets the next
     * generation. The first task is then started and then joined before the
     * second task is created and then joined.
     *
     * @param numberOfThreads the number of available processors
     */
    public static void createSeparateThreads(int numberOfThreads) {
        Runnable FirstThreadTask = () -> {
            createUpdateNeighborThreads(numberOfThreads);
        };
        Runnable SecondThreadTask = () -> {
            createSetNextGenThreads(numberOfThreads);
        };

        Thread firstThread = new Thread(FirstThreadTask);
        firstThread.start();
        try {
            firstThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkHive.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Done updating neighbour board, setting next generation
        firstThread = new Thread(SecondThreadTask);
        firstThread.start();

        try {
            firstThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkHive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Starting every second thread, waiting for every second thread to join
     * before starting the remaining threads and waiting for them to join before
     * returning. Since the with of the <code>Board</code> have been split into a number of thinner 
     * column this means that first the odd numbered columns will be checked
     * then the even numbered.
     *
     * @see #ants the <code>ArrayList</code> containing the threads
     * @throws InterruptedException if one of the threads are interrupted
     */
    public static void orderAnts() throws InterruptedException {
        for (int i = 0; i < ants.size(); i = i + 2) {
            ants.get(i).start();
        }

        for (int j = 0; j < ants.size(); j = j + 2) {
            ants.get(j).join();
        }

        for (int i = 1; i < ants.size(); i = i + 2) {
            ants.get(i).start();
        }

        for (int j = 1; j < ants.size(); j = j + 2) {
            ants.get(j).join();
        }
    }

    /**
     * Sets the <code>Board</code>-object on which to perform concurrent methods
     * on.
     *
     * @param board the board to set
     */
    public static void setBoard(Board board) {
        WorkHive.board = board;
        concur = new ConcurrentGeneration((DynamicBoard) board);
    }

}
