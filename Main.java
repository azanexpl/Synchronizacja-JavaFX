package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("PW Projekt Zad 16 Michal Fabianski I7Y2S1");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setMaxHeight(530);
        primaryStage.setMinHeight(530);
        primaryStage.setMinWidth(650);
        primaryStage.setMaxWidth(650);
    }
}


