package GUI;

import GUI.controllers.Authorization;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.ConverterService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FX extends Application {
    public static void main(String[] args) {
        System.setProperty("prism.allowhidpi", "false");
        launch(args);
    }
    private final Desktop desktop = Desktop.getDesktop();
    public void start(Stage primaryStage) throws Exception {
//        String filePath = "D:\\Alex\\Downloads\\Video\\sample_1280x720_surfing_with_audio.avi";
//        ConverterService.convertVideo(new File(filePath));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/authorization.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("RC5Crypto");
        primaryStage.setScene(new Scene(root,400,350));
        primaryStage.getIcons().add(new Image("vec1.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
        Authorization authorization = loader.getController();
        authorization.login();
    }
}
