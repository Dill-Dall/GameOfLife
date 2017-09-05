/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package controller;

import model.*;
import model.BoardPack.DynamicBoard;
import model.BoardPack.Board;
import model.FileHandler;
import model.PatternFormatException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import utilities.DragHandler;

/**
 * Controller class which follows the MVC principle. Communicates with the
 * packages: model and view. Connected to main window GoLFX.fxml.
 *
 * @version 1.0
 * @author T.Dahll, M.S.Olsen and N.Nanthawisit
 */
public class GoLFXController extends TopControl implements Initializable {

    //Main components
    private Stage stage;
    private Board data;
    private GraphicsContext gc;
    private Timeline timeline;

    @FXML
    private Canvas canvas;
    @FXML
    private Slider speedSlider, sizeSlider;
    @FXML
    private Button startButton, pauseButton, fitZoomButton;
    @FXML
    private Button qButton;
    @FXML
    private TextField insertRows, insertColumns;
    @FXML
    private Label iterationsLabel, aliveCellsLabel, authorLabel, titleLabel,
            speedLabel;
    @FXML
    private ColorPicker backgroundColorPick, gridColorPick, cellColorPick;
    @FXML
    private RadioButton disableButton;
    @FXML
    private CheckBox checkGrid, eraser, threadOn;
    @FXML
    protected Tooltip instructionToolTip, speedTooltip, cellSizeTooltip, ruleSetTooltip, musicTooltip, customRuleTooltip, eraseTooltip,
            gridToolTip, threadToolTip, statisticsTooltip, infoToolTip;
    @FXML
    private ComboBox selectMusic;

    private Sound sound;
    private DragHandler dragHandler;
    private CanvasInfo cInfo;
    private static final int speed = 1000;
    private String headerList = "";
    private String boardTitle = "";
    private String author = "";
    private String soundPath = "";
    private String descRule = "";

    protected int numProcessors = Runtime.getRuntime().availableProcessors();

    //------------------------INITIALIZE and LISTENERS ------------------------//
    /**
     * initialises the GoLFXController class. The method initialise the game by:
     * - setting up the listeners for the GUI-elements - setting up the
     * GraphicContext, <code>gc</code> - setting up the animation - loading an
     * empty board, <code>data</code> - loading a sound, <code>sound</code> -
     * providing information about the <code>canvas</code> to the
     * <code>cInfo</code> - setting the current rule set to the standard rule
     * set (B3/S23) - adjusting the zoom to the view - initialising the
     * <code>dragHandler</code> to enable the "dragging" -function of the
     * <code>canvas</code>
     *
     * @param url <code>URL</code>
     * @param rb <code>ResourceBundle</code>
     * @see #gc
     * @see #data
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addListeners();

        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 500, 500);

        createAnimation();

        data = new DynamicBoard(numProcessors * 2 + 20);
        //data = new StaticBoard(numProcessors);

        sound = new Sound();
        cInfo = new CanvasInfo(canvas, sizeSlider.getValue());
        cInfo.centerPlacement(data.getWidth(), data.getHeight());

        selectComboBox.setValue("B3/S23");
        soundPath = "src/Sound/Pim Poy Pocket.wav";
        FileHandler.parseRuleString("B3/S23");
        ruleLabel.setText("Conway's Life: B3/S23");
        fitZoom();
        initRuleComboBox();
        dragHandler = new DragHandler(cInfo);
        //Sets deafult generation speed to 1.
        setSpeedVal = 1;
        //init <code>qButton</code>.
        setFaqTest();

    }

    //--------------------MOUSE AND DRAW LOGIC -----------------------------//
    /**
     * Uses GraphicContext to first clear canvas of graphic. The method then
     * uses the <code>Board</code>-specific <code>draw</code>-method that takes
     * the <code>canvas</code> and the GraphicsContext <code>gc</code> to draw
     * the cells of the <code>data</code>-object.
     *
     * @see #data the Game of Life-object
     */
    protected void draw() {
        gc.clearRect(0, 0, canvas.widthProperty().doubleValue(),
                canvas.heightProperty().doubleValue());
        data.draw(gc, cInfo);
    }

    /**
     * Used to detect a MouseEvent, <code>me</code>, and initialise the dragging
     * of the canvas. This enables the "moving" of the canvas while pressing the
     * secondary button and dragging.
     *
     * @param me the MouseEvent from the user
     */
    @FXML
    public void mousePressed(MouseEvent me) {
        dragHandler.dragStart(me);
    }

    /**
     * Detects the release of the secondary button. This in turn disables the
     * ability to "drag" the canvas on the screen.
     *
     * @param me the MouseEvent from the user
     */
    @FXML
    public void mouseReleased(MouseEvent me) {
        dragHandler.dragEnd(me);
    }

    /**
     * Detects if the "dragging"-function is active. If the function is active,
     * the amount of movement is detected and used in order to alter the
     * position of the <code>canvas</code>.
     *
     * @param me the MouseEvent from the user
     */
    @FXML
    public void mouseDragged(MouseEvent me) {
        if (dragHandler.drag(me)) {
            draw();
        }
        drawWithMouse(me);
    }

    /**
     * Detects a MouseEvent from the user. If the primary button of the mouse is
     * used, the MouseEvent, <code>me</code> is used to draw cells on the canvas
     * of the <code>Board</code>-object.
     *
     * @param me the MouseEvent from the user
     * @see #drawWithMouse(javafx.scene.input.MouseEvent)
     */
    @FXML
    public void mouseClicked(MouseEvent me) {
        drawWithMouse(me);
    }

    /**
     * Placement of mouse is determined by the MouseEvent parameter. This uses
     * the Board <code>getPlacement</code> method to find the drawing since the
     * drawing is centered. Divides the placement of the <code>Board</code> on
     * the getCellSize, so that in turn an array address is returned. Method
     * turns the cell to 1 if 0, and opposite if eraser boolean is true.
     *
     * @param me the pointers placement on the canvas
     */
    @FXML
    private void drawWithMouse(MouseEvent me) {
        if (me.getButton() == MouseButton.PRIMARY) {
            int col = (int) (((me.getX() - cInfo.getPlacementX()) / cInfo.getCellSize()));
            int row = (int) (((me.getY() - cInfo.getPlacementY()) / cInfo.getCellSize()));

            boolean erase = false;
            if (me.isControlDown() || eraser.isSelected()) {
                erase = true;
            }
            data.setCellValue(row, col, erase, 2000);
            updateAC_label();
            draw();
        }
    }

    /**
     * Detects scrolling from the user. The <code>sizeSlider</code> updates the
     * position of the slider-point to represent the amount of zooming. The
     * amount of zooming is determined with the ScrollEvent, <code>event</code>,
     * and added to the present value of the <code>sizeSlider</code>.
     *
     * @param event the scrolling from the user
     */
    @FXML
    public void scrollZoom(ScrollEvent event) {
        sizeSlider.setValue(sizeSlider.getValue() + (event.getDeltaY() / 10));
    }

    /**
     * Placement of a new pattern. The pattern is placed onto the existing
     * <code>Board</code>-object with the <code>addArrayToBoard</code>-method.
     * The placement of the new pattern is relative to the dimensions of the
     * underlying board, effectively registering a movement of more cells at a
     * time if the board has dimensions which exceeds certain limits. The new
     * pattern is then added to the board with the "Enter"-key.
     *
     * @param evt the key pressed
     * @see #data the board to place the new pattern onto
     */
    @FXML
    private void keyPressed(KeyEvent evt) {
        if (data.topExist()) {
            KeyCode keyCode = evt.getCode();
            byte moveSpeed = 1;

            //changes the move speed according to Board size.
            if (data.getHeight() > 500 || data.getWidth() > 500) {
                moveSpeed = 2;
            } else if (data.getHeight() > 1000 || data.getWidth() > 1000) {
                moveSpeed = 4;
            }

            switch (keyCode) {
                case A:
                    data.setXmove(-moveSpeed);
                    break;
                case S:
                    data.setYmove(moveSpeed);
                    break;
                case D:
                    data.setXmove(moveSpeed);
                    break;
                case W:
                    data.setYmove(-moveSpeed);
                    break;
                case Q:
                    data.rotateTopBoard(true);
                    break;
                case E:
                    data.rotateTopBoard(false);
                    break;
                case ENTER:
                    data.addArrayToBoard(data.getTopBoard());
            }
        }
        draw();
    }

    /**
     * Draws the grid on the board. Determines if the <code>checkGrid</code> is
     * checked and then update the canvas with the method <code>draw</code>.
     *
     * @param e ActionEvent from the user interface
     * @see Board#setGrid(boolean)
     * @see #draw()
     */
    @FXML
    public void turnOnGrid(ActionEvent e) {
        data.setGrid(checkGrid.isSelected());
        draw();
    }

    /**
     * Changes the colour of the cells. Gets the current value from the
     * <code>cellColorPick</code> and updates the color of the cells before
     * calling method <code>draw</code> to redraw the board on the
     * <code>canvas</code>.
     *
     * @param ce ActionEvent from the user interface
     * @see Board#setCellColor(javafx.scene.paint.Color)
     * @see #draw()
     */
    @FXML
    public void changeColor(ActionEvent ce) {
        Board.setCellColor(cellColorPick.getValue());
        draw();
    }

    /**
     * Changes the background colour. Gets the current value from the
     * <code>backgroundColorPick</code>, sets the new background colour and then
     * updates the <code>canvas</code> with the method <code>draw</code>.
     *
     * @param ce ActionEvent from the user interface
     * @see #draw()
     * @see Board#setBackGroundColor(javafx.scene.paint.Color)
     */
    @FXML
    public void changeBackgroundColor(ActionEvent ce) {
        data.setBackGroundColor(backgroundColorPick.getValue());
        draw();
    }

    /**
     * Changes the grid colour. Gets the current value from the
     * <code>gridColorPick</code>, sets the new grid colour for the board and
     * then updates the <code>canvas</code> with the method <code>draw</code>.
     *
     * @param ce ActionEvent from the user interface via a ColorPicker An action
     * event to change the grid colour, via colour picker.
     * @see #draw()
     * @see Board#setGridColor(javafx.scene.paint.Color)
     */
    @FXML
    public void changeGridColor(ActionEvent ce) {
        Board.setGridColor(gridColorPick.getValue());
        draw();
    }

    //-------------------------------GEN LOGIC------------------------------//
    /**
     * Receives input through the <code>insertRows</code> and
     * <code>insertColumns</code> and creates a new board with the provided
     * dimensions. If the animation, <code>timeline</code> is already running it
     * is stopped while a new board is placed onto the screen.
     *
     * Previous method before dynamic Board. Now the <code>Board</code> adjusts
     * its own size.
     */
    @Deprecated
    @FXML
    private void useMatrix() {
        try {
            data.setNumRows(Integer.parseInt(insertRows.getText()));
            data.setNumColumns(Integer.parseInt(insertColumns.getText()));
            if (data != null) {
                timeline.stop();
                data = new DynamicBoard(data.getNumRows(), data.getNumColumns());
                data.reset_aliveCells();
                data.reset_iterations();
            } else {
            }
        } catch (NumberFormatException exception) {
            insertRows.setText("Invalid");
            insertColumns.setText("Invalid");
        }
        fitZoom();
    }

    /**
     * Adjusts the zoom-level based on the number of rows and the number of
     * columns from the board when the <code>fitZoomButton</code> is pressed.
     * The zoom-level is determined based on whichever is largest in order to
     * display the entire board in the <code>canvas</code>. The canvas is
     * updated through the <code>draw</code>-method.
     *
     * @see #data
     * @see #sizeSlider
     */
    @FXML
    public void fitZoom() {
        int boardSize;
        double size;
        if (data.getNumRows() > data.getNumColumns()) {
            boardSize = data.getNumRows();
        } else {
            boardSize = data.getWidth();
        }

        size = canvas.heightProperty().doubleValue() / boardSize;
        cInfo.setCellSize(size);
        sizeSlider.setValue(size);
        cInfo.centerPlacement(data.getWidth(), data.getHeight());
        draw();
    }

    /**
     * A method used to read text file about rule description for different
     * rules. The method assigns a description of the current rule set to the
     * variable <code>descRule</code> in order to display that information in a
     * dialog box. The variable <code>ruleSet</code> is assigned the value of
     * the current rule set if one of the following occur: a new rule set is
     * selected with the combo box containing the most known rule sets or if an
     * imported RLE-file contains a rule set.
     *
     * @see #ruleSet the current rule set
     * @see #rulesetButtonActionPerformed the method that displays the
     * information
     */
    public void readRuleDesc() {

        String newRuleSet = ruleSet.replace('/', ',').replaceAll("B|S", "").concat(".txt");
        File folder = new File("src/TXT");
        File[] listOfFiles = folder.listFiles();
        String path = "";

        for (File listOfFile : listOfFiles) {
            if (listOfFile.getName().equals(newRuleSet)) {
                path = listOfFile.getAbsolutePath();
            }
        }

        try (FileReader fr = new FileReader(path)) {
            BufferedReader br = new BufferedReader(fr);
            String S;
            while ((S = br.readLine()) != null) {
                descRule = S;
            }
        } catch (IOException ex) {
            errorMessage("Cannot find file containing rule descriptions");
        }

    }

    /**
     * Responds to the <code>threadOn</code> check box. If the
     * <code>threadOn</code> is checked, the optimizing-function of the
     * <code>Board</code>-object, <code>data</code> is turned off.
     *
     * @see DynamicBoard#setOptimaliser(boolean)
     */
    @FXML
    public void threadOnAction() {
        data.setOptimaliser(false);
    }

    //------------------------------ANIMATION-------------------------------//
    /**
     * Method for drawing graphics data to the screen, where the underlying data
     * changes between each image(Animation). The method sets up the
     * <code>timeline</code>-object by defining its cycle count and
     * <code>animDuration</code> as a <code>Duration</code>-object.
     *
     * Source for the <code>timeline</code> setup:
     * https://docs.oracle.com/javase/8/javafx/api/javafx/animation/Timeline.html
     *
     * @see #timeline the <code>Timeline</code>-object representing the
     * animation
     * @see #nextGeneration() the method to be called in the animation
     */
    public void createAnimation() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        Duration animDuration = Duration.millis(speed);

        EventHandler animEnd = (EventHandler<ActionEvent>) (ActionEvent event) -> {
            nextGeneration();
        };

        KeyFrame k = new KeyFrame(animDuration, animEnd);
        timeline.getKeyFrames().add(k);
    }

    /**
     * An action to be perform when start button is clicked. If the
     * <code>timeline</code> is not already either running or paused, the
     * <code>timeline</code> will be played (the animation starts). If the
     * <code>timeline</code> is already running or is paused, this method will
     * cause it to stop the animation.
     *
     * Pressing the button while the <code>timeline</code> is either running or
     * is paused will create a new <code>DynamicBoard</code>-object.
     *
     * @see #timeline the <code>Timeline</code>-object representing the
     * animation
     * @see #fitZoom()
     * @see #draw()
     * @see #updateLabels()
     * @throws IOException - throw exceptions related to the error loading file
     * and file loading.
     * (https://docs.oracle.com/javase/7/docs/api/java/io/IOException.html)
     */
    @FXML
    public void start() throws IOException {

        if (timeline.getStatus() == Animation.Status.RUNNING
                || timeline.getStatus() == Animation.Status.PAUSED) {
            timeline.stop();
            startButton.setText("Start");
            pauseButton.setText("Pause");

            //Reset parameters
            data.reset_aliveCells();
            data.reset_iterations();
            resetBoardInfo();
            data = new DynamicBoard(2 * numProcessors);
            updateLabels();
            draw();
            fitZoom();
        } else {
            timeline.play();
            startButton.setText("Reset");
        }

    }

    /**
     * An action to be perform when the pause button is clicked. Will pause the
     * <code>timeline</code> if it is currently running and resume the
     * <code>timeline</code> if it is already paused.
     *
     * @see #timeline
     */
    @FXML
    public void pause() {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
            pauseButton.setText("Play");
        } else if (timeline.getStatus() == Animation.Status.PAUSED) {
            timeline.play();
            pauseButton.setText("Pause");
        }
    }

    /**
     * Determines if the speed of generations has been lowered.
     */
    boolean benchMark = true;
    /**
     * value which decides the users chosen speed. Used by the program to
     */
    double setSpeedVal;

    /**
     * A method to update the game board so that we move to the next generation
     * of the game. The <code>Board</code>-object updates its board with its
     * appropriate <code>setNextGeneration</code>-method. It is also possible to
     * set the next generation of the <code>Board</code>-object on several
     * threads concurrently if <code>threadOn</code> is selected. If the next
     * generation is set with several threads, concurrently, the number of
     * available processors <code>numProcessors</code> is used as a parameter,
     * indicating the number of processors that are available. The board is then
     * drawn to the canvas with the <code>draw</code>-method.
     *
     * @see #updateLabels()
     * @see #draw()
     */
    @FXML
    public void nextGeneration() {
        long startTime = System.currentTimeMillis();

        if (threadOn.isSelected()) {
            WorkHive.setBoard(data);
            WorkHive.createSeparateThreads(numProcessors);
        } else {
            data.setNextGeneration();
        }

        updateLabels();
        draw();

        long deltaTime = System.currentTimeMillis() - startTime;

        if (deltaTime > 60) {
            speedSlider.setValue(speedSlider.getValue() - 2);
        } else if (deltaTime < 60 && speedSlider.getValue() < setSpeedVal) {
            speedSlider.setValue(speedSlider.getValue() + 1);
        }
        data.sizeTest();

        /*
         * Further implemantations should make this test work. That determines how
         * many alive cells there can be before the Board stops expanding.
         * 
         * data.aliveCellsTest();
         */
    }

    //------------------------PERFORMANCE-------------------------------------//
    /**
     * Results with threads deltaTime in ms: 133 deltaTime in ms: 129 deltaTime
     * in ms: 30 deltaTime in ms: 43 deltaTime in ms: 29 deltaTime in ms: 30
     * deltaTime in ms: 34 deltaTime in ms: 38 deltaTime in ms: 30
     *
     * Results without indexPair and optimaliser = false Board: TuringMachine
     * Iteration 1: 122 Iteration 2: 89 Iteration 3: 88 Iteration 4: 97
     * Iteration 5: 141
     */
    public void nextGenerationPrintPerformance() {
        long startTime = System.currentTimeMillis();
        data.updateNeighbourBoard();
        data.setNextGeneration();
        long deltaTime = System.currentTimeMillis() - startTime;
    }

    /**
     * Results with threads * 2, odd and even working deltaTime in ms: 236
     * deltaTime in ms: 28 deltaTime in ms: 27 deltaTime in ms: 28
     *
     *
     * Results without indexPair and optimaliser = false Board: TuringMachine
     * Iteration 1: 134 Iteration 2: 51 Iteration 3: 35 Iteration 4: 36
     * Iteration 5: 53
     */
    public void nextGenerationConcurrentPrintPerformance() {
        long startTime = System.currentTimeMillis();
        WorkHive.setBoard(data);
        WorkHive.createSeparateThreads(numProcessors);
        long deltaTime = System.currentTimeMillis() - startTime;
    }

    //---------------------------ACTIONS-------------------------------------//
    /**
     * Chooses a RLE or LIF file and places it on the active board. A
     * <code>FileChooser</code> is created to let the user choose a file from
     * the hard drive. If the file is successfully read, the board is added to
     * the existing <code>data</code>-object, available information about the
     * RLE-file is displayed and the board is drawn to the screen.
     *
     * About <code>setInitialDirectory</code>: there is currently a bug that
     * complicates the selection of a default directory, this is why the initial
     * directory is set to default.
     *
     * Further implementation should contain a library of .rle or .lif files
     * which are easily selectable in the .jar file.
     *
     * @see #fitZoom() adjusts the representation of the board to fit the
     * displayed canvas
     * @see #draw() draws the board on the canvas after <code>data</code> has
     * added the byte[][] representing the board
     */
    @FXML
    public void fileChooseAction() {

        FileChooser fC = new FileChooser();
        fC.setTitle("Select a rle or lif file");
        File C = fC.showOpenDialog(stage);

        //Returns to view if cancel is selected
        try {
            C.canExecute();
        } catch (Exception e) {
            return;
        }

        try {
            if (C.getName().endsWith(".rle") || (C.getName().endsWith(".lif"))) {
                FileHandler fH = new FileHandler();
                //converts the file c into a reader object.
                fH.convertToReader(C);

                data.setTopBoard(fH.getRleArray());
                cInfo.centerPlacement(data.getWidth(), data.getHeight());
                data.autoFit(data.getTopBoard());
                fitZoom();
                pause();
                boardTitle = fH.getBoardTitle();
                ruleSet = fH.getRuleSet();
                author = fH.getAuthors();
                headerList = fH.getHeaderList();
                updateLabels();

            } else {
                errorMessage("Error related to format of file, please make sure the file is a RLE- or LIF-file");
            }
        } catch (IOException ie) {
            errorMessage("Error related to reading file");
        } catch (PatternFormatException pfe) {
            errorMessage(pfe.getMessage());
        }

    }

    /**
     * Retrieves a board from an URL. Sends the URL as a String to method
     * <code>readGameBoardFromURL</code> which in turn extract pattern
     * information from the URL. If extraction of pattern information is
     * successful, the pattern is placed in the existing board. The labels of
     * the board is updated through <code>updateLabels</code> and the board is
     * drawn on the canvas.
     *
     * @see #updateLabels()
     * @see #draw()
     */
    @FXML
    public void chooseUrlAction() {
        FileChooser UrlC = new FileChooser();
        UrlC.setTitle("Enter url adress");
        TextInputDialog urlDialog = new TextInputDialog("Type url here");
        urlDialog.setTitle("Url Input Dialog");
        urlDialog.setHeaderText(null);
        urlDialog.setContentText("Please enter url here:");

        Optional<String> result = urlDialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        try {
            if (result.get().endsWith(".rle")) {
                FileHandler urlH = new FileHandler();
                urlH.readGameBoardFromURL(result.get());
                data.addArrayToBoard(urlH.getRleArray());
                boardTitle = urlH.getBoardTitle();
                ruleSet = urlH.getRuleSet();
                author = urlH.getAuthors();
                updateLabels();
                headerList = urlH.getHeaderList();
                draw();
            } else {
                errorMessage("Error related to provided URL, please make sure the URL contains a RLE-file");
            }

        } catch (IOException e) {
            errorMessage("Error related reading RLE-file from URL");
        } catch (PatternFormatException pfe) {
            errorMessage(pfe.getMessage());
        }

    }

    //--------------------------------EDITOR ---------------------------------//
    EditorController edit = new EditorController();

    /**
     * Opens the editor stage, which uses the <code>EditorController</code>.
     * Sets the <code>data</code>-object as a variable in the
     * <code>EditorController</code> and pauses the <code>timeline</code> while
     * the new <code>editorStage</code>.
     *
     * @see controller.EditorController {@link controller.EditorController}
     */
    @FXML
    public void openEditor() {
        //Determines how big a pattern that can be edited, can be.
        if (data.getWidth() < 150 && data.getHeight() < 150) {

            Stage editorStage = new Stage();
            editorStage.initOwner(startButton.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();

            try {
                BorderPane root = loader.load(getClass().getResource("/view/Editor.fxml").openStream());
                Scene scene = new Scene(root);
                EditorController editorController = loader.getController();
                editorController.setInit(data);
                editorStage.initModality(Modality.WINDOW_MODAL);
                timeline.pause();
                editorStage.setScene(scene);
                editorStage.setTitle("Pattern Editor");
                editorStage.sizeToScene();
                editorStage.showAndWait();

                data = editorController.getEditBoard();
                data.reset_iterations();
                data.reset_aliveCells();

                draw();
            } catch (IOException e) {
                e.printStackTrace();
                errorMessage("Error related to opening of pattern");
            }
        } else {
            errorMessage("The pattern is too big to use the pattern editor");
        }

    }

//--------------------------------------------------------------------------//
    /**
     * initialisation of listeners. The method is used in the
     * <code>initialize</code>-method in order to set up various
     * <code>Slider</code>-elements and <code>ColorPicker</code> -elements at
     * initialisation of the program.
     *
     * @see #backgroundColorPick current background color showing on ColorPicker
     * @see #gridColorPick current color of the grid showing on ColorPicker
     * @see #cellColorPick current color of the cell showing on ColorPicker
     * @see #ruleLabel a label containing information about rule
     * @see #ruleSet set the rule to the board
     * @see canvasInfo.setCellSize()
     * @see canvasInfo.centerPlacement()
     * @see Filehandler.pardseRuleString()
     */
    private void addListeners() {

        //Showing default color on ColorPicker (background, grid and cell) 
        backgroundColorPick.setValue(Board.getBackGroundColor());
        gridColorPick.setValue(Board.getGridColor());
        cellColorPick.setValue(Board.getCellColor());

        //Selection of music 
        selectMusic.getItems().addAll(listOfSongs);
        selectMusic.valueProperty().addListener((ObservableValue observable, Object selectComoldVal, Object newVal) -> {
            if (sound.isPlaying()) {
                sound.pauseMusic();
            }

            soundPath = ("src/Sound/" + newVal.toString() + ".wav");
            sound.setFile(soundPath);
            sound.playMusic();
            disableButton.setSelected(true);
        });

        //Zoom function
        sizeSlider.valueProperty().addListener((observable, oldVal, newVal) -> {
            cInfo.setCellSize(newVal.doubleValue());
            cInfo.centerPlacement(data.getWidth(), data.getHeight());
            draw();

        });

        //Adjusts speed of the draw sequences
        speedSlider.valueProperty().addListener((observable, oldValue, newVal) -> {
            double fps = ((double) newVal);
            timeline.setRate(fps);
            speedLabel.setText((int) fps + " fps");
        });

    }

    /**
     * Sets targeted speed on mouse action on the <code>speedSlider</code>.
     *
     * @see #speedSlider
     */
    @FXML
    public void onMouseSpeedClick() {
        setSpeedVal = speedSlider.getValue();
    }

    //LABELS
    /**
     * Updates the <code>iterationsLabel</code> with the current number of
     * iterations.
     *
     * @see #iterationsLabel containing the number of iterations
     */
    @FXML
    public void updateI_label() {
        iterationsLabel.setText("Iterations\n = " + data.getIterations());
    }

    /**
     * Updates the <code>aliveCellsLabel</code> with the current number of alive
     * cells.
     *
     * @see #aliveCellsLabel label containing the number of alive cells
     */
    @FXML
    public void updateAC_label() {
        aliveCellsLabel.setText("Alive cells\n = " + data.getAliveCells());
    }

    /**
     * Update all the labels on board (iterations, alive cells, board title,
     * rule and author).
     *
     * @see #updateLabels()
     * @see #updateAC_label()
     * @see #updateBoardInfo()
     */
    @FXML
    public void updateLabels() {
        updateI_label();
        updateAC_label();
        updateBoardInfo();
    }

    /**
     * A method that updates: board title, rule set and author. The method
     * updates the labels directly, not through their respective methods.
     *
     * @see #titleLabel the label containing information about the board title
     * @see #ruleLabel the label containing information about the current rule
     * set
     * @see #authorLabel the label containing information about the author of
     * the pattern
     */
    @FXML
    public void updateBoardInfo() {
        titleLabel.setText("Board title: " + boardTitle);
        ruleLabel.setText("Ruleset: " + ruleSet);
        authorLabel.setText("Author: " + author);
    }

    /**
     * Used to reset board information. Clears all the information fields.
     *
     * @see #headerList contain information about name of the pattern, comments
     * and author
     * @see #titleLabel the label containing information about the board title
     * @see #ruleLabel the label containing information about the current rule
     * set
     * @see #authorLabel the label containing information about the author of
     * the pattern
     */
    public void resetBoardInfo() {
        headerList = "";
        boardTitle = "";
        author = "";
    }

    /**
     * Gets the current rule set description. The rule set description is stored
     * in variable <code>descRule</code> after being read from file.
     *
     * @return the current rule description
     */
    public String getDescRule() {
        return descRule;
    }

    /**
     * A method to get information about board and show in a dialog box when the
     * board button is clicked. Opens a new alert dialog with the necessary
     * information.
     *
     * @see #headerList contains information about name of the pattern, comments
     * and author
     * @param e ActionEvent
     */
    public void boardButtonActionPerformed(ActionEvent e) {
        Alert aboutB = new Alert(AlertType.INFORMATION);
        aboutB.setTitle("Information");
        aboutB.setHeaderText(null);
        if (headerList.isEmpty()) {
            aboutB.setContentText("No file chosen");
        } else {
            aboutB.setContentText(headerList);
        }
        aboutB.showAndWait();
    }

    /**
     * A method used to read rule description and show information about the
     * selected rule set in a dialog box.
     *
     * @see #errorMessage(java.lang.String)
     * @see #readRuleDesc()
     * @param e ActionEvent
     */
    public void rulesetButtonActionPerformed(ActionEvent e) {
        Alert aboutR = new Alert(AlertType.INFORMATION);
        aboutR.setTitle("Information");
        aboutR.setHeaderText(null);
        readRuleDesc();
        if (ruleSet.isEmpty()) {
            try {
                String content;
                content = new String(Files.readAllBytes(Paths.get("src/TXT/3,23.txt")));
                aboutR.setContentText(content);
            } catch (IOException ex) {
                errorMessage("Error reading rule information from file");
            }

        } else {

            aboutR.setContentText(descRule);
        }
        aboutR.showAndWait();
    }

    /**
     * A method to read "readme.txt" when the instruction button is clicked. The
     * method opens a window to present this information to the user.
     *
     * Source:
     * http://alvinalexander.com/blog/post/java/read-text-file-from-jar-file.
     *
     * @throws java.io.IOException handles the file reading.
     * @see #errorMessage(java.lang.String)
     */
    public void instructionButtonActionPerformed() throws IOException {
        Alert instr = new Alert(AlertType.INFORMATION);

        instr.setTitle("Game of Life");
        instr.setHeaderText("Instructions");

        InputStream inputS = getClass().getResourceAsStream("Readme.txt");
        InputStreamReader inputSR = new InputStreamReader(inputS);
        BufferedReader bufferedR = new BufferedReader(inputSR);

        instructionView(bufferedR);
    }

    /**
     * Stack pane view which shows the text in a text file.
     *
     * @param txtFile to convert into an text view.
     * @throws IOException handles if <code>File</code> is readable or not.
     */
    public void instructionView(BufferedReader txtFile) throws IOException {
        StringBuilder readMe;

        BufferedReader br = new BufferedReader(txtFile);
        String S;
        readMe = new StringBuilder();
        while ((S = br.readLine()) != null) {
            readMe.append(S);
            readMe.append("\n");

        }
        Label readmeLabel = new Label(readMe.toString());
        StackPane textPane = new StackPane();
        textPane.getChildren().add(readmeLabel);
        textPane.setPadding(new Insets(10, 10, 10, 10));

        Stage faqStage = new Stage();
        faqStage.setScene(new Scene(textPane));
        faqStage.showAndWait();
    }

    //-----------------------STATISTICS---------------------------------------//
    /**
     * Used to open a new stage for statistics data. The <code>timeline</code>
     * is paused before opening the new stage in order to resume the animation
     * at a later point in time.
     *
     * @see StatisticsController#setBoard(model.BoardPack.Board)
     */
    @FXML
    public void statisticsAction() {
        try {
            timeline.pause();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Statistics.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            StatisticsController stController = fxmlLoader.getController();
            stController.setBoard(data);

            Stage stages = new Stage();
            stages.setTitle("Statistics Data");
            stages.initModality(Modality.APPLICATION_MODAL);
            stages.setScene(new Scene(root1));
            stages.showAndWait();
            stages.close();

            pauseButton.setText("Play");
        } catch (IOException ex) {
            errorMessage("Error reading from file");
        } catch (ClassNotFoundException ex) {
            errorMessage("Cannot find class to read object from");
        }
    }

    /**
     * Temporary method for string representation of GoL.
     *
     * @param t <code>byte[][]</code> to print
     */
    public static void stringRep(byte[][] t) {
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[0].length; j++) {
                System.out.print(t[i][j] + " ");
            }
            System.out.println();
        }
    }

//--------------------------------MUSIC---------------------------------------//
    /**
     * Enables and disables the background music when called.
     *
     * @see Sound#pauseMusic()
     */
    @FXML
    public void disableMusic() {
        if (disableButton.isSelected()) {
            sound.setFile(soundPath);
            sound.playMusic();
        } else if (!disableButton.isSelected()) {
            sound.pauseMusic();
        }

        while (!sound.isPlaying()) {
            disableButton.setSelected(true);
        }
    }

    /**
     * Getter for the current <code>Board</code>-object.
     *
     * @return the current board
     */
    public Board getData() {
        return data;
    }

    /**
     * Binds the canvas to the stage size making the size of the
     * <code>canvas</code> dependent on the size of the <code>stage</code>. The
     * <code>canvas</code> is set to be 0.80 of the <code>stage</code> height
     * and 0.50 of the <code>stage</code> width.
     *
     * Currently inactive because canvas moves around on usage.
     *
     * @param s stage which is loaded
     * @see #stage
     * @see #canvas
     */
    public void loadStage(Stage s) {
        stage = s;
        canvas.widthProperty().bind(stage.widthProperty().multiply(0.80));
        canvas.heightProperty().bind(stage.widthProperty().multiply(0.50));
    }

    /**
     * Creates the button which displays a message when the <code>qButton</code>
     * is hovered.
     *
     * @see #qButton the button that can be hovered
     */
    public void setFaqTest() {
        double r = 9.5;
        qButton.setShape(new Circle(r));
        qButton.setMinSize(2 * r, 2 * r);
        qButton.setMaxSize(2 * r, 2 * r);
        qButton.setText("?");
    }

}
