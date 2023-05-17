package main.GUI.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.domain.RC5Data;
import main.config.ControllersConfiguration;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.Timer;

public class Authorization {

	@Qualifier("mainView")
	@Autowired
	private ControllersConfiguration.ViewHolder mainView;

	@Autowired
	private AuthService authService;
	private RC5Data obj;
	Timer timer = new Timer();
	@FXML private Tooltip tooltip;
	@FXML private ResourceBundle resources;
	@FXML private Label errorMessage;
	@FXML private URL location;
	@FXML private Button loginButton;
	@FXML private TextField loginField;
	@FXML private PasswordField passField;

	@FXML
	void initialize() {
	}
	@PostConstruct
	void init() {
		//waitSec(4);
//		tooltip.setY()=== .setShowDelay(Duration.seconds(2));
		loginField.setText("Alex");
		passField.setText("Qwerty123+Йцук");
		loginButton.setOnAction(actionEvent -> {
			try {
				if (authService.isAuthLegal(loginField.getText(), passField.getText())) {
					login();
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		});
	}

	public void login(){
		Stage stage = new Stage();
		stage.setScene(new Scene(mainView.getView()));
		stage.setResizable(false);
		mainView.getController();
		loginButton.getScene().getWindow().hide();
		stage.setTitle("RC5Crypto");
		stage.getIcons().add(new Image("vec1.png"));
		stage.show();
	}


//	public void waitSec(int latency) {
//		TimerTask task;
//		task = new TimerTask() {
//		public void run() {
//			if (seconds < latency) {
//				System.out.println("Seconds = " + seconds);
//				seconds++;
//			} else {
//				timer.cancel();
//				System.out.println("Timer canceled");
//
//			}
//		}};
//		timer.schedule(task, 0, 1000);
//	}
}

