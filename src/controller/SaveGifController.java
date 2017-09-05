/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BoardPack.DynamicBoard;
import model.BoardPack.StaticBoard;
import model.Gif;
import model.Statistics;

/**
 * FXML Controller class controlling the creation of a gif from chosen
 * <code>Board</code> and <code>ruleSet</code>.
 *
 * @author tadah
 */
public class SaveGifController extends TopControl implements Initializable {

    @FXML
    Button saveButton;
    @FXML
    ColorPicker backgroundColor, cellColor;
    @FXML
    TextField iterationField;
    @FXML
    Slider speedSlider, iterationSlider;
    @FXML
    Label fpsLabel, iterationLabel;
    @FXML
    ChoiceBox resSize;
    @FXML
    Tooltip speedTooltip, iterationsTooltip;

    private Gif gif;
    private StaticBoard board;
    private int numOfIterations;
    private boolean check = false;

    ObservableList<String> resOptions = FXCollections.observableArrayList("640X640",
            "800X800", "1024X1024",
            "1280X1280", "1360X1360"
    );

    /**
     * initialises the controller class.
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cellColor.setValue(Color.BLACK);
        backgroundColor.setValue(Color.WHITE);
        resSize.setValue("640X640");
        fpsLabel.setText("60");
        speedSlider.setValue(60);
        iterationSlider.setValue(Statistics.dupInterval);
        iterationLabel.setText(Integer.toString(Statistics.dupInterval));
        numOfIterations = (int) iterationSlider.getValue();
        sliderListeners();
        sizeBoxListener();
    }

    /**
     * Sets the initial <code>DynamicBoard</code> for use in the .gif
     *
     * @param board is converted into a <code>StaticBoard</code>.
     */
    public void setInit(DynamicBoard board) {
        this.gif = new Gif();
        this.board = new StaticBoard(StaticBoard.listToArray(board.getBoardArray()));
    }

    /**
     * Picks javafx color and converts it into java awt color for use as cell
     * color.
     *
     * @param ce ActionEvent
     */
    @FXML
    public void pickCellColor(ActionEvent ce) {
        //Converts fx color in to fx color
        Color fxClr = cellColor.getValue();
        java.awt.Color awtClr = new java.awt.Color((float) fxClr.getRed(), (float) fxClr.getGreen(),
                (float) fxClr.getBlue(), (float) fxClr.getOpacity());
        gif.setCellColor(awtClr);
    }

    /**
     * Picks javafx color and converts it into java awt color for use as
     * backgroundColor.
     *
     * @param ce ActionEvent
     */
    @FXML
    public void pickBackgroundColor(ActionEvent ce) {
        //Converts fx color in to fx color
        Color fxClr = backgroundColor.getValue();
        java.awt.Color awtClr = new java.awt.Color((float) fxClr.getRed(), (float) fxClr.getGreen(),
                (float) fxClr.getBlue(), (float) fxClr.getOpacity());
        gif.setBackGroundClr(awtClr);
    }

    /**
     * Listener for <code>speedSlider</code> and <code>iterationSlider</code>.
     */
    public void sliderListeners() {
        
        speedSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            gif.setTimePerMilliSecond((int) ((100 - newValue.doubleValue()) / 60 * 1000));
            fpsLabel.setText(Integer.toString(newValue.intValue()));
        }));
        
        iterationSlider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            //Sets how many iterations there willo be.
            numOfIterations = newValue.intValue();
            iterationLabel.setText(Integer.toString(newValue.intValue()));

            //Advises the user if to high iteration is selected.
            if ((numOfIterations > 400) && check == false) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Be advised");
                alert.setHeaderText(null);
                alert.setContentText("If number of iterations exceed 400, serious lag or error may occur.");
                alert.showAndWait();
                check = true;
            }
        }));
    }

    /**
     * Selection of .gif sizes. Needed since a big boardArray needs
     * corresponding .gif sizes when the cell size become small enough.
     */
    public void sizeBoxListener() {

        resSize.getItems().addAll(resOptions);
        resSize.valueProperty().addListener((ObservableValue observable, Object oldVal, Object newVal) -> {
            String sizeSelect = newVal.toString();

            switch (sizeSelect) {
                case ("640X640"):
                    gif.setWidth(640);
                    gif.setHeight(640);
                    break;
                case ("800X800"):
                    gif.setWidth(800);
                    gif.setHeight(800);
                    break;
                case ("1024X1024"):
                    gif.setWidth(1024);
                    gif.setHeight(1024);
                    break;
                case ("1280X1280"):
                    gif.setWidth(1280);
                    gif.setHeight(1280);
                    break;
                case ("1360X1360"):
                    gif.setWidth(1360);
                    gif.setHeight(1360);
                    break;
            }
            //Checks if size is proportional to <code>boardArray</code>.
            sizeCheck();
        });
    }

    /**
     * Method called on Button action. Sets location of new file and calls the
     * <code>boardToGif</code>-method. And then creates a .gif file on that
     * location. Further implementation should be to add a progress bar to the
     * process.
     *
     * @see model.Gif
     */
    @FXML
    public void saveGif() {

        Stage stage = (Stage) saveButton.getScene().getWindow();
        FileChooser fc = new FileChooser();

        //Set file format to ".gif"
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("gif files (*.gif)", "*.gif");
        fc.getExtensionFilters().add(extFilter);
        fc.setTitle("Save gif");
        File file = fc.showSaveDialog(stage);

        //Returns to view if cancel is selected
        try {
            file.canExecute();
        } catch (Exception e) {
            return;
        }
        String fPath = file.getPath();
        fPath = fPath.replace("\\", "/");
        gif.setPath(fPath);

        beforeGifMessage();
        //Further development progressbar. Handle multithreaded javafx elements.

        /*Starts a thread which does the model.Gif.BoardToGif() which can take 
        some time
        Depending on boardArray size and numOfIterations.*/
        Runnable MyRunnableTaskB = () -> {
            try {
                gif.boardToGif(board, numOfIterations);
            } catch (IOException e) {
                Alert errorDialog = new Alert(Alert.AlertType.ERROR);
                errorDialog.setTitle("Error");
                errorDialog.setHeaderText("Error related to creating gif file");
                errorDialog.showAndWait();
            }
        };

        Thread myGifThread = new Thread(MyRunnableTaskB);
        myGifThread.start();
        stage.close();
    }

    /**
     * Checks the size of the board to create into a gif, since it needs to be
     * adjusted according to Board size.
     */
    public void sizeCheck() {
        if ((board.getHeight() > gif.getHeight())
                || board.getWidth() > gif.getWidth()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Be advised");
            alert.setHeaderText(null);
            alert.setContentText("If the sizes of the Game of Life Board are larger than the gif sizes."
                    + " the cells may be invisible, because the outer cells may exceed the gif size.");
            alert.showAndWait();
        }
    }
    
    /**
     * Message to show before .gif file begins to run.
     */
    public void beforeGifMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saving.gif file");
        alert.setHeaderText(null);
        alert.setContentText("The gif creation will run in the background.");
        alert.showAndWait();
    }
}
