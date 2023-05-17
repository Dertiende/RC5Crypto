package main;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import main.config.ControllersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NoArgsConstructor
public class Main extends AbstractApplication {


    @Qualifier("authView")
    @Autowired
    private ControllersConfiguration.ViewHolder view;

    public static void main(String[] args) {
        launchApp(Main.class, args);
    }

    @Override
    public void start(Stage stage) {
        System.setProperty("prism.allowhidpi", "false");
//        String filePath = "D:\\Alex\\Downloads\\Video\\sample_1280x720_surfing_with_audio.avi";
//        ConverterService.convertVideo(new File(filePath));
        stage.setTitle("RC5Crypto");
        stage.setScene(new Scene(view.getView(),400,350));
        stage.getIcons().add(new Image("vec1.png"));
        stage.setResizable(false);
        stage.show();
//        ((Authorization) view.getController()).login();
    }
}
