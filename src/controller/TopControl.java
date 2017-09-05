/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.FileHandler;

/**
 * A class containing information about the most common rule sets. Made to
 * systemise the more universal elements of the main controllers.
 *
 * @author T.Dahll
 */
public class TopControl {

    @FXML
    protected ComboBox selectComboBox;
    @FXML
    protected Label ruleLabel;
    @FXML
    private Button statButton;
    @FXML
    protected TextField ruleTextField;

    protected String ruleSet = "B3/S23";

    /**
     * List of the different rule sets. These are used in order to quickly
     * select a different rule set in the GolFXController.
     *
     * @see controller.GoLFXController
     */
    protected ObservableList<String> IC = FXCollections.observableArrayList(
            "Replicator: B1357/S1357",
            "Fredkin: B1357/S02468", "Seeds: B2/S",
            "Live Free or Die: B2/S0", "Life without death: B3/S012345678", "Flock:B3/S12",
            "Mazecetric: B3/S1234", "Maze: B3/S12345", "Conway's Life: B3/S23",
            "2x2: B36/S125", "HighLife: B36/S23", "Move: B368/S245",
            "Day & Night: B3678/S34678"
    );
    protected ObservableList<String> listOfSongs = FXCollections.observableArrayList(
            "DesiJourney",
            "Doublebass", "Into the infinity",
            "Pim Poy Pocket", "Pim Poy"
    );

    /**
     * Initiates the rule selection <code>selectComboBox</code>.
     */
    protected void initRuleComboBox() {

        selectComboBox.getItems().addAll(IC);
        
        //Selection of rule.
        selectComboBox.valueProperty().addListener((ObservableValue observable, 
                Object selectComoldVal, Object newVal) -> {
            String comboBoxselect = newVal.toString();
            Matcher matcher = Pattern.compile("[:]\\s*(.*)").matcher(comboBoxselect);
            
            if (matcher.find()) {
                FileHandler.parseRuleString(matcher.group(1));
                ruleLabel.setText(matcher.group(1));
                ruleSet = matcher.group(1);
                ruleTextField.setText(ruleSet);
            }
        });
    }
    
       /**
     * Used in order to set a custom rule set. The method parses a given
     * <code>rule</code> and sets the given rule set to the current board.
     *
     * @see #ruleLabel this label is updated to present the current rule set
     */
    @FXML
    protected void setCustomRule() {
        String rule = ruleTextField.getText();
        FileHandler.parseRuleString(rule);
        ruleSet = rule;
        ruleLabel.setText(rule);
    }
    
    
    /**
     * Showing a specific error message on the screen. The parameter should
     * contain a brief description of the error and will be displayed in a
     * dialog box.
     *
     * @param message message describing the error
     */
    public void errorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
