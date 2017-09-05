/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import model.BoardPack.Board;
import model.BoardPack.DynamicBoard;
import model.Statistics;

/**
 *
 * FXML Controller class for functionality related to statistics.
 * 
 * @author N.Nanthawisit
 */
public class StatisticsController { 

    @FXML
    LineChart<Number, Number> lineChart;
    @FXML
    Button loadStatistics, resetStatistics;

    private final Statistics statisticsObj = new Statistics();
    private DynamicBoard board;
    private XYChart.Series aliveCells, evolution, similarity;
    private int[][] storeData;
    private int[] similarityData;

    /**
     * Draw <code>lineChart</code> to illustrate number of
     * <code>aliveCells</code>, <code>evolution</code> in alive cells and
     * <code>similarity</code> measure over a particular interval of
     * time/iterations.
     *
     * @see Statistics.createStatistics()
     * @see #lineChart a chart which displays information as a series of data.
     * @see Statistics#duplicateNumber()
     */
    @FXML
    private void drawChart() {

        //defining number alive cells with data 
        aliveCells = new XYChart.Series<>();
        aliveCells.setName("Alive cells");

        //defining evolution in alive cells with data  
        evolution = new XYChart.Series<>();
        evolution.setName("Evolution in alive cells            ");

        //defining similarity measure   
        similarity = new XYChart.Series<>();
        similarity.setName("Similarity Measure");

        storeData = statisticsObj.createStatistics(board);
        similarityData = new int[storeData[0].length];

        //creating the chart
        evolution.getData().add(new XYChart.Data(0, 0));

        for (int j = 0; j < storeData[0].length; j++) {
            aliveCells.getData().add(new XYChart.Data(j, storeData[0][j]));
            evolution.getData().add(new XYChart.Data(j, storeData[1][j]));
            similarity.getData().add(new XYChart.Data(j, storeData[2][j]));
            similarityData[j] = storeData[2][j];
        }

        lineChart.getData().addAll(aliveCells, evolution, similarity);
        loadStatistics.setDisable(true);
        resetStatistics.setDisable(false);
        statisticsObj.duplicateNumber(similarityData);
    }

    /**
     * Resets statistics data/<code>storeData</code> and clear
     * <code>lineChart</code>.
     *
     * @see #storeData the array that contains the data about alive cells,
     * evolution in cells and similarity measure
     * @see #lineChart a chart which displays information as a series of data
     * @see #loadStatistics load statistics button
     */
    @FXML
    private void resetStatistics() {
        storeData = null;
        lineChart.getData().clear();
        loadStatistics.setDisable(false);
    }

    /**
     * Set the <code>Board</code> that will be use to calculate the statistics
     * data.
     *
     * @param board a current board to be use in Statistics class.
     * @see #resetStatistics the reset button.
     * @throws java.io.IOException exception
     * @throws java.lang.ClassNotFoundException exception
     * @see #resetStatistics the reset button.
     */
    public void setBoard(Board board) throws IOException, ClassNotFoundException {
        this.board = (DynamicBoard) statisticsObj.copy(board);
        resetStatistics.setDisable(true);
    }

    /**
     * Gets the current <code>board</code>.
     *
     * @return the current <code>board</code>
     */
    public Board getBoard() {
        return board;
    }

}
