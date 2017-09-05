/* 
 * University College of Oslo and Akershus, spring 2017. M.S.Olsen, N.Nanthawisit & T.A.Dahll.
 * School project, bachelor computer science, 1st year.  
 * Game of Life Application
 */
package maingol;

import controller.GoLFXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main method that starts the program.
 *
 */
public class MainGoL extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GoLFX.fxml"));
        Parent root = loader.load();
        GoLFXController control = loader.getController();

        Scene scene = new Scene(root);
        scene.getRoot().requestFocus();
        stage.setTitle("Game of Life");
        stage.setScene(scene);
        stage.show();

        stage.setResizable(false);
    }

    /**
     * The main-method for the program.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
