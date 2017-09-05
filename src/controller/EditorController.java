/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.BoardPack.Board;
import model.BoardPack.DynamicBoard;
import model.FileHandler;
import model.BoardPack.Rule;
import model.CanvasInfo;
import model.Statistics;

/**
 * FXML Controller class used to define and create patterns and their .rle
 * files. Saves .rle files into the predetermined format. And also showcases the
 * next 20 iterations of a pattern on the bottom canvas. The editor also can
 * send the pattern to the gif controller for .gif creation
 *
 * @see controller.SaveGifController
 * @author T.Dahll
 */
public class EditorController extends TopControl implements Initializable {

    @FXML
    private TextField nameBox, authorBox, descBox;

    /**
     * EditorController has two canvases.
     */
    @FXML
    private Canvas canvasEditor, strip;

    @FXML
    private Label duplicateLabel;

    @FXML
    private Button closeButton, saveGifButton;

    @FXML
    private CheckBox eraser;

    @FXML
    private Tooltip ruleSetTooltip;

    /**
     * GraphicsContext objects of different canvases.
     */
    private GraphicsContext gcS, gc;
   
    private CanvasInfo cInfo, sCInfo;
    private DynamicBoard stripBoard, editBoard;
    private Stage stage;
    /**
     * Padding between <code>stripBoard</code> drawings.
     */
    private double xpadding;
    /*
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ruleTextField.setText(Rule.getRuleString());
    }

    /**
     * The initialising of variables before Editor stage is opened.
     *
     * @param board object to edit.
     */
    public void setInit(Board board) {
        
        initRuleComboBox();
        editBoard = new DynamicBoard(board);
        
        //Creates graphicontext to the canvas'.
        gc = canvasEditor.getGraphicsContext2D();
        gcS = strip.getGraphicsContext2D();
        
        cInfo = new CanvasInfo(canvasEditor, 20);
        cInfo.setCellSize(canvasEditor.getHeight() / editBoard.getBoardArray().size());

        ruleTextField.setText("Custom rule");

        stripBoard = new DynamicBoard(editBoard.getBoardArray(), editBoard.getBoundingBox());
        sCInfo = new CanvasInfo(strip, 15);
        
        fitZoom();
        draw();
    }

    //----------------------DRAWING AND MOUSE LOGIG---------------------------// 
    /**
     * Draws on the <code>editorCanvas</code> and the <code>stripCanvas</code>.
     */
    protected void draw() {
        gc.clearRect(0, 0, canvasEditor.widthProperty().doubleValue(),
                canvasEditor.heightProperty().doubleValue());
        editBoard.draw(gc, cInfo);
        drawStrip();
    }

    /**
     * Draws the next 20 iterations of the active pattern on a slideable canvas.
     * Based on psuedocode by Henrik Lieng.
     */
    private void drawStrip() {
        stripBoard = new DynamicBoard(editBoard);
        gcS.setFill(Color.WHITE);
        gcS.fillRect(0, 0, strip.widthProperty().doubleValue(),
                strip.heightProperty().doubleValue());

        Affine xform = new Affine();
        double tx = xpadding;

        //Draws 20 iterations side by side on the canvas.
        for (int i = 0; i < 20; i++) {
            xform.setTx(tx);
            gcS.setTransform(xform);

            stripBoard = new DynamicBoard(stripBoard.getBoardArray(), stripBoard.getBoundingBox());
            sCInfo.setCellSize(strip.getHeight() / stripBoard.getBoardArray().size());
            stripBoard.drawStr(gcS, sCInfo);
            stripBoard.setNextGeneration();

            gcS.strokeRect(5, 5, stripBoard.getBoardArray().get(0).size() * sCInfo.getCellSize(),
                    stripBoard.getBoardArray().size() * sCInfo.getCellSize());

            tx += (stripBoard.getBoardArray().get(0).size() * sCInfo.getCellSize()) + 1;
        }
        xform.setTx(0.0);
        gcS.setTransform(xform);
    }

    /**
     * Changes cell state on cells of the pattern.
     *
     * @param me MousEvent collects the coordinates on the canvas.
     */
    @FXML
    protected void drawWithMouse(MouseEvent me) {
        
          if (me.getButton() == MouseButton.PRIMARY) {
            int col = (int) (((me.getX() - cInfo.getPlacementX()) / cInfo.getCellSize()));
            int row = (int) (((me.getY() - cInfo.getPlacementY()) / cInfo.getCellSize()));

            boolean erase = false;
            if (me.isControlDown() || eraser.isSelected()) {
                erase = true;
            }
            
            editBoard.setCellValue(row, col, erase,150);
            stripBoard = new DynamicBoard(editBoard.getBoardArray(), editBoard.getBoundingBox());
        }
        fitZoom();
        draw();
    }
    
    
    /**
     * Center the view of the canvas around the <code>Board</code> size.
     */
     public void fitZoom() {
        int boardSize;
        double size;
        if (editBoard.getNumRows() > editBoard.getNumColumns()) {
            boardSize = editBoard.getNumRows();
        } else {
            boardSize = editBoard.getWidth();
        }

        size = canvasEditor.heightProperty().doubleValue() / boardSize;
        cInfo.setCellSize(size);
        cInfo.centerPlacement(editBoard.getWidth(), editBoard.getHeight());
        draw();
    }

    //------------------------------FILE LOGIC--------------------------------//
     
    /**
     * Button action to save Pattern and text to .rle file.
     *
     * @throws IOException if write file operation fails.
     */
    @FXML
    private void saveRLEAction() throws IOException {
        String rleString = "";
        String rule;
        
        //Creates the text in the .rle file.
        rleString += "#N " + nameBox.getText() + "\n";
        rleString += "#O " + authorBox.getText() + "\n";
        rleString += "#C " + descBox.getText() + "\n";
        rleString += "x = " + Integer.toString(editBoard.getBoundArray().get(0).size()) + ", y = "
                + Integer.toString(editBoard.getBoundArray().size()) + ", rule = "
                + ruleSet + "\n";

        String boundString = editBoard.arraytoString(editBoard.getBoundArray());
        rleString += FileHandler.encodeArray(boundString);
        FileChooser fc = new FileChooser();

        //Set file format to ".rle"
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("rle files (*.rle)", "*.rle");
        fc.getExtensionFilters().add(extFilter);
        
        fc.setTitle("Save pattern to rle file");
        File file = fc.showSaveDialog(stage);
        
        // Handles void files.
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(rleString);
            } catch (IOException ex) {
                ErrorMassage("save");
                Logger.getLogger(EditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Draws the graphics again, but with different set of rules.
     */
    @FXML
    public void newRuleSelected() {
        draw();
    }

    /**
     * Shows alert box with predetermined message.
     *
     * @param from String which decides which content.text that shows.
     */
    public void ErrorMassage(String from) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        if (from.equals("save")) {
            alert.setContentText("Error occured parsing board to RLE file.");
        }
        alert.showAndWait();
    }

    /**
     * Opens up the view <code>/view/saveGif.fxm</code> where the user can save
     * the pattern to a .gif file with set iterations and speed.
     *
     * @throws IOException
     */
    @FXML
    private void openSaveGifController() throws IOException {

        Stage gifStage = new Stage();
        gifStage.initOwner(nameBox.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        AnchorPane root = loader.load(getClass().getResource("/view/SaveGif.fxml").openStream());

        SaveGifController saveGifController = loader.getController();
        saveGifController.setInit(editBoard);

        gifStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(root);

        gifStage.setScene(scene);
        gifStage.setTitle("Save Graphic Interchange Format");
        gifStage.showAndWait();
    }

    /**
     * Closes Stage safely.
     *
     * @param event
     */
    @FXML
    private void closeAction(ActionEvent event) {
        DynamicBoard.setPatternArray(editBoard.getBoardArray());
        stage = (Stage) closeButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        GoLFXController GoLFX = loader.getController();
        stage.close();
    }
   

    /**
     * @return the editBoard
     */
    public DynamicBoard getEditBoard() {
        return editBoard;
    }
    //--------------------------Statistics-----------------------------------//

    /**
     * Used to open a new stage for statistics data. Also pulls back the
     * iteration number where the pattern could be a duplicate.
     *
     * @see StatisticsController#setBoard(model.BoardPack.Board)
     */
    @FXML
    public void statisticsAction() {
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Statistics.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            StatisticsController stController = fxmlLoader.getController();
            stController.setBoard(editBoard);
            Stage stages = new Stage();
            stages.setTitle("Statistics Data");
            stages.initModality(Modality.APPLICATION_MODAL);
            stages.setScene(new Scene(root));
            stages.showAndWait();
            //Shows a possible action where the pattern may be the same.
            duplicateLabel.setText("Similar pattern at iteration \n "
                    + Integer.toString(Statistics.dupInterval));
            
        } catch (IOException ex) {
            errorMessage("Error reading from file, please reset game and then press "
                    + "the Statistics-button");
        } catch (ClassNotFoundException ex) {
            errorMessage("Cannot find class to read object from");
        }
    }
    
    @FXML
    @Override
    protected void setCustomRule() {
        String rule = ruleTextField.getText();
        FileHandler.parseRuleString(rule);
        ruleSet = rule;
        ruleLabel.setText(rule);
        draw();
    }
}
