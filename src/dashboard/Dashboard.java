package dashboard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crea il controller
        Controller controller = new Controller();

        // Prendi il root layout dal controller
        Scene scene = new Scene(controller.getRoot(), 500, 400);

        primaryStage.setTitle("GenderHack Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
