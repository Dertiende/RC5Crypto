package GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FX extends Application {
    public static void main(String[] args) {
        Application.launch();
    }
    private final Desktop desktop = Desktop.getDesktop();
    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle("RC5Kripto");
        primaryStage.setWidth(500);
        primaryStage.setHeight(800);
        Button openBtn = new Button("Select file");
        final FileChooser fileChooser = new FileChooser();
        final TextField inp = new TextField();
        VBox root = new VBox();
        root.setSpacing(5);

        root.getChildren().addAll(inp, openBtn);
        Scene scene = new Scene(root, 400, 200);
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e)
                    {
                        // get the file selected
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            inp.setText(file.getAbsolutePath());
                        }
                    }
                };
        openBtn.setOnAction(event);
        primaryStage.setScene(scene);
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
