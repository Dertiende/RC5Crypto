package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/authorization.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("RC5Crypto");
        primaryStage.setScene(new Scene(root,400,350));
        Button openBtn = new Button("Select file");
        final FileChooser fileChooser = new FileChooser();
        final TextField inp = new TextField();
//        VBox root = new VBox();
//        root.setSpacing(5);
//
//        root.getChildren().addAll(inp, openBtn);
//        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
//                    public void handle(ActionEvent e)
//                    {
//                        // get the file selected
//                        File file = fileChooser.showOpenDialog(primaryStage);
//                        if (file != null) {
//                            inp.setText(file.getAbsolutePath());
//                        }
//                    }
//                };
//       openBtn.setOnAction(event);
        primaryStage.getIcons().add(new Image("vec1.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }



    private void openFile(File file) {
        try {

            this.desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
