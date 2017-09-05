/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.scene.control.TextInputDialog;
import model.BoardPack.Board;

/**
 * This class contains the logic which use to create statistic data to present the development
 * of the pattern on the game board in terms of statistical data.
 * 
 * @author N.Nanthawisit
 */
public class Statistics {

    private int[][] statisticsData;
    private double[] geoFactor;

    private int[] similarityData;
    public static int dupInterval = 0;

    /**
     * A method to calculate number of alive cells(row0), evolution in alive
     * cells(row1) and similarity measure(row2).
     *
     * source: http://stackoverflow.com/questions/11919009/using-javax-sound-sampled-
     * clip-to-play-loop-and-stop-mutiple-sounds-in-a-game
     * @param board a current board to be calculated
     * @param iterations an interval of time/iterations
     * @return 2D array contains alive cells, evolution and similarity measure
     * over a particular interval of time/iterations.
     *
     */
    public int[][] statistics(Board board, int iterations) {

        int[][] statData = new int[3][iterations + 1];
        int ft1 = 0, ft2 = 0, dt = 0, gt = 0;
        double alfa = 0.5, beta = 3.0, gamma = 0.25, compare = 0, simiMeasure = 0;
        geoFactor = new double[statData[0].length];

        for (int j = 0; j < statData[0].length; j++) {

            //Store alive cells in row 0
            statData[0][j] = board.getAliveCells();
            ft1 = statData[0][j];

            board.setNextGeneration();
            ft2 = board.getAliveCells();

            //Store evolutions in cells in row 1
            dt = ft2 - ft1;
            if (j == 0) {
                statData[1][j] = 0;
            } else {
                statData[1][j] = dt;
            }

            //Store similarity measure in row 2
            gt = board.getSumOfAlive();
            geoFactor[j] = (alfa * ft1) + (ft2 * beta) + (gamma * gt);

            if (j == geoFactor.length - 1) {
                for (int i = 0; i < geoFactor.length; i++) {
                    compare = Math.min(geoFactor[i], geoFactor[j - i]) / Math.max(geoFactor[i], geoFactor[j - i]);
                    simiMeasure = floor(compare * 100);
                    statData[2][i] = (int) simiMeasure;
                }

            }
        }
        return statData;
    }

    /**
     * Draw line chart to illustrate number of alive cells, evolution in alive
     * cells and similarity measure over a particular interval of
     * time/iterations.
     *
     * @param board a current board
     * @see #statistics(model.BoardPack.Board, int)
     * @return statisticsData the array that contains the data about alive
     * cells, evolution in cells and similarity measure.
     */
    public int[][] createStatistics(Board board) {

        TextInputDialog inputDialog = new TextInputDialog("Type in numbers of iterations");
        inputDialog.setTitle("Input Dialog");
        inputDialog.setHeaderText(null);
        inputDialog.setContentText("Number of iterations");

        Optional<String> result = inputDialog.showAndWait();
        statisticsData = statistics(board, Integer.parseInt(result.get()));
        return statisticsData;

    }

    /**
     * Method for finding duplicate numbers in <code>similarity</code> data.
     *
     * @param similarity the array that store similarity data.
     * @see #indexOfDuplicateNumber(int)
     */
    public void duplicateNumber(int[] similarity) {

        this.similarityData = similarity;
        Set<Integer> totalNumbers = new HashSet<>();
        Set<Integer> duplicateNumbers = new LinkedHashSet<>();

        for (int i : similarityData) {

            if (totalNumbers.contains(i)) {
                duplicateNumbers.add(i);
            }

            totalNumbers.add(i);
        }
        indexOfDuplicateNumber(similarityData[0]);
    }

    /**
     * Method for finding index of the duplicate numbers.
     *
     * @param duplicateNumbers the number in the array that repeat more than one
     * time.
     * @see #duplicateNumber(int[])
     */
    public void indexOfDuplicateNumber(int duplicateNumbers) {

        List<Integer> indexs = new ArrayList<Integer>();

        for (int p = 0; p < similarityData.length; p++) {
            if (similarityData[p] == duplicateNumbers) {
                indexs.add(p);
            }
        }

        dupInterval = indexs.get(1);
    }

    /**
     *
     * Method for making a deep copy of <code>Board</code> object. Objects are
     * first serialized and then deserialized.
     *
     * source: http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
     * @param board a board to be copy
     * @return a copied board
     * @throws IOException - to capture exceptions related to the error loading
     * file and file loading.
     * (https://docs.oracle.com/javase/7/docs/api/java/io/IOException.html)
     * @throws ClassNotFoundException - exception throw if it doesn't found the
     * requested class in class path.
     */
    public Board copy(Board board) throws IOException, ClassNotFoundException {

        Board boardObj = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(board);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        boardObj = (Board) in.readObject();

        return boardObj;

    }

}
